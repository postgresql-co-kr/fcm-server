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
package com.ecobridge.fcm.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class FcmCommonQueryRepository {
    @PersistenceContext
    protected EntityManager entityManager;

    protected <T> void batchUpdate(List<T> entities) {
        int batchSize = 100;
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            entityManager.persist(entity);
            if ((i + 1) % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        if (entities.size() % batchSize != 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
