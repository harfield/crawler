package com.harfield.crawler.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by Administrator on 2016/1/8.
 */
public final class DBUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);

    public static final Properties DB_PROPS;
    public static final Properties DRUID_PROPS;

    public static final String FETCH_CRAWL_JOBS_SQL;
    public static final String FETCH_FIELD_EXTRACT_RULES_SQL;
    public static final String UPDATE_CRAWL_ATTEMPT_STATUS_BY_ID_AND_VERSION_SQL;
    public static final String FETCH_CRAWL_ATTEMPTS_SQL;

    public static DataSource DATASOURCE;

    static {
        DB_PROPS = new Properties();
        DRUID_PROPS = new Properties();
        try {
            DB_PROPS.load(DBUtils.class.getResourceAsStream("/crawldb.properties"));
            DRUID_PROPS.load(DBUtils.class.getResourceAsStream("/druid.properties"));

            FETCH_CRAWL_JOBS_SQL = DB_PROPS.getProperty("fetchCrawlJobsSql");
            FETCH_FIELD_EXTRACT_RULES_SQL = DB_PROPS.getProperty("fetchFieldExtractRulesSql");
            UPDATE_CRAWL_ATTEMPT_STATUS_BY_ID_AND_VERSION_SQL = DB_PROPS.getProperty("updateCrawlAttemptStatusByIdAndVersionSql");
            FETCH_CRAWL_ATTEMPTS_SQL = DB_PROPS.getProperty("fetchCrawlAttemptsSql");

            DATASOURCE = DruidDataSourceFactory.createDataSource(DRUID_PROPS);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws Exception {
        try {
            return DATASOURCE.getConnection();
        } catch (Throwable t) {
            closeDataSource();
            DATASOURCE = DruidDataSourceFactory.createDataSource(DRUID_PROPS);
            return DATASOURCE.getConnection();
        }
    }


    public static void closeDataSource() {
        if (DATASOURCE != null) {
            ((DruidDataSource) DATASOURCE).close();
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static void close(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                if (!preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static String escapeSql(String s) {
        if (null == s) {
            return null;
        }
        return s.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\");
    }


}
