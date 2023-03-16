package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class FcmMsgQueryRepository  {

    @PersistenceContext
    EntityManager entityManager;

    public List<FcmMsgEntity> findTargetList(String appName, Timestamp scrapTime) {

        String sql = """
            select
                *
            from
                fcm_msg
            where
                app_name = :appName
                and create_time >= :scrapTime
                and send_yn = 'N'
                """;
        TypedQuery<FcmMsgEntity> query = entityManager.createQuery(
                sql, FcmMsgEntity.class);
        query.setParameter("appName", appName);
        query.setParameter("scrapTime", scrapTime);
        return query.getResultList();
    }

    public List<FcmMsgEntity> findNextList(String appName, String msgKey) {

        String sql = """
            select
                *
            from
                fcm_msg
            where
                app_name = :appName
                and msg_seq < (
                    select
                        msg_seq
                    from
                        fcm_msg
                    where
                        msg_key = :msgKey
                )
                and send_yn = 'N'
                """;
        TypedQuery<FcmMsgEntity> query = entityManager.createQuery(
                sql, FcmMsgEntity.class);
        query.setParameter("appName", appName);
        query.setParameter("msgKey", msgKey);
        return query.getResultList();
    }
}
