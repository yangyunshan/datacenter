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
import org.vast.xml.DOMHelperException;

import java.util.*;

@SpringBootTest
class DataCenterApplicationTests implements DataCenterConstant {
    @Autowired
    private AirService service;

    @Test
    public void test01() throws Exception {
        service.insert24HoursData();
//        service.insert7DaysData();
//        service.insertDayDataByDate("2020-08-01", "2020-08-10");
        System.out.println();
    }

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
        GetObservationRequest request = getObservationService.getObservationRequest(DataCenterUtils.readFromFile("d://MEGA/Others/GetObservation.xml"));
        List<Observation> res = getObservationService.getObservationContent(request);
        System.out.println();
    }

    @Autowired
    private HimawariService himawariService;

    @Test
    public void test04() {
        String host = "ftp.ptree.jaxa.jp";
        String userName = "yangyunshan123_gmail.com";
        String password = "SP+wari8";
        FTPClient ftpClient = himawariService.getFTPClient(host, userName, password);

        boolean flag = himawariService.downloadFTP(ftpClient, "/","READMEfirst_P-Ttree_en.txt", "d://");
        System.out.println();
    }

    @Autowired
    private HdfsService hdfsService;
    @Test
    public void test05() throws Exception {
//        hdfsService.mkdir("/test");
//        hdfsService.uploadFile("d://StationInfo.txt", "/input");
//        List<Map<String, String>> res = hdfsService.listFile("/");
        String ss = hdfsService.readFile("/input/file1.txt");
//        boolean b = hdfsService.existFile("/input/StationInfo.txt");
        System.out.println(ss);
//        boolean c = hdfsService.deleteFile("/StationInfo.txt");
//        boolean d = hdfsService.deleteDir("/test");
//        hdfsService.downloadFile("/input/file1.txt", "d://test.txt");
        System.out.println();
    }

}
