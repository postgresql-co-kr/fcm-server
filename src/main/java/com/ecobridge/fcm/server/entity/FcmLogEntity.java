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
@Entity(name="fcmLog")
@Table(name="fcm_log")
public class FcmLogEntity implements Serializable {

    @Id
    @Column(name = "log_key", length = 255, nullable = false)
    private String logKey;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_seq", nullable = false)
    private Long logSeq;

    @Column(name = "app_name", nullable = false, length = 50)
    private String appName;

    @Column(name = "device_type", nullable = true, length = 50)
    private String deviceType;

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "success_yn", nullable = false, length = 1)
    private String successYn = "N";

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
