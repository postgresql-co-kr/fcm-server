package com.ecobridge.fcm.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    /**
     * Throwable 객체의 stack trace를 문자열 형태로 반환합니다.
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Throwable 객체의 root cause를 반환합니다.
     * @param throwable
     * @return
     */
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause == null) {
            return throwable;
        }
        return getRootCause(cause);
    }

    /**
     * Throwable 객체가 지정된 cause 클래스들 중 하나에 의해 발생한 것인지 확인합니다.
     * @param throwable
     * @param causeClasses
     * @return
     */
    public static boolean isCausedBy(Throwable throwable, Class<? extends Throwable>... causeClasses) {
        Throwable cause = throwable.getCause();
        if (cause == null) {
            return false;
        }
        for (Class<? extends Throwable> causeClass : causeClasses) {
            if (causeClass.isInstance(cause)) {
                return true;
            }
        }
        return isCausedBy(cause, causeClasses);
    }
}







