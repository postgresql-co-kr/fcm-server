package com.ecobridge.fcm.common.repository;

import com.ecobridge.fcm.common.entity.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRolesEntityRepository extends JpaRepository<UserRolesEntity, Long> {
    Optional<List<UserRolesEntity>> findByUserId(Long userId);

}