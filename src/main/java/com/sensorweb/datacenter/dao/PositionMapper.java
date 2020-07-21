package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PositionMapper {
    int insertData(Position position);

    int deleteById(int id);
    int deleteByIdentifier(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    Position selectById(int id);
    Position selectByIdentifier(String identifier);
    List<Position> selectByIdentifierNoNum(String identifier);
    List<Position> selectByEnvelope(@Param("minLon") double minLongitude, @Param("minLat") double minLatitude,
                                    @Param("maxLon") double maxLongitude, @Param("maxLat") double maxLatitude);
}
