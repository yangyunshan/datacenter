package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Offering;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OfferingMapper {
    int insertData(Offering offering);

    Offering getById(String id);

    List<Offering> getAll();
}
