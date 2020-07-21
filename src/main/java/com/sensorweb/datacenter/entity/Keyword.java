package com.sensorweb.datacenter.entity;


public class Keyword {
    private int id;
    private String identifier;
    private String values;//关键字拼接成一个字符串，中间以英文冒号":"分开

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
