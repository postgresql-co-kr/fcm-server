package com.ecobridge.fcm.server.util;

public class EnumFinder {
    public static <E extends Enum<E>> E findEnum(String enumString, Class<E> enumClass) {
        try {
            return Enum.valueOf(enumClass, enumString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
