package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.FoiOff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FoiOffMapper {
    int insertData(FoiOff foiOff);
}
