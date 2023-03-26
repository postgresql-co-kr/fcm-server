package com.ecobridge.fcm.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="fcmMsg")
@Table(name="fcm_msg")
public class FcmMsgEntity implements Serializable {
    @Id
    @Column(name = "msg_key", length = 255, nullable = false)
    private String msgKey;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_seq", nullable = false)
    private Long msgSeq;

    @Column(name = "app_name", nullable = false, length = 50)
    private String appName;

    @Column(name = "device_type", nullable = true, length = 50)
    private String deviceType;

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "body", nullable = false, length = 4000)
    private String body;

    @Column(name = "image", nullable = true, length = 2048)
    private String image;

    @Column(name = "push_yn", nullable = false, length = 1)
    private String pushYn = "N";

    @Column(name = "push_time", nullable = true)
    private Timestamp pushTime;

    @Column(name = "success_yn", nullable = false, length = 1)
    private String successYn = "N";

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
