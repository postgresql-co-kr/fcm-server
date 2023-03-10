package com.ecobridge.fcm.server.exception;

import com.ecobridge.fcm.server.vo.FcmResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FcmExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<FcmResponse> handleException(Exception e) {
        log.error("Fcm Server Exception:", e);
        return new ResponseEntity<>(
                FcmResponse.builder()
                        .isSuccessful(false)
                        .errorCode("500")
                        .message("Fcm Server Exception:" + e.getMessage())
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    @ExceptionHandler(FcmBizException.class)
    protected ResponseEntity<FcmResponse> handleFcmBizException(FcmBizException e) {
        log.error("Fcm FcmBizException:", e);
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
        log.error("Fcm Exception:", e);
        return new ResponseEntity<>(
                FcmResponse.builder()
                        .isSuccessful(false)
                        .errorCode(e.getErrorCode().toString())
                        .message(e.getMessage())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

}
