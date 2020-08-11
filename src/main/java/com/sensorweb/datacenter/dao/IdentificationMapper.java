package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Identification;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface IdentificationMapper {
    int insertData(Identification identification);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Identification selectById(int id);
    List<Identification> selectByLabel(String label);
    List<Identification> selectByValue(String value);
}
