package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Component;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ComponentMapper {
    int insertData(Component component);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Component selectById(int id);
    List<Component> selectByPlatformId(String platformId);
}
