package com.harfield.crawler.service;

import com.harfield.crawler.domain.Job;
import com.harfield.crawler.domain.Task;

import java.util.List;

public interface DBService {
    List<Task> getPendingTasks();

    List<Job> getRunningJobs();

    void markTaskQueuing(Task task);

    void markTaskRunning(Task task);

    void markTaskFailed(Task task);

    void markTaskFinished(Task task);
}
