package com.sensorweb.datacenter.entity.meteorological;

import java.util.Date;

/**
 * 任意时间段小时均数据
 */
public class IHourAqi {
    private String uniqueCode;
    private Date queryTime;
    private String stationName;
    private double pm25OneHour;
    private double pm25OneHourIAqi;
    private double pm10OneHour;
    private double pm10OneHourIAqi;
    private double so2OneHour;
    private double so2OneHourIAqi;
    private double no2OneHour;
    private double no2OneHourIAqi;
    private double coOneHour;
    private double coOneHourIAqi;
    private double o3OneHour;
    private double o3OneHourIAqi;
    private double aqi;
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

    public double getPm25OneHour() {
        return pm25OneHour;
    }

    public void setPm25OneHour(double pm25OneHour) {
        this.pm25OneHour = pm25OneHour;
    }

    public double getPm25OneHourIAqi() {
        return pm25OneHourIAqi;
    }

    public void setPm25OneHourIAqi(double pm25OneHourIAqi) {
        this.pm25OneHourIAqi = pm25OneHourIAqi;
    }

    public double getPm10OneHour() {
        return pm10OneHour;
    }

    public void setPm10OneHour(double pm10OneHour) {
        this.pm10OneHour = pm10OneHour;
    }

    public double getPm10OneHourIAqi() {
        return pm10OneHourIAqi;
    }

    public void setPm10OneHourIAqi(double pm10OneHourIAqi) {
        this.pm10OneHourIAqi = pm10OneHourIAqi;
    }

    public double getSo2OneHour() {
        return so2OneHour;
    }

    public void setSo2OneHour(double so2OneHour) {
        this.so2OneHour = so2OneHour;
    }

    public double getSo2OneHourIAqi() {
        return so2OneHourIAqi;
    }

    public void setSo2OneHourIAqi(double so2OneHourIAqi) {
        this.so2OneHourIAqi = so2OneHourIAqi;
    }

    public double getNo2OneHour() {
        return no2OneHour;
    }

    public void setNo2OneHour(double no2OneHour) {
        this.no2OneHour = no2OneHour;
    }

    public double getNo2OneHourIAqi() {
        return no2OneHourIAqi;
    }

    public void setNo2OneHourIAqi(double no2OneHourIAqi) {
        this.no2OneHourIAqi = no2OneHourIAqi;
    }

    public double getCoOneHour() {
        return coOneHour;
    }

    public void setCoOneHour(double coOneHour) {
        this.coOneHour = coOneHour;
    }

    public double getCoOneHourIAqi() {
        return coOneHourIAqi;
    }

    public void setCoOneHourIAqi(double coOneHourIAqi) {
        this.coOneHourIAqi = coOneHourIAqi;
    }

    public double getO3OneHour() {
        return o3OneHour;
    }

    public void setO3OneHour(double o3OneHour) {
        this.o3OneHour = o3OneHour;
    }

    public double getO3OneHourIAqi() {
        return o3OneHourIAqi;
    }

    public void setO3OneHourIAqi(double o3OneHourIAqi) {
        this.o3OneHourIAqi = o3OneHourIAqi;
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
