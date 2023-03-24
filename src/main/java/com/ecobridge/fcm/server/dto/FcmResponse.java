package com.ecobridge.fcm.server.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class FcmResponse implements Serializable {
    @Builder.Default
    private boolean isSuccessful = false;
    @Builder.Default
    private String messageId = "";
    @Builder.Default
    private String errorCode = "";
    @Builder.Default
    private String message = "";
    private List<FailureToken> failureTokens;
}
