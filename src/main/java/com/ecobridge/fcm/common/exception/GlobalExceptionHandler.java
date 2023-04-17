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
package com.ecobridge.fcm.common.exception;

import com.ecobridge.fcm.common.dto.ErrorResponse;
import com.ecobridge.fcm.common.enums.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.ecobridge.fcm")
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e, HttpServletRequest request) {
        log.error("handleAllExceptions:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                             .code(ResponseCode.INTERNAL_SERVER_ERROR_01.code())
                             .message(ResponseCode.INTERNAL_SERVER_ERROR_01.getMessage())
                             .path(request.getRequestURI())
                             .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                               .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                               .collect(Collectors.joining(", "));
        log.error("handleMethodArgumentNotValidException:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                             .code(ResponseCode.BAD_REQUEST_01.code())
                             .message(errorMessage)
                             .path(request.getRequestURI())
                             .build()
        );
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(Exception e, HttpServletRequest request) {
        log.error("UsernameNotFoundException:", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                             .code(ResponseCode.UNAUTHORIZED_01.code())
                             .message(ResponseCode.UNAUTHORIZED_01.getMessage())
                             .path(request.getRequestURI())
                             .build()
        );
    }
}
