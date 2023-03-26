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
