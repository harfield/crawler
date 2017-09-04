package com.harfield.crawler.domain;

import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by harfield on 2017/8/25.
 */
public class WebContent {
    private static final Logger LOG = LoggerFactory.getLogger(WebContent.class);

    public enum Type {
        IMAGE,
        FILE,
        HTML,
        JSON
    }

    private Type type;
    private Object body;
    private Map<String, Object> responseHeader;

    private int bufferSize = 512 * 1024;
    public static int MAX_SUPPORTED_LENGTH = 10 * 1024 * 1024;

    public WebContent(Type type, InputStream stream) {
        switch (type) {
            case FILE:
            case IMAGE:
                try {
                    ByteBuffer byteBuffer = readToByteBuffer(stream, MAX_SUPPORTED_LENGTH);
                    body = byteBuffer.array();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
                break;
            default:
                throw new RuntimeException("WebContent not support steam except file & image");
        }
    }

    public WebContent(Type type, String content) {
        this.type = type;
        this.body = content;
    }


    private ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        Validate.isTrue(maxSize >= 0, "maxSize must be 0 (unlimited) or larger");
        final boolean capped = maxSize > 0;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
        int read;
        int remaining = maxSize;

        while (true) {
            read = inStream.read(buffer);
            if (read == -1) break;
            if (capped) {
                if (read > remaining) {
                    outStream.write(buffer, 0, remaining);
                    break;
                }
                remaining -= read;
            }
            outStream.write(buffer, 0, read);
        }
        return ByteBuffer.wrap(outStream.toByteArray());
    }


}
