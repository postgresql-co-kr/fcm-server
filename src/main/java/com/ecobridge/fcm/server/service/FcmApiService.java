package com.ecobridge.fcm.server.service;

import com.ecobridge.fcm.server.vo.FailureToken;
import com.ecobridge.fcm.server.vo.FcmBuilder;
import com.ecobridge.fcm.server.vo.FcmMessage;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FcmApiService {
    private final int MAX_SEND_MESSAGES = 500; // send all 최대 500건 제한 구글 정책
    private final int MAX_MESSAGE_SIZE = 4 * 1024; // 4k 단건 , topic 2k 제한

    /**
     * 단건 메세지 전송
     *
     * @param msg 메시지
     * @return messageId
     * @throws FirebaseMessagingException
     */
    public String sendMessage(FcmMessage msg) throws FirebaseMessagingException {

        FcmBuilder fcmBuilder = getFcmBuilder(msg);
        Message message = Message.builder()
                .setNotification(fcmBuilder.getNotificationBuilder().build())
                .setAndroidConfig(fcmBuilder.getAosBuilder().build())
                .setApnsConfig(fcmBuilder.getIosBuilder().build())
                .setWebpushConfig(fcmBuilder.getWebBuilder().build())
                .putAllData(msg.getData())
                .setToken(msg.getToken())
                .build();

        return FirebaseMessaging.getInstance().send(message); // messageId

    }

    /**
     * 동일한 메시지로 여러건을 보내는 경우
     * 최대 500개 token만 설정가능
     *
     * @param msg
     * @return
     * @throws FirebaseMessagingException
     */
    public List<FailureToken> sendMulticast(FcmMessage msg) throws FirebaseMessagingException {
        FcmBuilder fcmBuilder = getFcmBuilder(msg);
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(fcmBuilder.getNotificationBuilder().build())
                .setAndroidConfig(fcmBuilder.getAosBuilder().build())
                .setApnsConfig(fcmBuilder.getIosBuilder().build())
                .setWebpushConfig(fcmBuilder.getWebBuilder().build())
                .putAllData(msg.getData())
                .addAllTokens(msg.getTokens())
                .build();

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
                }
            }
        }

        return failureTokenList;
    }

    /**
     * 개인화 메시지를 동시에 여러건 보내는 경우
     * 최대 500건 제한
     *
     * @param msgs
     * @return
     * @throws FirebaseMessagingException
     */
    public List<FailureToken> sendAll(List<FcmMessage> msgs) throws FirebaseMessagingException {

        List<Message> messageList = new ArrayList<>();

        for (FcmMessage msg : msgs) {
            FcmBuilder fcmBuilder = getFcmBuilder(msg);
            Message message = Message.builder()
                    .setNotification(fcmBuilder.getNotificationBuilder().build())
                    .setAndroidConfig(fcmBuilder.getAosBuilder().build())
                    .setApnsConfig(fcmBuilder.getIosBuilder().build())
                    .setWebpushConfig(fcmBuilder.getWebBuilder().build())
                    .putAllData(msg.getData())
                    .setToken(msg.getToken())
                    .build();
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
        return FailureToken.builder()
                .token(token)
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    /**
     * Message Builder
     *
     * @param msg Fcm Message
     * @return
     */
    private FcmBuilder getFcmBuilder(FcmMessage msg) {
        // 공통
        Notification.Builder notificationBuilder = Notification.builder()
                .setTitle(msg.getTitle())
                .setBody(msg.getBody());

        // AOS
        AndroidConfig.Builder aosBuilder = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH);

        // APN
        ApnsConfig.Builder iosBuilder = ApnsConfig.builder()
                .setAps(
                        Aps.builder()
                                .setContentAvailable(true) //IOS 푸시 뱃지 카운트
                                .setMutableContent(true) // 이미지표시
                                .build()
                );
        // Web
        WebpushConfig.Builder webBuilder = WebpushConfig.builder();

        // 이미지 설정
        if (StringUtils.hasLength(msg.getImage())) {
            aosBuilder.setNotification(
                    AndroidNotification.builder()
                            .setImage(msg.getImage()).build()
            );
            iosBuilder.setFcmOptions(
                    ApnsFcmOptions.builder()
                            .setImage(msg.getImage()).build()
            );
            webBuilder.setNotification(
                    WebpushNotification.builder()
                            .setImage(msg.getImage()).build()
            );
        }

        return FcmBuilder.builder()
                .notificationBuilder(notificationBuilder)
                .aosBuilder(aosBuilder)
                .iosBuilder(iosBuilder)
                .webBuilder(webBuilder).build();

    }

}
