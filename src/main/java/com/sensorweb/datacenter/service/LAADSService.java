package com.sensorweb.datacenter.service;

import com.sensorweb.datacenter.dao.EntryMapper;
import com.sensorweb.datacenter.entity.laads.Entry;
import com.sensorweb.datacenter.util.DataCenterConstant;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

@Service
public class LAADSService implements DataCenterConstant {
    private static final Logger logger = Logger.getLogger(LAADSService.class);

    @Autowired
    private EntryMapper entryMapper;

    @Value("${datacenter.path.modis.laads}")
    private String filePath;

    @Value("${datacenter.domain}")
    private String domain;

    @Scheduled(cron = "0 00 23 * * ?")//每天的23:00分执行一次
    public void insertDataByDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar calendar = Calendar.getInstance();
        String start = format.format(calendar.getTime());
//        calendar.add(Calendar.DATE,-1);
        String stop = format.format(calendar.getTime()).replace("00:00:00", "23:59:59");

        String product = "MOD04_3K,MOD021KM,MOD02HKM,MOD02QKM,MOD03";
        int collection = 61;
        String bbox = "102.58689518700001,24.6590085867,102.79493002099998,25.039009510899998";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String document = getModisInfoByOpenSearch(product, collection, start, stop, bbox);
                    List<Entry> entries = getEntryInfo(document);
                    insertData(entries);
                    System.out.println("LAADS接入时间: " + calendar.getTime().toString() + "Status: Success");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    System.out.println("LAADS接入时间: " + calendar.getTime().toString() + "Status: Fail");
                }
            }
        }).start();
    }

    /**
     * 通过API接口获取Modis数据信息，并进行解析，存储
     * @param product
     * @param collection
     * @param start
     * @param stop
     * @param bbox
     * @throws IOException
     */
    public String getModisInfoByOpenSearch(String product, int collection, String start, String stop, String bbox) throws IOException, DocumentException {
        String param = "product=" + URLEncoder.encode(product, "utf-8") + "&collection=" + URLEncoder.encode(String.valueOf(collection), "utf-8") +
                "&start=" + URLEncoder.encode(start, "utf-8") + "&stop=" + URLEncoder.encode(stop, "utf-8") +
                "&bbox=" + URLEncoder.encode(bbox, "utf-8");
        String document = DataCenterUtils.doGet(DataCenterConstant.LAADS_Web_Service + "/getOpenSearch", param);
        return document;
    }

    /**
     * 解析xml文档，获取Entry对象
     * @param document
     * @return
     * @throws DocumentException
     */
    public List<Entry> getEntryInfo(String document) throws DocumentException {
        //读取xml文档，返回Document对象
        Document xmlContent = DocumentHelper.parseText(document);
        Element root = xmlContent.getRootElement();
        List<Entry> entries = new ArrayList<>();
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            if (element.getName().equals("entry")) {
                Entry temp = new Entry();
                for (Iterator j = element.elementIterator(); j.hasNext();) {
                    Element entryElement = (Element) j.next();
                    if (entryElement.getName().equals("id")) {
                        temp.setEntryId(entryElement.getText());
                        continue;
                    }
                    if (entryElement.getName().equals("title")) {
                        temp.setTitle(entryElement.getText());
                        continue;
                    }
                    if (entryElement.getName().equals("updated")) {
                        temp.setUpdated(entryElement.getText());
                        continue;
                    }
                    if (entryElement.getName().equals("link") && entryElement.attributeValue("rel").equals("http://esipfed.org/ns/fedsearch/1.0/data")) {
                        temp.setLink(entryElement.attributeValue("href"));
                        continue;
                    }
                    if (entryElement.getName().equals("start")) {
                        Instant instant = DataCenterUtils.utc2Instant(entryElement.getText());
                        temp.setStart(instant);
                        continue;
                    }
                    if (entryElement.getName().equals("stop")) {
                        if (!entryElement.getText().equals("Not Available")) {
                            Instant instant = DataCenterUtils.utc2Instant(entryElement.getText());
                            temp.setStop(instant);
                            continue;
                        }
                    }
                    if (entryElement.getName().equals("box")) {
                        String[] bbox = entryElement.getText().split(",");
                        StringBuilder wkt = new StringBuilder("POLYGON((");
                        wkt.append(bbox[0]).append(" ").append(bbox[1]).append(",").append(bbox[0]).append(" ").append(bbox[3]).append(",");
                        wkt.append(bbox[2]).append(" ").append(bbox[1]).append(",").append(bbox[2]).append(" ").append(bbox[3]).append(",");
                        wkt.append(bbox[0]).append(" ").append(bbox[1]).append("))");
                        temp.setBbox(wkt.toString());
                        continue;
                    }
                    if (entryElement.getName().equals("summary")) {
                        temp.setSummary(entryElement.getText());
                    }
                }
                entries.add(temp);
            }
        }
        return entries;
    }

    /**
     * 将Entry对象信息注册到数据库中,并下载文件到本地
     * @param entries
     * @return
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertData(List<Entry> entries) {
        int count = 0;
        for (Entry entry:entries) {
            String fileName = entry.getLink().substring(entry.getLink().lastIndexOf("/") + 1);
            try {
                downloadFromUrl(entry.getLink(), fileName, filePath);
                entry.setLink(domain + "/" + filePath.substring(10) + fileName);
                entryMapper.insertData(entry);
                count++;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return count;
    }

    /**
     * 将查询到的Entry对象，封装成O&M格式信息
     */
    public String entry2OM(List<Entry> entries) {
        String res = "";

        return res;
    }

    /**
     * 根据条件查询数据
     */
    public List<Entry> getEntryByConditions(Instant startTime, Instant stopTime, String box) {
        String[] bbox = box.split(",");
        StringBuilder wkt = new StringBuilder("POLYGON((");
        wkt.append(bbox[0]).append(" ").append(bbox[1]).append(",").append(bbox[0]).append(" ").append(bbox[3]).append(",");
        wkt.append(bbox[2]).append(" ").append(bbox[1]).append(",").append(bbox[2]).append(" ").append(bbox[3]).append(",");
        wkt.append(bbox[0]).append(" ").append(bbox[1]).append("))");

        return entryMapper.selectByConditions(startTime, stopTime, wkt.toString());
    }

    /**
     * 根据条件查询数据2
     */
    public List<Entry> getEntryByConditions2(String startTime, String stopTime, String box) throws IOException, DocumentException {
        String product = "MOD04_3K,MOD021KM,MOD02HKM,MOD02QKM,MOD03";
        int collection = 61;
        String bbox = "102.58689518700001,24.6590085867,102.79493002099998,25.039009510899998";
        String document = getModisInfoByOpenSearch(product, collection, startTime, stopTime, bbox);
        List<Entry> entries = getEntryInfo(document);

        return entries;
    }

    /**
     * 通过url下载LAADS文件，需要Token授权的情况下
     * @param url
     * @param fileName
     * @param savePath
     */
    public static void downloadFromUrl(String url, String fileName, String savePath) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36");
            connection.setRequestProperty("Authorization", "Bearer c2Vuc29yd2ViOk1URXdOek0zTWpneE5rQnhjUzVqYjIwPToxNjA1MjUwMDAzOmNjY2JlMzA5NmM2MzI5NjkxMTkyZDA2MDMwOWVkNDlkN2RjYWVmYTg");
            InputStream inputStream = connection.getInputStream();
            byte[] getData = readInputStream(inputStream);
            // 文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static byte[] readInputStream(InputStream inputStream) throws IOException, IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
