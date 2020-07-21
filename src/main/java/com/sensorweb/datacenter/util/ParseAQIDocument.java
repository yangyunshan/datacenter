package com.sensorweb.datacenter.util;

import com.sensorweb.datacenter.entity.meteorological.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @description:解析湖北省气象中心AQIxml文档
 * @Author: yangyunshan
 * @Date: 2020/6/12 3:25 下午
 * @Version: 1.0
 */
@Component
public class ParseAQIDocument {

    public List<Object> parseXmlDoc(String document) throws Exception {
        //读取xml文档，返回Document对象
        Document xmlContent = DocumentHelper.parseText(document);
        Element root = xmlContent.getRootElement();
        String type = root.getName().substring(root.getName().lastIndexOf("_")+1);
        List<Object> objects = new ArrayList<>();
        //判断返回数据类型，选择对应的数据模型
        switch (type) {
            case "HourAqiModel":{
                HourAqi hourAqi = new HourAqi();
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    for (Iterator j = element.elementIterator(); j.hasNext();) {
                        Element attribute = (Element) j.next();
                        if (attribute.getName().equals("UniqueCode"))
                            hourAqi.setUniqueCode(attribute.getText());
                        if (attribute.getName().equals("QueryTime")) {
                            hourAqi.setQueryTime(stringToDate(attribute.getText()));}
                        if (attribute.getName().equals("StationName"))
                            hourAqi.setStationName(attribute.getText());
                        if (attribute.getName().equals("PM25OneHour"))
                            hourAqi.setPm25OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10OneHour"))
                            hourAqi.setPm10OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("SO2OneHour"))
                            hourAqi.setSo2OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("NO2OneHour"))
                            hourAqi.setNo2OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("COOneHour"))
                            hourAqi.setCoOneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3OneHour"))
                            hourAqi.setO3OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("AQI"))
                            hourAqi.setAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PrimaryEP"))
                            hourAqi.setPrimaryEP(attribute.getText());
                        if (attribute.getName().equals("AQDegree"))
                            hourAqi.setAqDegree(attribute.getText());
                        if (attribute.getName().equals("AQType"))
                            hourAqi.setAqType(attribute.getText());
                    }
                    objects.add(hourAqi);
                }
                break;
            }
            case "DayAqiModel":{
                DayAqi dayAqi = new DayAqi();
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    for (Iterator j = element.elementIterator(); j.hasNext();) {
                        Element attribute = (Element) j.next();
                        if (attribute.getName().equals("UniqueCode"))
                            dayAqi.setUniqueCode(attribute.getText());
                        if (attribute.getName().equals("QueryTime"))
                            dayAqi.setQueryTime(stringToDate(attribute.getText()));
                        if (attribute.getName().equals("StationName"))
                            dayAqi.setStationName(attribute.getText());
                        if (attribute.getName().equals("PM25"))
                            dayAqi.setPm25(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10"))
                            dayAqi.setPm10(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("SO2"))
                            dayAqi.setSo2(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("NO2"))
                            dayAqi.setNo2(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("CO"))
                            dayAqi.setCo(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3EightHour"))
                            dayAqi.setO3EightHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("AQI"))
                            dayAqi.setAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PrimaryEP"))
                            dayAqi.setPrimaryEP(attribute.getText());
                        if (attribute.getName().equals("AQDegree"))
                            dayAqi.setAqDegree(attribute.getText());
                        if (attribute.getName().equals("AQType"))
                            dayAqi.setAqType(attribute.getText());

                        objects.add(dayAqi);
                    }
                }
                break;
            }
            case "IDayAqiModel":{
                IDayAqi iDayAqi = new IDayAqi();
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    for (Iterator j = element.elementIterator(); j.hasNext();) {
                        Element attribute = (Element) j.next();
                        if (attribute.getName().equals("UniqueCode"))
                            iDayAqi.setUniqueCode(attribute.getText());
                        if (attribute.getName().equals("QueryTime"))
                            iDayAqi.setQueryTime(stringToDate(attribute.getText()));
                        if (attribute.getName().equals("City"))
                            iDayAqi.setCity(attribute.getText());
                        if (attribute.getName().equals("StationName"))
                            iDayAqi.setStationName(attribute.getText());
                        if (attribute.getName().equals("PM25"))
                            iDayAqi.setPm25(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM25IAQI"))
                            iDayAqi.setPm25IAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10"))
                            iDayAqi.setPm10(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10IAQI"))
                            iDayAqi.setPm10IAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("SO2"))
                            iDayAqi.setSo2(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("SO2IAQI"))
                            iDayAqi.setSo2IAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("NO2"))
                            iDayAqi.setNo2(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("NO2IAQI"))
                            iDayAqi.setNo2IAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("CO"))
                            iDayAqi.setCo(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("COIAQI"))
                            iDayAqi.setCoIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3OneHour"))
                            iDayAqi.setO3OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3OneHourIAQI"))
                            iDayAqi.setO3OneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3EightHour"))
                            iDayAqi.setO3EightHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3EightHourIAQI"))
                            iDayAqi.setO3EightHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("AQI"))
                            iDayAqi.setAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PrimaryEP"))
                            iDayAqi.setPrimaryEP(attribute.getText());
                        if (attribute.getName().equals("AQDegree"))
                            iDayAqi.setAqDegree(attribute.getText());
                        if (attribute.getName().equals("AQType"))
                            iDayAqi.setAqType(attribute.getText());

                        objects.add(iDayAqi);
                    }
                }
                break;
            }
            case "IHourAqiModel":{
                IHourAqi iHourAqi = new IHourAqi();
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    for (Iterator j = element.elementIterator(); j.hasNext();) {
                        Element attribute = (Element) j.next();
                        if (attribute.getName().equals("UniqueCode"))
                            iHourAqi.setUniqueCode(attribute.getText());
                        if (attribute.getName().equals("QueryTime"))
                            iHourAqi.setQueryTime(stringToDate(attribute.getText()));
                        if (attribute.getName().equals("StationName"))
                            iHourAqi.setStationName(attribute.getText());
                        if (attribute.getName().equals("SO2OneHour"))
                            iHourAqi.setSo2OneHour((Double.parseDouble(attribute.getText())));
                        if (attribute.getName().equals("SO2OneHourIAQI"))
                            iHourAqi.setSo2OneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("NO2OneHour"))
                            iHourAqi.setNo2OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("NO2OneHourIAQI"))
                            iHourAqi.setNo2OneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10OneHour"))
                            iHourAqi.setPm10OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10OneHourIAQI"))
                            iHourAqi.setPm10OneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("COOneHour"))
                            iHourAqi.setCoOneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("COOneHourIAQI"))
                            iHourAqi.setCoOneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3OneHour"))
                            iHourAqi.setO3OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3OneHourIAQI"))
                            iHourAqi.setO3OneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM25OneHour"))
                            iHourAqi.setPm25OneHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM25OneHourIAQI"))
                            iHourAqi.setPm25OneHourIAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("AQI"))
                            iHourAqi.setAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PrimaryEP"))
                            iHourAqi.setPrimaryEP(attribute.getText());
                        if (attribute.getName().equals("AQDegree"))
                            iHourAqi.setAqDegree(attribute.getText());
                        if (attribute.getName().equals("AQType"))
                            iHourAqi.setAqType(attribute.getText());

                        objects.add(iHourAqi);
                    }
                }
                break;
            }
            case "CDayModel":{
                CDay cDay = new CDay();
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    for (Iterator j = element.elementIterator(); j.hasNext();) {
                        Element attribute = (Element) j.next();
                        if (attribute.getName().equals("SDateTime"))
                            cDay.setsDateTime(stringToDate(attribute.getText()));
                        if (attribute.getName().equals("UniqueCode"))
                            cDay.setUniqueCode(attribute.getText());
                        if (attribute.getName().equals("SO2"))
                            cDay.setSo2((Double.parseDouble(attribute.getText())));
                        if (attribute.getName().equals("NO2"))
                            cDay.setNo2(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM10"))
                            cDay.setPm10(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("CO"))
                            cDay.setCo(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("O3EightHour"))
                            cDay.setO3EightHour(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PM25"))
                            cDay.setPm25(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("AQI"))
                            cDay.setAqi(Double.parseDouble(attribute.getText()));
                        if (attribute.getName().equals("PrimaryEP"))
                            cDay.setPrimaryEP(attribute.getText());
                        if (attribute.getName().equals("AQType"))
                            cDay.setAqType(attribute.getText());

                        objects.add(cDay);
                    }
                }
                break;
            }
        }
        return objects;
    }

    public Date stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        java.sql.Timestamp timestamp = Timestamp.valueOf(localDateTime);
        return timestamp;
    }

    /**
     * 将字符串转为double类型，如若失败，则返回-1
     * @param str
     * @return
     */
    public double stringToDouble(String str) {
        return 3;
    }
}
