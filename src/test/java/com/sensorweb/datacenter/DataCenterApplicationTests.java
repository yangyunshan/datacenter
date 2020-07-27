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
import org.vast.ows.swe.DeleteSensorRequest;
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

    @Autowired
    private DeleteSensorService deleteSensorService;

    @Autowired
    private GetObservationService getObservationService;

    @Test
    public void test() throws OWSException, DOMHelperException {
        DeleteSensorRequest request = deleteSensorService.getDeleteSensorRequest(DataCenterUtils.readFromFile("d://Others/DeleteSensor.xml"));
//        deleteSensorService.deleteSensor(deleteSensorService.getProcedureId(request));
        Element element = deleteSensorService.getDeleteSensorResponse(deleteSensorService.deleteSensor(deleteSensorService.getProcedureId(request)));
        String ss = DataCenterUtils.element2String(element);

        System.out.println("");
    }

}
