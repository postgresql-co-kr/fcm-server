package com.ecobridge.fcm.server.config;

import com.ecobridge.fcm.server.vo.FcmApp;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties
@Data
public class FcmPropsConfig {
    private List<FcmApp> fcmApps;
}
