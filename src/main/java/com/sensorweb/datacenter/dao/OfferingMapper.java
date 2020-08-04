package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Offering;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OfferingMapper {
    int insertData(Offering offering);

    int deleteByProcedure(String procedureId);

    Offering getById(String id);

    List<Offering> getAll();
}
