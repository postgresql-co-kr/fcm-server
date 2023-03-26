package com.ecobridge.fcm.common.entity;

import com.ecobridge.fcm.common.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_roles")
@Table(name = "user_roles")
public class UserRolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}