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
