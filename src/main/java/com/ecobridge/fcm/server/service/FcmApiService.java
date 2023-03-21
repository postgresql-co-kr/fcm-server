package com.ecobridge.fcm.server.service;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import com.ecobridge.fcm.server.config.FcmPropsConfig;
import com.ecobridge.fcm.server.entity.FcmLogEntity;
import com.ecobridge.fcm.server.exception.InvalidRequestException;
import com.ecobridge.fcm.server.repository.FcmLogEntityRepository;
import com.ecobridge.fcm.server.repository.FcmLogQueryRepository;
import com.ecobridge.fcm.server.repository.FcmMsgEntityRepository;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.common.util.URLChecker;
import com.ecobridge.fcm.server.vo.FailureToken;
import com.ecobridge.fcm.server.vo.FcmApp;
import com.ecobridge.fcm.server.vo.FcmBuilder;
import com.ecobridge.fcm.server.vo.FcmMessage;
import com.google.firebase.messaging.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FcmApiService {
    private final static Logger fcmErrorTokenLog = LoggerFactory.getLogger("fcmErrorToken");
    private final int MAX_SEND_MESSAGES = 500; // send all 최대 500건 제한 구글 정책
    private final int MAX_MESSAGE_SIZE = 4 * 1024; // 4k 단건 , topic 2k 제한

    private final Counter failMutilCastCounter;
    private final Counter failSendAllCastCounter;
    private final Counter failMessageCounter;
    private final Counter successMutilCastCounter;
    private final Counter successSendAllCastCounter;
    private final Counter successMessageCounter;
    private final FcmPropsConfig fcmPropsConfig;

    private final FcmMsgEntityRepository fcmMsgEntityRepository;
    private final FcmLogEntityRepository fcmLogEntityRepository;
    private final FcmMsgQueryRepository fcmMsgQueryRepository;
    private final FcmLogQueryRepository fcmLogQueryRepository;

    @Autowired
    public FcmApiService(MeterRegistry meterRegistry, Environment env, FcmPropsConfig fcmPropsConfig, FcmMsgEntityRepository fcmMsgEntityRepository, FcmLogEntityRepository fcmLogEntityRepository, FcmMsgQueryRepository fcmMsgQueryRepository, FcmLogQueryRepository fcmLogQueryRepository) {
        this.failMutilCastCounter = Counter.builder("fcm.send.counter")
                                           .description("The send count of fail/success by fcm method")
                                           .tag("type", "fail").tag("id", "multicast").register(meterRegistry);
        this.failSendAllCastCounter = Counter.builder("fcm.send.counter").tag("type", "fail").tag("id", "sendall")
                                             .register(meterRegistry);
        this.failMessageCounter = Counter.builder("fcm.send.counter").tag("type", "fail").tag("id", "message")
                                         .register(meterRegistry);
        this.successMutilCastCounter = Counter.builder("fcm.send.counter").tag("type", "success").tag("id", "multicast")
                                              .register(meterRegistry);
        this.successSendAllCastCounter = Counter.builder("fcm.send.counter").tag("type", "success").tag("id", "senall")
                                                .register(meterRegistry);
        this.successMessageCounter = Counter.builder("fcm.send.counter").tag("type", "success").tag("id", "message")
                                            .register(meterRegistry);
        this.fcmPropsConfig = fcmPropsConfig;
        this.fcmMsgEntityRepository = fcmMsgEntityRepository;
        this.fcmLogEntityRepository = fcmLogEntityRepository;
        this.fcmMsgQueryRepository = fcmMsgQueryRepository;
        this.fcmLogQueryRepository = fcmLogQueryRepository;
    }

    /**
     * 단건 메세지 전송
     *
     * @param msg 메시지
     * @return messageId
     * @throws FirebaseMessagingException
     */
    public String sendMessage(@Nonnull FcmMessage msg) throws FirebaseMessagingException {
        //null, app name, title, body check
        validateMessage(msg);

        // Token check
        if (!StringUtils.hasLength(msg.getToken())) {
            throw new InvalidRequestException("FcmMessage.token is required");
        }

        FcmBuilder fcmBuilder = createFcmBuilder(msg);
        Message message = Message.builder().setNotification(fcmBuilder.getNotificationBuilder().build())
                                 .setAndroidConfig(fcmBuilder.getAosBuilder().build())
                                 .setApnsConfig(fcmBuilder.getIosBuilder().build())
                                 .setWebpushConfig(fcmBuilder.getWebBuilder().build()).putAllData(msg.getData())
                                 .setToken(msg.getToken()).build();

        String messageId = FirebaseMessaging.getInstance().send(message);

        String successYn = null;
        if (StringUtils.hasLength(messageId)) {
            successYn = "Y";
            successMessageCounter.increment();

        } else {
            successYn = "N";
            failMessageCounter.increment();  // 전송 실패
            fcmErrorTokenLog.info("{},{},{}", msg.getToken(), "MESSAGE_ID_NULL", "SEND MESSAGE ERROR"); // 에러 토큰만 별도 관리
        }

        if (isDbLog(msg.getAppName())) {
            fcmLogEntityRepository.save(createFcmLogEntity(msg, successYn));
        }
        return messageId;
    }

    private boolean isDbLog(String appName) {
        FcmApp fcmApp = fcmPropsConfig.getFcmApps().stream().filter(vo -> vo.getName().equals(appName))
                               .findFirst().orElseGet(null);
        if (fcmApp != null && fcmApp.isDbLog()) {
            return true;
        }

        return false;

    }

    /**
     * 동일한 메시지로 여러건을 보내는 경우
     * 최대 500개 token만 설정가능
     * FcmMessage setTokens 설정
     *
     * @param msg 메시지
     * @return 실패한 토큰 정보
     * @throws FirebaseMessagingException
     */
    public List<FailureToken> sendMulticast(@Nonnull FcmMessage msg) throws FirebaseMessagingException {
        // null, app name, title, body check
        validateMessage(msg);

        // Token check
        if (msg.getTokens() == null || msg.getTokens().isEmpty()) {
            throw new InvalidRequestException("FcmMessage.token is required");
        }

        if (msg.getTokens().size() > 500) {
            throw new InvalidRequestException("FcmMessage.tokens: The maximum token count of 500 has been exceeded.");
        }

        FcmBuilder fcmBuilder = createFcmBuilder(msg);
        MulticastMessage message = MulticastMessage.builder()
                                                   .setNotification(fcmBuilder.getNotificationBuilder().build())
                                                   .setAndroidConfig(fcmBuilder.getAosBuilder().build())
                                                   .setApnsConfig(fcmBuilder.getIosBuilder().build())
                                                   .setWebpushConfig(fcmBuilder.getWebBuilder().build())
                                                   .putAllData(msg.getData()).addAllTokens(msg.getTokens()).build();

        List<FailureToken> failureTokenList = new ArrayList<>();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
        List<FcmLogEntity> logEntityList = new ArrayList<>();
        List<SendResponse> sendResponseList = response.getResponses();
        for (int i = 0; i < sendResponseList.size(); i++) {
            SendResponse sendResponse = sendResponseList.get(i);
            String successYn = null;
            if (!sendResponse.isSuccessful()) {
                FirebaseMessagingException exception = sendResponse.getException();
                String token = msg.getTokens().get(i);
                failureTokenList.add(makeFailureToken(token, exception));
                failMutilCastCounter.increment();
                successYn = "N";
            } else {
                successMutilCastCounter.increment();
                successYn = "Y";
            }

            if (isDbLog(msg.getAppName())) {
                FcmLogEntity logEntity =
                        createFcmLogEntity(
                                FcmMessage.builder().appName(msg.getAppName())
                                          .title(msg.getTitle())
                                          .body(msg.getBody()).token(msg.getToken())
                                          .device(msg.getDevice())
                                          .image(msg.getImage())
                                          .build(), successYn);
                logEntityList.add(logEntity);
            }

        }

        if (!logEntityList.isEmpty()) {
            fcmLogQueryRepository.batchUpdateLog(logEntityList);
        }

        return failureTokenList;
    }

    /**
     * 개인화 메시지를 동시에 여러건 보내는 경우
     * 최대 500건 제한
     *
     * @param msgs 개별 메시지 리스트
     * @return
     * @throws FirebaseMessagingException
     */
    public List<FailureToken> sendAll(@Nonnull List<FcmMessage> msgs) throws FirebaseMessagingException {

        // Token check
        if (msgs == null || msgs.isEmpty()) {
            throw new InvalidRequestException("FcmMessages is required");
        }

        if (msgs.size() > 500) {
            throw new InvalidRequestException("FcmMessage.tokens: The maximum token count of 500 has been exceeded.");
        }

        for (FcmMessage msg : msgs) {
            // null, app name, title, body check
            validateMessage(msg);

            // Token check
            if (!StringUtils.hasLength(msg.getToken())) {
                throw new InvalidRequestException("FcmMessage.token is required");
            }
        }

        List<Message> messageList = new ArrayList<>();

        for (FcmMessage msg : msgs) {
            FcmBuilder fcmBuilder = createFcmBuilder(msg);
            Message message = Message.builder().setNotification(fcmBuilder.getNotificationBuilder().build())
                                     .setAndroidConfig(fcmBuilder.getAosBuilder().build())
                                     .setApnsConfig(fcmBuilder.getIosBuilder().build())
                                     .setWebpushConfig(fcmBuilder.getWebBuilder().build()).putAllData(msg.getData())
                                     .setToken(msg.getToken()).build();
            messageList.add(message);
        }

        List<FailureToken> failureTokenList = new ArrayList<>();
        BatchResponse response = FirebaseMessaging.getInstance().sendAll(messageList);//sendAll
        List<FcmLogEntity> logEntityList = new ArrayList<>();
        List<SendResponse> sendResponseList = response.getResponses();
        for (int i = 0; i < sendResponseList.size(); i++) {
            SendResponse sendResponse = sendResponseList.get(i);
            String successYn = null;
            if (!sendResponse.isSuccessful()) {
                FirebaseMessagingException exception = sendResponse.getException();
                String token = msgs.get(i).getToken();
                failureTokenList.add(makeFailureToken(token, exception));
                failSendAllCastCounter.increment();
                successYn = "N";
            } else {
                successSendAllCastCounter.increment();
                successYn = "Y";
            }
            if (isDbLog(msgs.get(i).getAppName())) {
                logEntityList.add(createFcmLogEntity(msgs.get(i), successYn));
            }
        }

        if (!logEntityList.isEmpty()) {
            fcmLogQueryRepository.batchUpdateLog(logEntityList);
        }

        return failureTokenList;
    }


    @Nonnull
    public String getCsvLogFailTokens(String date) throws IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        FileAppender<?> fileAppender = (FileAppender<?>) loggerContext.getLogger("fcmErrorToken")
                                                                      .getAppender("ERROR_TOKEN_CSV");
        String filePath = fileAppender.getFile();
        String nowYmd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate ldDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (nowYmd.compareTo(date) > 0) {
            filePath = filePath.replaceAll("\\.log", "")
                               .concat("-")
                               .concat(ldDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                               .concat(".log");
        }
        log.info("Failed token log file path: {}", filePath);

        Path path = Path.of(filePath);
        if (Files.exists(path)) {
            return String.join(System.lineSeparator(), Files.readAllLines(path));
        }
        return "";
    }

    private FailureToken makeFailureToken(String token, FirebaseMessagingException exception) {
        String errorCode = "UNKNOWN";
        String message = "Exception is null, unknown exception!";
        if (exception != null) {
            errorCode = exception.getErrorCode().toString();
            message = exception.getMessage();
        }
        log.info("Failure Message: [ErrorCode]:{}\n\t\t[ErrorMessage]:{}\n\t\t[Token]:{}", errorCode, message, token);
        fcmErrorTokenLog.info("{},{},{}", token, errorCode, message); // 에러토큰만 별도 관리
        return FailureToken.builder().token(token).errorCode(errorCode).message(message).build();
    }

    /**
     * Message Builder
     *
     * @param msg Fcm Message
     * @return
     */
    private FcmBuilder createFcmBuilder(FcmMessage msg) {
        // 공통
        Notification.Builder notificationBuilder = Notification.builder().setTitle(msg.getTitle())
                                                               .setBody(msg.getBody());

        // AOS
        AndroidConfig.Builder aosBuilder = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH);

        // APN
        ApnsConfig.Builder iosBuilder = ApnsConfig.builder()
                                                  .setAps(Aps.builder().setContentAvailable(true) //IOS 푸시 뱃지 카운트
                                                             .setMutableContent(true) // 이미지표시
                                                             .build());
        // Web
        WebpushConfig.Builder webBuilder = WebpushConfig.builder();

        // 이미지 설정
        if (StringUtils.hasLength(msg.getImage())) {
            aosBuilder.setNotification(AndroidNotification.builder().setImage(msg.getImage()).build());
            iosBuilder.setFcmOptions(ApnsFcmOptions.builder().setImage(msg.getImage()).build());
            webBuilder.setNotification(WebpushNotification.builder().setImage(msg.getImage()).build());
        }

        return FcmBuilder.builder().notificationBuilder(notificationBuilder).aosBuilder(aosBuilder)
                         .iosBuilder(iosBuilder).webBuilder(webBuilder).build();

    }


    private void validateMessage(FcmMessage msg) {
        if (msg == null) {
            throw new InvalidRequestException("FcmMessage can not be null.");
        }
        if (!StringUtils.hasLength(msg.getAppName())) {
            throw new InvalidRequestException("FcmMessage.appName is required.");
        }
        if (!StringUtils.hasLength(msg.getTitle())) {
            throw new InvalidRequestException("FcmMessage.title is required.");
        }
        if (!StringUtils.hasLength(msg.getBody())) {
            throw new InvalidRequestException("FcmMessage.body is required.");
        }

        if (StringUtils.hasLength(msg.getImage())) {
            if (URLChecker.isHTTPS(msg.getImage())) {
                if (!msg.getData().containsKey("image")) {
                    msg.getData().put("image", msg.getImage());
                }
            } else {
                throw new InvalidRequestException("FcmMessage.image is not https protocol");
            }
        }
    }

    private FcmLogEntity createFcmLogEntity(FcmMessage msg, String successYn) {
        FcmLogEntity log = new FcmLogEntity();
        log.setLogKey(UUID.randomUUID().toString().replaceAll("\\-", ""));
        log.setAppName(msg.getAppName());
        log.setFcmToken(msg.getToken());
        log.setDeviceType(msg.getDevice().toString());
        log.setSuccessYn(successYn);
        return log;
    }

}
