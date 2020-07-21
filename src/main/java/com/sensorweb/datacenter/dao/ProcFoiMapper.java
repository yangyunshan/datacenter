package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.ProcFoi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Mapper
public interface ProcFoiMapper {
    int insertData(ProcFoi procFoi);

    List<ProcFoi> selectProcFois(@Param("procedureIds") Set<String> procedureIds,
                                 @Param("fois") Set<String> fois);
}
