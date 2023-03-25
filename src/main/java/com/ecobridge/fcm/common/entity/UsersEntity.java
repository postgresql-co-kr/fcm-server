package com.ecobridge.fcm.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "users")
@Table(name = "users")
@Data
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
    private Timestamp lastLoginAt; //마지막 로그인 일시
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
