package com.ecobridge.fcm.server.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FcmBizException extends RuntimeException {
    private String errorCode;
    private String message;

    public FcmBizException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
