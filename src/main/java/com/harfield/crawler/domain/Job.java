package com.harfield.crawler.domain;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Job {


    private long id;
    private String name;
    private String desc;
    private Integer requestMethod;
    private String requestParams;
    private Integer responseType;
    private Integer maxTry;
    private Integer maxFollowRedirects;
    private String useragent;
    private Integer timeout;
    private Integer useProxy;
    private String headers;
    private String cookies;
    private int rate;
    private int burst;
    private int status;
    private long duration;
    private String outputClass;
    private Date insertTime;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(Integer requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public Integer getResponseType() {
        return responseType;
    }

    public void setResponseType(Integer responseType) {
        this.responseType = responseType;
    }

    public Integer getMaxTry() {
        return maxTry;
    }

    public void setMaxTry(Integer maxTry) {
        this.maxTry = maxTry;
    }

    public Integer getMaxFollowRedirects() {
        return maxFollowRedirects;
    }

    public void setMaxFollowRedirects(Integer maxFollowRedirects) {
        this.maxFollowRedirects = maxFollowRedirects;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getUseProxy() {
        return useProxy;
    }

    public void setUseProxy(Integer useProxy) {
        this.useProxy = useProxy;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getBurst() {
        return burst;
    }

    public void setBurst(int burst) {
        this.burst = burst;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getOutputClass() {
        return outputClass;
    }

    public void setOutputClass(String outputClass) {
        this.outputClass = outputClass;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", requestMethod=" + requestMethod +
                ", requestParams='" + requestParams + '\'' +
                ", responseType=" + responseType +
                ", maxTry=" + maxTry +
                ", maxFollowRedirects=" + maxFollowRedirects +
                ", useragent='" + useragent + '\'' +
                ", timeout=" + timeout +
                ", useProxy=" + useProxy +
                ", headers='" + headers + '\'' +
                ", cookies='" + cookies + '\'' +
                ", rate=" + rate +
                ", burst=" + burst +
                ", status=" + status +
                ", duration=" + duration +
                ", outputClass='" + outputClass + '\'' +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
