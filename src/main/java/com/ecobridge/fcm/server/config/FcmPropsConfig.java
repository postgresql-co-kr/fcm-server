package com.ecobridge.fcm.server.config;

import com.ecobridge.fcm.server.vo.FcmApp;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
@Data
public class FcmPropsConfig {
    private List<FcmApp> fcmApps;
}
