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
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.ecobridge.fcm")
public class GlobalExceptionHandler {

    /*
    HttpRequestMethodNotSupportedException
    405 (SC_METHOD_NOT_ALLOWED)

    HttpMediaTypeNotSupportedException
    415 (SC_UNSUPPORTED_MEDIA_TYPE)

    HttpMediaTypeNotAcceptableException

  406 (SC_NOT_ACCEPTABLE)

      MissingPathVariableException

  500 (SC_INTERNAL_SERVER_ERROR)

      MissingServletRequestParameterException

  400 (SC_BAD_REQUEST)

      MissingServletRequestPartException

  400 (SC_BAD_REQUEST)

      ServletRequestBindingException

  400 (SC_BAD_REQUEST)

      ConversionNotSupportedException

  500 (SC_INTERNAL_SERVER_ERROR)

      TypeMismatchException

  400 (SC_BAD_REQUEST)

      HttpMessageNotReadableException

  400 (SC_BAD_REQUEST)

      HttpMessageNotWritableException

  500 (SC_INTERNAL_SERVER_ERROR)

      MethodArgumentNotValidException

  400 (SC_BAD_REQUEST)

      BindException

  400 (SC_BAD_REQUEST)

      NoHandlerFoundException

  404 (SC_NOT_FOUND)

      AsyncRequestTimeoutException

  503 (SC_SERVICE_UNAVAILABLE)

  */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", request.getRequestURI());
        log.error("Internal server error:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                               .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                               .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, request.getRequestURI());
        log.error("@valid exception:", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
