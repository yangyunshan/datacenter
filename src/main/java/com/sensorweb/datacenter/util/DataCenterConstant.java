package com.sensorweb.datacenter.util;

import org.springframework.stereotype.Component;

@Component
public interface DataCenterConstant {
    /**
     * 湖北省环境监测站数据请求用户名UsrName
     */
    String USER_NAME = "allstation";

    /**
     * 湖北省环境监测站数据请求密码passWord
     */
    String PASSWORD = "IWJD5r5j3Nx4kXWO";

    /**
     * 湖北省环境监测站url请求常量---GetLast24HourData
     */
    String GET_LAST_24_HOUR_DATA = "http://59.172.208.250:8001/AppServer/PublishData.asmx/GetLast24HourData";

    /**
     * 湖北省环境监测站url请求常量---GetLast7DaysData
     */
    String GET_LAST_7_Days_DATA = "http://59.172.208.250:8001/AppServer/PublishData.asmx/GetLast7DaysData";

    /**
     * 湖北省环境监测站url请求常量---GetLastHoursData
     */
    String GET_LAST_HOURS_DATA = "http://59.172.208.250:8001/AppServer/PublishData.asmx/GetLastHoursData";

    /**
     * 湖北省环境监测站url请求常量---GetOriqinalDayilyData
     */
    String GET_ORIGINAL_DAYILY_DATA = "http://59.172.208.250:8001/AppServer/PublishData.asmx/GetOriginalDayilyData";

    /**
     * 湖北省环境监测站url请求常量---GetOriginalHourlyData
     */
    String GET_ORIGINAL_HOURLY_DATA = "http://59.172.208.250:8001/AppServer/PublishData.asmx/GetOriginalHourlyData";

    /**
     * 湖北省环境监测站url请求常量---getLast40DaysData
     */
    String GET_LAST_40_DAYS_DATA = "http://59.172.208.250:8001/AppServer/AuditData.asmx/GetLast40DaysData";

    /**
     * SOS ObservationType
     */
    String OM_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation";
    String OM_MEASUREMENT = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
    String OM_CATEGORYOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation";
    String OM_COMPLEXOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation";
    String OM_COUNTOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/CountObservation";
    String OM_DISCRETECOVERAGEOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscreteCoverageObservation";
    String OM_GEOMETRYOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation";
    String OM_POINTCOVERAGE = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_PointCoverage";
    String OM_TEMPORALOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TemporalObservation";
    String OM_TIMESERIESOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TimeSeriesObservation";
    String OM_TRUTHOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation";

    /**
     * SOS FeatureOfInterestType
     */
    String SF_SAMPLINGPOINT = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";
    String SF_SAMPLINGCURVE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve";
    String SF_SAMPLINGSURFACE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSurface";
    String SF_SPECIMEN = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_Specimen";

}
