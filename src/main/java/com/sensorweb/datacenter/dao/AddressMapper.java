package com.sensorweb.datacenter.dao;

import com.sensorweb.datacenter.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Mapper
public interface AddressMapper {
    int insertData(Address address);

    int deleteById(int id);

    int deleteByIdentifier(String identifier);

    int updateMailByIdentifier(@Param("identifier") String identifier, @Param("mail") String electronicMailAddress);

    int updateInfoByIdentifier(@Param("identifier") String identifier, @Param("address") Address address);

    Address selectById(int id);
    Address selectByIdentifier(String identifier);
    List<Address> selectByCity(String city);
    List<Address> selectByAdministrativeArea(String administrativeArea);
    List<Address> selectByCountry(String country);
}
