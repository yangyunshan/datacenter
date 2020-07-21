package com.sensorweb.datacenter.entity;

/**
 * feature_of_interest表与offering表关联模型
 */
public class FoiOff {
    private String foiId;
    private String offeringId;

    public String getFoiId() {
        return foiId;
    }

    public void setFoiId(String foiId) {
        this.foiId = foiId;
    }

    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }
}
