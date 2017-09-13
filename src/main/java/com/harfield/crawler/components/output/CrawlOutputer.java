package com.harfield.crawler.components.output;

import java.util.Map;

/**
 * Created by Administrator on 2016/1/5.
 */
public interface CrawlOutputer {
    void output(Map<String, Object> map);
}
