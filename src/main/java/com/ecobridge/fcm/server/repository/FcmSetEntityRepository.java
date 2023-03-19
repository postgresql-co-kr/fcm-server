package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.server.entity.FcmSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmSetEntityRepository extends JpaRepository<FcmSetEntity, String> {

}
