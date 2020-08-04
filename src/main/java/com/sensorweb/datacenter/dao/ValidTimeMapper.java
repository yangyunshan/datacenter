package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.ValidTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Mapper
public interface ValidTimeMapper {
    int insertData(ValidTime validTime);

    int deleteById(int id);
    int deleteByIdentifier(String identifier);

    ValidTime selectById(int id);
    ValidTime selectByIdentifier(String identifier);
    List<ValidTime> selectByTime(@Param("begin") Timestamp beginTime, @Param("end") Timestamp endTime);
    List<ValidTime> selectByBeginTime(Timestamp begin);
    List<ValidTime> selectByEndTime(Timestamp end);
}
