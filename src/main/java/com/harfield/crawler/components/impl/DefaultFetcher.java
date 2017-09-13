package com.harfield.crawler.components.impl;

import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.domain.Task;
import com.harfield.crawler.domain.WebContent;
import com.harfield.crawler.components.Fetcher;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/31.
 */
public class DefaultFetcher implements Fetcher {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFetcher.class);


    @Override
    public WebContent fetch(Task task) throws Exception {
//        switch (task.getRequestType()) {
//            case 0:
//                return fetchByNormalConnection(task);
//            default:
//                return fetchByJsConnection(task);
//        }
        return null;
    }

    private WebContent fetchByNormalConnection(Task task) throws Exception {
//        Connection conn = HttpConnection.connect(task.getUrl());//url
//        //userAgent
//        String userAgent = task.getUserAgent();
//        if (userAgent != null && !"".equals(userAgent = userAgent.trim())) {
//            conn.userAgent(userAgent);
//        }
//        //todo
//       // ((MyHttpConnection) conn).proxy(new Proxy);
//        //referrer
//        String referrer = task.getReferrer();
//        if (referrer != null && !"".equals(referrer = referrer.trim())) {
//            conn.referrer(referrer);
//        }
//        //timeout
//        Long timeout = task.getTimeout();
//        if (timeout != null) {
//            conn.timeout(timeout.intValue());
//        }
//        //headers
//        JSONObject headers = task.getHeaders();
//        if (headers != null) {
//            for (String header : headers.keySet()) {
//                conn.header(header, headers.getString(header));
//            }
//        }
//        //cookies
//        JSONObject cookies = task.getCookies();
//        if (cookies != null) {
//            for (String cookie : cookies.keySet()) {
//                conn.cookie(cookie, cookies.getString(cookie));
//            }
//        }
//        //params
//        JSONObject params = task.getParams();
//        if (params != null) {
//            for (String paramName : params.keySet()) {
//                conn.data(paramName, params.getString(paramName));
//            }
//        }
//        conn.followRedirects(task.getFollowRedirects() != 0);//redirect
//        int method = task.getRequestMethod();//method
//        //maxTryRequest
//        int maxTryRequest = task.getMaxTryRequest();
//        for (int i = 0; i < maxTryRequest; i++) {
//            try {
//                switch (method) {
//                    case 1:
//                        return new WebContent(WebContent.Type.HTML, conn.get().outerHtml());
//                    case 2:
//                        return new WebContent(WebContent.Type.HTML, conn.post().outerHtml());
//                       // return conn.post().outerHtml();
//                    default:
//                        throw new RuntimeException("method=" + method + " is not supported");
//                }
//            } catch (IOException e) {
//            }
//        }
//        throw new RuntimeException("try request " + maxTryRequest + " times");
        return  null;
    }

    private WebContent fetchByJsConnection(Task task) throws Exception {
//        WebClient webClient = null;
//        try {
//            webClient = new WebClient(BrowserVersion.CHROME);
//            webClient.setJavaScriptTimeout(0);
//            WebClientOptions options = webClient.getOptions();
//            options.setCssEnabled(false);
//            options.setThrowExceptionOnScriptError(false);
//            //timeout
//            Long timeout = task.getTimeout();
//            if (timeout != null) {
//                options.setTimeout(timeout.intValue());
//            }
//            options.setRedirectEnabled(task.getFollowRedirects() != 0);//redirect
//            URL url = new URL(task.getUrl());//url
//            HttpMethod httpMethod = (task.getRequestMethod() == 2 ? HttpMethod.POST : HttpMethod.GET);//method
//            WebRequest webRequest = new WebRequest(url, httpMethod);
//            //headers
//            JSONObject headers = task.getHeaders();
//            if (headers != null) {
//                for (String header : headers.keySet()) {
//                    webRequest.setAdditionalHeader(header, headers.getString(header));
//                }
//            }
//            //userAgent
//            String userAgent = task.getUserAgent();
//            if (userAgent != null && !"".equals(userAgent = userAgent.trim())) {
//                webRequest.setAdditionalHeader("User-Agent", userAgent);
//            }
//            //referrer
//            String referrer = task.getReferrer();
//            if (referrer != null && !"".equals(referrer = referrer.trim())) {
//                webRequest.setAdditionalHeader("Referer", referrer);
//            }
//            //cookies
//            JSONObject cookies = task.getCookies();
//            if (cookies != null) {
//                CookieManager cookieManager = webClient.getCookieManager();
//                for (String cookie : cookies.keySet()) {
//                    cookieManager.addCookie(new Cookie(url.getHost(), cookie, cookies.getString(cookie)));
//                }
//            }
//            //params
//            JSONObject params = task.getParams();
//            if (params != null) {
//                List<NameValuePair> reqParams = new ArrayList<NameValuePair>();
//                for (String paramName : params.keySet()) {
//                    reqParams.add(new NameValuePair(paramName, params.getString(paramName)));
//                }
//                webRequest.setRequestParameters(reqParams);
//            }
//            //maxTryRequest
//            int maxTryRequest = task.getMaxTryRequest();
//            for (int i = 0; i < maxTryRequest; i++) {
//                try {
//                    HtmlPage page = webClient.getPage(webRequest);
////                    webClient.waitForBackgroundJavaScript(10000);
//                    int n1 = 0, n2 = 0, m = 0;
//                    while ((n1 = webClient.waitForBackgroundJavaScriptStartingBefore(1000)) != 0) {
//                        if (n1 == n2) {
//                            m++;
//                        } else {
//                            n2 = n1;
//                        }
//                        if (m > 3) {
//                            break;
//                        }
//                    }
//                    return new WebContent(WebContent.Type.HTML,page.asXml());
////                    return page.asXml()
//                } catch (IOException e1) {
//                } catch (FailingHttpStatusCodeException e2) {
//                }
//            }
//            throw new RuntimeException("try request " + maxTryRequest + " times");
//        } finally {
//            if (webClient != null) {
//                webClient.close();
//            }
//        }
        return null;
    }

}
