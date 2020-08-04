package com.sensorweb.datacenter.entity.meteorology;

import java.util.Date;

/**
 * 任意时间段小时均数据
 */
public class IHourAqi {
    private String uniqueCode;
    private Date queryTime;
    private String stationName;
    private String pm25OneHour;
    private String pm25OneHourIAqi;
    private String pm10OneHour;
    private String pm10OneHourIAqi;
    private String so2OneHour;
    private String so2OneHourIAqi;
    private String no2OneHour;
    private String no2OneHourIAqi;
    private String coOneHour;
    private String coOneHourIAqi;
    private String o3OneHour;
    private String o3OneHourIAqi;
    private String aqi;
    private String primaryEP;
    private String aqDegree;
    private String aqType;

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getPm25OneHour() {
        return pm25OneHour;
    }

    public void setPm25OneHour(String pm25OneHour) {
        this.pm25OneHour = pm25OneHour;
    }

    public String getPm25OneHourIAqi() {
        return pm25OneHourIAqi;
    }

    public void setPm25OneHourIAqi(String pm25OneHourIAqi) {
        this.pm25OneHourIAqi = pm25OneHourIAqi;
    }

    public String getPm10OneHour() {
        return pm10OneHour;
    }

    public void setPm10OneHour(String pm10OneHour) {
        this.pm10OneHour = pm10OneHour;
    }

    public String getPm10OneHourIAqi() {
        return pm10OneHourIAqi;
    }

    public void setPm10OneHourIAqi(String pm10OneHourIAqi) {
        this.pm10OneHourIAqi = pm10OneHourIAqi;
    }

    public String getSo2OneHour() {
        return so2OneHour;
    }

    public void setSo2OneHour(String so2OneHour) {
        this.so2OneHour = so2OneHour;
    }

    public String getSo2OneHourIAqi() {
        return so2OneHourIAqi;
    }

    public void setSo2OneHourIAqi(String so2OneHourIAqi) {
        this.so2OneHourIAqi = so2OneHourIAqi;
    }

    public String getNo2OneHour() {
        return no2OneHour;
    }

    public void setNo2OneHour(String no2OneHour) {
        this.no2OneHour = no2OneHour;
    }

    public String getNo2OneHourIAqi() {
        return no2OneHourIAqi;
    }

    public void setNo2OneHourIAqi(String no2OneHourIAqi) {
        this.no2OneHourIAqi = no2OneHourIAqi;
    }

    public String getCoOneHour() {
        return coOneHour;
    }

    public void setCoOneHour(String coOneHour) {
        this.coOneHour = coOneHour;
    }

    public String getCoOneHourIAqi() {
        return coOneHourIAqi;
    }

    public void setCoOneHourIAqi(String coOneHourIAqi) {
        this.coOneHourIAqi = coOneHourIAqi;
    }

    public String getO3OneHour() {
        return o3OneHour;
    }

    public void setO3OneHour(String o3OneHour) {
        this.o3OneHour = o3OneHour;
    }

    public String getO3OneHourIAqi() {
        return o3OneHourIAqi;
    }

    public void setO3OneHourIAqi(String o3OneHourIAqi) {
        this.o3OneHourIAqi = o3OneHourIAqi;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPrimaryEP() {
        return primaryEP;
    }

    public void setPrimaryEP(String primaryEP) {
        this.primaryEP = primaryEP;
    }

    public String getAqDegree() {
        return aqDegree;
    }

    public void setAqDegree(String aqDegree) {
        this.aqDegree = aqDegree;
    }

    public String getAqType() {
        return aqType;
    }

    public void setAqType(String aqType) {
        this.aqType = aqType;
    }
}
