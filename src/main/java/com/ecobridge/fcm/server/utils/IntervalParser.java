package com.ecobridge.fcm.server.utils;

import java.util.concurrent.TimeUnit;


/**
 * Interval Parser
 * @author jyo
 */
public class IntervalParser {
    /**
     *  parse 메소드는 문자열 인자 intervalStr을 받아 시간 간격을 밀리초 단위로 반환합니다.
     *  예를 들어, parse("5s")는 5을 반환하고, parse("1m")는 60을 반환합니다.
     *  1h, 1d
     *  만약 인자로 잘못된 문자열이 주어진다면 IllegalArgumentException이 발생합니다.
     * @param intervalStr
     * @return
     * @throws IllegalArgumentException
     */
    public static long parse(String intervalStr) throws IllegalArgumentException {
        if (intervalStr == null || intervalStr.length() < 2) {
            throw new IllegalArgumentException("Invalid interval string: " + intervalStr);
        }

        long duration = Long.parseLong(intervalStr.substring(0, intervalStr.length() - 1));
        char unitChar = Character.toLowerCase(intervalStr.charAt(intervalStr.length() - 1));
        TimeUnit unit = null;

        switch (unitChar) {
            case 's':
                unit = TimeUnit.SECONDS;
                break;
            case 'm':
                unit = TimeUnit.MINUTES;
                break;
            case 'h':
                unit = TimeUnit.HOURS;
                break;
            case 'd':
                unit = TimeUnit.DAYS;
                break;
            default:
                throw new IllegalArgumentException("Invalid interval unit: " + unitChar);
        }

        return unit.toSeconds(duration);
    }
}