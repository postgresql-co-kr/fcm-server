package com.ecobridge.fcm.server.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class FailureToken implements Serializable {
    private String token;
    private String errorCode;
    private String message;
}
