package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.PhenOff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PhenOffMapper {
    int insertData(PhenOff phenOff);

    List<PhenOff> getPhenOff(String offeringId);
}
