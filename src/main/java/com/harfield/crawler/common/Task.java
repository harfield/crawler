package com.harfield.crawler.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Task {
    public void setExtraDataByJsonStr(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr = jsonStr.trim())) {
            return;
        }
        this.extraJsonData = JSON.parseObject(jsonStr);
    }

    public void setHeadersByJsonStr(String jsonHeaders) {
        if (null == jsonHeaders || "".equals(jsonHeaders = jsonHeaders.trim())) {
            return;
        }
        this.headers = JSON.parseObject(jsonHeaders);
    }

    public void setCookiesByJsonStr(String jsonCookies) {
        if (null == jsonCookies || "".equals(jsonCookies = jsonCookies.trim())) {
            return;
        }
        this.cookies = JSON.parseObject(jsonCookies);
    }

    public void setParamsByJsonStr(String jsonParams) {
        if (null == jsonParams || "".equals(jsonParams = jsonParams.trim())) {
            return;
        }
        this.params = JSON.parseObject(jsonParams);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getRequestMethod() {
        if (requestMethod != null) {
            return requestMethod;
        }
        if (crawlJob != null) {
            return crawlJob.getRequestMethod();
        }
        throw new RuntimeException("requestMethod is not set");
    }

    public void setRequestMethod(Integer requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Integer getRequestType() {
        if (requestType != null) {
            return requestType;
        }
        if (crawlJob != null) {
            return crawlJob.getRequestType();
        }
        throw new RuntimeException("requestType is not set");
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Integer getMaxTryRequest() {
        if (maxTryRequest != null) {
            return maxTryRequest;
        }
        if (crawlJob != null) {
            return crawlJob.getMaxTryRequest();
        }
        throw new RuntimeException("maxTryRequest is not set");
    }

    public void setMaxTryRequest(Integer maxTryRequest) {
        this.maxTryRequest = maxTryRequest;
    }

    public Integer getFollowRedirects() {
        if (followRedirects != null) {
            return followRedirects;
        }
        if (crawlJob != null) {
            return crawlJob.getFollowRedirects();
        }
        throw new RuntimeException("followRedirects is not set");
    }

    public void setFollowRedirects(Integer followRedirects) {
        this.followRedirects = followRedirects;
    }

    public String getUserAgent() {
        if (userAgent != null) {
            return userAgent;
        }
        if (crawlJob != null) {
            return crawlJob.getUserAgent();
        }
        return null;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getTimeout() {
        if (timeout != null) {
            return timeout;
        }
        if (crawlJob != null) {
            return crawlJob.getTimeout();
        }
        return null;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Integer getUseProxy() {
        if (useProxy != null) {
            return useProxy;
        }
        if (crawlJob != null) {
            return crawlJob.getUseProxy();
        }
        throw new RuntimeException("useProxy is not set");
    }

    public void setUseProxy(Integer useProxy) {
        this.useProxy = useProxy;
    }

    public JSONObject getHeaders() {
        return headers;
    }

    public void setHeaders(JSONObject headers) {
        this.headers = headers;
    }

    public JSONObject getCookies() {
        return cookies;
    }

    public void setCookies(JSONObject cookies) {
        this.cookies = cookies;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getCrawlUpdateTime() {
        return crawlUpdateTime;
    }

    public void setCrawlUpdateTime(Date crawlUpdateTime) {
        this.crawlUpdateTime = crawlUpdateTime;
    }

    public Date getSendMqTime() {
        return sendMqTime;
    }

    public void setSendMqTime(Date sendMqTime) {
        this.sendMqTime = sendMqTime;
    }

    public Date getLastCrawlTime() {
        return lastCrawlTime;
    }

    public void setLastCrawlTime(Date lastCrawlTime) {
        this.lastCrawlTime = lastCrawlTime;
    }

    public Date getNextCrawlTime() {
        return nextCrawlTime;
    }

    public void setNextCrawlTime(Date nextCrawlTime) {
        this.nextCrawlTime = nextCrawlTime;
    }

    public int getCrawlTimes() {
        return crawlTimes;
    }

    public void setCrawlTimes(int crawlTimes) {
        this.crawlTimes = crawlTimes;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public CrawlJob getCrawlJob() {
        return crawlJob;
    }

    public void setCrawlJob(CrawlJob crawlJob) {
        this.crawlJob = crawlJob;
    }

    public JSONObject getExtraJsonData() {
        return extraJsonData;
    }

    public void setExtraJsonData(JSONObject extraJsonData) {
        this.extraJsonData = extraJsonData;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private Long id;
    private String url;//完整的url
    private String referrer;
    private Integer requestMethod;//0 无,1 get,2 post
    private Integer requestType;//0 普通,1 执行js
    private Integer maxTryRequest;//如果请求失败
    private Integer followRedirects = 1;//0 false,1 true
    private String userAgent;
    private Long timeout;//0 不限制
    private Integer useProxy;//0 false,1 true
    private JSONObject headers;
    private JSONObject cookies;
    private JSONObject params;
    private Date insertTime;
    private Date crawlUpdateTime;
    private Date sendMqTime;
    private Date lastCrawlTime;
    private Date nextCrawlTime;
    private int crawlTimes;//爬虫次数
    private int stat;//状态:0 新建,1 待爬,2 在爬,3 失败,4 成功
    private String remark;
    private JSONObject extraJsonData;
    private long version;
    private CrawlJob crawlJob;

    public static void main(String[] args) throws Exception {
        /*Task crawlAttempt = new Task();
        crawlAttempt.setUrl("test");
        PropertyDescriptor pd = new PropertyDescriptor("url1", crawlAttempt.getClass());
        Method readMethod = pd.getReadMethod();
        System.out.println(readMethod.invoke(crawlAttempt));*/

        /*Pattern p = Pattern.compile("(?<word>[a-zA-Z]+)(?<num>\\d+)");
        String s = "abc test123 456";
        Matcher m = p.matcher(s);
        if (m.find()) {
            System.out.println(m.group("word"));
            System.out.println(m.group("num"));
        }*/
    }
}