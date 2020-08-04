package com.sensorweb.datacenter.entity.sos;

/**
 * procedure表与feature_of_interest表关联模型
 */
public class ProcFoi {
    private String procedureId;
    private String offeringId;

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
}
