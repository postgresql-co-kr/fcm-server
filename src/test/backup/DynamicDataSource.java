package com.osstem.mcs.core.config;

import com.osstem.mcs.core.context.DataSourceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.List;
import java.util.Random;

@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final List<String> SLAVE_DATASOURCES = List.of("master", "slave1", "slave2");
    private final Random random = new Random();

    @Override
    protected Object determineCurrentLookupKey() {
        String currentDataSource = DataSourceContext.getCurrentDataSource();
        if ("master".equalsIgnoreCase(currentDataSource)) {
            log.info("Selected DataSource: master");
            return "master";
        } else if ("slave".equalsIgnoreCase(currentDataSource)) {
            // 일반적인 슬레이브 요청이 있을 경우, 랜덤하게 슬레이브를 선택
            String selectedSlave = SLAVE_DATASOURCES.get(random.nextInt(SLAVE_DATASOURCES.size()));
            log.info("Selected DataSource: {}", selectedSlave); // 선택된 슬레이브 로그 출력
            return selectedSlave;
        }

        log.debug("Selected DataSource: master");
        return "master";
    }


}
