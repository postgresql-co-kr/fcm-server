/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.server.controller;

import com.ecobridge.fcm.server.service.FcmApiService;
import com.ecobridge.fcm.server.dto.FailureToken;
import com.ecobridge.fcm.server.dto.FcmMessage;
import com.ecobridge.fcm.server.dto.FcmResponse;
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
@RequestMapping("/api/v1/fcm")
@Timed(value = "fcm.fcm.api.controller.timed")
public class FcmApiController {
    private final FcmApiService service;

    public FcmApiController(FcmApiService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public String hello() throws FirebaseMessagingException {
        return "Hello fcm-server";
    }

    @PostMapping("/send/multicast")
    public FcmResponse multicast(@RequestBody FcmMessage msg) throws FirebaseMessagingException {
        log.debug("/send/multicast request body: {}", msg);
        List<FailureToken> failureTokens = service.sendMulticast(msg);
        return FcmResponse.builder()
                .isSuccessful(true)
                .failureTokens(failureTokens)
                .build();
    }

    @PostMapping("/send/all")
    public FcmResponse sendAll(@RequestBody List<FcmMessage> msgs) throws FirebaseMessagingException {
        log.debug("/send/all request body: {}", msgs);
        List<FailureToken> failureTokens = service.sendAll(msgs);
        return FcmResponse.builder()
                .isSuccessful(true)
                .failureTokens(failureTokens)
                .build();
    }

    @PostMapping("/send/message")
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
