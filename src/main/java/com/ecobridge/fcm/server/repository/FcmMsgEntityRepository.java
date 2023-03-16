package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmMsgEntityRepository extends JpaRepository<FcmMsgEntity, String> {

}
