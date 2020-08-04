package com.sensorweb.datacenter.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataCenterUtils {

    /**
     * 讲列表内的字符串信息拼成一个字符串，以英文冒号“|”分割
     * @param list
     * @return
     */
    public static String list2String(List<String> list) {
        StringBuilder result = new StringBuilder();

        if (list!=null && list.size()>0) {
            for (String temp : list) {
                result.append("|"+temp);
            }
        }
        return result.toString();
    }

    public static List<String> string2List(String str) {
        List<String> res = new ArrayList<>();

        if(!StringUtils.isBlank(str)) {
            String[] temp = str.split("|");
            res = Arrays.asList(temp);
        }

        return res;
    }

    /**
     * Instant转LocalDateTime
     * @param instant
     * @return
     */
    public static LocalDateTime instant2LocalDateTime(Instant instant) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime;
    }

    public static Instant string2Instant(String date) {
        Date time = null;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (time!=null) {
            return time.toInstant();
        }
        return null;
    }

    /**
     * 字符串转LocalDateTime
     * @param time
     * @return
     * @throws ParseException
     */
    public static LocalDateTime string2LocalDateTime(String time) throws ParseException {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return localDateTime;
    }

    /**
     * 读取文件内容
     * @param filePath
     * @return
     */
    public static String readFromFile(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        StringBuilder stringBuilder = null;

        try {
            if (file.length()!=0) {
                fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                stringBuilder = new StringBuilder();
                while ((line=bufferedReader.readLine())!=null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                isr.close();
                fis.close();
            } else {
                System.out.println("file is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串内容写到文件
     * @param filePath
     * @param content
     */
    public static boolean write2File(String filePath, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath);
            writer.write(content);
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从MultipartFile读取文件内容
     * @param file
     * @return
     */
    public static String readFromMultipartFile(MultipartFile file) {
        StringBuilder stringBuilder = null;
        FileInputStream fis = null;

        try {
            if (file.getSize()>0) {
                InputStreamReader isr = new InputStreamReader(file.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                stringBuilder = new StringBuilder();
                while ((line=bufferedReader.readLine())!=null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                isr.close();
                fis.close();
            } else {
                System.out.println("file is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 生成随机字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");

    }

    /**
     * MD5加密
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * Element转字符串
     * @param element
     * @return
     */
    public static String element2String(Element element) {
        String str = "";
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(element);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(domSource,result);
            str = result.getWriter().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 给矩形框的四个角坐标，生成对应的wkt
     * @return
     */
    public static String coodinate2Wkt(double minX, double minY, double maxX, double maxY) {
        StringBuilder stringBuilder = new StringBuilder("POLYGON(");
        stringBuilder.append(minX);
        stringBuilder.append(" ");
        stringBuilder.append(minY);
        stringBuilder.append(",");
        stringBuilder.append(minX);
        stringBuilder.append(" ");
        stringBuilder.append(maxY);
        stringBuilder.append(",");
        stringBuilder.append(maxX);
        stringBuilder.append(" ");
        stringBuilder.append(minY);
        stringBuilder.append(",");
        stringBuilder.append(maxX);
        stringBuilder.append(" ");
        stringBuilder.append(maxY);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    /**
     * 将bean中的字段和属性值写入到map中
     * @param bean
     * @return
     */
    public static Map<String, Object> bean2Map(Object bean) {
        Map<String, Object> res = new HashMap<>();
        Field[] fields = bean.getClass().getDeclaredFields();
        if (fields.length>0) {
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                if(!accessible){
                    field.setAccessible(true);
                }
                try {
                    res.put(field.getName(), field.get(bean));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            }
        }
        return res;
    }

    /**
     * 日期转字符串
     * @param date
     * @return
     */
    public static String date2Str(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
