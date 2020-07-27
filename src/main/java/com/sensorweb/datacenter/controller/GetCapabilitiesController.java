package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.Observation;
import com.sensorweb.datacenter.service.sos.GetCapabilitiesService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.vast.ows.GetCapabilitiesRequest;
import org.vast.ows.sos.GetObservationRequest;
import org.w3c.dom.Element;

import java.util.List;

@Controller
public class GetCapabilitiesController {

    @Autowired
    private GetCapabilitiesService service;

    @RequestMapping(path = "/getCapabilities", method = RequestMethod.POST)
    public String getCapabilities(Model model, String requestContent) {
        Element element = null;
        try {
            GetCapabilitiesRequest request = service.getGetCapabilitiesRequest(requestContent);
            element = service.getGetCapabilitiesResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (element!=null) {
            model.addAttribute("GetCapabilitiesRequest", requestContent);
            model.addAttribute("GetCapabilitiesResponse", DataCenterUtils.element2String(element));
        }

        return "index";
    }
}
