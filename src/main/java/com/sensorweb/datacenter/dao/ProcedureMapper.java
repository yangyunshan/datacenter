package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Procedure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProcedureMapper {
    int insertData(Procedure procedure);

    int deleteById(int id);
    int deleteByIdentifier(String identifier);

    int updateStatusByIdentifier(@Param("identifier") String identifier, @Param("status") char status);

    Procedure selectById(int id);
    Procedure selectByIdentifier(String identifier);
    List<Procedure> selectByFuzzyIdentifier(String identifier);
    List<Procedure> selectByName(String name);
    List<Procedure> selectByDescription(String description);
    List<Procedure> selectByStatus(char status);
    List<Procedure> selectByIsType(char isType);
    List<Procedure> selectByIsAggregation(char isAggregation);
    List<Procedure> selectByIsMobile(char isMobile);
    List<Procedure> selectByIsInsitu(char isInsitu);
    List<Procedure> selectAll();

}
