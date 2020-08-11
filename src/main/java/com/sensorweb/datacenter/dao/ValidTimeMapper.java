package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.ValidTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Mapper
public interface ValidTimeMapper {
    int insertData(ValidTime validTime);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    ValidTime selectById(int id);
    List<ValidTime> selectByTime(@Param("begin") Instant beginTime, @Param("end") Instant endTime);
    List<ValidTime> selectByBeginTime(Instant begin);
    List<ValidTime> selectByEndTime(Instant end);
}
