package com.harfield.crawler.common;

/**
 * Created by Administrator on 2015/12/31.
 */
public class FieldExtractRule {
    @Override
    public String toString() {
        return "FieldExtractRule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", extractType=" + extractType +
                ", rule='" + rule + '\'' +
                ", valueType=" + valueType +
                ", defaultValue='" + defaultValue + '\'' +
                ", ord=" + ord +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExtractType() {
        return extractType;
    }

    public void setExtractType(int extractType) {
        this.extractType = extractType;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    private long id;
    private String name;
    private int extractType;//1 正则, 2 xpath, 3 css
    private String rule;
    private int valueType;//0 常规,1 url
    private String defaultValue;
    private int ord;
}
