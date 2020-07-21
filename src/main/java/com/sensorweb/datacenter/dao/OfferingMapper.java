package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Offering;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OfferingMapper {
    int insertData(Offering offering);
}
