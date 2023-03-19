package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.server.entity.FcmLogEntity;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FcmLogQueryRepository extends FcmCommonQueryRepository {
    @Timed(value = "fcm.log.query.repository.sql.timed")
    public void batchUpdateLog(List<FcmLogEntity> fcmLogEntities) {
        super.batchUpdate(fcmLogEntities);
    }
}
