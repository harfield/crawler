package com.harfield.crawler;

import com.harfield.crawler.domain.Task;
import com.harfield.crawler.domain.PageRule;

import java.util.List;
import java.util.Map;

public interface Parser {
    String CAPTURE_GROUP = "g";
    Map<String, Object> parse(String src, List<PageRule> pageRules, Task task);
}
