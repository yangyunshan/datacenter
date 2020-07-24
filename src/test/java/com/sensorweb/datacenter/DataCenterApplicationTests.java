package com.sensorweb.datacenter;

import com.sensorweb.datacenter.entity.Capability;
import com.sensorweb.datacenter.entity.Observation;
import com.sensorweb.datacenter.entity.Offering;
import com.sensorweb.datacenter.entity.Procedure;
import com.sensorweb.datacenter.service.sos.*;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.vast.ows.GetCapabilitiesRequest;
import org.vast.ows.OWSException;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.OWSUtils;
import org.vast.ows.sos.*;
import org.vast.ows.swe.DescribeSensorRequest;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;
import org.vast.xml.XMLWriterException;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@SpringBootTest
class DataCenterApplicationTests {

    @Autowired
    private InsertSensorService sensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Test
    public void test01() throws OWSException, IOException {
        InsertSensorRequest request = sensorService.getInsertSensorRequest(DataCenterUtils.readFromFile("d://Others/InsertSensor.xml"));

        String[] res = sensorService.insertSensor(request);
        System.out.println(res);
    }

    @Test
    public void test02() throws OWSException, DOMHelperException, XMLReaderException {
        DescribeSensorRequest request = describeSensorService.getDescribeSensorRequest(DataCenterUtils.readFromFile("d://Others/DescribeSensor.xml"));
        String ss = describeSensorService.getDescribeSensorResponse(describeSensorService.getProcedureId(request), describeSensorService.getDescriptionFormat(request));
        System.out.println(ss);
    }

    @Autowired
    private InsertObservationService insertObservationService;

    @Test
    public void test03() throws OWSException, DOMHelperException, ParseException, XMLWriterException {
        InsertObservationRequest request = insertObservationService.getInsertObservationRequest(DataCenterUtils.readFromFile("d://Others/InsertObservation.xml"));
        List<String> ss = insertObservationService.insertObservation(request);
        System.out.println();
    }

    @Autowired
    private GetObservationService getObservationService;

    @Test
    public void test() throws OWSException, DOMHelperException {
        GetObservationRequest request = getObservationService.getObservationRequest(DataCenterUtils.readFromFile("d://Others/GetObservation.xml"));
        List<Observation> ss = getObservationService.getObservationContent(request);
        System.out.println(ss);
    }

}
