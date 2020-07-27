package com.sensorweb.datacenter.service.sos;

import com.sensorweb.datacenter.dao.OfferingMapper;
import com.sensorweb.datacenter.dao.ProcedureMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.ows.OWSException;
import org.vast.ows.swe.DeleteSensorReaderV20;
import org.vast.ows.swe.DeleteSensorRequest;
import org.vast.ows.swe.DeleteSensorResponse;
import org.vast.ows.swe.DeleteSensorResponseWriterV20;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;

@Service
public class DeleteSensorService {

    @Autowired
    private ProcedureMapper procedureMapper;

    @Autowired
    private OfferingMapper offeringMapper;

    /**
     * 解析DeleteSensorRequest请求，获取DeleteSensorRequest对象
     * @param requestContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     */
    public DeleteSensorRequest getDeleteSensorRequest(String requestContent) throws DOMHelperException, OWSException {
        if (StringUtils.isBlank(requestContent)) {
            return null;
        }

        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()), false);
        DeleteSensorReaderV20 reader = new DeleteSensorReaderV20();
        return reader.readXMLQuery(domHelper, domHelper.getRootElement());
    }

    /**
     * 解析DeleteSensorRequest请求，获取请求中的procedureId参数
     * @param request
     * @return
     */
    public String getProcedureId(DeleteSensorRequest request) {
        if (request==null) {
            return null;
        }
        return request.getProcedureId();
    }

    /**
     * 根据请求中的待删除的procedureId参数，删除procedure以及相关联的offering
     * @param procedureId
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String deleteSensor(String procedureId) {
        if (StringUtils.isBlank(procedureId)) {
            return null;
        }

        //delete sensor
        procedureMapper.deleteById(procedureId);

        //delete offering
        offeringMapper.deleteByProcedure(procedureId);

        return procedureId;
    }

    /**
     * 删除成功，返回相应请求
     * @param procedureId 成功删除的procedure的id
     * @return
     * @throws OWSException
     */
    public Element getDeleteSensorResponse(String procedureId) throws OWSException {
        DeleteSensorResponse response = new DeleteSensorResponse("SOS");
        response.setDeletedProcedure(procedureId);
        DOMHelper domHelper = new DOMHelper();
        DeleteSensorResponseWriterV20 writer = new DeleteSensorResponseWriterV20();
        return writer.buildXMLResponse(domHelper, response, "2.0");
    }
}
