package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.server.entity.FcmLogEntity;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class FcmLogQueryRepository extends FcmCommonQueryRepository {
    @Timed(value = "fcm.log.query.repository.sql.timed")
    public void batchUpdateLog(List<FcmLogEntity> fcmLogEntities) {
        super.batchUpdate(fcmLogEntities);
    }
}
