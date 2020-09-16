package com.sensorweb.datacenter.controller;

import com.sensorweb.datacenter.entity.sos.Observation;
import com.sensorweb.datacenter.service.HimawariService;
import com.sensorweb.datacenter.service.sos.GetObservationService;
import com.sensorweb.datacenter.service.sos.InsertObservationService;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vast.ows.sos.GetObservationRequest;
import org.vast.ows.sos.InsertObservationRequest;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Controller
@RequestMapping(path = "/observation")
public class ObservationController {

    @Autowired
    private InsertObservationService insertObservationService;

    @Autowired
    private GetObservationService getObservationService;

    @RequestMapping(path = "/insertObservation", method = RequestMethod.POST)
    public String insertObservation(Model model, String requestContent) {
        return "";
    }

    @RequestMapping(path = "/getObservation", method = RequestMethod.POST)
    public String getObservation(Model model, String requestContent) {
        Element element = null;
        try {
            GetObservationRequest request = getObservationService.getObservationRequest(requestContent);
            List<Observation> observations = getObservationService.getObservationContent(request);

            if (observations!=null && observations.size()>0) {
                for (Observation observation : observations) {
//                    element = getObservationService.getObservationResponse(observation.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (element!=null) {
            model.addAttribute("GetObservationResponse", DataCenterUtils.element2String(element));
        }
        model.addAttribute("GetObservationRequest", requestContent);
        model.addAttribute("tag", "GetObservation");

        return "index";
    }

//    @ResponseBody
//    @RequestMapping(value = "/download/data/{url}")
//    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("url") String fileUrl) throws IOException {
//        int pointIndex = fileUrl.lastIndexOf("/");
//        BufferedInputStream bis;
//        if (pointIndex!=-1) {
//            String fileName = fileUrl.substring(pointIndex, fileUrl.length());
//            URL url = new URL(fileUrl);
//            URLConnection connection = url.openConnection();
//            int fileSize = connection.getContentLength();
//            bis = new BufferedInputStream(connection.getInputStream());
//
//            //清空response
//            response.reset();
//            //文件名称转换编码格式为utf-8，保证不出现乱码，这个文件名称用于浏览器的下载框中自动显示的文件名
//            response.addHeader("Content-Disposition", "attachment;filename="+
//                    new String(fileName.getBytes("utf-8"), "iso8859-1"));
//            response.addHeader("Content-Length", "" + fileSize);
//            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
//
//            //从输入流中读入字节流，然后写到文件中
//            byte[] buffer = new byte[1024];
//            int nRead;
//            while ((nRead = bis.read(buffer, 0, 1024)) > 0) {
//                bos.write(buffer, 0, nRead);
//            }
//            bis.close();
//            bos.flush();
//            bos.close();
//        }
//    }

}
