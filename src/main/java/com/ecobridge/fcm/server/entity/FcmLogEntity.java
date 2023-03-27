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
@Entity(name="fcmLog")
@Table(name="fcm_log")
public class FcmLogEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String logKey;

    @Column(insertable = false)
    private Long logSeq;

    private String appName;

    private String deviceType;

    private String fcmToken;

    private String successYn = "N";

    @CreationTimestamp
    private LocalDateTime createdAt;
}
