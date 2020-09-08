package com.sensorweb.datacenter.service;

import com.sensorweb.datacenter.entity.air.*;
import com.sensorweb.datacenter.entity.sos.FeatureOfInterest;
import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.service.sos.InsertObservationService;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import com.sensorweb.datacenter.util.SWEModelUtils;
import net.opengis.gml.v32.*;
import net.opengis.swe.v20.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vast.data.*;
import org.vast.ogc.def.DefinitionRef;
import org.vast.ogc.gml.FeatureRef;
import org.vast.ogc.gml.IGeoFeature;
import org.vast.ogc.om.*;
import org.vast.ogc.xlink.IXlinkReference;
import org.vast.ows.OWSException;
import org.vast.ows.sos.InsertObservationRequest;
import org.vast.ows.sos.SOSUtils;
import org.vast.util.TimeExtent;

import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Configuration
@EnableScheduling
public class AirService implements DataCenterConstant {

    @Autowired
    InsertObservationService service;

    /**
     * 每天执行一次接入数据
     * @throws Exception
     */
    @Scheduled(cron = "0 10 0 * * ?")//每天的0:10分执行一次
    public void insertDataByDay() throws Exception {
        insert24HoursData();
    }

    /**
     * 每周执行一次接入数据
     * @throws Exception
     */
    @Scheduled(cron = "0 10 0 ? * MON")//，每周一的0点10分执行一次
    public void insertDataByWeek() throws Exception {
        insert7DaysData();
    }

