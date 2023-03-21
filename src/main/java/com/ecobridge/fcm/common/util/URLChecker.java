package com.ecobridge.fcm.common.util;

import java.net.URL;

public class URLChecker {
    public static boolean isHTTPS(String urlString) {
        if (urlString == null || urlString.length() <= 8) {
            return false;
        }
        try {
            URL url = new URL(urlString);
            return url.getProtocol().equals("https");
        } catch (Exception e) {
            return false;
        }
    }
}

