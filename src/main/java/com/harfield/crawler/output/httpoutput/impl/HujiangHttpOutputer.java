package com.harfield.crawler.output.httpoutput.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.output.httpoutput.HttpOutputer;
import com.harfield.crawler.output.httpoutput.HttpUtility;

import java.util.HashMap;
import java.util.Map;

public final class HujiangHttpOutputer extends HttpOutputer {
    @Override
    protected Map<String, String> getMeta(Map<String, Object> map) {
        checkAsyn(map);
        Map<String, String> meta = new HashMap<String, String>();
        JSONObject exData = new JSONObject();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (key.startsWith("meta_info.")) {
                String k = key.substring(10);
                exData.put(k, value);
            } else {
                String v = value instanceof String ? value + "" : JSONObject.toJSONString(value);
                meta.put(key, v);
            }
        }
        meta.put("meta_info", exData.toJSONString());
        return meta;
    }

    private void checkAsyn(Map<String, Object> map) {
        try {
            Object v = map.get("material_id");
            if (null == v) {
                LOG.warn("material_id is null. The result map: {}", map);
                return;
            }
            String material_id = "" + v;
            if (!map.containsKey("meta_info.student_level")) {
                String url = "http://class.hujiang.com/app/handlers/class_feature.ashx?classid=" + material_id;
                String body = HttpUtility.getResponseBody(url);
                if (null == body) {
                    LOG.warn("Request url={} return null", url);
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    if (jsonObject.getBoolean("isSuccess")) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("PeopleClassFeature");
                        JSONArray arr = new JSONArray();
                        int size = jsonArray.size();
                        for (int i = 0; i < size; i++) {
                            arr.add(jsonArray.getJSONObject(i).getString("FeatureName"));
                        }
                        map.put("meta_info.student_level", arr);
                    } else {
                        LOG.warn("get student_level failed material_id :{}", material_id);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
