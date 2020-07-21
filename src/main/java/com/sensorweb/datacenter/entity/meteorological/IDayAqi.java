package com.sensorweb.datacenter.entity.meteorological;

import java.util.Date;

/**
 * 任意时间段日均数据
 */
public class IDayAqi {
    private String uniqueCode;
    private Date queryTime;
    private String city;
    private String stationName;
    private double pm25;
    private double pm25IAqi;
    private double pm10;
    private double pm10IAqi;
    private double so2;
    private double so2IAqi;
    private double no2;
    private double no2IAqi;
    private double co;
    private double coIAqi;
    private double o3OneHour;
    private double o3OneHourIAqi;
    private double o3EightHour;
    private double o3EightHourIAqi;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getPm25IAqi() {
        return pm25IAqi;
    }

    public void setPm25IAqi(double pm25IAqi) {
        this.pm25IAqi = pm25IAqi;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public double getPm10IAqi() {
        return pm10IAqi;
    }

    public void setPm10IAqi(double pm10IAqi) {
        this.pm10IAqi = pm10IAqi;
    }

    public double getSo2() {
        return so2;
    }

    public void setSo2(double so2) {
        this.so2 = so2;
    }

    public double getSo2IAqi() {
        return so2IAqi;
    }

    public void setSo2IAqi(double so2IAqi) {
        this.so2IAqi = so2IAqi;
    }

    public double getNo2() {
        return no2;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public double getNo2IAqi() {
        return no2IAqi;
    }

    public void setNo2IAqi(double no2IAqi) {
        this.no2IAqi = no2IAqi;
    }

    public double getCo() {
        return co;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public double getCoIAqi() {
        return coIAqi;
    }

    public void setCoIAqi(double coIAqi) {
        this.coIAqi = coIAqi;
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

    public double getO3EightHour() {
        return o3EightHour;
    }

    public void setO3EightHour(double o3EightHour) {
        this.o3EightHour = o3EightHour;
    }

    public double getO3EightHourIAqi() {
        return o3EightHourIAqi;
    }

    public void setO3EightHourIAqi(double o3EightHourIAqi) {
        this.o3EightHourIAqi = o3EightHourIAqi;
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
