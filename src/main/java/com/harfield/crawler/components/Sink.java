package com.harfield.crawler.components;

import com.harfield.crawler.domain.Task;

import java.util.Map;

/**
 * Created by harfield on 17/9/10.
 */
public interface Sink {
    void sink(Map<String, Object> map,Task task);
}
