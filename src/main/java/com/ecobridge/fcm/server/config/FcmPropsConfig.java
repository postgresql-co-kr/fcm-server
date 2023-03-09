package com.ecobridge.fcm.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fcm")
@Data
public class FcmPropsConfig {
    private String googleApplicationCredentials;
    private int connectionTimeout = 3;
    private int readTimeout = 5;
}
