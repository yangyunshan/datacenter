package com.sensorweb.datacenter.entity.sos;

/**
 * phenomenon表与offering表关联模型
 */
public class PhenOff {
    private String phenomenonId;
    private String offeringId;

    public String getPhenomenonId() {
        return phenomenonId;
    }

    public void setPhenomenonId(String phenomenonId) {
        this.phenomenonId = phenomenonId;
    }

    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }
}
