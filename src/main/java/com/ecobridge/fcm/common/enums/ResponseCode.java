package com.ecobridge.fcm.common.enums;

import org.springframework.http.HttpStatus;

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
public enum ResponseCode {
    OK("00000", ""),
    OK_SAVE("00001", "save ok"),

    BAD_REQUEST_01(HttpStatus.BAD_REQUEST.value() + "01", "MethodArgumentNotValidException"),
    BAD_REQUEST_02(HttpStatus.BAD_REQUEST.value() + "02", "02"),
    UNAUTHORIZED_01(HttpStatus.UNAUTHORIZED.value() + "01", "Check usename or Email/password"),
    UNAUTHORIZED_02(HttpStatus.UNAUTHORIZED.value() + "02", "02"),
    INTERNAL_SERVER_ERROR_01(HttpStatus.INTERNAL_SERVER_ERROR.value() + "01", "Internal server error : Exception & Runtime"),
    INTERNAL_SERVER_ERROR_02(HttpStatus.INTERNAL_SERVER_ERROR.value() + "02", "Internal server error : "),
    FORBIDDEN_01(HttpStatus.FORBIDDEN.value() + "01", "01"),
    FORBIDDEN_02(HttpStatus.FORBIDDEN.value() + "02", "02");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String value() {
        return this.code;
    }

    public String code() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
