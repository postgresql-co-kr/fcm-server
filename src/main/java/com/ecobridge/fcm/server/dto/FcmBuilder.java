package com.ecobridge.fcm.server.dto;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class FcmBuilder implements Serializable {
    // Notification
    private Notification.Builder notificationBuilder;
    // AOS
    private AndroidConfig.Builder aosBuilder;
    // APN
    private ApnsConfig.Builder iosBuilder;
    // Web
    private WebpushConfig.Builder webBuilder;
}
