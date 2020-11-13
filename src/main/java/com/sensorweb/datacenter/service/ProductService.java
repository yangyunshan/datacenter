package com.sensorweb.datacenter.service;

import com.sensorweb.datacenter.dao.ProductMapper;
import com.sensorweb.datacenter.entity.Product;
import com.sensorweb.datacenter.util.DataCenterUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.List;


@Service
public class ProductService {
    private static final Logger logger = Logger.getLogger(ProductService.class);

    @Autowired
    ProductMapper productMapper;

    @Value("${datacenter.path.product}")
    private String filePath;

    @Value("${datacenter.domain}")
    private String domain;

    public List<Product> getproductByServiceAndTime(String service, String time) {
        return productMapper.selectByServiceAndTime(service, time);
    }

    public void InsertProduct(Product info) {
        String url = info.getDownloadAddress();
        String serviceName = info.getServiceName();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        try {
            if (serviceName.equals("PMStation") && url.endsWith(".tif")) {
                File temp = new File(filePath + "pmStation_tif/");
                if (!temp.exists()) {
                    temp.mkdirs();
                }
                DataCenterUtils.downloadHttpUrl(url, filePath + "pmStation_tif/", fileName);
                info.setDownloadAddress(domain + "/" + filePath.substring(10) + "pmStation_tif/" + fileName);
            } else {
                File file = new File(filePath + serviceName + "/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                DataCenterUtils.downloadHttpUrl(url, filePath + serviceName + "/", fileName);
                info.setDownloadAddress(domain + "/" + filePath.substring(10) + serviceName + "/" + fileName);
            }
            productMapper.insertSelective(info);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
