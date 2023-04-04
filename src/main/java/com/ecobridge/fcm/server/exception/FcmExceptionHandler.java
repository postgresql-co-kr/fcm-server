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
package com.ecobridge.fcm.server.exception;

import com.ecobridge.fcm.server.dto.FcmResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.ecobridge.fcm.server")
public class FcmExceptionHandler {


    @ExceptionHandler(FcmBizException.class)
    protected ResponseEntity<FcmResponse> handleFcmBizException(FcmBizException e) {
        log.error("FCM FcmBizException:", e);
        return new ResponseEntity<>(
                FcmResponse.builder()
                        .isSuccessful(false)
                        .errorCode(e.getErrorCode())
                        .message(e.getMessage())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(InvalidRequestException.class)
    protected ResponseEntity<FcmResponse> handleInvalidRequestException(InvalidRequestException e) {
        log.error("FCM InvalidRequestException:", e);
        return new ResponseEntity<>(
                FcmResponse.builder()
                        .isSuccessful(false)
                        .errorCode(e.getErrorCode())
                        .message(e.getMessage())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(FirebaseMessagingException.class)
    protected ResponseEntity<FcmResponse> handleFcmBizException(FirebaseMessagingException e) {
        log.error("FCM FirebaseMessagingException:", e);
        return new ResponseEntity<>(
                FcmResponse.builder()
                        .isSuccessful(false)
                        .errorCode(e.getErrorCode().toString())
                        .message(e.getMessage())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

}
