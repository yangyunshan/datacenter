package com.sensorweb.datacenter;

import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.service.AirService;
import com.sensorweb.datacenter.service.HdfsService;
import com.sensorweb.datacenter.service.HimawariService;
import com.sensorweb.datacenter.service.sos.*;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.vast.ows.OWSException;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLWriterException;

import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.util.*;

@SpringBootTest
class DataCenterApplicationTests implements DataCenterConstant {
    @Autowired
    DeleteSensorService deleteSensorService;
    @Test
    public void test02() throws Exception {
        deleteSensorService.deleteSensor("urn:湖北省:def:identifier:OGC:2.0:环境监测站数据中心");
        System.out.println();
    }

    @Autowired
    private GetObservationService getObservationService;
    @Test
    public void test03() throws OWSException, DOMHelperException {
        GetObservationRequest request = getObservationService.getObservationRequest(DataCenterUtils.readFromFile("/home/yangyunshan/MEGA/Others/GetObservation.xml"));
        List<Observation> res = getObservationService.getObservationContent(request);
        System.out.println();
    }

    @Autowired
    private AirService airService;
    @Test
    public void test04() throws Exception {
        airService.insertDataByHour();
        System.out.println();
    }

    @Autowired
    private HimawariService himawariService;
    @Test
    public void test05() throws ParseException, XMLWriterException {
        himawariService.insertData("2020-09-07T21:50:00");
    }
    
}

