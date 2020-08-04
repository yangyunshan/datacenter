package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.FoiMapper;
import com.sensorweb.datacenter.dao.ObservationMapper;
import com.sensorweb.datacenter.entity.sos.FeatureOfInterest;
import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.OMUtils;
import org.vast.ows.OWSException;
import org.vast.ows.sos.GetObservationReaderV20;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.GetObservationResponse;
import org.vast.ows.sos.GetObservationResponseWriter;
import org.vast.util.Bbox;
import org.vast.util.TimeExtent;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class GetObservationService {

    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private FoiMapper foiMapper;

    /**
     * 根据查询获得的O&M数据内容，封装成Response，并以Element的形式返回
     * @param omContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     * @throws XMLReaderException
     */
    public Element getObservationResponse(String omContent) throws DOMHelperException, OWSException, XMLReaderException {
        GetObservationResponse response = new GetObservationResponse();
        OMUtils omUtils = new OMUtils(OMUtils.V2_0);
        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(omContent.getBytes()), false);
        GetObservationResponseWriter writer = new GetObservationResponseWriter();
        IObservation observation = omUtils.readObservation(domHelper, domHelper.getRootElement());
        response.setObservation(observation);

        return writer.buildXMLResponse(domHelper, response, "2.0");
    }

    /**
     * 根据查询请求查询结果，目前查询条件还不完善，仅支持单表查询
     * @param request
     * @return
     */
    public List<Observation> getObservationContent(GetObservationRequest request) {
        List<Observation> result = new ArrayList<>();

        Set<String> procedureIds = getProcedureId(request);
        Set<String> offerings = getOffering(request);
        Set<String> observedProperties = getObservable(request); //暂不考虑
        Set<String> fois = getFoi(request);
        Instant[] temporal = getTemporalFilter(request);
        double[] spatial = getSpatialFilter(request);

        //获得满足offering、procedureId、observedProperties、foi、temporal条件的observation
        List<Observation> observations = observationMapper.selectObservationsByConditions(offerings, procedureIds, fois, observedProperties,
                temporal[0], temporal[1]);
        //从上面的查询结果中进一步筛选出满足空间条件的结果
        if (observations!=null && observations.size()>0 && !(spatial[0]==0.0 && spatial[1]==0.0 && spatial[2]==0.0) && spatial[3]==0.0) {
            for (Observation temp : observations) {
                //将空间参数转为wkt字符串
                String spatialCondition = DataCenterUtils.coodinate2Wkt(spatial[0], spatial[1], spatial[2], spatial[3]);
                //查询此观测结果对应的foi
                boolean flag = foiMapper.selectByIdAndGeom(temp.getFoiId(), spatialCondition);
                if (!flag) {
                    observations.remove(temp);
                }
            }
        }

        return observations;
    }

    /**
     * 解析GetObservation请求字符串，得到GetObservationRequest对象，并返回
     * @param requestContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     */
    public GetObservationRequest getObservationRequest(String requestContent) throws DOMHelperException, OWSException {
        if (StringUtils.isBlank(requestContent)) {
            return null;
        }
        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()), false);
        GetObservationReaderV20 reader = new GetObservationReaderV20();
        GetObservationRequest request = reader.readXMLQuery(domHelper, domHelper.getRootElement());
        return request;
    }

    /**
     * 根据Request请求对象，获取请求中的Procedure参数，以集合的形式返回
     * @param request
     * @return
     */
    public Set<String> getProcedureId(GetObservationRequest request) {
        if (request!=null) {
            Set<String> procedureIds = request.getProcedures();
            if (!procedureIds.isEmpty()) {
                return procedureIds;
            }
        }
        return null;
    }

    /**
     * 根据Request请求对象，获取请求中的offeringId参数，以集合的形式返回
     * @param request
     * @return
     */
    public Set<String> getOffering(GetObservationRequest request) {
        if (request!=null) {
            Set<String> offerings = request.getOfferings();
            if (!offerings.isEmpty()) {
                return offerings;
            }
        }
        return null;
    }

    /**
     * 根据Request请求对象，获取请求中的observable参数，以集合的形式返回
     * @param request
     * @return
     */
    public Set<String> getObservable(GetObservationRequest request) {
        if (request!=null) {
            Set<String> observables = request.getObservables();
            if (!observables.isEmpty()) {
                return observables;
            }
        }
        return null;
    }

    /**
     * 根据Request请求对象，获取请求中的foiId参数，以集合的形式返回
     * @param request
     * @return
     */
    public Set<String> getFoi(GetObservationRequest request) {
        if (request!=null) {
            Set<String> fois = request.getFoiIDs();
            if (!fois.isEmpty()) {
                return fois;
            }
        }
        return null;
    }

    /**
     * 解析Request请求，获取请求中的时间过滤参数，以数组的形式保存开始时间和结束时间，并返回
     * @param request
     * @return
     */
    public Instant[] getTemporalFilter(GetObservationRequest request) {
        Instant[] temporal = new Instant[2];
        if (request!=null) {
            TimeExtent timeExtent = request.getTime();
            if (timeExtent!=null) {
                Instant begin = timeExtent.begin();
                Instant end = timeExtent.end();
                temporal[0] = begin;
                temporal[1] = end;
            }
        }
        return temporal;
    }

    /**
     * 解析Request请求，获取请求中的空间过滤参数，即uppercorner和lowercorner，以数组的形式存储其minX,minY,maxX,maxY数据，并返回
     * @param request
     * @return
     */
    public double[] getSpatialFilter(GetObservationRequest request) {
        double[] spatial = new double[4];
        if (request!=null) {
            Bbox bbox = request.getBbox();
            if (bbox!=null) {
                double minX = bbox.getMinX();
                double minY = bbox.getMinY();
                double maxX = bbox.getMaxX();
                double maxY = bbox.getMaxY();

                spatial[0] = minX;
                spatial[1] = minY;
                spatial[2] = maxX;
                spatial[3] = maxY;
            }
        }
        return spatial;
    }

    /**
     * 解析Request请求，获取请求中的ResponseFormat参数，以字符串的形式返回
     * @param request
     * @return
     */
    public String getFormat(GetObservationRequest request) {
        if (request!=null) {
            return request.getFormat();
        }
        return null;
    }
}
