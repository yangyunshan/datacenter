package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PositionMapper {
    int insertData(Position position);

    int deleteById(int id);
    int deleteByProcedureId(String identifier);

    Position selectById(int id);
    List<Position> selectByName(String name);
    List<Position> selectByEnvelope(@Param("minLon") double minLongitude, @Param("minLat") double minLatitude,
                                    @Param("maxLon") double maxLongitude, @Param("maxLat") double maxLatitude);
}
