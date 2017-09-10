package com.harfield.crawler.components.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.harfield.crawler.domain.PageRule;
import com.harfield.crawler.domain.Task;
import com.harfield.crawler.components.Parser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xpath
 *
 * jsonPath
 *
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


}
