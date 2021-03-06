package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Characteristic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CharacteristicMapper {
    int insertData(Characteristic characteristic);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Characteristic selectById(int id);
    List<Characteristic> selectByName(String name);
}
