package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Event;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@Mapper
public interface EventMapper {
    int insertData(Event event);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Event selectById(int id);
    List<Event> selectByLabel(String label);
    List<Event> selectByDocumentUrl(String url);
    List<Event> selectByTime(Instant time);
}
