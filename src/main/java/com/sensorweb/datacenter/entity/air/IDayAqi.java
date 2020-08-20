package com.sensorweb.datacenter.entity.air;

import java.time.Instant;

/**
 * 任意时间段日均数据
 */
public class IDayAqi {
    private String uniqueCode;
    private Instant queryTime;
    private String city;
    private String stationName;
    private String pm25;
    private String pm25IAqi;
    private String pm10;
    private String pm10IAqi;
    private String so2;
    private String so2IAqi;
    private String no2;
    private String no2IAqi;
    private String co;
    private String coIAqi;
    private String o3OneHour;
    private String o3OneHourIAqi;
    private String o3EightHour;
    private String o3EightHourIAqi;
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

    public Instant getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Instant queryTime) {
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

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPm25IAqi() {
        return pm25IAqi;
    }

    public void setPm25IAqi(String pm25IAqi) {
        this.pm25IAqi = pm25IAqi;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm10IAqi() {
        return pm10IAqi;
    }

    public void setPm10IAqi(String pm10IAqi) {
        this.pm10IAqi = pm10IAqi;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getSo2IAqi() {
        return so2IAqi;
    }

    public void setSo2IAqi(String so2IAqi) {
        this.so2IAqi = so2IAqi;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getNo2IAqi() {
        return no2IAqi;
    }

    public void setNo2IAqi(String no2IAqi) {
        this.no2IAqi = no2IAqi;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getCoIAqi() {
        return coIAqi;
    }

    public void setCoIAqi(String coIAqi) {
        this.coIAqi = coIAqi;
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

    public String getO3EightHour() {
        return o3EightHour;
    }

    public void setO3EightHour(String o3EightHour) {
        this.o3EightHour = o3EightHour;
    }

    public String getO3EightHourIAqi() {
        return o3EightHourIAqi;
    }

    public void setO3EightHourIAqi(String o3EightHourIAqi) {
        this.o3EightHourIAqi = o3EightHourIAqi;
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
