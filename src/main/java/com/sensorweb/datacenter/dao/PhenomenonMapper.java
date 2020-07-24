package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Phenomenon;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PhenomenonMapper {
    int insertData(Phenomenon phenomenon);

    Phenomenon getPhenomenon(String id);
}
