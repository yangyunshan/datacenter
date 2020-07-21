package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.FoiMapper;
import com.sensorweb.datacenter.dao.ObservationMapper;
import com.sensorweb.datacenter.dao.OfferingMapper;
import com.sensorweb.datacenter.entity.FeatureOfInterest;
import com.sensorweb.datacenter.entity.Observation;
import com.sensorweb.datacenter.entity.Offering;
import com.sensorweb.datacenter.entity.Phenomenon;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.ogc.gml.IGeoFeature;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.IProcedure;
import org.vast.ogc.om.OMUtils;
import org.vast.ows.OWSException;
import org.vast.ows.sos.*;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLWriterException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InsertObservationService {

    @Autowired
    private FoiMapper foiMapper;

    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private OfferingMapper offeringMapper;

    @Value("${datacenter.path.upload}")
    private String uploadPath;

    public Element getInsertObservationResponse(String obsId) throws OWSException {
        InsertObservationResponseWriterV20 writer = new InsertObservationResponseWriterV20();
        InsertObservationResponse response = new InsertObservationResponse();
        response.setObsId(obsId);
        DOMHelper domHelper = new DOMHelper();
        Element element = writer.buildXMLResponse(domHelper, response, "2.0");
        return element;
    }

    /**
     * 通过InsertObservation请求，将Observation插入到数据库中，返回插入成功的Observation的id
     * 注：理论上可以支持一次性插入多条观测数据，但因为后续生成Response文档的时候，参数为一个字符串而不是字符串数组，因此，此处应该每次插入一条观测数据
     * 如有一次性插入多条数据的需求，可以再改
     * @param request
     * @return
     * @throws ParseException
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String insertObservation(InsertObservationRequest request) throws ParseException, XMLWriterException {
        String obsId = "";
        Offering offering = getOffering(request);
        List<Observation> observations = new ArrayList<>();
        List<FeatureOfInterest> fois = new ArrayList<>();

        //确认文件是否保存，
        String filePath = getOM(getObservation(request));
        if (StringUtils.isBlank(filePath)) {
            return "";
        }

        List<IObservation> iObservations = getObservation(request);
        if (observations!=null && iObservations.size()>0) {
            for (IObservation observation : iObservations) {
                Observation temp = getObservation(observation);
                temp.setFilePath(filePath);
                observations.add(temp);
                FeatureOfInterest featureOfInterest = getFoi(observation);
                fois.add(featureOfInterest);
            }
        }

        if (observations!=null && observations.size()>0) {
            obsId = observations.get(0).getId();
            for (Observation observation : observations) {
                observationMapper.insertData(observation);
            }
        }

        if (fois!=null && fois.size()>0) {
            for (FeatureOfInterest featureOfInterest : fois) {
                foiMapper.insertData(featureOfInterest);
            }
        }

        offeringMapper.insertData(offering);

        return obsId;
    }

    /**
     * 解析InsertSensor请求文档，将其中的om内容写入文件保存，返回文件路径
     * @param observations
     * @return
     * @throws XMLWriterException
     */
    public String getOM(List<IObservation> observations) throws XMLWriterException {
        OMUtils omUtils = new OMUtils(OMUtils.V2_0);
        DOMHelper domHelper = new DOMHelper();
        Element element = omUtils.writeObservation(domHelper, observations.get(0), "2.0");
        String ss = DataCenterUtils.element2String(element);

        //写入文件
        String fileName = DataCenterUtils.generateUUID() + ".xml";
        try {
            DataCenterUtils.write2File(uploadPath + "/om/" + fileName, DataCenterUtils.element2String(element));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadPath + "/om/" + fileName;
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
        InsertObservationRequest request = reader.readXMLQuery(domHelper, domHelper.getRootElement());

//        Element element = domHelper.getElement("observation/OM_Observation");
//        String str = DataCenterUtils.element2String(element);
        return request;
    }

    /**
     * 解析InsertObservation请求对象，获取Observation集合
     * @param request
     * @return
     */
    public List<IObservation> getObservation(InsertObservationRequest request) {
        if (request==null) {
            return null;
        } else {
            return request.getObservations();
        }
    }

    /**
     * 解析InsertObservation请求对象，获取并封装成Offering对象
     * @param request
     * @return
     */
    public Offering getOffering(InsertObservationRequest request) {
        Offering offering = new Offering();

        if (request!=null) {
            offering.setId(request.getOffering());
        }
        return offering;
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
    public Observation getObservation(IObservation iObservation) throws ParseException {
        Observation observation = new Observation();
        if (iObservation!=null) {
            observation.setId(iObservation.getUniqueIdentifier());
            observation.setFoiId(iObservation.getFeatureOfInterest().getUniqueIdentifier());
            observation.setPhenomenonId(iObservation.getUniqueIdentifier());

            IProcedure procedure = iObservation.getProcedure();
            observation.setProcedureId(iObservation.getProcedure().getUniqueIdentifier());
            Instant time = iObservation.getResultTime();
            observation.setTime(DataCenterUtils.instant2LocalDateTime(iObservation.getResultTime()));
            observation.setValue(iObservation.getResult().getData().getStringValue());
        }
        return observation;
    }

//    public Phenomenon getPhenomenon(IObservation iObservation) {
//        Phenomenon phenomenon = null;
//        if (iObservation!=null) {
//            iObservation.getPhenomenonTime().
//        }
//        return phenomenon;
//    }


}
