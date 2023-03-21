package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.common.repository.FcmCommonQueryRepository;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class FcmMsgQueryRepository extends FcmCommonQueryRepository {

    @Timed(value = "fcm.msg.query.repository.sql.timed")
    public List<FcmMsgEntity> findTargetList(String appName, Timestamp scrapeTime) {
        String sql = """
                SELECT 
                     m
                FROM
                    fcmMsg m
                WHERE
                    m.appName = :appName
                    AND m.createdAt >= :scrapeTime
                    AND m.pushYn = 'N'
                ORDER BY
                    m.msgSeq desc
                LIMIT 500             
                    """;
        TypedQuery<FcmMsgEntity> query = entityManager.createQuery(
                sql, FcmMsgEntity.class);
        query.setParameter("appName", appName);
        query.setParameter("scrapeTime", scrapeTime);
        query.setHint("jakarta.persistence.lock.timeout", 5000);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.getResultList();
    }

    @Timed(value = "fcm.msg.query.repository.sql.timed")
    public List<FcmMsgEntity> findNextList(String appName, String msgKey) {
        String sql = """
            SELECT
                m        
            FROM
                fcmMsg m
            WHERE
                m.appName = :appName
                AND m.msgSeq < (
                    SELECT
                        s.msgSeq
                    FROM
                        fcmMsg s
                    WHERE
                        s.msgKey = :msgKey
                )
                AND m.sendYn = 'N'
            ORDER BY
                m.msgSeq desc
            LIMIT 500
                """;
        TypedQuery<FcmMsgEntity> query = entityManager.createQuery(
                sql, FcmMsgEntity.class);
        query.setParameter("appName", appName);
        query.setParameter("msgKey", msgKey);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.getResultList();
    }

    @Timed(value = "fcm.msg.query.repository.sql.timed")
    public void batchUpdateMsg(List<FcmMsgEntity> fcmMsgEntities) {
       super.batchUpdate(fcmMsgEntities);
    }
}
