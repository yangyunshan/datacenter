package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.laads.Entry;
import com.sensorweb.datacenter.service.LAADSService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LAADSController {
    @Autowired
    private LAADSService laadsService;

    @GetMapping("/modis")
    public List<Entry> getModisInfo(String startTime, String stopTime, String bbox) {
        List<Entry> entries = laadsService.getEntryByConditions(DataCenterUtils.string2Instant(startTime+" 00:00:00"), DataCenterUtils.string2Instant(stopTime+" 00:00:00"), bbox);
        return entries;
    }

    @GetMapping("/getModis")
    public List<Entry> getModisInfo1(String startTime, String stopTime, String bbox) {
        List<Entry> entries = new ArrayList<>();
        try {
            entries = laadsService.getEntryByConditions2(startTime, stopTime, bbox);
            return entries;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }
}
