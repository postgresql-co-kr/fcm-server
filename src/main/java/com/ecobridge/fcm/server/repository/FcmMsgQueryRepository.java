package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class FcmMsgQueryRepository  {

    @PersistenceContext
    EntityManager entityManager;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
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

    public void batchUpdate(List<FcmMsgEntity> fcmMsgEntities) {
        int batchSize = 100;
        for (int i = 0; i < fcmMsgEntities.size(); i++) {
            FcmMsgEntity fcmMsgEntity = fcmMsgEntities.get(i);
            entityManager.persist(fcmMsgEntity);
            if ((i + 1) % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        if (fcmMsgEntities.size() % batchSize != 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
