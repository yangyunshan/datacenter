package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.ProcOff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ProcOffMapper {
    int insertData(ProcOff procOff);
}
