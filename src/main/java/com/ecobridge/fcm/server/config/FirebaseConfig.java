/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.server.config;

import com.ecobridge.fcm.common.config.FcmPropsConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FcmPropsConfig fcmPropsConfig;

    @PostConstruct
    public void init() {
        log.info("Fcm-server configuration init...");

        List<FcmPropsConfig.FcmApp> fcmAppsList = fcmPropsConfig.getApps();
        for (FcmPropsConfig.FcmApp fcmApp: fcmAppsList) {
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
