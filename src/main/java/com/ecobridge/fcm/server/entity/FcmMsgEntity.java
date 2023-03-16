package com.ecobridge.fcm.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name="fcm_msg")
@Data
public class FcmMsgEntity {
    /**
    msg_key VARCHAR(255) NOT NULL PRIMARY KEY,
    msg_seq SERIAL,
    app_name VARCHAR(50) NOT NULL,
    fcm_token  VARCHAR(255) NOT NULL,
    title  VARCHAR(250) NOT NULL,
    body  VARCHAR(4000) NOT NULL,
    image VARCHAR(2048) NULL,
    send_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
    send_time TIMESTAMP NULL,
    success_yn VARCHAR(1) DEFAULT 'N' NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
    */

    @Id
    @Column(name = "msg_key", length = 255, nullable = false)
    private String msgKey;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_seq", nullable = false)
    private Long msgSeq;

    @Column(name = "app_name", nullable = false, length = 50)
    private String appName;

    @Column(name = "fcm_token", nullable = false, length = 255)
    private String fcmToken;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "body", nullable = false, length = 4000)
    private String body;

    @Column(name = "image", nullable = true, length = 2048)
    private String image;

    @Column(name = "send_yn", nullable = false, length = 1)
    private String sendYn = "N";

    @Column(name = "send_time", nullable = true)
    private Timestamp sendTime;

    @Column(name = "success_yn", nullable = false, length = 1)
    private String successYn = "N";

    @Column(name = "created_time", nullable = false)
    private Timestamp createdTime;
}
