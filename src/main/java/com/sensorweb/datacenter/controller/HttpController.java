package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.meteorological.*;
import com.sensorweb.datacenter.service.HttpService;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.ParseAQIDocument;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.util.List;


@RestController
@RequestMapping(path = "/http")
public class HttpController implements DataCenterConstant {

//    @Autowired
//    private HttpService httpService;
//
//    @Autowired
//    private ParseAQIDocument parseAQIDocument;
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @RequestMapping(path = "getLast24HourData", method = RequestMethod.GET)
//    public String getLast24HourData() throws Exception {
//        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD;
//        String result = httpService.doGet(DataCenterConstant.GET_LAST_24_HOUR_DATA, param);
//
//        List<Object> objects = parseAQIDocument.parseXmlDoc(result);
//        if (objects == null || objects.size() <= 0) {
//            return "failure";
//        }
//        for (Object object : objects) {
//            HourAqi hourAqi = (HourAqi) object;
//            mongoTemplate.save(hourAqi);
//        }
//        return "success";
//    }
//
//    @RequestMapping(path = "getLast7DaysData", method = RequestMethod.GET)
//    public String getLast7DaysData() throws Exception {
//        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD;
//        String result = httpService.doGet(DataCenterConstant.GET_LAST_7_Days_DATA, param);
//
//        List<Object> objects = parseAQIDocument.parseXmlDoc(result);
//        if (objects == null || objects.size() <= 0) {
//            return "failure";
//        }
//        for (Object object : objects) {
//            DayAqi dayAqi = (DayAqi) object;
//            mongoTemplate.save(dayAqi);
//        }
//        return "success";
//    }
//
//    @RequestMapping(path = "getLastHoursData", method = RequestMethod.GET)
//    public String getLastHoursData(String date) throws Exception {
//        //为了防止出现400错误（url中存在特殊字符，如！ 空格等），需要首先将date中的空格进行格式化
//        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD + "&date=" + URLEncoder.encode(date, "utf-8");
//        String result = httpService.doGet(DataCenterConstant.GET_LAST_HOURS_DATA, param);
//
//        List<Object> objects = parseAQIDocument.parseXmlDoc(result);
//        if (objects == null || objects.size() <= 0) {
//            return "failure";
//        }
//        for (Object object : objects) {
//            HourAqi hourAqi = (HourAqi) object;
//            mongoTemplate.save(hourAqi);
//        }
//        return "success";
//    }
//
//    @RequestMapping(path = "getOriqinalDayilyData", method = RequestMethod.GET)
//    public String getOriqinalDayilyData(String beginTime, String endTime) throws Exception {
//        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD + "&beginTime=" + URLEncoder.encode(beginTime, "utf-8") + "&endTime=" + URLEncoder.encode(endTime, "utf-8");
//        String result = httpService.doGet(DataCenterConstant.GET_ORIGINAL_DAYILY_DATA, param);
//
//        List<Object> objects = parseAQIDocument.parseXmlDoc(result);
//        if (objects == null || objects.size() <= 0) {
//            return "failure";
//        }
//        for (Object object : objects) {
//            IDayAqi iDayAqi = (IDayAqi) object;
//            mongoTemplate.save(iDayAqi);
//        }
//        return "success";
//    }
//
//    @RequestMapping(path = "getOriginalHourlyData", method = RequestMethod.GET)
//    public String getOriginalHourlyData(String beginTime, String endTime) throws Exception {
//        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD + "&beginTime=" + URLEncoder.encode(beginTime, "utf-8") + "&endTime=" + URLEncoder.encode(endTime, "utf-8");
//        String result = httpService.doGet(DataCenterConstant.GET_ORIGINAL_HOURLY_DATA, param);
//
//        List<Object> objects = parseAQIDocument.parseXmlDoc(result);
//        if (objects == null || objects.size() <= 0) {
//            return "failure";
//        }
//        for (Object object : objects) {
//            IHourAqi iHourAqi = (IHourAqi) object;
//            mongoTemplate.save(iHourAqi);
//        }
//        return "success";
//    }
//
//    @RequestMapping(path = "getLast40DaysData", method = RequestMethod.GET)
//    public String getLast40DaysData(String date) throws Exception {
//        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD + "&date=" + URLEncoder.encode(date, "utf-8");
//        String result = httpService.doGet(DataCenterConstant.GET_LAST_40_DAYS_DATA, param);
//
//        List<Object> objects = parseAQIDocument.parseXmlDoc(result);
//        if (objects == null || objects.size() <= 0) {
//            return "failure";
//        }
//        for (Object object : objects) {
//            CDay cDay = (CDay) object;
//            mongoTemplate.save(cDay);
//        }
//        return "success";
//    }
//
//    @RequestMapping(path = "doPost", method = RequestMethod.POST)
//    public String doPost(String url, String param) {
//        String result = httpService.doPost(url, param);
//        return result;
//    }
}
