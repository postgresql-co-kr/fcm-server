package com.ecobridge.fcm.server.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FailureToken {
    private String token;
    private String errorCode;
    private String message;
}
