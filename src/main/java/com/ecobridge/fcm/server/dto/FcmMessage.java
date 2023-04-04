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
package com.ecobridge.fcm.server.dto;

import com.ecobridge.fcm.server.enums.FcmDevice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmMessage implements Serializable {
    /**
     * 복수건 Token 일때 필수
     */
    private List<String> tokens;
    /**
     * 단건 Fcm Token 일때 필수
     */
    private String token;
    /**
     * 사용자 데이터
     */
    private Map<String, String> data;  // 단말기에서 데이터를 취득하여 별도 표시(제목,내용,이미지를 다시 담아서 보내줌)

    // ---------------------
    // Notification
    // ---------------------
    @NonNull
    private String appName; // App name
    @NonNull
    private String title; //알림 제목
    @NonNull
    private String body; //알림 본문
    private String image; //알림 이미지 IOS
    private FcmDevice device; // APN 오류
}
