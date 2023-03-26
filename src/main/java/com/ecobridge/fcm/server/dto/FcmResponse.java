package com.ecobridge.fcm.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
