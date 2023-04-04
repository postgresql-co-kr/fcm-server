/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.server.repository;

import com.ecobridge.fcm.common.repository.FcmCommonQueryRepository;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FcmMsgQueryRepository extends FcmCommonQueryRepository {

    @Timed(value = "fcm.msg.query.repository.sql.timed")
    public List<FcmMsgEntity> findTargetList(String appName, LocalDateTime scrapeTime) {
        String jpql = """
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
                jpql, FcmMsgEntity.class);
        query.setParameter("appName", appName);
        query.setParameter("scrapeTime", scrapeTime);
        query.setHint("jakarta.persistence.lock.timeout", 5000);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.getResultList();
    }

    @Timed(value = "fcm.msg.query.repository.sql.timed")
    public List<FcmMsgEntity> findNextList(String appName, String msgKey) {
        String jpql = """
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
                        AND s.appName = :appName
                )
                AND m.successYn = 'N'
            ORDER BY
                m.msgSeq desc
            LIMIT 500
                """;
        TypedQuery<FcmMsgEntity> query = entityManager.createQuery(
                jpql, FcmMsgEntity.class);
        query.setParameter("appName", appName);
        query.setParameter("msgKey", msgKey);
        query.setHint("jakarta.persistence.lock.timeout", 5000);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.getResultList();
    }

    @Timed(value = "fcm.msg.query.repository.sql.timed")
    public void batchUpdateMsg(List<FcmMsgEntity> fcmMsgEntities) {
       super.batchUpdate(fcmMsgEntities);
    }
}
