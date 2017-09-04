package com.harfield.crawler.service;

import com.harfield.crawler.common.Task;

import java.util.List;

public interface DBService {
    List<Task> getPendingAttempts();

}