    /**
     * 接入最近24小时内的数据
     * @throws Exception
     */
    public void insert24HoursData() throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD;
        String document = doGet(DataCenterConstant.GET_LAST_24_HOUR_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 接入最近7天内的数据
     * @throws Exception
     */
    public void insert7DaysData() throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD;
        String document = doGet(DataCenterConstant.GET_LAST_7_Days_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 根据时间接入指定日期内的天数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     * @param beginTime/endTime格式: "2020-08-01 00:00:00", "2020-08-10 00:00:00"
     * @throws Exception
     */
    public void insertDayDataByDate(String beginTime, String endTime) throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD +
                "&beginTime=" + URLEncoder.encode(beginTime, "utf-8") +
                "&endTime=" + URLEncoder.encode(endTime, "utf-8");
        String document = doGet(DataCenterConstant.GET_ORIGINAL_DAYILY_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 根据时间接入指定日期内的小时数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     * @throws Exception
     */
    public void insertHourDataByDate(String beginTime, String endTime) throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD +
                "&beginTime=" + URLEncoder.encode(beginTime, "utf-8") +
                "&endTime=" + URLEncoder.encode(endTime, "utf-8");;
        String document = doGet(DataCenterConstant.GET_ORIGINAL_HOURLY_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    public String doGet(String url, String param) throws IOException {
        //打开postman
        //这一步相当于运行main方法。
        //创建request连接 3、填写url和请求方式
        HttpGet get = new HttpGet(url + "?" + param);
        //如果有参数添加参数 get请求不需要参数，省略
        CloseableHttpClient client = HttpClients.createDefault();
        //点击发送按钮，发送请求、获取响应报文
        CloseableHttpResponse response = client.execute(get);
        //格式化响应报文
        HttpEntity entity = response.getEntity();

        return EntityUtils.toString(entity);
    }

    /**
     * 向指定url发送post请求
     * @param url
     * @param param
     * @return
     */
    public String doPost(String url, String param) throws IOException {
        //打开postman
        //这一步相当于运行main方法。
        //创建request连接、填写url和请求方式
        HttpPost httpPost = new HttpPost(url);
        //额外设置Content-Type请求头
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        //如果有参数添加参数
        CloseableHttpClient client = HttpClients.createDefault();
        httpPost.setEntity(new StringEntity(param,"UTF-8"));
        //点击发送按钮，发送请求、获取响应报文
        CloseableHttpResponse response = client.execute(httpPost);
        //格式化响应报文
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * 获取IProcedure属性
     * @param o
     * @return
     */
    public IProcedure getProcedure(Object o) {
        IProcedure procedure = new ProcedureRef();
        String id = "";
        if (o instanceof AirQualityDay) {
           id = ((AirQualityDay) o).getUniqueCode();
        } else {
            id = ((AirQualityHour) o).getUniqueCode();
        }
        ((IXlinkReference)procedure).setHref(id);
        return procedure;
    }

    /**
     * 获取id属性
     * @param o
     * @return
     */
    public String getId(Object o) {
        String id = "";
        if (o instanceof AirQualityDay) {
            id = ((AirQualityDay) o).getUniqueCode();
        } else {
            id = ((AirQualityHour) o).getUniqueCode();
        }
        return id;
    }

    public DefinitionRef getObservedProperty(Object o) {
        DefinitionRef observedProperty = new DefinitionRef();
        String property = "";
        if (o instanceof AirQualityHour) {
            property = "AirQualityByHour";
        } else {
            property = "AirQualityByDay";
        }
        observedProperty.setHref(property);
        return observedProperty;
    }

    /**
     * 设置DataComponent属性，为Observation的Result
     *
     * @param object 数据模型为HourAqi、DayAqi、CDay、IDayAqi、IHourAqi
     * @return
     */
    public DataComponent setDataComponent(Object object) {
        SWEFactory factory = new SWEFactory();
        DataRecord dataRecord = factory.newDataRecord();
        writeResult(object, dataRecord);
        return dataRecord;
    }

    /**
     * 定义CDay数据模型字段
     * @param dataRecord
     */
    public void writeCDayElementType(DataRecord dataRecord, CDay cDay) {
        Text uniqueCode = new TextImpl();
        Time time = new TimeImpl();
        Quantity so2 = new QuantityImpl();
        Quantity no2 = new QuantityImpl();
        Quantity pm10 = new QuantityImpl();
        Quantity co = new QuantityImpl();
        Quantity o3EightHour = new QuantityImpl();
        Quantity pm25 = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        Text primaryEP = new TextImpl();
        Category aqType = new CategoryImpl();
        if (cDay!=null) {
            //set uniqueCode value
            SWEModelUtils.setText(uniqueCode, cDay.getUniqueCode(), "");
            uniqueCode.setValue(cDay.getUniqueCode());
            //set time value
            SWEModelUtils.setTime(time, cDay.getsDateTime(), "");
            //set so2 value
            SWEModelUtils.setQuantity(so2, cDay.getSo2(), "", "");
            //set no2 value
            SWEModelUtils.setQuantity(no2, cDay.getNo2(), "", "");
            //set pm10 value
            SWEModelUtils.setQuantity(pm10, cDay.getPm10(), "", "");
            //set CO value
            SWEModelUtils.setQuantity(co, cDay.getCo(), "", "");
            //set o3EightHour value
            SWEModelUtils.setQuantity(o3EightHour, cDay.getO3EightHour(), "", "");
            //set pm25 value
            SWEModelUtils.setQuantity(pm25, cDay.getPm25(), "", "");
            //set aqi value
            SWEModelUtils.setQuantity(aqi, cDay.getAqi(), "", "");
            //set primaryEP value
            SWEModelUtils.setText(primaryEP, cDay.getPrimaryEP(), "");
            //set aqType value
            SWEModelUtils.setCategory(aqType, cDay.getAqType(), "");
        }
        //add uniqueCode field
        uniqueCode.setDefinition("UniqueCode");
        dataRecord.addField("UniqueCode", uniqueCode);
        //add SDateTime field
        time.setDefinition("http://www.opengis.net/def/property/OGC/0/PhenomenonTime");
        dataRecord.addField("SDateTime", time);
        //add SO2 field
        so2.setDefinition("SO2");
        dataRecord.addField("SO2", so2);
        //add no2 field
        no2.setDefinition("NO2");
        dataRecord.addField("NO2", no2);
        //add pm10 field
        pm10.setDefinition("PM10");
        dataRecord.addField("PM10", pm10);
        //add co field
        co.setDefinition("CO");
        dataRecord.addField("CO", co);
        //add O3EightHour
        o3EightHour.setDefinition("O3EightHour");
        dataRecord.addField("O3EightHour", o3EightHour);
        //add pm25 field
        pm25.setDefinition("PM25");
        dataRecord.addField("PM25", pm25);
        //add aqi field
        aqi.setDefinition("AQI");
        dataRecord.addField("AQI", aqi);
        //add primaryEP field
        primaryEP.setDefinition("PrimaryEP");
        dataRecord.addField("PrimaryEP", primaryEP);
        //add aqType field
        aqType.setDefinition("AQType");
        dataRecord.addField("AQType", aqType);
    }

    /**
     * 定义DayAqi数据模型字段
     * @param dataRecord
     */
    public void writeDayAqiElementType(DataRecord dataRecord, DayAqi dayAqi) {
        Text stationName = new TextImpl();
        Text uniqueCode = new TextImpl();
        Time time = new TimeImpl();
        Quantity pm25 = new QuantityImpl();
        Quantity pm10 = new QuantityImpl();
        Quantity so2 = new QuantityImpl();
        Quantity no2 = new QuantityImpl();
        Quantity co = new QuantityImpl();
        Quantity O3EightHour = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        Text primaryEP = new TextImpl();
        Category aqDegree = new CategoryImpl();
        Category aqType = new CategoryImpl();
        if (dayAqi!=null) {
            //set stationName value
            SWEModelUtils.setText(stationName, dayAqi.getStationName(), "");
            //set uniqueCode value
            SWEModelUtils.setText(uniqueCode, dayAqi.getUniqueCode(), "");
            //set time value
            SWEModelUtils.setTime(time, dayAqi.getQueryTime(), "");
            //set pm25 value
            SWEModelUtils.setQuantity(pm25, dayAqi.getPm25(), "", "");
            //set pm10 value
            SWEModelUtils.setQuantity(pm10, dayAqi.getPm10(), "", "");
            //set so2 value
            SWEModelUtils.setQuantity(so2, dayAqi.getSo2(), "", "");
            //set no2 value
            SWEModelUtils.setQuantity(no2, dayAqi.getNo2(), "", "");
            //set co value
            SWEModelUtils.setQuantity(co, dayAqi.getCo(), "", "");
            //set O3EightHour value
            SWEModelUtils.setQuantity(O3EightHour, dayAqi.getO3EightHour(), "", "");
            //set aqi value
            SWEModelUtils.setQuantity(aqi, dayAqi.getAqi(), "", "");
            //set PrimaryEP value
            SWEModelUtils.setText(primaryEP, dayAqi.getPrimaryEP(), "");
            //set aqDegree value
            SWEModelUtils.setCategory(aqDegree, dayAqi.getAqDegree(), "");
            //set aqType value
            SWEModelUtils.setCategory(aqDegree, dayAqi.getAqType(), "");
        }
        //add StationName field
        stationName.setDefinition("StationName");
        dataRecord.addField("StationName", stationName);
        //add uniqueCode field
        uniqueCode.setDefinition("UniqueCode");
        dataRecord.addField("UniqueCode", uniqueCode);
        //add SDateTime field
        time.setDefinition("http://www.opengis.net/def/property/OGC/0/PhenomenonTime");
        dataRecord.addField("QueryTime", time);
        //add pm25 field
        pm25.setDefinition("PM25");
        dataRecord.addField("PM25", pm25);
        //add pm10 field
        pm10.setDefinition("PM10");
        dataRecord.addField("PM10", pm10);
        //add SO2 field
        so2.setDefinition("SO2");
        dataRecord.addField("SO2", so2);
        //add no2 field
        no2.setDefinition("NO2");
        dataRecord.addField("NO2", no2);
        //add co field
        co.setDefinition("CO");
        dataRecord.addField("CO", co);
        //add O3EightHour
        O3EightHour.setDefinition("O3EightHour");
        dataRecord.addField("O3EightHour", O3EightHour);
        //add aqi field
        aqi.setDefinition("AQI");
        dataRecord.addField("AQI", aqi);
        //add primaryEP field
        primaryEP.setDefinition("PrimaryEP");
        dataRecord.addField("PrimaryEP", primaryEP);
        //add aqType field
        aqDegree.setDefinition("AQDegree");
        dataRecord.addField("AQDegree", aqDegree);
        //add aqType field
        aqType.setDefinition("AQType");
        dataRecord.addField("AQType", aqType);
    }

    /**
     * 定义HourAqi数据模型字段
     * @param dataRecord
     */
    public void writeHourAqiElementType(DataRecord dataRecord, HourAqi hourAqi) {
        Text stationName = new TextImpl();
        Text uniqueCode = new TextImpl();
        Time time = new TimeImpl();
        Quantity pm25OneHour = new QuantityImpl();
        Quantity pm10OneHour = new QuantityImpl();
        Quantity so2OneHour = new QuantityImpl();
        Quantity no2OneHour = new QuantityImpl();
        Quantity coOneHour = new QuantityImpl();
        Quantity o3OneHour = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        Text primaryEP = new TextImpl();
        Category aqDegree = new CategoryImpl();
        Category aqType = new CategoryImpl();
        if (hourAqi!=null) {
            SWEModelUtils.setText(stationName, hourAqi.getStationName(), "");
            SWEModelUtils.setText(uniqueCode, hourAqi.getUniqueCode(), "");
            SWEModelUtils.setTime(time, hourAqi.getQueryTime(), "");
            SWEModelUtils.setQuantity(pm10OneHour, hourAqi.getPm10OneHour(), "", "");
            SWEModelUtils.setQuantity(pm25OneHour, hourAqi.getPm25OneHour(), "", "");
            SWEModelUtils.setQuantity(so2OneHour, hourAqi.getSo2OneHour(), "", "");
            SWEModelUtils.setQuantity(no2OneHour, hourAqi.getNo2OneHour(), "", "");
            SWEModelUtils.setQuantity(coOneHour, hourAqi.getCoOneHour(), "", "");
            SWEModelUtils.setQuantity(o3OneHour, hourAqi.getO3OneHour(), "", "");
            SWEModelUtils.setQuantity(aqi, hourAqi.getAqi(), "", "");
            SWEModelUtils.setText(primaryEP, hourAqi.getPrimaryEP(), "");
            SWEModelUtils.setCategory(aqDegree, hourAqi.getAqDegree(), "");
            SWEModelUtils.setCategory(aqType, hourAqi.getAqType(), "");
        }
        //add StationName field
        stationName.setDefinition("StationName");
        dataRecord.addField("StationName", stationName);
        //add uniqueCode field
        uniqueCode.setDefinition("UniqueCode");
        dataRecord.addField("UniqueCode", uniqueCode);
        //add SDateTime field
        time.setDefinition("http://www.opengis.net/def/property/OGC/0/PhenomenonTime");
        dataRecord.addField("QueryTime", time);
        //add pm25OneHour field
        pm25OneHour.setDefinition("Pm25OneHour");
        dataRecord.addField("Pm25OneHour", pm25OneHour);
        //add pm10OneHour field
        pm10OneHour.setDefinition("PM10OneHour");
        dataRecord.addField("PM10OneHour", pm10OneHour);
        //add SO2OneHour field
        so2OneHour.setDefinition("SO2OneHour");
        dataRecord.addField("SO2OneHour", so2OneHour);
        //add no2OneHour field
        no2OneHour.setDefinition("NO2OneHour");
        dataRecord.addField("NO2OneHour", no2OneHour);
        //add coOneHour field
        coOneHour.setDefinition("COOneHour");
        dataRecord.addField("COOneHour", coOneHour);
        //add O3OneHour
        o3OneHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3OneHour);
        //add aqi field
        aqi.setDefinition("AQI");
        dataRecord.addField("AQI", aqi);
        //add primaryEP field
        primaryEP.setDefinition("primaryEP");
        dataRecord.addField("primaryEP", primaryEP);
        //add aqType field
        aqDegree.setDefinition("AQDegree");
        dataRecord.addField("aqDegree", aqDegree);
        //add aqType field
        aqType.setDefinition("AQType");
        dataRecord.addField("aqType", aqType);
    }

    /**
     * 定义IDayAqi数据模型字段
     * @param dataRecord
     */
    public void writeIDayAqiElementType(DataRecord dataRecord, IDayAqi iDayAqi) {
        Text uniqueCode = new TextImpl();
        Time time = new TimeImpl();
        Text stationName = new TextImpl();
        Text city = new TextImpl();
        Quantity pm25 = new QuantityImpl();
        Quantity pm25IAqi = new QuantityImpl();
        Quantity pm10 = new QuantityImpl();
        Quantity pm10IAqi = new QuantityImpl();
        Quantity so2 = new QuantityImpl();
        Quantity so2IAqi = new QuantityImpl();
        Quantity no2 = new QuantityImpl();
        Quantity no2IAqi = new QuantityImpl();
        Quantity co = new QuantityImpl();
        Quantity coIAqi = new QuantityImpl();
        Quantity o3OneHour = new QuantityImpl();
        Quantity o3OneHourIAqi = new QuantityImpl();
        Quantity o3EightHour = new QuantityImpl();
        Quantity o3EightHourIAqi = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        Text primaryEP = new TextImpl();
        Category aqDegree = new CategoryImpl();
        Category aqType = new CategoryImpl();
        if (iDayAqi!=null) {
            SWEModelUtils.setText(uniqueCode, iDayAqi.getUniqueCode(), "");
            SWEModelUtils.setText(stationName, iDayAqi.getStationName(), "");
            SWEModelUtils.setText(city, iDayAqi.getCity(), "");
            SWEModelUtils.setTime(time,iDayAqi.getQueryTime(), "");
            SWEModelUtils.setQuantity(pm25, iDayAqi.getPm25(), "", "");
            SWEModelUtils.setQuantity(pm25IAqi, iDayAqi.getPm25IAqi(), "", "");
            SWEModelUtils.setQuantity(pm10, iDayAqi.getPm10(), "", "");
            SWEModelUtils.setQuantity(pm10IAqi, iDayAqi.getPm10IAqi(), "", "");
            SWEModelUtils.setQuantity(so2, iDayAqi.getSo2(), "", "");
            SWEModelUtils.setQuantity(so2IAqi, iDayAqi.getSo2IAqi(), "", "");
            SWEModelUtils.setQuantity(no2, iDayAqi.getNo2(), "", "");
            SWEModelUtils.setQuantity(no2IAqi, iDayAqi.getNo2IAqi(), "", "");
            SWEModelUtils.setQuantity(co, iDayAqi.getCo(), "", "");
            SWEModelUtils.setQuantity(coIAqi, iDayAqi.getCoIAqi(), "", "");
            SWEModelUtils.setQuantity(o3EightHour, iDayAqi.getO3EightHour(), "", "");
            SWEModelUtils.setQuantity(o3EightHourIAqi, iDayAqi.getO3EightHourIAqi(), "", "");
            SWEModelUtils.setQuantity(o3OneHour, iDayAqi.getO3OneHour(), "", "");
            SWEModelUtils.setQuantity(o3OneHourIAqi, iDayAqi.getO3OneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(aqi, iDayAqi.getAqi(), "", "");
            SWEModelUtils.setText(primaryEP, iDayAqi.getPrimaryEP(), "");
            SWEModelUtils.setCategory(aqDegree, iDayAqi.getAqDegree(), "");
            SWEModelUtils.setCategory(aqType, iDayAqi.getAqType(), "");

        }
        //add uniqueCode field
        uniqueCode.setDefinition("UniqueCode");
        dataRecord.addField("UniqueCode", uniqueCode);
        //add SDateTime field
        time.setDefinition("http://www.opengis.net/def/property/OGC/0/PhenomenonTime");
        dataRecord.addField("QueryTime", time);
        //add StationName field
        stationName.setDefinition("StationName");
        dataRecord.addField("StationName", stationName);
        //add city field
        city.setDefinition("City");
        dataRecord.addField("City", city);
        //add pm25 field
        pm25.setDefinition("Pm25");
        dataRecord.addField("Pm25", pm25);
        //add pm25IAqi field
        pm25IAqi.setDefinition("Pm25IAqi");
        dataRecord.addField("Pm25IAqi", pm25IAqi);
        //add pm10 field
        pm10.setDefinition("PM10");
        dataRecord.addField("PM10", pm10);
        //add pm10IAqi field
        pm10IAqi.setDefinition("PM10IAqi");
        dataRecord.addField("PM10IAqi", pm10IAqi);
        //add SO2 field
        so2.setDefinition("SO2");
        dataRecord.addField("SO2", so2);
        //add SO2IAqi field
        so2IAqi.setDefinition("SO2IAQI");
        dataRecord.addField("SO2IAQI", so2IAqi);
        //add no2 field
        no2.setDefinition("NO2");
        dataRecord.addField("NO2", no2);
        //add no2IAqi field
        no2IAqi.setDefinition("NO2IAQI");
        dataRecord.addField("NO2IAQI", no2IAqi);
        //add co field
        co.setDefinition("CO");
        dataRecord.addField("CO", co);
        //add coIAqi field
        coIAqi.setDefinition("COIAQI");
        dataRecord.addField("COIAQI", coIAqi);
        //add O3OneHour field
        o3OneHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3OneHour);
        //add O3OneHourIAqi field
        o3OneHourIAqi.setDefinition("O3OneHourIAQI");
        dataRecord.addField("O3OneHourIAQI", o3OneHourIAqi);
        //add O3OneHour field
        o3EightHour.setDefinition("O3EightHour");
        dataRecord.addField("O3EightHour", o3EightHour);
        //add O3EightHourIAqi field
        o3EightHourIAqi.setDefinition("O3EightHourIAQI");
        dataRecord.addField("O3EightHourIAQI", o3EightHourIAqi);
        //add aqi field
        aqi.setDefinition("AQI");
        dataRecord.addField("AQI", aqi);
        //add primaryEP field
        primaryEP.setDefinition("primaryEP");
        dataRecord.addField("primaryEP", primaryEP);
        //add aqType field
        aqDegree.setDefinition("AQDegree");
        dataRecord.addField("aqDegree", aqDegree);
        //add aqType field
        aqType.setDefinition("AQType");
        dataRecord.addField("aqType", aqType);
    }

    /**
     * 定义IHourAqi数据模型字段
     * @param dataRecord
     * @param iHourAqi 为空时不写入数据，进写入字段信息
     */
    public void writeIHourAqiElementType(DataRecord dataRecord, IHourAqi iHourAqi) {
        Text uniqueCode = new TextImpl();
        Time time = new TimeImpl();
        Text stationName = new TextImpl();
        Quantity pm25OneHour = new QuantityImpl();
        Quantity pm25OneHourIAqi = new QuantityImpl();
        Quantity pm10OneHour = new QuantityImpl();
        Quantity pm10OneHourIAqi = new QuantityImpl();
        Quantity so2OneHour = new QuantityImpl();
        Quantity so2OneHourIAqi = new QuantityImpl();
        Quantity no2OneHour = new QuantityImpl();
        Quantity no2OneHourIAqi = new QuantityImpl();
        Quantity coOneHour = new QuantityImpl();
        Quantity coOneHourIAqi = new QuantityImpl();
        Quantity o3OneHour = new QuantityImpl();
        Quantity o3OneHourIAqi = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        Text primaryEP = new TextImpl();
        Category aqDegree = new CategoryImpl();
        Category aqType = new CategoryImpl();
        if (iHourAqi!=null) {
            SWEModelUtils.setText(uniqueCode, iHourAqi.getUniqueCode(), "");
            SWEModelUtils.setText(stationName, iHourAqi.getStationName(), "");
            SWEModelUtils.setTime(time, iHourAqi.getQueryTime(), "");
            SWEModelUtils.setQuantity(pm25OneHour, iHourAqi.getPm25OneHour(), "" ,"");
            SWEModelUtils.setQuantity(pm25OneHourIAqi, iHourAqi.getPm25OneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(pm10OneHour, iHourAqi.getPm10OneHour(), "", "");
            SWEModelUtils.setQuantity(pm10OneHourIAqi, iHourAqi.getPm10OneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(so2OneHour, iHourAqi.getSo2OneHour(), "", "");
            SWEModelUtils.setQuantity(so2OneHourIAqi, iHourAqi.getSo2OneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(no2OneHour, iHourAqi.getNo2OneHour(), "", "");
            SWEModelUtils.setQuantity(no2OneHourIAqi, iHourAqi.getNo2OneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(coOneHour, iHourAqi.getCoOneHour(), "", "");
            SWEModelUtils.setQuantity(coOneHourIAqi, iHourAqi.getCoOneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(o3OneHour, iHourAqi.getO3OneHour(), "", "");
            SWEModelUtils.setQuantity(o3OneHourIAqi, iHourAqi.getO3OneHourIAqi(), "", "");
            SWEModelUtils.setQuantity(aqi, iHourAqi.getAqi(), "", "");
            SWEModelUtils.setText(primaryEP, iHourAqi.getPrimaryEP(), "");
            SWEModelUtils.setCategory(aqDegree, iHourAqi.getAqDegree(), "");
            SWEModelUtils.setCategory(aqType, iHourAqi.getAqType(), "");
        }
        //add uniqueCode field
        uniqueCode.setDefinition("UniqueCode");
        dataRecord.addField("UniqueCode", uniqueCode);
        //add SDateTime field
        time.setDefinition("http://www.opengis.net/def/property/OGC/0/PhenomenonTime");
        dataRecord.addField("QueryTime", time);
        //add StationName field
        stationName.setDefinition("StationName");
        dataRecord.addField("StationName", stationName);
        //add pm25OneHour field
        pm25OneHour.setDefinition("Pm25OneHour");
        dataRecord.addField("Pm25OneHour", pm25OneHour);
        //add pm25OneHourIAqi field
        pm25OneHourIAqi.setDefinition("Pm25OneHourIAQI");
        dataRecord.addField("Pm25OneHourIAQI", pm25OneHourIAqi);
        //add pm10OneHour field
        pm10OneHour.setDefinition("PM10OneHour");
        dataRecord.addField("PM10OneHour", pm10OneHour);
        //add pm10OneHourIAqi field
        pm10OneHourIAqi.setDefinition("PM10OneHourIAQI");
        dataRecord.addField("PM10OneHourIAQI", pm10OneHourIAqi);
        //add SO2OneHour field
        so2OneHour.setDefinition("SO2OneHour");
        dataRecord.addField("SO2OneHour", so2OneHour);
        //add SO2OneHourIAqi field
        so2OneHourIAqi.setDefinition("SO2OneHourIAQI");
        dataRecord.addField("SO2OneHourIAQI", so2OneHourIAqi);
        //add no2OneHour field
        no2OneHour.setDefinition("NO2OneHour");
        dataRecord.addField("NO2OneHour", no2OneHour);
        //add no2OneHourIAqi field
        no2OneHourIAqi.setDefinition("NO2OneHourIAQI");
        dataRecord.addField("NO2OneHourIAQI", no2OneHourIAqi);
        //add coOneHour field
        coOneHour.setDefinition("COOneHour");
        dataRecord.addField("COOneHour", coOneHour);
        //add coOneHourIAqi field
        coOneHourIAqi.setDefinition("COOneHourIAQI");
        dataRecord.addField("COOneHourIAQI", coOneHourIAqi);
        //add O3OneHour
        o3OneHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3OneHour);
        //add O3OneHourIAqi
        o3OneHourIAqi.setDefinition("O3OneHourIAQI");
        dataRecord.addField("O3OneHourIAQI", o3OneHourIAqi);
        //add aqi field
        aqi.setDefinition("AQI");
        dataRecord.addField("AQI", aqi);
        //add primaryEP field
        primaryEP.setDefinition("primaryEP");
        dataRecord.addField("primaryEP", primaryEP);
        //add aqType field
        aqDegree.setDefinition("AQDegree");
        dataRecord.addField("aqDegree", aqDegree);
        //add aqType field
        aqType.setDefinition("AQType");
        dataRecord.addField("aqType", aqType);
    }

    /**
     * 定义AirQualityHour数据模型字段
     * @param dataRecord
     * @param airQualityHour
     */
    public void writeAirQualityHourType(DataRecord dataRecord, AirQualityHour airQualityHour) {
        Quantity pm25OneHour = new QuantityImpl();
        Quantity pm10OneHour = new QuantityImpl();
        Quantity so2OneHour = new QuantityImpl();
        Quantity no2OneHour = new QuantityImpl();
        Quantity coOneHour = new QuantityImpl();
        Quantity o3OneHour = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        if (airQualityHour!=null) {
            SWEModelUtils.setQuantity(pm25OneHour, airQualityHour.getPm25OneHour(), "" ,"μg/m3");
            SWEModelUtils.setQuantity(pm10OneHour, airQualityHour.getPm10OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(so2OneHour, airQualityHour.getSo2OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(no2OneHour, airQualityHour.getNo2OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(coOneHour, airQualityHour.getCoOneHour(), "", "mg/m3");
            SWEModelUtils.setQuantity(o3OneHour, airQualityHour.getO3OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(aqi, airQualityHour.getAqi(), "", "");
        }
        //add pm25OneHour field
        pm25OneHour.setDefinition("Pm25OneHour");
        dataRecord.addField("Pm25OneHour", pm25OneHour);
        //add pm10OneHour field
        pm10OneHour.setDefinition("PM10OneHour");
        dataRecord.addField("PM10OneHour", pm10OneHour);
        //add SO2OneHour field
        so2OneHour.setDefinition("SO2OneHour");
        dataRecord.addField("SO2OneHour", so2OneHour);
        //add no2OneHour field
        no2OneHour.setDefinition("NO2OneHour");
        dataRecord.addField("NO2OneHour", no2OneHour);
        //add coOneHour field
        coOneHour.setDefinition("COOneHour");
        dataRecord.addField("COOneHour", coOneHour);
        //add O3OneHour
        o3OneHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3OneHour);
    }

    /**
     * 定义AirQualityDay数据模型字段
     * @param dataRecord
     * @param airQualityDay
     */
    public void writeAirQualityDayType(DataRecord dataRecord, AirQualityDay airQualityDay) {
        Quantity pm25 = new QuantityImpl();
        Quantity pm10 = new QuantityImpl();
        Quantity so2 = new QuantityImpl();
        Quantity no2 = new QuantityImpl();
        Quantity co = new QuantityImpl();
        Quantity o3EightHour = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        if (airQualityDay!=null) {
            SWEModelUtils.setQuantity(pm25, airQualityDay.getPm25(), "" ,"μg/m3");
            SWEModelUtils.setQuantity(pm10, airQualityDay.getPm10(), "", "μg/m3");
            SWEModelUtils.setQuantity(so2, airQualityDay.getSo2(), "", "μg/m3");
            SWEModelUtils.setQuantity(no2, airQualityDay.getNo2(), "", "μg/m3");
            SWEModelUtils.setQuantity(co, airQualityDay.getCo(), "", "mg/m3");
            SWEModelUtils.setQuantity(o3EightHour, airQualityDay.getO3EightHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(aqi, airQualityDay.getAqi(), "", "");
        }
        //add pm25OneHour field
        pm25.setDefinition("Pm25OneHour");
        dataRecord.addField("Pm25OneHour", pm25);
        //add pm10OneHour field
        pm10.setDefinition("PM10OneHour");
        dataRecord.addField("PM10OneHour", pm10);
        //add SO2OneHour field
        so2.setDefinition("SO2OneHour");
        dataRecord.addField("SO2OneHour", so2);
        //add no2OneHour field
        no2.setDefinition("NO2OneHour");
        dataRecord.addField("NO2OneHour", no2);
        //add coOneHour field
        co.setDefinition("COOneHour");
        dataRecord.addField("COOneHour", co);
        //add O3EightHour
        o3EightHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3EightHour);
    }

    /**
     * 写结果属性,使用DataRecord方式
     * @param object
     * @param dataRecord
     */
    public void writeResult(Object object, DataRecord dataRecord) {
        if (object!=null) {
            if (object instanceof AirQualityHour) {
                writeAirQualityHourType(dataRecord, (AirQualityHour) object);
            } else {
                writeAirQualityDayType(dataRecord, (AirQualityDay) object);
            }
        }
    }

    /**
     * 写结果属性,使用DataArray方式
     * @param dataArray
     * @param objects
     */
    public void writeResult(List<Object> objects, DataArray dataArray) throws UnsupportedEncodingException {
        SWEFactory factory = new SWEFactory();
        DataRecord dataRecord = factory.newDataRecord();
        StringBuilder stringBuilder = new StringBuilder();

        if (objects!=null && objects.size()>0) {
            //add elementCount
            Count count = factory.newCount();
            count.setValue(objects.size());
            dataArray.setElementCount(count);
            //写elementType，DataRecord中的Fileds
            if (objects.get(0) instanceof CDay) {
                writeCDayElementType(dataRecord, null);
            } else if (objects.get(0) instanceof DayAqi) {
                writeDayAqiElementType(dataRecord, null);
            } else if (objects.get(0) instanceof HourAqi) {
                writeHourAqiElementType(dataRecord, null);
            } else if (objects.get(0) instanceof IHourAqi) {
                writeIHourAqiElementType(dataRecord, null);
            } else if (objects.get(0) instanceof IDayAqi) {
                writeIDayAqiElementType(dataRecord, null);
            }
            dataArray.setElementType("components", dataRecord);
            //add Encoding
            TextEncoding encoding = factory.newTextEncoding();
            encoding.setBlockSeparator(" ");
            encoding.setTokenSeparator(",");
            encoding.setDecimalSeparator(".");
            dataArray.setEncoding(encoding);
            //add values
            for (Object object : objects) {
                List<Object> temp = DataCenterUtils.bean2List(object);
                if (temp.size()>0) {
                    for (Object o : temp) {
                        stringBuilder.append(StringUtils.isBlank(o.toString()) || o.toString().equals("NA") ? "-1":o.toString());
                        stringBuilder.append(" ");
                    }
                }
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
                stringBuilder.append(",");
            }
            String sss = stringBuilder.toString();
            EncodedValues encodedValues = factory.newEncodedValuesProperty();
            String succeedStr = new String(stringBuilder.toString().getBytes());
            encodedValues.setAsText(dataArray, encoding, succeedStr);
            dataArray.setValues(encodedValues);
            System.out.println();
        }
    }

    /**
     * 根据不同模型，获取唯一id
     * @param object
     * @return
     */
    public String getUniqueCode(Object object) {
        String res = null;
        if (object instanceof AirQualityDay) {
            res = ((AirQualityDay) object).getUniqueCode();
        } else {
            res = ((AirQualityHour) object).getUniqueCode();
        }
        return res;
    }

    /**
     * 根据不同的数据模型，获取对应的时间数据
     * @param object
     * @return
     */
    public Instant getRusltTime(Object object) {
        Instant res = null;
        if (object instanceof AirQualityDay) {
            res = ((AirQualityDay) object).getTime();
        } else {
            res = ((AirQualityHour) object).getTime();
        }
        return res;
    }

    /**
     * 根据不同的数据模型，获取对应的现象时间数据
     * @param object
     * @return
     */
    public TimeExtent getPhenomenonTime(Object object) {
        Instant end = getRusltTime(object);
        Instant begin = null;
        if (object instanceof AirQualityHour) {
            begin = end.minusSeconds(60*60);
        } else {
            begin = end.minusSeconds(24*60*60);
        }
        if (end!=null && begin!=null) {
            return TimeExtent.period(begin, end);
        } else {
            return null;
        }
    }

    /**
     * 根据不同的数据模型，获取对应的FOI数据
     * @param object
     * @return
     */
    public IGeoFeature getFeatureOfInterset(Object object) {
        String name = "";
        String id = "";
        if (object instanceof AirQualityHour) {
            name = ((AirQualityHour) object).getStationName();
            id = ((AirQualityHour) object).getUniqueCode();
        } else {
            name = ((AirQualityDay) object).getStationName();
            id = ((AirQualityDay) object).getUniqueCode();
        }
        FeatureRef<?> ref = new FeatureRef<>();
        ref.setHref(name);
        return ref;

//        SamplingFeature<AbstractGeometry> res = new SamplingFeature<>();
//        res.setUniqueIdentifier(id);
//        res.setName(name);
//        return res;
    }
    /**
     * 根据Object对象，首先判断参数对象属于哪种数据模型，然后封装成IObservation对象进行返回
     * @param objects
     * @return
     */
    public List<IObservation> getObservation(List<Object> objects) throws UnsupportedEncodingException {
        List<IObservation> res = new ArrayList<>();

        if (objects!=null && objects.size()>0) {
            for (Object object : objects) {
                ObservationImpl observation = new ObservationImpl();
                observation.setId(getId(object));
                observation.setUniqueIdentifier(getId(object));
                observation.setProcedure(getProcedure(object));
                observation.setResultTime(getRusltTime(object));
                observation.setPhenomenonTime(getPhenomenonTime(object));
                observation.setFeatureOfInterest(getFeatureOfInterset(object));
                observation.setPhenomenonTime(getPhenomenonTime(object));
                observation.setObservedProperty(getObservedProperty(object));
                observation.setType("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement");
                observation.setResult(setDataComponent(object));
                res.add(observation);
            }
        }
        return res;
    }

    /**
     * 获取Request请求对象
     * @param objects
     * @return
     * @throws OWSException
     * @throws UnsupportedEncodingException
     */
    public InsertObservationRequest writeInsertObservationRequest(List<Object> objects) throws UnsupportedEncodingException {
        InsertObservationRequest request = new InsertObservationRequest();
        SOSUtils.loadRegistry();
        request.setVersion("2.0");
        request.setService(SOSUtils.SOS);
        request.getObservations().addAll(getObservation(objects));
        return request;
    }

    public List<Object> parseXmlDoc(String document) throws Exception {
        //读取xml文档，返回Document对象
        Document xmlContent = DocumentHelper.parseText(document);
        Element root = xmlContent.getRootElement();
        String type = root.getName().substring(root.getName().lastIndexOf("_")+1);
        List<Object> objects = new ArrayList<>();
        //判断返回数据类型，选择对应的数据模型
        switch (type) {
            case "HourAqiModel":
            case "IHourAqiModel": {
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getHourAqiInfo(element));
                }
                break;
            }
            case "DayAqiModel":
            case "IDayAqiModel":
            case "CDayModel": {
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getDayAqiInfo(element));
                }
                break;
            }
        }
        return objects;
    }

    /**
     * 解析CDay要素节点，获取相关信息，包装成CDay模型返回
     * @param cDayElement
     * @return
     */
    public CDay getCDayInfo(Element cDayElement) {
        CDay cDay = new CDay();
        for (Iterator j = cDayElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("SDateTime")) {
                cDay.setsDateTime(DataCenterUtils.string2Instant(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("UniqueCode")) {
                cDay.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                cDay.setSo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                cDay.setNo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                cDay.setPm10(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                cDay.setCo(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                cDay.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                cDay.setPm25(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                cDay.setAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PrimaryEP")) {
                cDay.setPrimaryEP(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQType")) {
                cDay.setAqType(attribute.getText());
                continue;
            }
        }
        return cDay;
    }

    /**
     * 解析IHourAqi要素节点，获取相关信息，包装成IHourAqi模型返回
     * @param iHourAqiElement
     * @return
     */
    public IHourAqi getIHourAqiInfo(Element iHourAqiElement) {
        IHourAqi iHourAqi = new IHourAqi();
        for (Iterator j = iHourAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                iHourAqi.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                iHourAqi.setQueryTime(DataCenterUtils.string2Instant(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                iHourAqi.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2OneHour")) {
                iHourAqi.setSo2OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2OneHourIAQI")) {
                iHourAqi.setSo2OneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHour")) {
                iHourAqi.setNo2OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHourIAQI")) {
                iHourAqi.setNo2OneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHour")) {
                iHourAqi.setPm10OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHourIAQI")) {
                iHourAqi.setPm10OneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHour")) {
                iHourAqi.setCoOneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHourIAQI")) {
                iHourAqi.setCoOneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                iHourAqi.setO3OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHourIAQI")) {
                iHourAqi.setO3OneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHour")) {
                iHourAqi.setPm25OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHourIAQI")) {
                iHourAqi.setPm25OneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                iHourAqi.setAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PrimaryEP")) {
                iHourAqi.setPrimaryEP(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQDegree")) {
                iHourAqi.setAqDegree(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQType")) {
                iHourAqi.setAqType(attribute.getText());
                continue;
            }
        }
        return iHourAqi;
    }

    /**
     * 解析IDayAqi要素节点，获取相关信息，包装成IDayAqi模型返回
     * @param iDayAqiElement
     * @return
     */
    public IDayAqi getIDayAqiInfo(Element iDayAqiElement) {
        IDayAqi iDayAqi = new IDayAqi();
        for (Iterator j = iDayAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                iDayAqi.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                iDayAqi.setQueryTime(DataCenterUtils.string2Instant(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("City")) {
                iDayAqi.setCity(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                iDayAqi.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                iDayAqi.setPm25(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25IAQI")) {
                iDayAqi.setPm25IAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                iDayAqi.setPm10(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10IAQI")) {
                iDayAqi.setPm10IAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                iDayAqi.setSo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2IAQI")) {
                iDayAqi.setSo2IAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                iDayAqi.setNo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2IAQI")) {
                iDayAqi.setNo2IAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                iDayAqi.setCo(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COIAQI")) {
                iDayAqi.setCoIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                iDayAqi.setO3OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHourIAQI")) {
                iDayAqi.setO3OneHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                iDayAqi.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHourIAQI")) {
                iDayAqi.setO3EightHourIAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                iDayAqi.setAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PrimaryEP")) {
                iDayAqi.setPrimaryEP(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQDegree")) {
                iDayAqi.setAqDegree(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQType")) {
                iDayAqi.setAqType(attribute.getText());
                continue;
            }
        }
        return iDayAqi;
    }

    /**
     * 解析DayAqi要素节点，获取相关信息，包装成DayAqi模型返回
     * @param dayAqiElement
     * @return
     */
    public DayAqi _getDayAqiInfo(Element dayAqiElement) {
        DayAqi dayAqi = new DayAqi();
        for (Iterator j = dayAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                dayAqi.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                dayAqi.setQueryTime(DataCenterUtils.string2Instant(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                dayAqi.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                dayAqi.setPm25(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                dayAqi.setPm10(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                dayAqi.setSo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                dayAqi.setNo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                dayAqi.setCo(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                dayAqi.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                dayAqi.setAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PrimaryEP")) {
                dayAqi.setPrimaryEP(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQDegree")) {
                dayAqi.setAqDegree(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQType")) {
                dayAqi.setAqType(attribute.getText());
                continue;
            }
        }
        return dayAqi;
    }

    /**
     * 解析Element要素节点，获取相关信息，包装成HourAqi模型返回
     * @param hourAqiElement
     * @return
     */
    public AirQualityHour getHourAqiInfo(Element hourAqiElement) throws ParseException {
        AirQualityHour airQualityHour = new AirQualityHour();
        for (Iterator j = hourAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                airQualityHour.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                airQualityHour.setTime(DataCenterUtils.string2LocalDateTime2(attribute.getText()).toInstant(ZoneOffset.ofHours(+8)));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                airQualityHour.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHour")) {
                airQualityHour.setPm25OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHour")) {
                airQualityHour.setPm10OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2OneHour")) {
                airQualityHour.setSo2OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHour")) {
                airQualityHour.setNo2OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHour")) {
                airQualityHour.setCoOneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                airQualityHour.setO3OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                airQualityHour.setO3OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
            }
        }
        return airQualityHour;
    }

    /**
     * 解析Element要素节点，获取相关信息，包装成HourAqi模型返回
     * @param hourAqiElement
     * @return
     */
    public AirQualityDay getDayAqiInfo(Element hourAqiElement) throws ParseException {
        AirQualityDay airQualityDay = new AirQualityDay();
        for (Iterator j = hourAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                airQualityDay.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime") || attribute.getName().equals("SDateTime")) {
                airQualityDay.setTime(DataCenterUtils.string2LocalDateTime2(attribute.getText()).toInstant(ZoneOffset.ofHours(+8)));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                airQualityDay.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                airQualityDay.setPm25(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                airQualityDay.setPm10(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                airQualityDay.setSo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                airQualityDay.setNo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                airQualityDay.setCo(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                airQualityDay.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                airQualityDay.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
            }
        }
        return airQualityDay;
    }

    /**
     * 将有用地信息输出到文本文件
     * @param objects
     */
    public void writeInfo(List<Object> objects) {
        StringBuilder sb = new StringBuilder();
        if (objects!=null && objects.size()>0) {
            if (objects.get(0) instanceof CDay) {
                for (Object object : objects) {
                    CDay cDay = (CDay) object;
                    sb.append("UniqueCode: ").append(cDay.getUniqueCode());
                    sb.append("\n");
                }
            } else if (objects.get(0) instanceof DayAqi) {
                for (Object object : objects) {
                    DayAqi dayAqi = (DayAqi) object;
                    sb.append("UniqueCode: ").append(dayAqi.getUniqueCode()).append("\t");
                    sb.append("StationName: ").append(dayAqi.getStationName()).append("\t");
                    sb.append("\n");
                }
            } else if (objects.get(0) instanceof HourAqi) {
                for (Object object : objects) {
                    HourAqi hourAqi = (HourAqi) object;
                    sb.append("UniqueCode: ").append(hourAqi.getUniqueCode()).append("\t");
                    sb.append("StationName: ").append(hourAqi.getStationName()).append("\t");
                    sb.append("\n");
                }
            } else if (objects.get(0) instanceof IDayAqi) {
                for (Object object : objects) {
                    IDayAqi iDayAqi = (IDayAqi) object;
                    sb.append("UniqueCode: ").append(iDayAqi.getUniqueCode()).append("\t");
                    sb.append("StationName: ").append(iDayAqi.getStationName()).append("\t");
                    sb.append("City: ").append(iDayAqi.getCity()).append("\t");
                    sb.append("\n");
                }
            } else if (objects.get(0) instanceof IHourAqi) {
                for (Object object : objects) {
                    IHourAqi iHourAqi = (IHourAqi) object;
                    sb.append("UniqueCode: ").append(iHourAqi.getUniqueCode()).append("\t");
                    sb.append("StationName: ").append(iHourAqi.getStationName()).append("\t");
                    sb.append("\n");
                }
            }
        }

        DataCenterUtils.write2File("d://StationInfo.txt", sb.toString());
    }
}
