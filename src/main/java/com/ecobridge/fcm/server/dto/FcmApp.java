package com.ecobridge.fcm.server.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FcmApp implements Serializable {
    private String name;
    private String googleApplicationCredentials;
    private int connectionTimeout = 3;
    private int readTimeout = 5;
    private boolean dbLog = false;
    private boolean dbPush = false;
    private String dbMinusTime = "5m";
}
