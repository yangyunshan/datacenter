package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.service.sos.DescribeSensorExpandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @RequestMapping(path = "index")
    public String jump2Index() {
        return "index";
    }

    @RequestMapping(path = "sensor")
    public String jump2service() {
        return "html/sensor";
    }

    @RequestMapping(path = "observation")
    public String jump2Observation() {
        return "html/observation";
    }

    @Autowired
    private DescribeSensorExpandService describeSensorExpandService;

    @RequestMapping(path = "getTOC", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getAllProcedureInfo(Model model) {
        Map<String, String> res = new HashMap<>();
        String info = describeSensorExpandService.getTOC();
        if (!StringUtils.isBlank(info)) {
            res.put("allProcedureInfo", info);
        } else {
            res.put("allProcedureInfo", null);
        }
        return res;
    }
}

