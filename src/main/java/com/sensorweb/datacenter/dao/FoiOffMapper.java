package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.FoiOff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FoiOffMapper {
    int insertData(FoiOff foiOff);

    List<FoiOff> getFoiOff(String offeringId);
}
