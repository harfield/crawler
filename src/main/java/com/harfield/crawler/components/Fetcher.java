package com.harfield.crawler.components;

import com.harfield.crawler.domain.Task;
import com.harfield.crawler.domain.WebContent;


/**
 */
public interface Fetcher {

     WebContent fetch(Task task) throws Exception;

}
