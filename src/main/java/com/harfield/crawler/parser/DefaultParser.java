package com.harfield.crawler.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.domain.PageRule;
import com.harfield.crawler.domain.Task;
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
    public Map<String, Object> parse(String src, List<PageRule> pageRules, Task task) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (null == src || null == pageRules || pageRules.size() == 0) {
            return resultMap;
        }
        org.w3c.dom.Document w3cDoc = null;
        XPath xpath = null;
        org.jsoup.nodes.Document jsoupDoc = null;
        StringBuilder sb = new StringBuilder();
        for (PageRule pageRule : pageRules) {
            try {
                String name = pageRule.getName();
                String rule = pageRule.getRule();
                switch (pageRule.getExtractType()) {
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
                        LOG.warn("{} extractType is in valid", pageRule);
                        break;
                }
                Object v = resultMap.get(name);
                if (v == null) {
                    String defaultValue = pageRule.getDefaultValue();
                    if (defaultValue != null && !"".equals(defaultValue = defaultValue.trim())) {
                        resultMap.put(name, defaultValue);
                    }
                } else {
                    if (pageRule.getValueType() == 1) {
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
                LOG.error("Parse {} failed: {}", pageRule, e.getMessage());
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

        List<PageRule> pageRules = new ArrayList<PageRule>();

        PageRule pageRule1 = new PageRule();
        pageRule1.setName("meta_info.category");
        pageRule1.setExtractType(2);
        pageRule1.setRule("//*[@id=\"firstCate\"]|//*[@id=\"firstCate\"]/following-sibling::*");
        pageRules.add(pageRule1);

        PageRule pageRule2 = new PageRule();
        pageRule2.setName("name");
        pageRule2.setExtractType(2);
        pageRule2.setRule("//h2[@class=\"intro-title\"]/text()");
        pageRules.add(pageRule2);

        PageRule pageRule3 = new PageRule();
        pageRule3.setName("meta_info.description");
        pageRule3.setExtractType(2);
        pageRule3.setRule("//p[@class=\"intro-desc\"]/text()");
        pageRules.add(pageRule3);

        PageRule pageRule4 = new PageRule();
        pageRule4.setName("meta_info.lesson");
        pageRule4.setExtractType(3);
        pageRule4.setRule(".timeline-total");
        pageRules.add(pageRule4);

        PageRule pageRule5 = new PageRule();
        pageRule5.setName("meta_info.student_level");
        pageRule5.setExtractType(2);
        pageRule5.setRule("//dd[@id=\"dl_people_tag_container\"]//span[@class=\"people-item\"]");
        pageRules.add(pageRule5);

        PageRule pageRule6 = new PageRule();
        pageRule6.setName("meta_info.start_time");
        pageRule6.setExtractType(3);
        pageRule6.setRule(".timeline-start span");
        pageRules.add(pageRule6);

        PageRule pageRule7 = new PageRule();
        pageRule7.setName("meta_info.end_time");
        pageRule7.setExtractType(3);
        pageRule7.setRule(".timeline-end span");
        pageRules.add(pageRule7);

        PageRule pageRule8 = new PageRule();
        pageRule8.setName("meta_info.value");
        pageRule8.setExtractType(1);
//        pageRule8.setRule("(?<=价格：￥)[^<]+");
        pageRule8.setRule("价格：￥(?<g>[^<]+)");
        pageRules.add(pageRule8);

        PageRule pageRule9 = new PageRule();
        pageRule9.setName("price");
        pageRule9.setExtractType(2);
        pageRule9.setRule("//div[@class=\"intro-info-t\"]//span[@class=\"class-price\"]/text()");
        pageRules.add(pageRule9);

        PageRule pageRule10 = new PageRule();
        pageRule10.setName("meta_info.discount");
        pageRule10.setExtractType(2);
        pageRule10.setRule("//div[@class=\"intro-info-t\"]/p/text()");
        pageRules.add(pageRule10);

        PageRule pageRule11 = new PageRule();
        pageRule11.setName("meta_info.promotion");
        pageRule11.setExtractType(2);
        pageRule11.setRule("//ul[@class=\"coupon-list\"]/li");
        pageRules.add(pageRule11);

        PageRule pageRule12 = new PageRule();
        pageRule12.setName("target_url");
        pageRule12.setExtractType(0);
        pageRule12.setRule("url");
        pageRules.add(pageRule12);

        PageRule pageRule13 = new PageRule();
        pageRule13.setName("original_url");
        pageRule13.setExtractType(2);
        pageRule13.setValueType(1);
        pageRule13.setRule("//div[@class=\"intro-info-l\"]//img/@src");
        pageRules.add(pageRule13);

        PageRule pageRule14 = new PageRule();
        pageRule14.setName("tag");
        pageRule14.setExtractType(0);
        pageRule14.setDefaultValue("1617");
        pageRules.add(pageRule14);

        PageRule pageRule15 = new PageRule();
        pageRule15.setName("advertiser_id");
        pageRule15.setExtractType(0);
        pageRule15.setRule("advertiser_id");
        pageRules.add(pageRule15);

        PageRule pageRule16 = new PageRule();
        pageRule16.setName("material_id");
        pageRule16.setExtractType(0);
        pageRule16.setRule("product_id");
        pageRules.add(pageRule16);

        PageRule pageRule17 = new PageRule();
        pageRule17.setName("material_id");
        pageRule17.setExtractType(1);
        pageRule17.setRule("data-cid=\"(?<g>[^\"]+)");
        pageRule17.setOrd(1);
        pageRules.add(pageRule17);

        /*Parser parser = new DefaultParser();
        Map<String, Object> map = parser.parse(src.replaceAll("http://i1\\.c\\.hjfile\\.cn/lesson/intro/201411/", ""), pageRules, task);
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
            for (PageRule pageRule : pageRules) {
                preparedStatement.setString(1, pageRule.getName());
                preparedStatement.setInt(2, pageRule.getExtractType());
                String rule = pageRule.getRule();
                if (null == rule) {
                    preparedStatement.setNull(3, Types.VARCHAR);
                } else {
                    preparedStatement.setString(3, rule);
                }
                preparedStatement.setInt(4, pageRule.getValueType());
                String defaultValue = pageRule.getDefaultValue();
                if (null == defaultValue) {
                    preparedStatement.setNull(5, Types.VARCHAR);
                } else {
                    preparedStatement.setString(5, defaultValue);
                }
                preparedStatement.setLong(6, 1);
                preparedStatement.setInt(7, pageRule.getOrd());
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
