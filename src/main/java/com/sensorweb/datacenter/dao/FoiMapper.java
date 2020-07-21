package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.FeatureOfInterest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FoiMapper {
    int insertData(FeatureOfInterest foi);

    int deleteById(String id);
    int deleteByName(String name);

    FeatureOfInterest selectById(String id);
}
