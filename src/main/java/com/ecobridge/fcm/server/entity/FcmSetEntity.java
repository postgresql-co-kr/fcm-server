package com.ecobridge.fcm.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name="fcmSet")
@Table(name="fcm_set")
@Data
public class FcmSetEntity {

    @Id
    @Column(name = "app_name", nullable = false, length = 50)
    private String appName;


    @Column(name = "key_path", nullable = false, length = 4000)
    private String keyPath;

    @Column(name = "connection_timeout", nullable = false)
    private Integer connectionTimeout = 3;
    @Column(name = "read_timeout", nullable = false)
    private Integer readTimeout = 3;
    @Column(name = "db_log_yn", nullable = false, length = 1)
    private String dbLogYn = "N";
    @Column(name = "db_push_yn", nullable = false, length = 1)
    private String dbPushYn = "N";
    @Column(name = "db_minus_time", nullable = false, length = 10)
    private String dbMinusTime = "5m";
    @Column(name = "update_id", nullable = false, length = 255)
    private String updateId;
    @Column(name = "update_at", nullable = false)
    private Timestamp updateAt;
    @Column(name = "created_id", nullable = false, length = 255)
    private String createdId;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
