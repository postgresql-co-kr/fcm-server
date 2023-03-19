package com.ecobridge.fcm.server.controller;

import com.ecobridge.fcm.server.service.FcmApiService;
import com.ecobridge.fcm.server.vo.FailureToken;
import com.ecobridge.fcm.server.vo.FcmMessage;
import com.ecobridge.fcm.server.vo.FcmResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@Timed
public class FcmAdminController {
    private final FcmApiService service;

    public FcmAdminController(FcmApiService service) {
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
