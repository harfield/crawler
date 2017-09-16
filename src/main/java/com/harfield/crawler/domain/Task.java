package com.harfield.crawler.domain;


import java.util.Date;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Task {
    public static final int RUNNING = -1;
    public static final int SUCCEED = 0;
    public static final int FAILED = 1;
    private long id;
    private String url;
    private String referrer;
    private Date sendToMqTime;
    private Date nextCrawlTime;
    private Date startTime;
    private Date finishTime ;
    private int finishStatus;
    private int remainingRetry;
    private String msg;
    private Job job;
    private Date createTime;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Date getSendToMqTime() {
        return sendToMqTime;
    }

    public void setSendToMqTime(Date sendToMqTime) {
        this.sendToMqTime = sendToMqTime;
    }

    public Date getNextCrawlTime() {
        return nextCrawlTime;
    }

    public void setNextCrawlTime(Date nextCrawlTime) {
        this.nextCrawlTime = nextCrawlTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int getFinishStatus() {
        return finishStatus;
    }

    public void setFinishStatus(int finishStatus) {
        this.finishStatus = finishStatus;
    }

    public int getRemainingRetry() {
        return remainingRetry;
    }

    public void setRemainingRetry(int remainingRetry) {
        this.remainingRetry = remainingRetry;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", referrer='" + referrer + '\'' +
                ", sendToMqTime=" + sendToMqTime +
                ", nextCrawlTime=" + nextCrawlTime +
                ", finishTime=" + finishTime +
                ", finishStatus=" + finishStatus +
                ", remainingRetry=" + remainingRetry +
                ", msg='" + msg + '\'' +
                ", job=" + job +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}