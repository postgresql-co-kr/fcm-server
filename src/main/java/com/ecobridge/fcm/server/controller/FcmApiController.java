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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/fcm")
@Timed
public class FcmApiController {
    private final FcmApiService service;

    public FcmApiController(FcmApiService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    @Timed(value = "fcm.fcm.api.controller.timed")
    public String hello() throws FirebaseMessagingException {
        return "Hello fcm-server";
    }

    @PostMapping("/send/multicast")
    @Timed(value = "fcm.api.controller.timed")
    public FcmResponse multicast(@RequestBody FcmMessage msg) throws FirebaseMessagingException {
        log.debug("/send/multicast request body: {}", msg);
        List<FailureToken> failureTokens = service.sendMulticast(msg);
        return FcmResponse.builder()
                .isSuccessful(true)
                .failureTokens(failureTokens)
                .build();
    }

    @PostMapping("/send/all")
    @Timed(value = "fcm.api.controller.timed")
    public FcmResponse sendAll(@RequestBody List<FcmMessage> msgs) throws FirebaseMessagingException {
        log.debug("/send/all request body: {}", msgs);
        List<FailureToken> failureTokens = service.sendAll(msgs);
        return FcmResponse.builder()
                .isSuccessful(true)
                .failureTokens(failureTokens)
                .build();
    }

    @PostMapping("/send/message")
    @Timed(value = "fcm.api.controller.timed")
    public FcmResponse sendMessage(@RequestBody FcmMessage msg) throws FirebaseMessagingException {
        log.debug("/send/message request body: {}", msg);
        String messageId = service.sendMessage(msg);
        return FcmResponse.builder()
                .isSuccessful(true)
                .messageId(messageId)
                .failureTokens(List.of())
                .build();
    }

    @GetMapping(value = "/log/failed/tokens/{date}", produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    @Timed(value = "fcm.api.controller.timed")
    public String getLogFailTokens(@PathVariable("date") String date, HttpServletRequest request) throws IOException {
        //CSV 형식으로 반환하는 로직
        String result = service.getCsvLogFailTokens(date);
        if (!isCsvRequest(request)) {
            //Json 형식으로 반환하는 로직
            return csvToJson(result);
        }
        return result;
    }

    private String csvToJson(String csvString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> lineList = Arrays.stream(csvString.split(System.lineSeparator())).toList();
        List<Map<String, String>> result = new ArrayList<>();
        for (String line : lineList) {
            if (StringUtils.hasLength(line)) {
                Map<String, String> map = new HashMap<>();
                String[] data = line.split(",");
                map.put("datetime", data[0]);
                map.put("token", data[1]);
                result.add(map);
            }
        }
        return objectMapper.writeValueAsString(result);
    }

    private boolean isCsvRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("text/csv");
    }

}
