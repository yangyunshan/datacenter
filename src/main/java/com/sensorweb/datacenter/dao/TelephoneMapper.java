package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Telephone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TelephoneMapper {
    int insertData(Telephone telephone);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Telephone selectById(int id);
    Telephone selectByVoice(String voice);
    Telephone selectByFacsimile(String facsimile);
}
