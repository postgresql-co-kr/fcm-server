package com.ecobridge.fcm.server.controller;

import com.ecobridge.fcm.server.service.FcmApiService;
import com.ecobridge.fcm.server.vo.FailureToken;
import com.ecobridge.fcm.server.vo.FcmMessage;
import com.ecobridge.fcm.server.vo.FcmResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fcm")
public class FcmApiController {
    private final FcmApiService service;

    public FcmApiController(FcmApiService service) {
        this.service = service;
    }


    @PostMapping("/send/multicast")
    @Timed(value = "send.multicast.time", description = "multicast response time")
    public FcmResponse multicast(@RequestBody FcmMessage msg) throws FirebaseMessagingException {
        log.debug("/send/multicast request body: {}", msg);
        List<FailureToken> failureTokens = service.sendMulticast(msg);
        return FcmResponse.builder()
                .isSuccessful(true)
                .failureTokens(failureTokens)
                .build();
    }

    @PostMapping("/send/all")
    @Timed(value = "send.all.time", description = "send all response time")
    public FcmResponse sendAll(@RequestBody List<FcmMessage> msgs) throws FirebaseMessagingException {
        log.debug("/send/all request body: {}", msgs);
        List<FailureToken> failureTokens = service.sendAll(msgs);
        return FcmResponse.builder()
                .isSuccessful(true)
                .failureTokens(failureTokens)
                .build();
    }

    @PostMapping("/send/message")
    @Timed(value = "send.message.time", description = "message response time")
    public FcmResponse sendMessage(@RequestBody FcmMessage msg) throws FirebaseMessagingException {
        log.debug("/send/message request body: {}", msg);
        String messageId = service.sendMessage(msg);
        return FcmResponse.builder()
                .isSuccessful(true)
                .messageId(messageId)
                .failureTokens(List.of())
                .build();
    }
}
