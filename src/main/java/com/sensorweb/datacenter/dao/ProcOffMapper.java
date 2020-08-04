package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.ProcOff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProcOffMapper {
    int insertData(ProcOff procOff);

    List<ProcOff> getProcOff(String offeringId);
}
