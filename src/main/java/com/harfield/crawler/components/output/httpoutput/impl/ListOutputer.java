package com.harfield.crawler.components.output.httpoutput.impl;


import com.harfield.crawler.components.output.CrawlOutputer;
import com.harfield.crawler.util.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

public final class ListOutputer implements CrawlOutputer {
    private static final Logger LOG = LoggerFactory.getLogger(ListOutputer.class);
    @Override
    public void output(Map<String, Object> map) {
        try {
            Connection connection = DBUtils.getConnection();
//            PreparedStatement psm = connection.prepareStatement(DBUtils.ADD_ATTEMPTS);

        } catch (Exception e) {
            LOG.error(e.getMessage(),e);

        }

    }
}
