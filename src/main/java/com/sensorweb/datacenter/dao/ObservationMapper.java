package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Observation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
@Mapper
public interface ObservationMapper {
    int insertData(Observation observation);

    List<Observation> selectObservations(@Param("procedureIds") Set<String> procedureIds,
                                         @Param("fois") Set<String> fois,
                                         @Param("temporal") Instant[] temporal);
}
