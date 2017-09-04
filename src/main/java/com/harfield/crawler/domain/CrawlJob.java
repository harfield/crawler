package com.harfield.crawler.domain;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/4.
 */
public class CrawlJob {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getMaxTryRequest() {
        return maxTryRequest;
    }

    public void setMaxTryRequest(int maxTryRequest) {
        this.maxTryRequest = maxTryRequest;
    }

    public int getFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(int followRedirects) {
        this.followRedirects = followRedirects;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getUseProxy() {
        return useProxy;
    }

    public void setUseProxy(int useProxy) {
        this.useProxy = useProxy;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getBurst() {
        return burst;
    }

    public void setBurst(int burst) {
        this.burst = burst;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getOwnTime() {
        return ownTime;
    }

    public void setOwnTime(Date ownTime) {
        this.ownTime = ownTime;
    }

    public String getOutputClass() {
        return outputClass;
    }

    public void setOutputClass(String outputClass) {
        this.outputClass = outputClass;
    }

    private Long id;
    private String name;
    private String desc;
    private int requestMethod = 1;//0 无,1 get,2 post
    private int requestType;//0 普通,1 执行js
    private int maxTryRequest = 2;//如果请求失败
    private int followRedirects = 1;//0 false,1 true
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private long timeout;//0 不限制
    private int useProxy;//0 false,1 true
    private float rate;//每秒爬n个url,rate等于1/n
    private int burst;//并发度
    private String remark;
    private int stat;//爬虫状态:0 待爬,1 暂停,2 进行中,3 结束
    private String owner;//考虑到hostname可能变化,建议使用IP
    private Date ownTime;//认领时间

    private String outputClass;
}
