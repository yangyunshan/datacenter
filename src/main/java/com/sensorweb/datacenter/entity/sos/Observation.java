package com.sensorweb.datacenter.entity.sos;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

public class Observation {
    private String id;
    private String description;
    private LocalDateTime time;
    private String procedureId;
    private String offeringId;
    private String foiId;
    private String observedProperty;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public String getFoiId() {
        return foiId;
    }

    public void setFoiId(String foiId) {
        this.foiId = foiId;
    }

    public String getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
