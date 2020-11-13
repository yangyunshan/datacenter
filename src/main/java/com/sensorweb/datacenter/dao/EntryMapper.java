package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.laads.Entry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@Mapper
public interface EntryMapper {
    int insertData(Entry entry);

    Entry selectByEntryId(String entryId);
    List<Entry> selectByBBox(String bbox);
    List<Entry> selectByTime(Instant startTime, Instant stopTime);
    List<Entry> selectByConditions(Instant startTime, Instant stopTime, String bbox);
}
