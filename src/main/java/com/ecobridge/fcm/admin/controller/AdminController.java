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
package com.ecobridge.fcm.admin.controller;

import com.ecobridge.fcm.server.dto.FcmMessage;
import com.ecobridge.fcm.server.dto.FcmResponse;
import com.ecobridge.fcm.server.service.FcmApiService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@Timed(value = "fcm.admin.controller.timed")
@RequiredArgsConstructor
public class AdminController {
    private final FcmApiService service;

    @PostMapping("/set/fcm")
    public FcmResponse setFcm(@RequestBody FcmMessage msg)  {
        log.debug("/set/fcm request body: {}", msg);
        return FcmResponse.builder()
                          .isSuccessful(true)
                          .build();
    }
}
