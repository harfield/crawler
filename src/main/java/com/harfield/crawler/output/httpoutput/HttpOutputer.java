package com.harfield.crawler.output.httpoutput;

import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.output.CrawlOutputer;
import com.harfield.crawler.util.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * 数据库中的error code
 * 500 请求资源是出错 包括  下载素材  上传到远程服务器
 * 100 程序逻辑异常
 * 0  通过恢复成功上传
 * 900 尝试恢复但依旧失败
 * other  远程服务器返回的错误码，结合remark
 */
public abstract class HttpOutputer implements CrawlOutputer {
    protected static final Logger LOG = LoggerFactory.getLogger(HttpOutputer.class);
    protected static Set<String> notNullField = new HashSet<String>();
    //    public static String serviceAddr = "http://192.168.108.58/material/api/";

    public static String serviceAddr = null;

    static {
        Properties props = new Properties();
        try {
            props.load(HttpOutputer.class.getResourceAsStream("/output-http.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        serviceAddr = props.getProperty("remote.server.address");
    }


    public static String CRAWL_BACKUP_ID = "crawl_backup_Id";

    static {
        notNullField.addAll(Arrays.asList("advertiser_id", "material_id", "name", "tag"));
    }

    boolean isRecovery = false;
    Long crawlBackupId = null;

    @Override
    public void output(Map<String, Object> map) {
        if (isRecovery) {
            crawlBackupId = (Long) map.get(CRAWL_BACKUP_ID);
            if (crawlBackupId == null) {
                throw new RuntimeException("missing id in recovery mode!");
            } else {
                map.remove(CRAWL_BACKUP_ID);
            }
        }
        //检查并抓取素材
        String url = getLocationOfMeterial(map);
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("can't get proper material url");
        }
        checkMeta(map);
        int executeStatus = 0;
        Exception exception = null;
        try {
            Map<String, String> meta = getMeta(map);
            Material material = HttpUtility.getMaterial(url);
            String response = HttpUtility.postMutiPart(serviceAddr, meta, material, "image");
            JSONObject serverReturn = null;
            serverReturn = JSONObject.parseObject(response);
            Integer code = serverReturn.getInteger("code");
            if (code != 0) {
                executeStatus = code;
                String mess = serverReturn.getString("msg");
                exception = new RuntimeException(mess);
            }

        } catch (HttpRuntimeException e) {
            executeStatus = 500;//远程传输错误
            exception = e;
        } catch (Exception e) {
            executeStatus = 100;//I don't know
            exception = e;
        }
        if (!isRecovery) {
            if (executeStatus != 0) {
                String data = JSONObject.toJSON(map).toString();
                LOG.error("failed to post data to remote:{},cause:{}", data, exception.getMessage());

//                MailUtils.sendMail("easy-crawler :save crawl data failed! ",
//                        "failed to post crawl data:" + data + ",cause:" + exception.getMessage());
                Connection conn = null;
                PreparedStatement preparedStatement = null;
                try {
                    conn = DBUtils.getConnection();
                    preparedStatement = conn.prepareStatement("INSERT  into crawl_backup(`backupData`,`backupStat`,`backupRemark` ,`backupOutputClass`,`backupInsertTime`,`backupUpdateTime`)" +
                            " VALUES (?,?,?,?,?,?) ");
                    preparedStatement.setString(1, data);
                    preparedStatement.setInt(2, executeStatus);
                    preparedStatement.setString(3, exception.getMessage());
                    preparedStatement.setString(4, this.getClass().getName());
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    preparedStatement.setTimestamp(5, timestamp);
                    preparedStatement.setTimestamp(6, timestamp);
                    int i = preparedStatement.executeUpdate();
                    if (i == 0) {
                        LOG.error("failed in save data to crawl_backup ,cause:{}", exception.getMessage());
//                        MailUtils.sendMail("easy-crawler :save crawl data failed! ", "failed in save data to crawl_backup ,cause :" + exception.getMessage());

                    }
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                } finally {
                    DBUtils.close(preparedStatement);
                    DBUtils.close(conn);
                }
            }

        } else/*if (isRecovery)*/ {
            Connection conn = null;
            PreparedStatement preparedStatement = null;
            try {
                conn = DBUtils.getConnection();
                preparedStatement = conn.prepareStatement("UPDATE   crawl_backup set backupStat = ?, backupRemark =? ,backupUpdateTime= ? where backupId = ? ");
                if (executeStatus != 0) {
                    LOG.error("in recovery mode failed to post data to remote id:{},cause:{}", crawlBackupId, exception.getMessage());
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    preparedStatement.setInt(1, 900);//恢复仍然错误
                    preparedStatement.setString(2, "try to recovery , but failed:" + exception.getMessage());
                    preparedStatement.setTimestamp(3, timestamp);
                    preparedStatement.setLong(4, crawlBackupId);
                } else {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    preparedStatement.setInt(1, 0);
                    preparedStatement.setString(2, "succeed in recovery!");
                    preparedStatement.setTimestamp(3, timestamp);
                    preparedStatement.setLong(4, crawlBackupId);
                }


                int i = preparedStatement.executeUpdate();
                if (i == 0) {
                    LOG.error("failed in save data to crawl_backup ,cause:{}", exception.getMessage());
//                    MailUtils.sendMail("easy-crawler :save crawl data failed! ", "failed in save data to crawl_backup ,cause :" + exception.getMessage());
                }
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            } finally {
                DBUtils.close(preparedStatement);
                DBUtils.close(conn);
            }


        }
    }

    private boolean checkMeta(Map<String, Object> meta) {
        for (String key : notNullField) {
            if (!meta.containsKey(key)) {
                throw new RuntimeException("missing required arguments : " + key);
            }
        }

        return true;
    }

    //格式化数据
    protected abstract Map<String, String> getMeta(Map<String, Object> map);

    public void setIsRecory() {
        this.isRecovery = true;
    }


    String getLocationOfMeterial(Map<String, Object> map) {
        Object originalUrl = map.get("original_url");
        return originalUrl == null ? null : "" + originalUrl;
    }
}
