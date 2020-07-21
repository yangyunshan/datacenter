package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.service.HdfsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.BlockLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/hadoop/hdfs")
public class HdfsController {
    private static Logger LOGGER = LoggerFactory.getLogger(HdfsController.class);

    @Autowired
    private HdfsService hdfsService;

    /**
     * 创建文件夹
     */
    @PostMapping(path = "/mkdir")
    public String mkdir(@RequestParam("path") String path) throws Exception {
        if (StringUtils.isBlank(path)) {
            LOGGER.debug("请求参数为空");
            return "请求参数为空";
        }
        //创建空文件夹
        boolean isOk = hdfsService.mkdir(path);
        if (isOk) {
            LOGGER.debug("文件夹创建成功");
            return "文件夹创建成功";
        } else {
            LOGGER.debug("文件夹创建失败");
            return "文件夹创建失败";
        }
    }

    /**
     * 读取HDFS目录信息(只是读取目录信息，而不是递归列出所有目录)
     */
    @PostMapping("/readPathInfo")
    public List<Map<String, Object>> readPathInfo(@RequestParam("path") String path) throws Exception {
        List<Map<String, Object>> list = hdfsService.readPathInfo(path);
        return list;
    }

    /**
     * 获取HDFS文件在集群中的位置
     * 目前还没有创建集群
     */
    @PostMapping("/getFileBlockLocations")
    public BlockLocation[] getFileBlockLocations(@RequestParam("path") String path) throws Exception {
        BlockLocation[] blockLocations = hdfsService.getFileBlockLocations(path);
        return blockLocations;
    }

    /**
     * 创建文件
     */
    @PostMapping("/createFile")
    public String createFile(@RequestParam("path") String path, MultipartFile file)
            throws Exception {
        if (StringUtils.isEmpty(path) || null == file.getBytes()) {
            return "请求参数为空";
        }
        hdfsService.createFile(path, file);
        return "创建文件成功";
    }

    /**
     * 读取HDFS文件内容
     */
    @PostMapping("/readFile")
    public String readFile(@RequestParam("path") String path) throws Exception {
        String targetPath = hdfsService.readFile(path);
        return targetPath;
    }

    /**
     * 读取HDFS文件转换成Byte类型
     */
    @PostMapping("/openFileToBytes")
    public byte[] openFileToBytes(@RequestParam("path") String path) throws Exception {
        byte[] files = hdfsService.openFileToBytes(path);
        return files;
    }

    /**
     * 读取文件列表
     */
    @PostMapping("/listFile")
    public List<Map<String, String>> listFile(@RequestParam("path") String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        List<Map<String, String>> returnList = hdfsService.listFile(path);
        return returnList;
    }

    /**
     * 重命名文件
     * 可用，参数都是全限定名
     */
    @PostMapping("/renameFile")
    public String renameFile(@RequestParam("oldName") String oldName, @RequestParam("newName") String newName)
            throws Exception {
        if (StringUtils.isEmpty(oldName) || StringUtils.isEmpty(newName)) {
            return "请求参数为空";
        }
        boolean isOk = hdfsService.renameFile(oldName, newName);
        if (isOk) {
            return "文件重命名成功";
        } else {
            return "文件重命名失败";
        }
    }

    /**
     * 删除文件
     */
    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("path") String path) throws Exception {
        boolean isOk = hdfsService.deleteFile(path);
        if (isOk) {
            return "delete file success";
        } else {
            return "delete file fail";
        }
    }

    /**
     * 上传文件
     */
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("path") String path, @RequestParam("uploadPath") String uploadPath)
            throws Exception {
        hdfsService.uploadFile(path, uploadPath);
        return "upload file success";
    }

    /**
     * 下载文件
     */
    @PostMapping("/downloadFile")
    public String downloadFile(@RequestParam("path") String path, @RequestParam("downloadPath") String downloadPath)
            throws Exception {
        hdfsService.downloadFile(path, downloadPath);
        return "download file success";
    }

    /**
     * HDFS文件复制
     */
    @PostMapping("/copyFile")
    public String copyFile(@RequestParam("sourcePath") String sourcePath, @RequestParam("targetPath") String targetPath)
            throws Exception {
        hdfsService.copyFile(sourcePath, targetPath);
        return "copy file success";
    }

    /**
     * 查看文件是否已存在
     */
    @PostMapping("/existFile")
    public String existFile(@RequestParam("path") String path) throws Exception {
        boolean isExist = hdfsService.existFile(path);
        return "file isExist: " + isExist;
    }
}
