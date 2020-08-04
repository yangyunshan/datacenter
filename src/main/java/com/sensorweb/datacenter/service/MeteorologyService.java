package com.sensorweb.datacenter.service;

import com.sensorweb.datacenter.entity.meteorology.*;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.OgcProperty;
import net.opengis.OgcPropertyImpl;
import net.opengis.gml.v32.*;
import net.opengis.gml.v32.impl.AbstractGeometryImpl;
import net.opengis.gml.v32.impl.GMLFactory;
import net.opengis.gml.v32.impl.ReferenceImpl;
import net.opengis.swe.v20.*;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.vast.data.CategoryImpl;
import org.vast.data.DataBlockString;
import org.vast.data.DataRecordImpl;
import org.vast.data.SWEFactory;
import org.vast.ogc.def.DefinitionRef;
import org.vast.ogc.gml.FeatureRef;
import org.vast.ogc.gml.GMLUtils;
import org.vast.ogc.gml.IGeoFeature;
import org.vast.ogc.om.*;
import org.vast.ogc.xlink.IXlinkReference;
import org.vast.ogc.xlink.XlinkUtils;
import org.vast.ows.OWSException;
import org.vast.ows.sos.InsertObservationReaderV20;
import org.vast.ows.sos.InsertObservationRequest;
import org.vast.ows.sos.InsertObservationWriterV20;
import org.vast.ows.sos.SOSUtils;
import org.vast.ows.swe.SWESUtils;
import org.vast.swe.SWEUtils;
import org.vast.xml.DOMHelper;
import org.vast.xml.XMLWriterException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MeteorologyService {

    /**
     * 向指定的url发送GET请求
     * @param url
     * @param param
     * @return
     */
    public String doGet(String url, String param) {
        String result = "";
        String totalUrl = url + "?" + param;
        try {
            URL realUrl = new URL(totalUrl);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MISE 6.0; Windows NT 5.1; SV1)");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null)
                result += line;
        } catch (Exception e) {
            System.out.println("发送Get请求异常");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 向指定url发送post请求
     * @param url
     * @param param
     * @return
     */
    public String doPost(String url, String param) {
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MISE 6.0; Windows NT 5.1; SV1)");
            //发送post请求必须设置以下两行
            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            //发送请求参数
            writer.print(param);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求失败");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置IProcedure属性
     * @param procedureId
     * @return
     */
    public IProcedure setProcedure(String procedureId) {
        IProcedure procedure = new ProcedureRef();
        ((IXlinkReference)procedure).setHref(procedureId);
        return procedure;
    }

    public DefinitionRef setObservedProperty(String property) {
        DefinitionRef observedProperty = new DefinitionRef();
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
        writeDataComponent(object, dataRecord);
        return dataRecord;
    }

    /**
     * 写结果属性
     * @param dataRecord
     * @param object
     */
    public void writeDataComponent(Object object, DataRecord dataRecord) {
        Map<String, Object> kv = DataCenterUtils.bean2Map(object);
        SWEFactory factory = new SWEFactory();

        if (!kv.isEmpty()) {
            for (String key : kv.keySet()) {
                OgcProperty<DataComponent> fieldProp = new OgcPropertyImpl<>();
                try {
                    String value = (String) kv.get(key);
                    if (!StringUtils.isBlank(value) && !value.equals("NA")) {
                        double res = Double.parseDouble(value);
                        Quantity quantity = factory.newQuantity();
                        fieldProp.setName(key);
                        quantity.setValue(res);
                        fieldProp.setValue(quantity);
                    }
                } catch (Exception e) {
                    String value = null;
                    if (kv.get(key) instanceof Date) {
                        value = DataCenterUtils.date2Str((Date) kv.get(key));
                    } else {
                        value = (String) kv.get(key);
                    }
                    Text text = factory.newText();
                    fieldProp.setName(key);
                    text.setValue(value);
                    fieldProp.setValue(text);
                }
                dataRecord.getFieldList().add(fieldProp);
            }
        }
    }

    /**
     * 设置FOI参数，目前参数设置为引用地址为unknown（气象站提供的只有大致位置，没有详细的坐标位置,也没有提供相应的引用地址）
     * @param object
     * @return
     */
    public IGeoFeature setFOI(Object object) {
        FeatureRef featureRef = new FeatureRef();
        featureRef.setHref("unkown");

        return featureRef;
    }

    /**
     * 根据不同模型，获取唯一id
     * @param object
     * @return
     */
    public String getUniqueCode(Object object) {
        String res = null;
        if (object instanceof CDay) {
            res = ((CDay) object).getUniqueCode();
        } else if (object instanceof DayAqi) {
            res = ((DayAqi) object).getUniqueCode();
        } else if (object instanceof HourAqi) {
            res = ((HourAqi) object).getUniqueCode();
        } else if (object instanceof IDayAqi) {
            res = ((IDayAqi) object).getUniqueCode();
        } else {
            res = ((IHourAqi) object).getUniqueCode();
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
        if (object instanceof CDay) {
            res = ((CDay) object).getsDateTime().toInstant();
        } else if (object instanceof DayAqi) {
            res = ((DayAqi) object).getQueryTime().toInstant();
        } else if (object instanceof HourAqi) {
            res = ((HourAqi) object).getQueryTime().toInstant();
        } else if (object instanceof IDayAqi) {
            res = ((IDayAqi) object).getQueryTime().toInstant();
        } else {
            res = ((IHourAqi) object).getQueryTime().toInstant();
        }
        return res;
    }

    /**
     * 根据Object对象，首先判断参数对象属于哪种数据模型，然后封装成IObservation对象进行返回
     * @param object
     * @return
     */
    public IObservation getObservation(Object object) {
        ObservationImpl observation = new ObservationImpl();

        observation.setProcedure(setProcedure("airStation"));
        observation.setId(getUniqueCode(object));
        observation.setResultTime(getRusltTime(object));
        observation.setResult(setDataComponent(object));
        observation.setObservedProperty(setObservedProperty("airQuantity"));
        observation.setType("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement");
        observation.setFeatureOfInterest(setFOI(object));
//        observation.setDescription("");
        return observation;
    }
    /**
     * 对数据模型进行建模，O&M，返回字符串的Element对象
     * @param object
     * @return
     * @throws XMLWriterException
     */
    public org.w3c.dom.Element buildXmlElement(Object object) throws XMLWriterException {
        DOMHelper domHelper = new DOMHelper();
        OMUtils omUtils = new OMUtils(OMUtils.OM);
        ObservationWriterV20 writerV20 = new ObservationWriterV20();
        return writerV20.write(domHelper, getObservation(object));
    }

    public org.w3c.dom.Element writeRequest(Object object) throws OWSException {
        InsertObservationRequest request = new InsertObservationRequest();
        SOSUtils.loadRegistry();
        request.setOffering("auhdafaw");
        request.setVersion("2.0");
        request.setService(SOSUtils.SOS);
        request.getObservations().add(getObservation(object));

        InsertObservationWriterV20 writerV20 = new InsertObservationWriterV20();
        DOMHelper domHelper = new DOMHelper();
        return writerV20.buildXMLQuery(domHelper, request);
    }

    public List<Object> parseXmlDoc(String document) throws Exception {
        //读取xml文档，返回Document对象
        Document xmlContent = DocumentHelper.parseText(document);
        Element root = xmlContent.getRootElement();
        String type = root.getName().substring(root.getName().lastIndexOf("_")+1);
        List<Object> objects = new ArrayList<>();
        //判断返回数据类型，选择对应的数据模型
        switch (type) {
            case "HourAqiModel":{
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getHourAqiInfo(element));
                }
                break;
            }
            case "DayAqiModel":{
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getDayAqiInfo(element));
                }
                break;
            }
            case "IDayAqiModel":{
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getIDayAqiInfo(element));
                }
                break;
            }
            case "IHourAqiModel":{
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getIHourAqiInfo(element));
                }
                break;
            }
            case "CDayModel":{
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getCDayInfo(element));
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
                cDay.setsDateTime(stringToDate(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("UniqueCode")) {
                cDay.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                cDay.setSo2((attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                cDay.setNo2(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                cDay.setPm10(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                cDay.setCo(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                cDay.setO3EightHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                cDay.setPm25(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                cDay.setAqi(attribute.getText());
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
                iHourAqi.setQueryTime(stringToDate(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                iHourAqi.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2OneHour")) {
                iHourAqi.setSo2OneHour((attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("SO2OneHourIAQI")) {
                iHourAqi.setSo2OneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHour")) {
                iHourAqi.setNo2OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHourIAQI")) {
                iHourAqi.setNo2OneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHour")) {
                iHourAqi.setPm10OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHourIAQI")) {
                iHourAqi.setPm10OneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHour")) {
                iHourAqi.setCoOneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHourIAQI")) {
                iHourAqi.setCoOneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                iHourAqi.setO3OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHourIAQI")) {
                iHourAqi.setO3OneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHour")) {
                iHourAqi.setPm25OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHourIAQI")) {
                iHourAqi.setPm25OneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                iHourAqi.setAqi(attribute.getText());
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
                iDayAqi.setQueryTime(stringToDate(attribute.getText()));
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
                iDayAqi.setPm25(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25IAQI")) {
                iDayAqi.setPm25IAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                iDayAqi.setPm10(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10IAQI")) {
                iDayAqi.setPm10IAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                iDayAqi.setSo2(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2IAQI")) {
                iDayAqi.setSo2IAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                iDayAqi.setNo2(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2IAQI")) {
                iDayAqi.setNo2IAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                iDayAqi.setCo(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COIAQI")) {
                iDayAqi.setCoIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                iDayAqi.setO3OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHourIAQI")) {
                iDayAqi.setO3OneHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                iDayAqi.setO3EightHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHourIAQI")) {
                iDayAqi.setO3EightHourIAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                iDayAqi.setAqi(attribute.getText());
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
    public DayAqi getDayAqiInfo(Element dayAqiElement) {
        DayAqi dayAqi = new DayAqi();
        for (Iterator j = dayAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                dayAqi.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                dayAqi.setQueryTime(stringToDate(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                dayAqi.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                dayAqi.setPm25(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                dayAqi.setPm10(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                dayAqi.setSo2(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                dayAqi.setNo2(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                dayAqi.setCo(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                dayAqi.setO3EightHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                dayAqi.setAqi(attribute.getText());
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
     * 解析HourAqi要素节点，获取相关信息，包装成HourAqi模型返回
     * @param hourAqiElement
     * @return
     */
    public HourAqi getHourAqiInfo(Element hourAqiElement) {
        HourAqi hourAqi = new HourAqi();
        for (Iterator j = hourAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                hourAqi.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                hourAqi.setQueryTime(stringToDate(attribute.getText()));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                hourAqi.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHour")) {
                hourAqi.setPm25OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHour")) {
                hourAqi.setPm10OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2OneHour")) {
                hourAqi.setSo2OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHour")) {
                hourAqi.setNo2OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHour")) {
                hourAqi.setCoOneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                hourAqi.setO3OneHour(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                hourAqi.setAqi(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PrimaryEP")) {
                hourAqi.setPrimaryEP(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQDegree")) {
                hourAqi.setAqDegree(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQType")) {
                hourAqi.setAqType(attribute.getText());
                continue;
            }
        }
        return hourAqi;
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
