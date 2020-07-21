package com.sensorweb.datacenter.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@Service
public class HttpService {

    /**
     * 向指定的url发送GET请求
     * @param url
     * @param param
     * @return
     */
    public String doGet(String url, String param) {
        String result = "";
        String totalUrl = url + "?" + param;
        try {
            URL realUrl = new URL(totalUrl);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MISE 6.0; Windows NT 5.1; SV1)");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null)
                result += line;
        } catch (Exception e) {
            System.out.println("发送Get请求异常");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 向指定url发送post请求
     * @param url
     * @param param
     * @return
     */
    public String doPost(String url, String param) {
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MISE 6.0; Windows NT 5.1; SV1)");
            //发送post请求必须设置以下两行
            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            //发送请求参数
            writer.print(param);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求失败");
            e.printStackTrace();
        }
        return result;
    }

}
