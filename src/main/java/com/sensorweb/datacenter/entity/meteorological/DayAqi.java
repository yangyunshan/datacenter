package com.sensorweb.datacenter.entity.meteorological;

import java.util.Date;

/**
 * 日均数据
 */
public class DayAqi {
    private String stationName;
    private String uniqueCode;
    private Date queryTime;
    private double pm25;
    private double pm10;
    private double so2;
    private double no2;
    private double co;
    private double o3EightHour;
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

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public double getSo2() {
        return so2;
    }

    public void setSo2(double so2) {
        this.so2 = so2;
    }

    public double getNo2() {
        return no2;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public double getCo() {
        return co;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public double getO3EightHour() {
        return o3EightHour;
    }

    public void setO3EightHour(double o3EightHour) {
        this.o3EightHour = o3EightHour;
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
