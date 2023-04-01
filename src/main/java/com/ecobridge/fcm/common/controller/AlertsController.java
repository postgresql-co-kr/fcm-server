package com.ecobridge.fcm.common.controller;

import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Timed(value = "fcm.auth.controller.timed")
public class AlertsController {
    @RequestMapping("/v1/alerts")
    public ResponseEntity<?> alerts(HttpServletRequest request) throws IOException {
        log.debug("alerts: {}",request.getReader().lines().collect(Collectors.toList()));
        return ResponseEntity.ok("ok");
    }
}
