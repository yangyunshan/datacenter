package com.sensorweb.datacenter.entity.sos;

/**
 * proceduere表与phenomenon表关联模型
 */
public class ProcPhen {
    private String procedureId;
    private String phenomenonId;

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getPhenomenonId() {
        return phenomenonId;
    }

    public void setPhenomenonId(String phenomenonId) {
        this.phenomenonId = phenomenonId;
    }
}
