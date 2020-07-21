package com.sensorweb.datacenter.entity.meteorological;

import java.util.Date;

/**
 * 任意时间小时数据
 */
public class HourAqi {
    private String stationName;
    private String uniqueCode;
    private Date queryTime;
    private double pm25OneHour;
    private double pm10OneHour;
    private double so2OneHour;
    private double no2OneHour;
    private double coOneHour;
    private double o3OneHour;
    private double aqi;
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

    public double getPm25OneHour() {
        return pm25OneHour;
    }

    public void setPm25OneHour(double pm25OneHour) {
        this.pm25OneHour = pm25OneHour;
    }

    public double getPm10OneHour() {
        return pm10OneHour;
    }

    public void setPm10OneHour(double pm10OneHour) {
        this.pm10OneHour = pm10OneHour;
    }

    public double getSo2OneHour() {
        return so2OneHour;
    }

    public void setSo2OneHour(double so2OneHour) {
        this.so2OneHour = so2OneHour;
    }

    public double getNo2OneHour() {
        return no2OneHour;
    }

    public void setNo2OneHour(double no2OneHour) {
        this.no2OneHour = no2OneHour;
    }

    public double getCoOneHour() {
        return coOneHour;
    }

    public void setCoOneHour(double coOneHour) {
        this.coOneHour = coOneHour;
    }

    public double getO3OneHour() {
        return o3OneHour;
    }

    public void setO3OneHour(double o3OneHour) {
        this.o3OneHour = o3OneHour;
    }

    public double getAqi() {
        return aqi;
    }

    public void setAqi(double aqi) {
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
