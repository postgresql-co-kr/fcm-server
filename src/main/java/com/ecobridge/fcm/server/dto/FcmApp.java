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
public class FcmApp implements Serializable {
    private String name;
    private String googleApplicationCredentials;
    private int connectionTimeout = 3;
    private int readTimeout = 5;
    private boolean dbLog = false;
    private boolean dbPush = false;
    private String dbMinusTime = "5m";
}
