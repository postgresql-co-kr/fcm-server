package com.ecobridge.fcm.server;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UTCDateTest {

    @Test
    public void dateTest() {


        LocalDateTime localStart = LocalDateTime.of(2023, 3, 1, 0, 0, 0);
        LocalDateTime localEnd = LocalDateTime.of(2023, 3, 31, 23, 59, 59);

        ZonedDateTime zonedStart = ZonedDateTime.of(localStart, ZoneId.of("Asia/Seoul"));
        ZonedDateTime zonedEnd = ZonedDateTime.of(localEnd, ZoneId.of("Asia/Seoul"));

        String startTimeUtc = zonedStart.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        String endTimeUtc = zonedEnd.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        System.out.println("startTimeUtc: " + startTimeUtc);
        System.out.println("endTimeUtc: " + endTimeUtc);


    }
}
