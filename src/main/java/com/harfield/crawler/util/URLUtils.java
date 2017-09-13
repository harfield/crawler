package com.harfield.crawler.util;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yanghao on 8/4/16.
 */
public final class URLUtils {
    public static String normalize(String urlStr) {
        if (null == urlStr || "".equals(urlStr = urlStr.trim())) {
            return null;
        }
        if (!urlStr.matches("^\\w+://.*")) {
            urlStr = "http://" + urlStr;
        }
        URL url = null;
        try {
            url = new URL(urlStr);
            String protocol = url.getProtocol();
            int port = url.getPort();
            String host = url.getHost().toLowerCase();
            String path = url.getPath();
            String queryString = url.getQuery();

            String normalizedQueryStr = null;
            StringBuilder builder = new StringBuilder();

            if (queryString != null && !"".equals(queryString = queryString.trim())/*end with ?*/) {
                List<String> queryStringParts = Arrays.asList(queryString.split("&"));
                Collections.sort(queryStringParts);

                for (String param : queryStringParts) {
                    builder.append("&").append(param);
                }
                if (builder.length() > 0) {
                    normalizedQueryStr = builder.toString().substring(1);
                }


            }
            String ref = url.getRef();
            builder.setLength(0);
            builder.append(protocol).append("://").append(host);
            if (port != -1) {
                builder.append(":").append(port);
            }
            builder.append(path);
            if (normalizedQueryStr != null) {
                builder.append("?").append(normalizedQueryStr);
            }
            if (ref != null) {
                builder.append("#");
                builder.append(ref);
            }
            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
