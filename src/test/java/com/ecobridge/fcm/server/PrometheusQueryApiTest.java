package com.ecobridge.fcm.server;

import com.ecobridge.fcm.common.dto.PrometheusQueryRangeResponse;
import com.ecobridge.fcm.common.util.DateTimeUtil;
import com.ecobridge.fcm.common.util.PrometheusQueryRangeResponseParser;
import com.ecobridge.fcm.server.fegin.PrometheusClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class PrometheusQueryApiTest {

    @Autowired
    Environment env;
    @Autowired
    private PrometheusClient prometheusClient;

    @Test
    void queryApiTest() throws IOException {
        String metricName = "http_server_requests_seconds_count";
        int days = 10;

        String result = getMetricValue(metricName, days);
        PrometheusQueryRangeResponseParser parser = new PrometheusQueryRangeResponseParser();
        PrometheusQueryRangeResponse response = parser.parse(result);
        log.debug("{}", response);
        long timestamp = Long.valueOf(response.getData().getResults().get(0).getValues().get(0).get(0).toString());
        log.debug(DateTimeUtil.fromTimestamp(timestamp * 1000).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }


    public String getMetricValue(String metricName, int days) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(days);
        String startTime = DateTimeUtil.ofPrometheusDatetime(start);
        String endTime = DateTimeUtil.ofPrometheusDatetime(now);

        String query = String.format(
                "increase(%s{uri=\"/api/v1/fcm/hello\"}[1d])",
                metricName
        );


        String result = prometheusClient.queryRange(query, startTime, endTime, "1d");
        // TODO: result 파싱 로직 추가

        return result;
    }

}
