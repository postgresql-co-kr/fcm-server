package com.ecobridge.fcm.server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {
    private FcmPropsConfig fcmPropsConfig;

    public FirebaseConfig(FcmPropsConfig fcmPropsConfig) {
        this.fcmPropsConfig = fcmPropsConfig;
    }

    @PostConstruct
    public void init() {
        log.info("Fcm-server configuration init...");
        try(FileInputStream serviceAccount = new FileInputStream(fcmPropsConfig.getGoogleApplicationCredentials())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setConnectTimeout(fcmPropsConfig.getConnectionTimeout())
                    .setReadTimeout(fcmPropsConfig.getReadTimeout())
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("Failed to configure FCM server. Please check the json file location of Google credentials!");
        }
    }
}
