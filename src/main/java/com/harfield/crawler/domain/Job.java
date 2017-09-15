package com.harfield.crawler.domain;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Job {


    private long id;
    private int rate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", rate=" + rate +
                '}';
    }
}
