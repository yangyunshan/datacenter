package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.service.sos.DeleteSensorService;
import com.sensorweb.datacenter.service.sos.DescribeSensorService;
import com.sensorweb.datacenter.service.sos.InsertSensorService;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.vast.ows.OWSException;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.ows.swe.DeleteSensorRequest;
import org.vast.ows.swe.DescribeSensorRequest;
import org.w3c.dom.Element;

import java.io.IOException;

@Controller
@RequestMapping(path = "/sensor")
public class SensorController implements DataCenterConstant {

    @Autowired
    private InsertSensorService insertSensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Autowired
    private DeleteSensorService deleteSensorService;

    private static Logger LOGGER = LoggerFactory.getLogger(HdfsController.class);

    /**
     * 批量注册传感器/procedure
     * @param model
     * @param files
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/registry", method = RequestMethod.POST)
    public String insertSensor(Model model, @RequestParam("files") MultipartFile[] files) throws Exception {
        if (files!=null && files.length>0) {
            for (int i=0; i< files.length; i++) {
                MultipartFile file = files[i];
                String temp = DataCenterUtils.readFromMultipartFile(file);
                String content = temp.substring(temp.indexOf("<sml"));
                InsertSensorRequest request = insertSensorService.getInsertSensorRequest(DataCenterConstant.INSERT_SENSOR_PREFIX + content + DataCenterConstant.INSERT_SENSOR_SUFFIX);
                insertSensorService.insertSensor(request);
            }
        }

        return "index";
    }

    @RequestMapping(path = "/describeSensor", method = RequestMethod.POST)
    public String describeSensor(Model model, String requestContent) {
        String result = null;
        try {
            DescribeSensorRequest request = describeSensorService.getDescribeSensorRequest(requestContent);
            result = describeSensorService.getDescribeSensorResponse(request.getProcedureID(), request.getFormat());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!StringUtils.isBlank(result)) {
            model.addAttribute("DescribeSensorResponse", result);
        }
        model.addAttribute("DescribeSensorRequest", requestContent);
        model.addAttribute("tag","DescribeSensor");

        return "index";
    }

    @RequestMapping(path = "/deleteSensor", method = RequestMethod.POST)
    public String deleteSensor(Model model, String requestContent) {
        String result = null;
        try {
            DeleteSensorRequest request = deleteSensorService.getDeleteSensorRequest(requestContent);
            String procedureId = deleteSensorService.getProcedureId(request);
            Element element = deleteSensorService.getDeleteSensorResponse(deleteSensorService.deleteSensor(procedureId));
            result = DataCenterUtils.element2String(element);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!StringUtils.isBlank(result)) {

            model.addAttribute("DeleteSensorResponse", result);

        }
        model.addAttribute("DeleteSensorRequest", requestContent);
        model.addAttribute("tag","DeleteSensor");

        return "index";
    }


}
