package com.sensorweb.datacenter.entity.meteorology;

import java.util.Date;

/**
 * 任意时间小时数据
 */
public class HourAqi {
    private String stationName;
    private String uniqueCode;
    private Date queryTime;
    private String pm25OneHour;
    private String pm10OneHour;
    private String so2OneHour;
    private String no2OneHour;
    private String coOneHour;
    private String o3OneHour;
    private String aqi;
    private String primaryEP;
    private String aqDegree;
    private String aqType;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

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

    public String getPm25OneHour() {
        return pm25OneHour;
    }

    public void setPm25OneHour(String pm25OneHour) {
        this.pm25OneHour = pm25OneHour;
    }

    public String getPm10OneHour() {
        return pm10OneHour;
    }

    public void setPm10OneHour(String pm10OneHour) {
        this.pm10OneHour = pm10OneHour;
    }

    public String getSo2OneHour() {
        return so2OneHour;
    }

    public void setSo2OneHour(String so2OneHour) {
        this.so2OneHour = so2OneHour;
    }

    public String getNo2OneHour() {
        return no2OneHour;
    }

    public void setNo2OneHour(String no2OneHour) {
        this.no2OneHour = no2OneHour;
    }

    public String getCoOneHour() {
        return coOneHour;
    }

    public void setCoOneHour(String coOneHour) {
        this.coOneHour = coOneHour;
    }

    public String getO3OneHour() {
        return o3OneHour;
    }

    public void setO3OneHour(String o3OneHour) {
        this.o3OneHour = o3OneHour;
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
