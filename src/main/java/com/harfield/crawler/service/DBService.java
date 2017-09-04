package com.harfield.crawler.service;

import com.harfield.crawler.domain.Task;

import java.util.List;

public interface DBService {
    List<Task> getPendingAttempts();

}
