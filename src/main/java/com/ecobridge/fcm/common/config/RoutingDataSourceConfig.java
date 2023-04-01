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
