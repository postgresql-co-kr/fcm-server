package com.ecobridge.fcm.admin.exception;

import com.ecobridge.fcm.server.dto.FcmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.ecobridge.fcm.admin")
public class AdminExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<FcmResponse> handleException(Exception e) {
        log.error("FCM Server Exception:", e);
        return new ResponseEntity<>(
                FcmResponse.builder()
                        .isSuccessful(false)
                        .errorCode("SERVER_ERROR")
                        .message("Fcm Server Exception:" + e.getMessage())
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
