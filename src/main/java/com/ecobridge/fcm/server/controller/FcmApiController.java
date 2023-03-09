package com.ecobridge.fcm.server.controller;

import com.ecobridge.fcm.server.service.FcmApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fcm")
public class FcmApiController {
    private final FcmApiService service;

    public FcmApiController(FcmApiService service) {
        this.service = service;
    }


}
