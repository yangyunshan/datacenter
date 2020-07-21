package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.Procedure;
import com.sensorweb.datacenter.service.HandleSensorMLService;
import com.sensorweb.datacenter.service.QueryDataService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.sensorml.v20.Mode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping(path = "/sensor")
public class SensorController {
    private static Logger LOGGER = LoggerFactory.getLogger(HdfsController.class);

    @Autowired
    private HandleSensorMLService handleSensorMLService;

    @Autowired
    private QueryDataService queryDataService;

    @Value("${datacenter.path.domain}")
    private String domin;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${datacenter.path.upload}")
    private String uploadPath;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public String registrySensor(MultipartFile file, Model model) {
        if (file==null) {
            model.addAttribute("error", "您还没有选择文件");
            return "index";
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || !suffix.equals(".xml")) {
            model.addAttribute("error", "文件格式不正确");
        }

        //生产随机文件名
        fileName = DataCenterUtils.generateUUID() + suffix;
        //确定文件存放路径
        String path = uploadPath + "/" + fileName;
        File dest = new File(path);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败", e);
        }

        try {
            String sensorml = DataCenterUtils.readFromFile(path);
            String descriptionFilePath = domin + contextPath + "/sensor/getContent/" + fileName;

            handleSensorMLService.storePhysicalSystemData(sensorml,descriptionFilePath);

            model.addAttribute("success", "注册成功");
        } catch (Exception e) {
            dest.delete();
            throw new RuntimeException("注册传感器失败", e);
        }

        return "index";
    }

    @RequestMapping(path = "/getContent/{descriptionFilePath}", method = RequestMethod.GET)
    public String getContent(@PathVariable("descriptionFilePath") String descriptionFilePath, Model model) {
        //服务器存放地址
        descriptionFilePath = uploadPath + "/" + descriptionFilePath;
        String content = DataCenterUtils.readFromFile(descriptionFilePath);
        model.addAttribute("sensorMLContent", content);
        return "index";
    }

    @RequestMapping(path = "/queryByIdentifier", method = RequestMethod.GET)
    public String querySensorByIdentifier(Model model, String identifier) {
        List<Procedure> procedures = queryDataService.getProcedureByFuzzyIdentifier(identifier);

        if (procedures!=null && procedures.size()>0) {
            model.addAttribute("procedures", procedures);
        }

        return "index";
    }

    @RequestMapping(path = "/queryAllSensor", method = RequestMethod.GET)
    public String querySensorMLByIdentifier(Model model) {
        List<Procedure> procedures = queryDataService.getAllProcedures();

        if (procedures!=null && procedures.size()>0) {
            model.addAttribute("procedures", procedures);
        }

        return "index";
    }

    @RequestMapping(path = "/queryByStatus", method = RequestMethod.GET)
    public String querySensorByStatus(Model model, char status) {
        List<Procedure> procedures = queryDataService.getProcedureByStatus(status);

        if (procedures!=null && procedures.size()>0) {
            model.addAttribute("procedures", procedures);
        }

        return "index";
    }

    @RequestMapping(path = "/queryByName", method = RequestMethod.GET)
    public String querySensorByName(Model model, String name) {
        List<Procedure> procedures = queryDataService.getProcedureByName(name);

        if (procedures!=null && procedures.size()>0) {
            model.addAttribute("procedures", procedures);
        }

        return "index";
    }
}
