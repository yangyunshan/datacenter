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
}
