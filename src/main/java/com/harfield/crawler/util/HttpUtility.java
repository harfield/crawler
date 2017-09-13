package com.harfield.crawler.util;

import com.harfield.crawler.components.output.httpoutput.HttpRuntimeException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/1/7.
 */
public class HttpUtility {
    private static CloseableHttpClient httpClient = null;
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtility.class);
    private static Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(100);
        // Increase max connections for localhost:80 to 50
        RequestConfig requestConfig = RequestConfig.custom().build();
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();

    }


    public static Material getMaterial(String imageAddr) {
        CloseableHttpResponse httpResponse = null;
        try {
            HttpGet httpGet = new HttpGet(imageAddr);
            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new HttpRuntimeException("HttpUtility.getMaterial response status not 200 url:" + imageAddr);
            }
            HttpEntity entity = httpResponse.getEntity();
            Header contentTypeHeader = entity.getContentType();
            if (!Material.acceptedType(contentTypeHeader)) {
                throw new HttpRuntimeException("HttpUtility.getMaterial Response Entity type is not supported : " + contentTypeHeader.getValue());
            }
            Material material = new Material(httpResponse.getEntity());
            return material;
        } catch (IOException e) {
            throw new HttpRuntimeException(e.getMessage());
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String getResponseBody(String url) {
        CloseableHttpResponse httpResponse = null;
        BufferedReader reader = null;
        try {
            httpResponse = httpClient.execute(new HttpGet(url));
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new HttpRuntimeException("HttpUtility.getResponseBody response status not 200 url:" + url);
            }
            HttpEntity entity = httpResponse.getEntity();

            String encoding = getContentEncoding(entity);


            InputStream content = entity.getContent();
            reader = new BufferedReader(new InputStreamReader(content, encoding));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new HttpRuntimeException("HttpUtility.getResponseBody failed:\n\t" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static String getContentEncoding(HttpEntity entity) {
        String encoding = null;
        Header contentEncoding = entity.getContentEncoding();
        if (contentEncoding != null) {
            encoding = contentEncoding.getValue();
        }

        Header contentType = entity.getContentType();
        if ((encoding == null || !Charset.isSupported(encoding)) && contentType != null) {
            Matcher matcher = charsetPattern.matcher(contentType.getValue());
            if (matcher.find()) {
                String charset = matcher.group(1).trim().toLowerCase();
                encoding = charset.replace("charset=", "");
            }
        }
        if (encoding == null || !Charset.isSupported(encoding)) {
            encoding = "utf-8";
        }
        return encoding;
    }


    public static String postMutiPart(String host, Map<String, String> params, Material material, String paramName) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        if (material != null) {
            entityBuilder.addBinaryBody(paramName, material.getFileBody(), material.getContentType(), material.getName());
        }
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                entityBuilder.addPart(e.getKey(), new StringBody(e.getValue(), ContentType.APPLICATION_JSON));
            }
        }

        HttpPost post = new HttpPost(host);
        post.setEntity(entityBuilder.build());
        CloseableHttpResponse response = null;
        BufferedReader reader = null;
        try {
            response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new HttpRuntimeException("HttpUtility.postMutiPart remote server return status not 200 , code:" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String contentEncoding = getContentEncoding(entity);
            InputStream content = entity.getContent();
            reader = new BufferedReader(new InputStreamReader(content, contentEncoding));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new HttpRuntimeException("HttpUtility.postMutiPart:" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (response != null)
                try {
                    response.close();
                } catch (IOException e) {
                }
        }

    }




}

class Material {
    private static final Logger LOG = LoggerFactory.getLogger(Material.class);
    String name = null;
    byte[] fileBody;
    ContentType contentType;
    static Map<String, String> mime = new HashMap<String, String>();
    public static Set<String> acceptedContentType = new HashSet<String>();

    static {

        mime.put("image/svg+xml", "svg");
        mime.put("image/svg+xml", "svgz");
        mime.put("image/gif", "gif");
        mime.put("image/jpeg", "jpg");
        mime.put("image/png", "png");
//        mime.put("image/tiff", "tif");
//        mime.put("image/ico", "ico");
        mime.put("image/bmp", "bmp");


    }

    static {
        acceptedContentType.addAll(Arrays.asList("image", "flash", "text-icon", "text-url", "video"));
    }

    public static boolean acceptedType(Header contentType) {
        String[] split = contentType.getValue().split("/");
        if (split.length < 2) return false;
        else {
            return acceptedContentType.contains(split[0]);
        }
    }

    public Material() {

    }

    public Material(HttpEntity entity) throws IOException {
        fileBody = EntityUtils.toByteArray(entity);
        Header contentType = entity.getContentType();
        if (contentType != null) {
            this.contentType = ContentType.create(contentType.getValue());
        }
    }

    public Material(InputStream inputStream, long length, Header header) {
        int len = (int) length;
        fileBody = new byte[len];
      /*  try {
            int read = inputStream.read(fileBody, 0, len);
            System.out.println(read);

        } catch (IOException e) {
            LOG.error("builder material failed !{}", e.getMessage());
        }*/

        byte[] bytes = new byte[1024];
        int l = -1;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((l = inputStream.read(bytes, 0, bytes.length)) != -1) {
                os.write(bytes, 0, l);
            }
            fileBody = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentType = ContentType.create(header.getValue());
    }

    public String getName() {
        if (name == null) {
            String suffix = mime.get(contentType.getMimeType());
            if (suffix == null)
                throw new HttpRuntimeException("mime type not supported :" + contentType.getMimeType());
            name = UUID.randomUUID() + "." + suffix;
//            name = contentType.getMimeType().replace("/", ".");
        }

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}

