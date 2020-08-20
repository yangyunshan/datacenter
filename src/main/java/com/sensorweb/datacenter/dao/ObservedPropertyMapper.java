package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.ObservedProperty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ObservedPropertyMapper {
    int insertData(ObservedProperty observedProperty);

    int deleteById(String id);
    int deleteByProcedureId(String procedureId);

    ObservedProperty selectById(String id);
    boolean isExist(String id);
}
