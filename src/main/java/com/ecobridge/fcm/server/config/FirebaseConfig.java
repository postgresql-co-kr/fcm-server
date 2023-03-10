package com.ecobridge.fcm.server.config;

import com.ecobridge.fcm.server.vo.FcmApp;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Value("${spring.profiles.active}")
    private String activeProfile;
    private final FcmPropsConfig fcmPropsConfig;

    public FirebaseConfig(FcmPropsConfig fcmPropsConfig) {
        this.fcmPropsConfig = fcmPropsConfig;
    }

    @PostConstruct
    public void init() {
        log.info("Fcm-server configuration init...");
        log.info("Fcm-server active profile - {}", activeProfile);
        List<FcmApp> fcmAppsList = fcmPropsConfig.getFcmApps();
        for (FcmApp fcmApp: fcmAppsList) {
            try(FileInputStream serviceAccount = new FileInputStream(fcmApp.getGoogleApplicationCredentials())) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setConnectTimeout(fcmApp.getConnectionTimeout())
                        .setReadTimeout(fcmApp.getReadTimeout())
                        .build();
                FirebaseApp.initializeApp(options, fcmApp.getName());
                log.info("fcm {} app init completed ", fcmApp.getName());
            } catch (IOException e) {
                log.error("Failed to configure FCM server. Please check the json file location of Google credentials!");
            }
        }
    }
}
