package com.sensorweb.datacenter.webservice.impl;

import com.sensorweb.datacenter.entity.Observation;
import com.sensorweb.datacenter.entity.Procedure;
import com.sensorweb.datacenter.service.sos.DescribeSensorService;
import com.sensorweb.datacenter.service.sos.GetObservationService;
import com.sensorweb.datacenter.service.sos.InsertObservationService;
import com.sensorweb.datacenter.service.sos.InsertSensorService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import com.sensorweb.datacenter.webservice.SOSWebservice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.InsertObservationRequest;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.ows.sos.InsertSensorResponse;
import org.vast.ows.swe.DescribeSensorRequest;
import org.vast.ows.swe.InsertSensorResponseWriterV20;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;

import javax.jws.WebService;
import java.util.List;

@WebService(serviceName = "SOS", targetNamespace = "http://webservice.datacenter.sensorweb.com/",
        endpointInterface = "com.sensorweb.datacenter.webservice.SOSWebservice")
@Component
public class SOSWebServiceImpl implements SOSWebservice {

    @Autowired
    private InsertSensorService insertSensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Autowired
    private InsertObservationService insertObservationService;

    @Autowired
    private GetObservationService getObservationService;


    @Override
    public String InsertSensor(String requestContent) {
        String str = "";
        InsertSensorResponse response = new InsertSensorResponse();
        try {
            InsertSensorRequest request = insertSensorService.getInsertSensorRequest(requestContent);
            String[] result = insertSensorService.insertSensor(request);
            if (result!=null && result.length>0) {
                if (!StringUtils.isBlank(result[0])) {
                    response.setAssignedProcedureId(result[0]);
                }
                if (!StringUtils.isBlank(result[1])) {
                    response.setAssignedProcedureId(result[1]);
                }

                InsertSensorResponseWriterV20 responseWriter = new InsertSensorResponseWriterV20();
                DOMHelper domHelper = new DOMHelper();
                Element element = responseWriter.buildXMLResponse(domHelper, response, "2.0");
                str = DataCenterUtils.element2String(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    @Override
    public String DescribeSensor(String requestContent) {
        String str = "";

        try {
            DescribeSensorRequest request = describeSensorService.getDescribeSensorRequest(requestContent);
            String procedureId = describeSensorService.getProcedureId(request);
            String descriptionFormat = describeSensorService.getDescriptionFormat(request);
            str = describeSensorService.getDescribeSensorResponse(procedureId, descriptionFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    @Override
    public String InsertObservation(String requestContent) {
        String str = "";

        try {
            InsertObservationRequest request = insertObservationService.getInsertObservationRequest(requestContent);
            List<String> obsIds = insertObservationService.insertObservation(request);
            if (obsIds!=null && obsIds.size()>0) {
                Element element = insertObservationService.getInsertObservationResponse(obsIds);
                str = DataCenterUtils.element2String(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    @Override
    public String GetObservation(String requestContent) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            GetObservationRequest request = getObservationService.getObservationRequest(requestContent);
            List<Observation> observations = getObservationService.getObservationContent(request);
            if (observations!=null && observations.size()>0) {
                for (Observation observation : observations) {
                    Element element = getObservationService.getObservationResponse(observation.getValue());
                    stringBuilder.append(DataCenterUtils.element2String(element));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
