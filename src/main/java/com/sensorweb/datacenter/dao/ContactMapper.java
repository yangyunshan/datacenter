package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.sos.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ContactMapper {
    int insertData(Contact contact);

    int deleteById(int id);

    int deleteByIdentifier(String identifier);
    int deleteByIdentifierNoNum(String identifier);

    int updateRoleById(@Param("role") String role, @Param("id") int id);

    int updateNameById(@Param("name") String organizationName, @Param("id") int id);

    Contact selectById(int id);
    Contact selectByIdentifier(String identifier);
    List<Contact> selectByIdentifierNoNum(String identifier);
    List<Contact> selectByName(String name);
    List<Contact> selectByRole(String role);
}
