package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Classification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ClassificationMapper {
    int insertData(Classification classification);

    int deleteById(int id);
    int deleteByProcedureId(String identifier);

    Classification selectById(int id);
    List<Classification> selectByValue(String value);
    List<Classification> selectByLabel(String label);
}
