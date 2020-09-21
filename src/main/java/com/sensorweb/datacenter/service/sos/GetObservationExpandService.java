package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.ObservationMapper;
import com.sensorweb.datacenter.entity.sos.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.OMUtils;
import org.vast.ows.sos.GetObservationResponse;
import org.vast.ows.sos.GetObservationResponseWriter;
import org.vast.xml.DOMHelper;
import org.vast.xml.XMLReaderException;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;

@Service
public class GetObservationExpandService {
    @Autowired
    private GetObservationService service;

    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private InsertObservationService insertObservationService;

    /**
     * 根据日期查询观测数据
     * @param
     * @param
     * @return
     */
    public List<Observation> getObservationInfo(String procedureId) throws XMLReaderException {
        return observationMapper.selectObservationsBySensorId(procedureId);
    }
}
