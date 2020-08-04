package com.sensorweb.datacenter;

import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.service.MeteorologyService;
import com.sensorweb.datacenter.service.sos.*;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.swe.v20.DataBlock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.vast.data.DataBlockString;
import org.vast.ows.GetCapabilitiesRequest;
import org.vast.ows.OWSException;
import org.vast.ows.sos.*;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;

import java.net.URLEncoder;
import java.util.List;

@SpringBootTest
class DataCenterApplicationTests implements DataCenterConstant {

    @Autowired
    private InsertSensorService sensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Autowired
    private DeleteSensorService deleteSensorService;

    @Autowired
    private GetObservationService getObservationService;

    @Autowired
    private InsertObservationService insertObservationService;

    @Autowired
    private GetCapabilitiesService getCapabilitiesService;

    @Test
    public void test() throws OWSException, DOMHelperException {
        InsertObservationRequest request = insertObservationService.getInsertObservationRequest(DataCenterUtils.readFromFile("d://MEGA/Others/test01.xml"));
        String s = request.getObservations().get(0).getResult().getComponent(0).getData().getStringValue();
        DataBlock dataBlock = new DataBlockString();
        request.getObservations().get(0).getResult().getComponent(1);
        System.out.println();
    }

    @Autowired
    private MeteorologyService service;

    @Test
    public void test01() throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD + "&date=" + URLEncoder.encode("2019-05-09 12:00:00", "utf-8");

        String document = service.doGet(DataCenterConstant.GET_LAST_HOURS_DATA, param);
        List<Object> res = service.parseXmlDoc(document);
//        service.getObservation(res.get(0));
        Element element = service.writeRequest(res.get(0));
        String ss = DataCenterUtils.element2String(element);
        System.out.println(ss);
    }

    @Test
    public void test02() throws OWSException, DOMHelperException {
        GetObservationRequest request = getObservationService.getObservationRequest(DataCenterUtils.readFromFile("d://MEGA/Others/GetObservation.xml"));
        List<Observation> observations = getObservationService.getObservationContent(request);
        System.out.println();
    }

    @Test
    public void test03() throws OWSException, DOMHelperException {
        GetCapabilitiesRequest request = getCapabilitiesService.getGetCapabilitiesRequest(DataCenterUtils.readFromFile("d://MEGA/Others/GetCapabilitiesRequest.xml"));
        Element element = getCapabilitiesService.getGetCapabilitiesResponse(request);
        String ss = DataCenterUtils.element2String(element);
        System.out.println();
    }

}
