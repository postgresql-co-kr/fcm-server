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
package com.ecobridge.fcm.common.config;

import com.ecobridge.fcm.common.aop.RoutingDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

//@Configuration
@RequiredArgsConstructor
public class RoutingDataSourceConfig {
    private final JdbcTemplate jdbcTemplate;
    @Bean
    public AbstractRoutingDataSource routingDataSource() {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return RoutingDataSourceContextHolder.getDataSourceKey();
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        //TODO: datasource create
 //       targetDataSources.put("default", jdbcTemplate.getDataSource());
//        targetDataSources.put("dataSource2", dataSource2);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource("default");
        return routingDataSource;
    }
}
