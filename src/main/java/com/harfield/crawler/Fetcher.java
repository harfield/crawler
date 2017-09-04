package com.harfield.crawler;

import com.harfield.crawler.common.Task;
import com.harfield.crawler.common.WebContent;

/**
 * Created by Administrator on 2015/12/31.
 */
public interface Fetcher {
    WebContent fetch(Task task) throws Exception;
}
