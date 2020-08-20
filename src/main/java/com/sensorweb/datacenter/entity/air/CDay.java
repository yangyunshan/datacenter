package com.sensorweb.datacenter.entity.air;

import java.time.Instant;

/**
 * 指定时间审核日均
 */
public class CDay {
    private String uniqueCode;
    private Instant sDateTime;
    private String so2;
    private String no2;
    private String pm10;
    private String co;
    private String o3EightHour;
    private String pm25;
    private String aqi;
    private String primaryEP;
    private String aqType;

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Instant getsDateTime() {
        return sDateTime;
    }

    public void setsDateTime(Instant sDateTime) {
        this.sDateTime = sDateTime;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getO3EightHour() {
        return o3EightHour;
    }

    public void setO3EightHour(String o3EightHour) {
        this.o3EightHour = o3EightHour;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
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

    public String getAqType() {
        return aqType;
    }

    public void setAqType(String aqType) {
        this.aqType = aqType;
    }
}
