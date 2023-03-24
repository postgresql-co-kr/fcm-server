package com.ecobridge.fcm.admin.controller;

import com.ecobridge.fcm.server.service.FcmApiService;
import com.ecobridge.fcm.server.dto.FcmMessage;
import com.ecobridge.fcm.server.dto.FcmResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@Timed
public class AdminController {
    private final FcmApiService service;

    public AdminController(FcmApiService service) {
        this.service = service;
    }


    @PostMapping("/set/fcm")
    @Timed(value = "fcm.admin.controller.timed")
    public FcmResponse setFcm(@RequestBody FcmMessage msg) throws FirebaseMessagingException {
        log.debug("/set/fcm request body: {}", msg);

        return FcmResponse.builder()
                          .isSuccessful(true)
                          .build();
    }

}
