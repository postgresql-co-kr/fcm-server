package com.ecobridge.fcm.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
@Table(name = "users")
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 참조 사용자 ID
    @Column(nullable = false, unique = true)
    private String username; // 사용자 아이디
    @Column(nullable = true)
    private String password;
    @Column(nullable = true)
    private String email;
    private LocalDateTime lastLoginAt; //마지막 로그인 일시
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
