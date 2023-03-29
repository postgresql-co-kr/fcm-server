package com.ecobridge.fcm.common.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateTimeUtil {
    public static String ofPrometheusDatetime(LocalDateTime datetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(datetime, ZoneOffset.systemDefault());
        String prometheusDatetime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        log.debug("prometheusDatetime: {}", prometheusDatetime);
        return prometheusDatetime;
    }

    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.systemDefault());
    }
}
