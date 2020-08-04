package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Capability;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CapabilityMapper {
    int insertData(Capability capability);

    int deleteById(int id);

    int deleteByIdentifier(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    Capability selectById(int id);
    Capability selectByIdentifier(String identifier);
    List<Capability> selectByName(String name);
    List<Capability> selectByLabel(String label);
    List<Capability> selectByIdentifierNoNum(String identifier);
}
