package com.ecobridge.fcm.server.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class FcmResponse implements Serializable {
    @Builder.Default
    private boolean isSuccessful = false;
    private String messageId;
    private String errorCode;
    private String message;
    private List<FailureToken> failureTokens;
}
