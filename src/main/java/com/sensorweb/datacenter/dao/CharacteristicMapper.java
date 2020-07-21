package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Characteristic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CharacteristicMapper {
    int insertData(Characteristic characteristic);

    int deleteById(int id);

    int deleteByIdentifier(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    Characteristic selectById(int id);
    Characteristic selectByIdentifier(String identifier);
    List<Characteristic> selectByIdentifierNoNum(String identifier);
    List<Characteristic> selectByName(String name);
    List<Characteristic> selectByLabel(String label);
}
