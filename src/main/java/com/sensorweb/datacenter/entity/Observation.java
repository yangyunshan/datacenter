package com.sensorweb.datacenter.entity;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

public class Observation {
    private String id;
    private LocalDateTime time;
    private String procedureId;
    private String foiId;
    private String phenomenonId;
    private String value;
    private String filePath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getFoiId() {
        return foiId;
    }

    public void setFoiId(String foiId) {
        this.foiId = foiId;
    }

    public String getPhenomenonId() {
        return phenomenonId;
    }

    public void setPhenomenonId(String phenomenonId) {
        this.phenomenonId = phenomenonId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
