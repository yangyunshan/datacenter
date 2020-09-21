package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.dao.ObservationMapper;
import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.service.sos.GetObservationService;
import com.sensorweb.datacenter.service.sos.InsertObservationService;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.vast.ogc.om.IObservation;
import org.vast.ows.OWSException;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.InsertObservationRequest;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequestMapping(path = "/observation")
public class ObservationController implements DataCenterConstant {

    @Autowired
    private InsertObservationService insertObservationService;

    @Autowired
    private GetObservationService getObservationService;

    @RequestMapping(path = "/insertObservation", method = RequestMethod.POST)
    public String insertObservation(Model model, String content) throws Exception {
        if (StringUtils.isBlank(content)) {
            model.addAttribute("msg", "failed");
        } else {
            String temp = INSERT_OBSERVATION_PREFIX + content + INSERT_OBSERVATION_SUFFIX;
            InsertObservationRequest request = insertObservationService.getInsertObservationRequest(temp);
            List<IObservation> observations = request.getObservations();
            if (observations!=null && observations.size()>0) {
                for (IObservation observation :observations) {
                    insertObservationService.insertObservation(observation);
                }
                model.addAttribute("msg", "success");
            }
        }

        return "html/observation";
    }

    @Autowired
    private ObservationMapper observationMapper;

    @RequestMapping(path = "/getObservationCountOfMonth", method = RequestMethod.GET)
    public String getObservation(Model model) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        List<Map<String, Integer>> res = new ArrayList<>();
        calendar.add(Calendar.MONTH, -12);
        for (int i=0; i<12; i++) {
            int month = calendar.get(Calendar.MONTH);
            String begin = DataCenterUtils.getFirstDay(calendar);
            String end = DataCenterUtils.getLastDay(calendar);

            Instant start = DataCenterUtils.string2Instant(begin);
            Instant stop = DataCenterUtils.string2Instant(end);
            int count = observationMapper.selectObservationsByDateTime(start, stop);

            Map<String, Integer> temp = new HashMap<>();
            temp.put(month+"", count);
            res.add(temp);
            calendar.add(Calendar.MONTH, 1);
        }
        model.addAttribute("observationCountOfMonth", res);
        return "html/observation";
    }
}
