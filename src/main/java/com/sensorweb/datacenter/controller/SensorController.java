package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.Procedure;
import com.sensorweb.datacenter.service.HandleSensorMLService;
import com.sensorweb.datacenter.service.QueryDataService;
import com.sensorweb.datacenter.service.sos.DescribeSensorService;
import com.sensorweb.datacenter.service.sos.InsertSensorService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.sensorml.v20.Mode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.ows.swe.DescribeSensorRequest;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping(path = "/sensor")
public class SensorController {

    @Autowired
    private InsertSensorService insertSensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    private static Logger LOGGER = LoggerFactory.getLogger(HdfsController.class);

    @RequestMapping(path = "/insertSensor", method = RequestMethod.POST)
    public String insertSensor(Model model, String requestContent) {
        Element element = null;
        try {
            InsertSensorRequest request = insertSensorService.getInsertSensorRequest(requestContent);
            String[] result = insertSensorService.insertSensor(request);

            element = insertSensorService.getInsertSensorResponse(result[0], result[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (element!=null) {
            model.addAttribute("InsertSensorRequest", requestContent);
            model.addAttribute("InsertSensorResponse", DataCenterUtils.element2String(element));
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
            model.addAttribute("DescribeSensorRequest", requestContent);
            model.addAttribute("DescribeSensorResponse", result);
        }

        return "index";
    }


}
