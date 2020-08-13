package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CategoryMapper {
    int insertData(Category category);

    int deleteById(int id);
    int deleteByOutId(String outId);

    List<String> selectByNameAndValue(@Param("name") String name, @Param("value") String value);
}
