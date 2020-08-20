package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.FoiMapper;
import com.sensorweb.datacenter.dao.ObservationMapper;
import com.sensorweb.datacenter.entity.sos.FeatureOfInterest;
import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.ogc.gml.IGeoFeature;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.OMUtils;
import org.vast.ows.OWSException;
import org.vast.ows.sos.*;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLWriterException;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsertObservationService {

    @Autowired
    private FoiMapper foiMapper;

    @Autowired
    private ObservationMapper observationMapper;

    private String offeringId;

    /**
     * 根据返回的插入成功的观测id，自动生成InsertObservationResponse
     * @param obsIds
     * @return
     * @throws OWSException
     */
    public Element getInsertObservationResponse(List<String> obsIds) throws OWSException {
        InsertObservationResponseWriterV20 writer = new InsertObservationResponseWriterV20();
        InsertObservationResponse response = new InsertObservationResponse();
        response.setObsId(DataCenterUtils.list2String(obsIds));
        DOMHelper domHelper = new DOMHelper();

        return writer.buildXMLResponse(domHelper, response, "2.0");
    }

    /**
     * 通过InsertObservation请求，将Observation插入到数据库中，返回插入成功的Observation的id
     * @param request
     * @return
     * @throws ParseException
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> insertObservation(InsertObservationRequest request) throws ParseException, XMLWriterException {
        List<Observation> observations = new ArrayList<>();
        List<FeatureOfInterest> fois = new ArrayList<>();

        List<IObservation> iObservations = getObservation(request);
        if (iObservations!=null && iObservations.size()>0) {
            for (IObservation iObservation : iObservations) {
                Observation temp = getObservation(iObservation);
                observations.add(temp);
                FeatureOfInterest featureOfInterest = getFoi(iObservation);
                fois.add(featureOfInterest);
            }
        }

        //定义一个list存储插入的obs
        List<String> obsIds = new ArrayList<>();
        //插入观测数据
        if (observations.size()>0) {
            for (Observation observation : observations) {
                observationMapper.insertData(observation);
                obsIds.add(observation.getId());
            }
        }

        //将FOI数据写入数据库
        if (fois.size()>0) {
            for (FeatureOfInterest featureOfInterest : fois) {
                foiMapper.insertData(featureOfInterest);
            }
        }

        return obsIds;
    }

    /**
     * 解析InsertObservation请求文档，将其中的om内容以字符串形式返回
     * @param observation
     * @return
     * @throws XMLWriterException
     */
    public String getOM(IObservation observation) throws XMLWriterException {
        OMUtils omUtils = new OMUtils(OMUtils.V2_0);
        DOMHelper domHelper = new DOMHelper();
        Element element = omUtils.writeObservation(domHelper, observation, "2.0");

        return DataCenterUtils.element2String(element);
    }

    /**
     * 解析InsertObservation请求字符串信息，返回请求对象InsertObservationRequest
     * @param requestContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     */
    public InsertObservationRequest getInsertObservationRequest(String requestContent) throws DOMHelperException, OWSException {
        if (StringUtils.isBlank(requestContent)) {
            return null;
        }
        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()), false);
        InsertObservationReaderV20 reader = new InsertObservationReaderV20();

        return reader.readXMLQuery(domHelper, domHelper.getRootElement());
    }

    /**
     * 解析InsertObservationRequestion请求对象，获取Observation集合
     * @param request
     * @return
     */
    public List<IObservation> getObservation(InsertObservationRequest request) {
        if (request==null) {
            return null;
        } else {
            offeringId = request.getOffering();
            return request.getObservations();
        }
    }

    /**
     * 解析IObservation对象，获取该Observation中的FeatureOfInterest
     * @param
     * @return
     */
    public FeatureOfInterest getFoi(IObservation iObservation) {
        FeatureOfInterest foi = new FeatureOfInterest();
        if (iObservation!=null) {
            IGeoFeature geoFeature = iObservation.getFeatureOfInterest();
            if (geoFeature!=null) {
                foi.setId(geoFeature.getUniqueIdentifier());
                foi.setName(geoFeature.getName());
                foi.setDescription(geoFeature.getDescription());
                foi.setGeom(geoFeature.getGeometry().toString());
            }
        }
        return foi;
    }

    /**
     * 解析IObservation对象，获取观测结果
     * @param iObservation
     * @return
     * @throws ParseException
     */
    public Observation getObservation(IObservation iObservation) throws ParseException, XMLWriterException {
        Observation observation = new Observation();
        if (iObservation!=null) {
            observation.setId(iObservation.getUniqueIdentifier());
            observation.setDescription(iObservation.getDescription());
            observation.setFoiId(iObservation.getFeatureOfInterest().getUniqueIdentifier());
            observation.setProcedureId(iObservation.getProcedure().getUniqueIdentifier());
            observation.setOfferingId(offeringId);
            observation.setTime(iObservation.getResultTime());
            observation.setValue(getOM(iObservation));
            observation.setObservedProperty(iObservation.getObservedProperty().getHref());
        }
        return observation;
    }
}
