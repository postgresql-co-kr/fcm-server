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
