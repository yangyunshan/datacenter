package com.sensorweb.datacenter.service;

import com.sensorweb.datacenter.dao.ProcedureMapper;
import com.sensorweb.datacenter.entity.Procedure;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryDataService {

    @Autowired
    private ProcedureMapper procedureMapper;

    //使用identifier精确查询
    public Procedure getProcedureByIdentifier(String identifier) {
        return procedureMapper.selectByIdentifier(identifier);
    }

    //使用identifier模糊查询
    public List<Procedure> getProcedureByFuzzyIdentifier(String identifier) {
        return procedureMapper.selectByFuzzyIdentifier(identifier);
    }
    //使用name属性查询
    public List<Procedure> getProcedureByName(String name) {
        return procedureMapper.selectByName(name);
    }

    public List<Procedure> getProcedureByDescription(String description) {
        return procedureMapper.selectByDescription(description);
    }

    public List<Procedure> getProcedureByStatus(char status) {
        return procedureMapper.selectByStatus(status);
    }

    public List<Procedure> getProcedureByIsType(char isType) {
        return procedureMapper.selectByIsType(isType);
    }

    public List<Procedure> getProcedureByIsAggregation(char isAggregation) {
        return procedureMapper.selectByIsAggregation(isAggregation);
    }

    public List<Procedure> getProcedureByIsMobile(char isMobile) {
        return procedureMapper.selectByIsMobile(isMobile);
    }

    public List<Procedure> getProcedureByIsInsitu(char IsInsitu) {
        return procedureMapper.selectByIsInsitu(IsInsitu);
    }

    public List<Procedure> getAllProcedures() {
        return procedureMapper.selectAll();
    }

    public String getSensorMLContent(String url) {
        return DataCenterUtils.readFromFile(url);
    }
}
