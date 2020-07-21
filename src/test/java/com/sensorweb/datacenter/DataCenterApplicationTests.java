package com.sensorweb.datacenter;

import com.sensorweb.datacenter.service.sos.GetCapabilitiesService;
import com.sensorweb.datacenter.service.sos.GetObservationService;
import com.sensorweb.datacenter.service.sos.InsertObservationService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.vast.ows.GetCapabilitiesRequest;
import org.vast.ows.OWSException;
import org.vast.ows.OWSServiceCapabilities;
import org.vast.ows.OWSUtils;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.GetObservationResponse;
import org.vast.ows.sos.GetObservationResponseWriter;
import org.vast.ows.sos.InsertObservationRequest;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;
import org.vast.xml.XMLWriterException;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootTest
class DataCenterApplicationTests {

    @Autowired
    private GetCapabilitiesService service;
    @Autowired
    private GetObservationService observationService;

    @Test
    public void test01() throws OWSException, DOMHelperException, XMLReaderException {
        GetCapabilitiesRequest request = service.getCapabilitiesRequest(DataCenterUtils.readFromFile("d://Others/GetCapabilitiesRequest.xml"));

        OWSUtils.loadRegistry();
        OWSServiceCapabilities response = service.getOWSServiceCapabilities();

        service.getCapabilitiesDocument(response);
        System.out.println(request);
    }

}
