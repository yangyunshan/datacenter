package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.service.sos.GetObservationService;
import com.sensorweb.datacenter.service.sos.InsertObservationService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.InsertObservationRequest;
import org.w3c.dom.Element;

import java.util.List;

@Controller
@RequestMapping(path = "/observation")
public class ObservationController {

    @Autowired
    private InsertObservationService insertObservationService;

    @Autowired
    private GetObservationService getObservationService;

    @RequestMapping(path = "/insertObservation", method = RequestMethod.POST)
    public String insertObservation(Model model, String requestContent) {
        return "";
    }

    @RequestMapping(path = "/getObservation", method = RequestMethod.POST)
    public String getObservation(Model model, String requestContent) {
        Element element = null;
        try {
            GetObservationRequest request = getObservationService.getObservationRequest(requestContent);
            List<Observation> observations = getObservationService.getObservationContent(request);

            if (observations!=null && observations.size()>0) {
                for (Observation observation : observations) {
                    element = getObservationService.getObservationResponse(observation.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (element!=null) {
            model.addAttribute("GetObservationResponse", DataCenterUtils.element2String(element));
        }
        model.addAttribute("GetObservationRequest", requestContent);
        model.addAttribute("tag", "GetObservation");

        return "index";
    }
}
