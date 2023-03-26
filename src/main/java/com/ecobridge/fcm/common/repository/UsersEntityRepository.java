package com.ecobridge.fcm.common.repository;

import com.ecobridge.fcm.common.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersEntityRepository extends JpaRepository<UsersEntity, Long> {
    boolean existsByUsername(String username);
    Optional<UsersEntity> findByUsername(String username);




}

