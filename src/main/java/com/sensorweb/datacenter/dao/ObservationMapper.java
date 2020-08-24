package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Observation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@Mapper
public interface ObservationMapper {
    int insertData(Observation observation);

    int deleteByProcedureId(String procedureId);

    List<Observation> selectObservationsByConditions(@Param("procedureIds") Set<String> procedureIds,
                                                     @Param("observedProperties") Set<String> observedProperties,
                                                     @Param("begin") Timestamp begin,
                                                     @Param("end") Timestamp end);

    List<Observation> selectObservationsBySpatial(@Param("minX") double minX, @Param("minY") double minY,
                                                  @Param("maxX") double maxX, @Param("maxY") double maxY);
}
