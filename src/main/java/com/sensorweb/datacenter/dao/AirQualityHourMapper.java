package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.air.AirQualityHour;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AirQualityHourMapper {
    int insertData(AirQualityHour airQualityHour);

    List<AirQualityHour> selectById(String id);
}
