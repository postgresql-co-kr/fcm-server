package com.ecobridge.fcm.admin.repository;

import com.ecobridge.fcm.admin.entity.FcmSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmSetEntityRepository extends JpaRepository<FcmSetEntity, String> {

}
