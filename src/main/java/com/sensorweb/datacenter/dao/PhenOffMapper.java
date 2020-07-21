package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.PhenOff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PhenOffMapper {
    int insertData(PhenOff phenOff);
}
