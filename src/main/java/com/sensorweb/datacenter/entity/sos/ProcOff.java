package com.sensorweb.datacenter.entity.sos;

/**
 * procedure表与offering表关联模型
 */
public class ProcOff {
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
