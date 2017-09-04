package com.harfield.crawler;

import com.harfield.crawler.common.Task;
import com.harfield.crawler.common.FieldExtractRule;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/31.
 */
public interface Parser {
    String CAPTURE_GROUP = "g";
    Map<String, Object> parse(String src, List<FieldExtractRule> fieldExtractRules, Task task);
}
