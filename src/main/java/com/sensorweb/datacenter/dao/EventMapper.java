package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Mapper
public interface EventMapper {
    int insertData(Event event);

    int deleteById(int id);
    int deleteByIdentifier(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    Event selectById(int id);
    List<Event> selectByIdentifierNoNum(String identifier);
    Event selectByIdentifier(String identifier);
    List<Event> selectByLabel(String label);
    List<Event> selectByDocumentUrl(String url);
    List<Event> selectByTime(Timestamp time);
}
