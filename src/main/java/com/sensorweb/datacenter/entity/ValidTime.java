package com.sensorweb.datacenter.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class ValidTime {
    private int id;
    private String identifier;
    private LocalDateTime beginPosition;
    private LocalDateTime endPosition;

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

    public LocalDateTime getBeginPosition() {
        return beginPosition;
    }

    public void setBeginPosition(LocalDateTime beginPosition) {
        this.beginPosition = beginPosition;
    }

    public LocalDateTime getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(LocalDateTime endPosition) {
        this.endPosition = endPosition;
    }
}
