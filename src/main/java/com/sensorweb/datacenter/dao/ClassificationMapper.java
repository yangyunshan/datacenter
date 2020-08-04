package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Classification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ClassificationMapper {
    int insertData(Classification classification);

    int deleteById(int id);

    int deleteByIdentifer(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    int updateValueByIdentifierAndLabel(@Param("identifier") String identifier, @Param("label") String label, @Param("value") String value);

    Classification selectById(int id);
    Classification selectByIdentifier(String identifier);
    List<Classification> selectByIdentifierNoNum(String identifier);
    List<Classification> selectByValue(String value);
    List<Classification> selectByLabel(String label);
}
