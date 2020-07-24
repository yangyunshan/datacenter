package com.sensorweb.datacenter.entity;

import java.util.Date;

public class Offering {
    private String id;
    private String name;
    private String procedureId;
    private String observableProperty;
    private String relatedFeature;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getObservableProperty() {
        return observableProperty;
    }

    public void setObservableProperty(String observableProperty) {
        this.observableProperty = observableProperty;
    }

    public String getRelatedFeature() {
        return relatedFeature;
    }

    public void setRelatedFeature(String relatedFeature) {
        this.relatedFeature = relatedFeature;
    }
}
