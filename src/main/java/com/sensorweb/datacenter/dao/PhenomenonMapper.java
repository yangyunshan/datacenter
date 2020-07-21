package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Phenomenon;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PhenomenonMapper {
    int insertData(Phenomenon phenomenon);
}
