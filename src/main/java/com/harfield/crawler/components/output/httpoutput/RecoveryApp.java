package com.harfield.crawler.components.output.httpoutput;

import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/13.
 */
public class RecoveryApp {
    public static Map<String, HttpOutputer> cacheMap = new HashMap<String, HttpOutputer>();

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            return;
        Connection conn = null;
        PreparedStatement pstm = null;
        conn = DBUtils.getConnection();
        pstm = conn.prepareStatement("SELECT `backupId`,`backupData`,`backupStat`,`backupRemark` ,`backupOutputClass` from crawl_backup where backupStat = ?");
        pstm.setInt(1, Integer.parseInt(args[0]));
        ResultSet resultSet = pstm.executeQuery();
        while (resultSet.next()) {
            try {
                long backupId = resultSet.getLong("backupId");
                String backupOutputClass = resultSet.getString("backupOutputClass");
                String backupData = resultSet.getString("backupData");
                HttpOutputer outputer = cacheMap.get(backupOutputClass);
                if (outputer == null) {
                    Class<?> aClass = Class.forName(backupOutputClass);
                    outputer = (HttpOutputer) aClass.newInstance();
                    cacheMap.put(backupOutputClass, outputer);
                    outputer.setIsRecory();
                }
                JSONObject data = JSONObject.parseObject(backupData);
                data.put(HttpOutputer.CRAWL_BACKUP_ID, backupId);
                outputer.output(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
        DBUtils.close(resultSet);
        DBUtils.close(pstm);
        DBUtils.close(conn);


    }
}
