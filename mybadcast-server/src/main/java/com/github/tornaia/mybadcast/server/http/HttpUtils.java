package com.github.tornaia.mybadcast.server.http;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class HttpUtils {

    private static final String X_FORWARDED_FOR_HEADER_NAME = "X-Forwarded-For";

    private HttpUtils() {
    }

    public static void consumeRequestInputStream(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();

            if (inputStream != null) {
                byte[] buf = new byte[16384];

                while (true) {
                    int readBytes = inputStream.read(buf);
                    if (readBytes == -1) {
                        break;
                    }
                }
                inputStream.close();
            }
        } catch (IOException ignore) {
        }
    }

    public static String getIpAddress(HttpServletRequest httpServletRequest) {
        String xForwardedFor = httpServletRequest.getHeader(X_FORWARDED_FOR_HEADER_NAME);
        boolean hasXForwardedFor = xForwardedFor != null;
        if (hasXForwardedFor) {
            String[] ipAddresses = xForwardedFor.split(", ");
            return ipAddresses[0];
        } else {
            return httpServletRequest.getRemoteAddr();
        }
    }
}
