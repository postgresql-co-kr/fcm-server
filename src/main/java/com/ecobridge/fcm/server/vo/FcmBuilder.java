package com.ecobridge.fcm.server.vo;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FcmBuilder {
    // Notification
    private Notification.Builder notificationBuilder;
    // AOS
    private AndroidConfig.Builder aosBuilder;
    // APN
    private ApnsConfig.Builder iosBuilder;
    // Web
    private WebpushConfig.Builder webBuilder;
}
