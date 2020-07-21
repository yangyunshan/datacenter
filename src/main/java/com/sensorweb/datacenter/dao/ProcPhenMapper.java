package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.ProcPhen;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ProcPhenMapper {
    int insertData(ProcPhen procPhen);
}
