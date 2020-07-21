package com.sensorweb.datacenter.service.sos;

import org.springframework.stereotype.Service;
import org.vast.ows.*;
import org.vast.ows.sos.SOSCapabilitiesWriterV20;
import org.vast.ows.sos.SOSUtils;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;

@Service
public class GetCapabilitiesService {

    /**
     * 解析GetCapabilitiesRequest请求，获取请求参数，并封装成GetCapabilitiesRequest对象返回
     * @param requestContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     */
    public GetCapabilitiesRequest getCapabilitiesRequest(String requestContent) throws DOMHelperException, OWSException {
        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()), false);
        GetCapabilitiesReader reader = new GetCapabilitiesReader();

        return reader.readXMLQuery(domHelper, domHelper.getRootElement());
    }

    public void getCapabilitiesDocument(OWSServiceCapabilities response) throws OWSException {
        SOSCapabilitiesWriterV20 writer = new SOSCapabilitiesWriterV20();
        DOMHelper domHelper = new DOMHelper();

        Element element = writer.buildXMLResponse(domHelper, response, "2.0");
        System.out.println("!!!");
    }

    public OWSServiceCapabilities getOWSServiceCapabilities() {
        OWSServiceCapabilities response = new OWSServiceCapabilities();

        response.setFees("null");
        response.setService("SOS");
        response.setAccessConstraints("unknown constraints");

        return response;
    }
}
