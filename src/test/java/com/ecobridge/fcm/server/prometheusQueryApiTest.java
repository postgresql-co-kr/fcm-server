package com.ecobridge.fcm.server;

import com.ecobridge.fcm.server.fegin.PrometheusClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class prometheusQueryApiTest {

    @Autowired
    Environment env;
    @Autowired
    private PrometheusClient prometheusClient;

    @Test
    void queryApiTest() {
        String metricName = "http_server_requests_seconds_count";
        int days = 10;

        String result = getMetricValue(metricName, days);
        log.debug("{}", result);

    }


    public String getPrometheusDatetime(LocalDateTime datetime) {

        ZoneOffset zoneOffset = ZoneOffset.UTC;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String prometheusDatetime = datetime.atOffset(zoneOffset).format(formatter);
        System.out.println(prometheusDatetime);
        return prometheusDatetime;
    }

    public String getMetricValue(String metricName, int days) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(days);
        String startTime = getPrometheusDatetime(start);
        String endTime = getPrometheusDatetime(now);

        String query = String.format(
                "increase(%s{uri=\"/api/v1/fcm/hello\"}[1d])",
                metricName,
                days
        );


        String result = prometheusClient.queryRange(query, startTime, endTime, "1d");
        // TODO: result 파싱 로직 추가

        return result;
    }

}
