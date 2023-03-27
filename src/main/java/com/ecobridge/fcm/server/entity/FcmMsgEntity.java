package com.ecobridge.fcm.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="fcmMsg")
@Table(name="fcm_msg")
public class FcmMsgEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String msgKey;

    @Column(insertable = false)
    private Long msgSeq;

    private String appName;

    private String deviceType;

    private String fcmToken;

    private String title;

    private String body;

    private String image;

    private String pushYn = "N";

    private LocalDateTime pushTime;

    private String successYn = "N";

    @CreationTimestamp
    private LocalDateTime createdAt;
}
