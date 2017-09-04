package com.harfield.crawler.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.domain.Task;
import com.harfield.crawler.domain.FieldExtractRule;
import com.harfield.crawler.Parser;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/31.
 */
public class DefaultParser implements Parser {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultParser.class);

    @Override
    public Map<String, Object> parse(String src, List<FieldExtractRule> fieldExtractRules, Task task) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (null == src || null == fieldExtractRules || fieldExtractRules.size() == 0) {
            return resultMap;
        }
        org.w3c.dom.Document w3cDoc = null;
        XPath xpath = null;
        org.jsoup.nodes.Document jsoupDoc = null;
        StringBuilder sb = new StringBuilder();
        for (FieldExtractRule fieldExtractRule : fieldExtractRules) {
            try {
                String name = fieldExtractRule.getName();
                String rule = fieldExtractRule.getRule();
                switch (fieldExtractRule.getExtractType()) {
                    case 0:
                        if (rule != null && task != null) {
                            JSONObject extraJsonData = task.getExtraJsonData();
                            if (extraJsonData != null) {
                                Object o = extraJsonData.get(rule);
                                if (o != null && !"".equals(o)) {
                                    resultMap.put(name, o);
                                    break;
                                }
                            }
                            PropertyDescriptor pd = new PropertyDescriptor(rule, task.getClass());
                            Method readMethod = pd.getReadMethod();
                            readMethod.setAccessible(true);
                            Object o = readMethod.invoke(task);
                            if (o != null && !"".equals(o)) {
                                resultMap.put(name, o);
                            }
                        }
                        break;
                    case 1:
                        Pattern p = Pattern.compile(rule, Pattern.DOTALL);
                        Matcher m = p.matcher(src);
                        if (m.find()) {
                            String o = m.group(CAPTURE_GROUP);
                            if (o != null && !"".equals(o = o.trim())) {
                                resultMap.put(name, o);
                            }
                        }
                        break;
                    case 2:
                        if (null == w3cDoc) {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                            dbf.setValidating(false);
                            DocumentBuilder db = dbf.newDocumentBuilder();
                            w3cDoc = db.parse(new InputSource(new StringReader(src)));
                        }
                        if (null == xpath) {
                            XPathFactory factory = XPathFactory.newInstance();
                            xpath = factory.newXPath();
                        }
                        NodeList nodeList = (NodeList) xpath.evaluate(rule, w3cDoc, XPathConstants.NODESET);
                        int len = nodeList.getLength();
                        JSONArray jsonArr = new JSONArray();
                        for (int i = 0; i < len; i++) {
                            Node node = nodeList.item(i);
                            String textContent = node.getTextContent();
                            if (textContent != null) {
                                if (node instanceof Element) {
                                    jsonArr.add(textContent.replaceAll("\\s+", " ").trim());
                                } else {
                                    sb.append(textContent).append(" ");
                                }
                            }
                        }
                        if (jsonArr.size() > 0) {
                            resultMap.put(name, jsonArr.toJSONString());
                        } else {
                            String o = sb.toString().trim();
                            if (!"".equals(o)) {
                                resultMap.put(name, o);
                            }
                        }
                        sb.setLength(0);
                        break;
                    case 3:
                        if (null == jsoupDoc) {
                            jsoupDoc = Jsoup.parse(src);
                        }
                        org.jsoup.select.Elements elements = jsoupDoc.select(rule);
                        if (elements != null && elements.size() > 0) {
                            for (org.jsoup.nodes.Element e : elements) {
                                sb.append(",").append(e.text().trim());
                            }
                            String o = sb.substring(1).trim();
                            if (!"".equals(o)) {
                                resultMap.put(name, o);
                            }
                            sb.setLength(0);
                        }
                        break;
                    default:
                        LOG.warn("{} extractType is in valid", fieldExtractRule);
                        break;
                }
                Object v = resultMap.get(name);
                if (v == null) {
                    String defaultValue = fieldExtractRule.getDefaultValue();
                    if (defaultValue != null && !"".equals(defaultValue = defaultValue.trim())) {
                        resultMap.put(name, defaultValue);
                    }
                } else {
                    if (fieldExtractRule.getValueType() == 1) {
                        URI uri = new URI((String) v);
                        if (!uri.isAbsolute()) {
                            String baseUrl = task.getUrl();
                            URI baseUri = new URI(baseUrl);
                            if ("".equals(baseUri.getPath())) {
                                baseUri = new URI(baseUrl + "/");
                            }
                            resultMap.put(name, baseUri.resolve(uri).toString());
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Parse {} failed: {}", fieldExtractRule, e.getMessage());
            }
        }
        return resultMap;
    }

    public static void main(String[] args) throws Exception {
        Task task = new Task();
        task.setUrl("http://class.hujiang.com/15209274/intro");
//        task.setUrl("http://class.hujiang.com/15196498/intro");
//        task.setUrl("http://class.hujiang.com/159918/intro");
        task.setRequestType(1);
        task.setRequestMethod(1);
        task.setFollowRedirects(1);
        task.setMaxTryRequest(2);
        task.setExtraDataByJsonStr("{'advertiser_id':'12'}");

//        Fetcher fetcher = new DefaultFetcher();
//        String src = fetcher.fetch(task);
//        System.out.println(src);

        List<FieldExtractRule> fieldExtractRules = new ArrayList<FieldExtractRule>();

        FieldExtractRule fieldExtractRule1 = new FieldExtractRule();
        fieldExtractRule1.setName("meta_info.category");
        fieldExtractRule1.setExtractType(2);
        fieldExtractRule1.setRule("//*[@id=\"firstCate\"]|//*[@id=\"firstCate\"]/following-sibling::*");
        fieldExtractRules.add(fieldExtractRule1);

        FieldExtractRule fieldExtractRule2 = new FieldExtractRule();
        fieldExtractRule2.setName("name");
        fieldExtractRule2.setExtractType(2);
        fieldExtractRule2.setRule("//h2[@class=\"intro-title\"]/text()");
        fieldExtractRules.add(fieldExtractRule2);

        FieldExtractRule fieldExtractRule3 = new FieldExtractRule();
        fieldExtractRule3.setName("meta_info.description");
        fieldExtractRule3.setExtractType(2);
        fieldExtractRule3.setRule("//p[@class=\"intro-desc\"]/text()");
        fieldExtractRules.add(fieldExtractRule3);

        FieldExtractRule fieldExtractRule4 = new FieldExtractRule();
        fieldExtractRule4.setName("meta_info.lesson");
        fieldExtractRule4.setExtractType(3);
        fieldExtractRule4.setRule(".timeline-total");
        fieldExtractRules.add(fieldExtractRule4);

        FieldExtractRule fieldExtractRule5 = new FieldExtractRule();
        fieldExtractRule5.setName("meta_info.student_level");
        fieldExtractRule5.setExtractType(2);
        fieldExtractRule5.setRule("//dd[@id=\"dl_people_tag_container\"]//span[@class=\"people-item\"]");
        fieldExtractRules.add(fieldExtractRule5);

        FieldExtractRule fieldExtractRule6 = new FieldExtractRule();
        fieldExtractRule6.setName("meta_info.start_time");
        fieldExtractRule6.setExtractType(3);
        fieldExtractRule6.setRule(".timeline-start span");
        fieldExtractRules.add(fieldExtractRule6);

        FieldExtractRule fieldExtractRule7 = new FieldExtractRule();
        fieldExtractRule7.setName("meta_info.end_time");
        fieldExtractRule7.setExtractType(3);
        fieldExtractRule7.setRule(".timeline-end span");
        fieldExtractRules.add(fieldExtractRule7);

        FieldExtractRule fieldExtractRule8 = new FieldExtractRule();
        fieldExtractRule8.setName("meta_info.value");
        fieldExtractRule8.setExtractType(1);
//        fieldExtractRule8.setRule("(?<=价格：￥)[^<]+");
        fieldExtractRule8.setRule("价格：￥(?<g>[^<]+)");
        fieldExtractRules.add(fieldExtractRule8);

        FieldExtractRule fieldExtractRule9 = new FieldExtractRule();
        fieldExtractRule9.setName("price");
        fieldExtractRule9.setExtractType(2);
        fieldExtractRule9.setRule("//div[@class=\"intro-info-t\"]//span[@class=\"class-price\"]/text()");
        fieldExtractRules.add(fieldExtractRule9);

        FieldExtractRule fieldExtractRule10 = new FieldExtractRule();
        fieldExtractRule10.setName("meta_info.discount");
        fieldExtractRule10.setExtractType(2);
        fieldExtractRule10.setRule("//div[@class=\"intro-info-t\"]/p/text()");
        fieldExtractRules.add(fieldExtractRule10);

        FieldExtractRule fieldExtractRule11 = new FieldExtractRule();
        fieldExtractRule11.setName("meta_info.promotion");
        fieldExtractRule11.setExtractType(2);
        fieldExtractRule11.setRule("//ul[@class=\"coupon-list\"]/li");
        fieldExtractRules.add(fieldExtractRule11);

        FieldExtractRule fieldExtractRule12 = new FieldExtractRule();
        fieldExtractRule12.setName("target_url");
        fieldExtractRule12.setExtractType(0);
        fieldExtractRule12.setRule("url");
        fieldExtractRules.add(fieldExtractRule12);

        FieldExtractRule fieldExtractRule13 = new FieldExtractRule();
        fieldExtractRule13.setName("original_url");
        fieldExtractRule13.setExtractType(2);
        fieldExtractRule13.setValueType(1);
        fieldExtractRule13.setRule("//div[@class=\"intro-info-l\"]//img/@src");
        fieldExtractRules.add(fieldExtractRule13);

        FieldExtractRule fieldExtractRule14 = new FieldExtractRule();
        fieldExtractRule14.setName("tag");
        fieldExtractRule14.setExtractType(0);
        fieldExtractRule14.setDefaultValue("1617");
        fieldExtractRules.add(fieldExtractRule14);

        FieldExtractRule fieldExtractRule15 = new FieldExtractRule();
        fieldExtractRule15.setName("advertiser_id");
        fieldExtractRule15.setExtractType(0);
        fieldExtractRule15.setRule("advertiser_id");
        fieldExtractRules.add(fieldExtractRule15);

        FieldExtractRule fieldExtractRule16 = new FieldExtractRule();
        fieldExtractRule16.setName("material_id");
        fieldExtractRule16.setExtractType(0);
        fieldExtractRule16.setRule("product_id");
        fieldExtractRules.add(fieldExtractRule16);

        FieldExtractRule fieldExtractRule17 = new FieldExtractRule();
        fieldExtractRule17.setName("material_id");
        fieldExtractRule17.setExtractType(1);
        fieldExtractRule17.setRule("data-cid=\"(?<g>[^\"]+)");
        fieldExtractRule17.setOrd(1);
        fieldExtractRules.add(fieldExtractRule17);

        /*Parser parser = new DefaultParser();
        Map<String, Object> map = parser.parse(src.replaceAll("http://i1\\.c\\.hjfile\\.cn/lesson/intro/201411/", ""), fieldExtractRules, task);
        if (map != null) {
            for (String key : map.keySet()) {
                System.out.println(key + ": " + map.get(key));
            }
        }*/

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            String jdbcUrl = "jdbc:mysql://192.168.13.113/easy_crawler_dev?useUnicode=true&characterEncoding=UTF-8";
            String jdbcUsername = "dw";
            String jdbcPassword = "123456";
            conn = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
            conn.setAutoCommit(false);
            String updateCrawlJobStatusSql = "INSERT INTO field_extract_rule(fieldExtractRuleName,fieldExtractRuleType,fieldExtractRule,fieldExtractRuleValueType,fieldExtractRuleDefaultValue,fieldExtractRuleCrawlJobId,fieldExtractRuleOrder) VALUES (?,?,?,?,?,?,?)";
            preparedStatement = conn.prepareStatement(updateCrawlJobStatusSql);
            for (FieldExtractRule fieldExtractRule : fieldExtractRules) {
                preparedStatement.setString(1, fieldExtractRule.getName());
                preparedStatement.setInt(2, fieldExtractRule.getExtractType());
                String rule = fieldExtractRule.getRule();
                if (null == rule) {
                    preparedStatement.setNull(3, Types.VARCHAR);
                } else {
                    preparedStatement.setString(3, rule);
                }
                preparedStatement.setInt(4, fieldExtractRule.getValueType());
                String defaultValue = fieldExtractRule.getDefaultValue();
                if (null == defaultValue) {
                    preparedStatement.setNull(5, Types.VARCHAR);
                } else {
                    preparedStatement.setString(5, defaultValue);
                }
                preparedStatement.setLong(6, 1);
                preparedStatement.setInt(7, fieldExtractRule.getOrd());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
