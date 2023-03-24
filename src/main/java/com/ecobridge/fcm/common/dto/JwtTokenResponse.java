package com.ecobridge.fcm.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtTokenResponse implements Serializable {
    private String token;
    private String refreshToken;

    public JwtTokenResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
