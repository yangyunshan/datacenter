package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Keyword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface KeywordMapper {
    int insertData(Keyword keyword);

    int deleteById(int id);
    int deleteByIdentifier(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    Keyword selectById(int id);
    Keyword selectByIdentifier(String identifier);
    List<Keyword> selectByValue(String value);
}
