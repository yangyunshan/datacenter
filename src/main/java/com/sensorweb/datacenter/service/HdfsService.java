package com.sensorweb.datacenter.service;

import net.minidev.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.util.IOUtilsClient;
import org.apache.hadoop.hdfs.web.JsonUtil;
import org.apache.hadoop.hdfs.web.JsonUtilClient;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HdfsService {

    @Value("${hdfs.path}")
    private String hdfsPath;

    @Value("${hdfs.username}")
    private String hdfsName;

    private static final int bufferSize = 1024 * 1024 * 64;

    /**
     * 获取HDFS配置信息
     */
    private Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", hdfsPath);
        return configuration;
    }

    /**
     * 获取HDFS文件系统对象
     */
    public FileSystem getFileSystem() throws Exception {
        // 客户端去操作hdfs时是有一个用户身份的，默认情况下hdfs客户端api会从jvm中获取一个参数作为自己的用户身份
        // DHADOOP_USER_NAME=hadoop
        // 也可以在构造客户端fs对象时，通过参数传递进去
        FileSystem fileSystem = FileSystem.get(new URI(hdfsPath), getConfiguration(), hdfsName);
        return fileSystem;
    }

    /**
     * 在HDFS上创建文件夹
     */
    public boolean mkdir(String dir) throws Exception {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        if (existFile(dir)) {
            return true;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(dir);
        boolean isOk = fs.mkdirs(srcPath);
        fs.close();
        return isOk;
    }

    /**
     * 删除文件夹
     */
    public boolean deleteDir(String dir) throws Exception {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        if (!existFile(dir)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        Path path = new Path(dir);
        boolean isOk = fs.delete(path, true);
        fs.close();
        return isOk;
    }

    /**
     * 判断File or Path是否存在
     */
    public boolean existFile(String fileOrPath) throws Exception {
        if (StringUtils.isBlank(fileOrPath)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(fileOrPath);
        boolean isExists = fs.exists(srcPath);
        return isExists;
    }

    /**
     * 读取HDFS目录信息(只是读取目录信息，而不是递归列出所有目录)
     */
    public List<Map<String, Object>> readPathInfo(String fileOrPath) throws Exception {
        if (StringUtils.isBlank(fileOrPath)) {
            return null;
        }
        if (!existFile(fileOrPath)) {
            return null;
        }
        FileSystem fileSystem = getFileSystem();
        Path newPath = new Path(fileOrPath);
        FileStatus[] statusList = fileSystem.listStatus(newPath);
        List<Map<String, Object>> list = new ArrayList<>();
        if (statusList != null && statusList.length > 0) {
            for (FileStatus fileStatus : statusList) {
                Map<String, Object> map = new HashMap<>();
                map.put("filePath", fileStatus.getPath());
                map.put("fileStatus", fileStatus.toString());
                list.add(map);
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * HDFS创建文件
     */
    public void createFile(String filePath, MultipartFile file) throws Exception {
        if (StringUtils.isBlank(filePath) || file.getBytes() ==null) {
            return;
        }
        String fileName = file.getOriginalFilename();
        FileSystem fs = getFileSystem();
        //上传时默认当前目录，后面自动拼接文件的目录
        Path newPath = new Path(filePath + "/" + fileName);
        //打开一个输出流
        FSDataOutputStream outputStream = fs.create(newPath);
        outputStream.write(file.getBytes());
        outputStream.close();
        fs.close();
    }

    /**
     * 读取文件内容
     */
    public String readFile(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        if (!existFile(filePath)) {
            return null;
        }
        FileSystem fs = getFileSystem();

        Path srcPath = new Path(filePath);
        FSDataInputStream inputStream = null;
        try {
            inputStream = fs.open(srcPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String lineTxt = "";
            StringBuffer sb = new StringBuffer();
            while ((lineTxt = reader.readLine()) != null) {
                sb.append(lineTxt);
            }
            return sb.toString();
        } finally {
            inputStream.close();
            fs.close();
        }
    }

    /**
     * 读取HDFS文件列表
     */
    public List<Map<String, String>> listFile(String path) throws Exception {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(path);
        RemoteIterator<LocatedFileStatus> fileList = fs.listFiles(srcPath, true);
        List<Map<String, String>> returnList = new ArrayList<>();
        while (fileList.hasNext()) {
            LocatedFileStatus next = fileList.next();
            String fileName = next.getPath().getName();
            Path filePath = next.getPath();
            Map<String, String> map = new HashMap<>();
            map.put("fileName", fileName);
            map.put("filePath", filePath.toString());
            returnList.add(map);
        }
        fs.close();
        return returnList;
    }

    /**
     * HDFS重命名文件
     */
    public boolean renameFile(String oldName, String newName) throws Exception {
        if (StringUtils.isBlank(oldName) || StringUtils.isBlank(newName)) {
            return false;
        }
        FileSystem fs = getFileSystem();

        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        boolean isOk = fs.rename(oldPath, newPath);
        fs.close();
        return isOk;
    }

    /**
     * 删除HDFS文件
     */
    public boolean deleteFile(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        if (!existFile(filePath)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(filePath);
        boolean isOk = fs.deleteOnExit(srcPath);
        fs.close();
        return isOk;
    }

    /**
     * 上传HDFS文件
     */
    public void uploadFile(String localFilePath, String uploadFilePath) throws Exception {
        if (StringUtils.isBlank(localFilePath) || StringUtils.isBlank(uploadFilePath)) {
            return;
        }
        FileSystem fs = getFileSystem();
        //上传路径
        Path clientPath = new Path(localFilePath);
        //目标路径
        Path serverpath = new Path(uploadFilePath);
        //调用文件系统的文件复制方法，第一个参数是否删除原文件true为删除，默认为false
        fs.copyFromLocalFile(false, clientPath, serverpath);
        fs.close();
    }

    /**
     * 下载HDFS文件
     */
    public void downloadFile(String sourceFilePath, String downloadFilePath) throws Exception {
        if (StringUtils.isBlank(sourceFilePath) || StringUtils.isBlank(downloadFilePath)) {
            return;
        }
        FileSystem fs = getFileSystem();

        Path clientPath = new Path(sourceFilePath);
        Path serverPath = new Path(downloadFilePath);

        fs.copyToLocalFile(false, clientPath, serverPath);
        fs.close();
    }

    /**
     * HDFS文件复制
     * @Param sourcePath和targetPath都要为文件的全限定名
     */
    public void copyFile(String sourceFilePath, String targetFilePath) throws Exception {
        if (StringUtils.isBlank(sourceFilePath) || StringUtils.isBlank(targetFilePath)) {
            return;
        }
        FileSystem fs = getFileSystem();
        Path oldPath = new Path(sourceFilePath);
        Path newPath = new Path(targetFilePath);

        FSDataOutputStream outputStream = null;
        FSDataInputStream inputStream = null;

        try {
            inputStream = fs.open(oldPath);
            outputStream = fs.create(newPath);
            IOUtils.copyBytes(inputStream, outputStream, bufferSize, false);
        } finally {
            inputStream.close();
            outputStream.close();
            fs.close();
        }
    }

    /**
     * 打开HDFS上的文件并返回byte数组
     */
    public byte[] openFileToBytes(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        if (!existFile(filePath)) {
            return null;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(filePath);
        try {
            FSDataInputStream inputStream = fs.open(srcPath);
            return IOUtils.readFullyToByteArray(inputStream);
        } finally {
            fs.close();
        }
    }

    /**
     * 获取某个文件在集群中的位置
     */
    public BlockLocation[] getFileBlockLocations(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        if (!existFile(filePath)) {
            return null;
        }
        FileSystem fs = getFileSystem();

        Path srcPath = new Path(filePath);
        FileStatus fileStatus = fs.getFileStatus(srcPath);
        return fs.getFileBlockLocations(fileStatus,0,fileStatus.getLen());
    }
}
