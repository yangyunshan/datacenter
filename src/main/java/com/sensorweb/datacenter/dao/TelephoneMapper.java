package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Telephone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TelephoneMapper {
    int insertData(Telephone telephone);

    int deleteById(int id);
    int deleteByIdentifier(String identifier);

    int updateVoiceByIdentifier(@Param("identifier") String identifier, @Param("voice") String voice);
    int updateFacsimileByIdentifier(@Param("identifier") String identifier, @Param("facsimile") String facsimile);

    Telephone selectById(int id);
    Telephone selectByIdentifier(String identifier);
    Telephone selectByVoice(String voice);
    Telephone selectByFacsimile(String facsimile);
}
