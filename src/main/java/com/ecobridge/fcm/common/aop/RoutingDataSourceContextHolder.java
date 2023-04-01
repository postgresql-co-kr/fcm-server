package com.ecobridge.fcm.common.aop;

public class RoutingDataSourceContextHolder {
    private static final ThreadLocal<String> dataSourceKeyHolder = new ThreadLocal<>();

    public static String getDataSourceKey() {
        return dataSourceKeyHolder.get();
    }

    public static void setDataSourceKey(String dataSourceKey) {
        dataSourceKeyHolder.set(dataSourceKey);
    }

    public static void clearDataSourceKey() {
        dataSourceKeyHolder.remove();
    }
}
