package com.ecobridge.fcm.server.vo;

import lombok.Data;

@Data
public class FcmApp {
    private String name;
    private String googleApplicationCredentials;
    private int connectionTimeout = 3;
    private int readTimeout = 5;
    private boolean dbLog = false;
    private boolean dbPush = false;
    private String dbMinusTime = "5m";
}
