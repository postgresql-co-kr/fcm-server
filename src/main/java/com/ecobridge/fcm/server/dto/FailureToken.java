package com.ecobridge.fcm.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureToken implements Serializable {
    private String token;
    private String errorCode;
    private String message;
}
