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
import java.nio.charset.StandardCharsets;
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
     * 去除重复元素
     * @param lists
     * @return
     */
    public static List<String> removeDuplicate(List<List<String>> lists) {
        List<String> res = new ArrayList<>();
        if (lists!=null && lists.size()>0) {
            res = lists.get(0);
            for (int i = 0; i < lists.size(); i++) {
                res.retainAll(lists.get(i));
            }
        }
        return res;
    }

    /**
     * 去掉字符串的后缀
     * @param suffix
     * @param str
     * @return
     */
    public static String removeSuffix(String suffix, String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return str.substring(0, str.indexOf(suffix));
    }

    /**
     * 分割=两边的值
     * @param str
     * @return
     */
    public static String splitEqula(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] temp = str.split("=");
        return temp[1].trim();
    }

    /**
     * 解析SWE数据模型字符串，转换为K-V形式
     * @param str
     * @return
     */
    public static Map<String, String> string2Map(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        Map<String, String> res = new HashMap<>();

        if (str.startsWith("DataRecord")) {
            String[] temp = str.split("\n");
            for (int i = 1; i< temp.length; i++) {
                String[] kvValue = temp[i].split(":");
                if (kvValue.length>1) {
                    res.put(kvValue[0].trim(), splitEqula(kvValue[1].trim()));
                }
            }
        } else if (str.startsWith("Text")) {
            String[] temp = str.split("=");
            res.put(temp[0].trim(), temp[1].trim());
        } else if (str.startsWith("POINT")) {
            String temp = str.substring(str.indexOf("("));
            //StringUtils.strip();去掉字符串两边的字符
            String[] xy = StringUtils.strip(temp,"()").split(" ");
            if (xy.length>1) {
                res.put("Lon", xy[0]);
                res.put("Lat", xy[1]);
            }
        } else if (str.startsWith("Vector")) {
            String[] temp = str.split("\n");
            for (int i = 1; i< temp.length; i = i+2) {
                res.put(temp[i].trim().substring(0, temp[i].trim().indexOf(":")), splitEqula(temp[i+1]));
            }
        }

        return res;
    }

    /**
     * 讲列表内的字符串信息拼成一个字符串，以英文空格“ ”分割
     * @param list
     * @return
     */
    public static String list2String(List<String> list) {
        StringBuilder result = new StringBuilder();

        if (list!=null && list.size()>0) {
            for (String temp : list) {
                result.append(temp);
                result.append(" ");
            }
        }
        return result.toString().trim();
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
        Set<String> ss = ZoneId.getAvailableZoneIds();
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
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
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
