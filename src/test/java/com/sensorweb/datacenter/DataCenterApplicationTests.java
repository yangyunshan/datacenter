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
    private InsertObservationService insertObservationService;

    @Test
    public void test() throws OWSException, DOMHelperException {
        String filePath = "d://MEGA/Others/InsertObservation.xml";
        InsertObservationRequest request = insertObservationService.getInsertObservationRequest(DataCenterUtils.readFromFile(filePath));
//        String s = request.getObservations().get(0).getResult().getComponent(0).getData().getStringValue();
//        DataBlock dataBlock = new DataBlockString();
//        request.getObservations().get(0).getResult().getComponent(1);
        System.out.println();
    }

    @Autowired
    private AirService service;

    @Test
    public void test01() throws Exception {
        service.insert24HoursData();
        System.out.println();
    }

    @Autowired
    private DeleteSensorService deleteSensorService;

    @Test
    public void test02() throws Exception {
        deleteSensorService.deleteSensor("urn:湖北省:def:identifier:OGC:2.0:环境监测站数据中心");
        System.out.println();
    }

    @Test
    public void test04() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("python D://demo.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine())!=null) {
                System.out.println(line);
            }
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
