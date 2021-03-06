package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Quantity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuantityMapper {
    int insertData(Quantity quantity);

    int deleteById(int id);
    int deleteByOutId(String outId);

    List<String> selectByNameAndValue(@Param("name") String value, @Param("minValue") double minValue, @Param("maxValue") double maxValue);
}
