package com.ecobridge.fcm.server.service;

import com.ecobridge.fcm.server.vo.FailureToken;
import com.ecobridge.fcm.server.vo.FcmBuilder;
import com.ecobridge.fcm.server.vo.FcmMessage;
import com.google.firebase.messaging.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    public FcmApiService(MeterRegistry meterRegistry) {
        this.failMutilCastCounter = Counter.builder("fcm.send.counter").description("The send count of fail/success by fcm method").tag("type", "fail").tag("id", "multicast").register(meterRegistry);
        this.failSendAllCastCounter = Counter.builder("fcm.send.counter").tag("type", "fail").tag("id", "sendall").register(meterRegistry);
        this.failMessageCounter = Counter.builder("fcm.send.counter").tag("type", "fail").tag("id", "message").register(meterRegistry);
        this.successMutilCastCounter = Counter.builder("fcm.send.counter").tag("type", "success").tag("id", "multicast").register(meterRegistry);
        this.successSendAllCastCounter = Counter.builder("fcm.send.counter").tag("type", "success").tag("id", "senall").register(meterRegistry);
        this.successMessageCounter = Counter.builder("fcm.send.counter").tag("type", "success").tag("id", "message").register(meterRegistry);
    }

    /**
     * 단건 메세지 전송
     *
     * @param msg 메시지
     * @return messageId
     * @throws FirebaseMessagingException
     */
    public String sendMessage(@Nonnull FcmMessage msg) throws FirebaseMessagingException {

        FcmBuilder fcmBuilder = createFcmBuilder(msg);
        Message message = Message.builder().setNotification(fcmBuilder.getNotificationBuilder().build()).setAndroidConfig(fcmBuilder.getAosBuilder().build()).setApnsConfig(fcmBuilder.getIosBuilder().build()).setWebpushConfig(fcmBuilder.getWebBuilder().build()).putAllData(msg.getData()).setToken(msg.getToken()).build();

        String messageId = FirebaseMessaging.getInstance().send(message);
        if (messageId == null) { // 전송실패
            failMessageCounter.increment();
            fcmErrorTokenLog.info("{},{},{}", msg.getToken(), "MESSAGE_ID_NULL", "SEND MESSAGE ERROR"); // 에러토큰만 별도 관리
        }
        successMessageCounter.increment();
        return messageId;
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
        FcmBuilder fcmBuilder = createFcmBuilder(msg);
        MulticastMessage message = MulticastMessage.builder().setNotification(fcmBuilder.getNotificationBuilder().build()).setAndroidConfig(fcmBuilder.getAosBuilder().build()).setApnsConfig(fcmBuilder.getIosBuilder().build()).setWebpushConfig(fcmBuilder.getWebBuilder().build()).putAllData(msg.getData()).addAllTokens(msg.getTokens()).build();

        List<FailureToken> failureTokenList = new ArrayList<>();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

        if (response.getFailureCount() > 0) {
            List<SendResponse> sendResponseList = response.getResponses();
            for (int i = 0; i < sendResponseList.size(); i++) {
                SendResponse sendResponse = sendResponseList.get(i);
                if (!sendResponse.isSuccessful()) {
                    FirebaseMessagingException exception = sendResponse.getException();
                    String token = msg.getTokens().get(i);
                    failureTokenList.add(makeFailureToken(token, exception));
                    failMutilCastCounter.increment();

                } else {
                    successMutilCastCounter.increment();
                }
            }
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

        List<Message> messageList = new ArrayList<>();

        for (FcmMessage msg : msgs) {
            FcmBuilder fcmBuilder = createFcmBuilder(msg);
            Message message = Message.builder().setNotification(fcmBuilder.getNotificationBuilder().build()).setAndroidConfig(fcmBuilder.getAosBuilder().build()).setApnsConfig(fcmBuilder.getIosBuilder().build()).setWebpushConfig(fcmBuilder.getWebBuilder().build()).putAllData(msg.getData()).setToken(msg.getToken()).build();
            messageList.add(message);
        }

        List<FailureToken> failureTokenList = new ArrayList<>();
        BatchResponse response = FirebaseMessaging.getInstance().sendAll(messageList);//sendAll

        if (response.getFailureCount() > 0) {
            List<SendResponse> sendResponseList = response.getResponses();
            for (int i = 0; i < sendResponseList.size(); i++) {
                SendResponse sendResponse = sendResponseList.get(i);
                if (!sendResponse.isSuccessful()) {
                    FirebaseMessagingException exception = sendResponse.getException();
                    String token = msgs.get(i).getToken();
                    failureTokenList.add(makeFailureToken(token, exception));
                    failSendAllCastCounter.increment();
                } else {
                    successSendAllCastCounter.increment();
                }
            }
        }

        return failureTokenList;
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
        Notification.Builder notificationBuilder = Notification.builder().setTitle(msg.getTitle()).setBody(msg.getBody());

        // AOS
        AndroidConfig.Builder aosBuilder = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH);

        // APN
        ApnsConfig.Builder iosBuilder = ApnsConfig.builder().setAps(Aps.builder().setContentAvailable(true) //IOS 푸시 뱃지 카운트
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

        return FcmBuilder.builder().notificationBuilder(notificationBuilder).aosBuilder(aosBuilder).iosBuilder(iosBuilder).webBuilder(webBuilder).build();

    }

}
