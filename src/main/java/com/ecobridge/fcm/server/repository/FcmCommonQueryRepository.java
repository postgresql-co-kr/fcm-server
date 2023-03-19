package com.ecobridge.fcm.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class FcmCommonQueryRepository {
    @PersistenceContext
    EntityManager entityManager;

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
