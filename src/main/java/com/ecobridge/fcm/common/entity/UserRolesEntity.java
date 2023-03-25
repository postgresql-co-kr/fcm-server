package com.ecobridge.fcm.common.entity;

import com.ecobridge.fcm.common.enums.RoleName;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author
 */
@Entity(name = "user_roles")
@Table(name = "user_roles")
@Data
public class UserRolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName roleName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}