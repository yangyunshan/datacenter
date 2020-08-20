package com.sensorweb.datacenter;

import com.alibaba.fastjson.JSONObject;
import com.sensorweb.datacenter.controller.SensorController;
import com.sensorweb.datacenter.service.AirService;
import com.sensorweb.datacenter.service.sos.*;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.vast.ows.OWSException;
import org.vast.ows.sos.*;
import org.vast.xml.DOMHelperException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DataCenterApplicationTests implements DataCenterConstant {
    @Autowired
    private AirService service;

    @Test
    public void test01() throws Exception {
        service.insert24HoursData();
//        service.insert7DaysData();
        System.out.println();
    }

    @Autowired
    DeleteSensorService deleteSensorService;
    @Test
    public void test02() throws Exception {
        deleteSensorService.deleteSensor("urn:湖北省:def:identifier:OGC:2.0:环境监测站数据中心");
        System.out.println();
    }

}
