package com.harfield.crawler.components;

import com.harfield.crawler.domain.Task;
import com.harfield.crawler.domain.PageRule;
import com.harfield.crawler.domain.WebContent;

import java.util.List;
import java.util.Map;

public interface Parser {
    Map<String, Object> parse(WebContent content, Task task);
}
