package com.ecobridge.fcm.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtRefreshTokenRequest implements Serializable {

    private String refreshToken;

    public JwtRefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
