package com.ecobridge.fcm.common.repository;

import com.ecobridge.fcm.common.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersEntityRepository extends JpaRepository<UsersEntity, Long> {
    boolean existsByUsername(String username);
    Optional<UsersEntity> findByUsername(String username);




}

