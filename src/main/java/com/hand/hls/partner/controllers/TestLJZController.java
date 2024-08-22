package com.hand.hls.partner.controllers;

import com.hand.hap.system.controllers.BaseController;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/20 15:58
 */
@Controller
@RequestMapping("/r")
public class TestLJZController extends BaseController {
    @RequestMapping(
            value = {"/test/ljz"},
            method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String a() throws IOException {
        File file = new File("C:\\Users\\PC\\新建文本文档.txt");
        String fileName = file.getName();
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        String encodedFileName = new String(fileNameBytes, StandardCharsets.UTF_8);
        HttpPut httpPut = new HttpPut("http://172.16.0.196:8080/r/api/di/upload");
        try{
            HttpEntity entity = MultipartEntityBuilder.create().setCharset(StandardCharsets.UTF_8).
                    setMode(HttpMultipartMode.BROWSER_COMPATIBLE).
                    addBinaryBody("file",file, ContentType.create("charsets", Charsets.UTF_8),encodedFileName)
                    .addTextBody("fileId","55158d13-b739-4c34-b3bc-2b0973448c13").build();
            httpPut.setEntity(entity);
            httpPut.setHeader( "Authorization", "Bearer "+ "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiYXBpLXJlc291cmNlIl0sImNvbXBhbnlJZCI6NDEsInJvbGVJZHMiOlsxMDE0Nl0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsiZGVmYXVsdCJdLCJleHAiOjE3MjQyMTQyMTcsInVzZXJJZCI6MTAwMDEsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiIsIkFETUlOIl0sImp0aSI6IjQ0ZGUwYzk3LTg5ZmYtNDcyOS1hZTZkLWJlZGE4MmYyNjc0MCIsImNsaWVudF9pZCI6InlsIiwiZW1wbG95ZWVDb2RlIjoiQURNSU4ifQ.aSQMb_Dkz3gSOVZBPBMYnYPePiXZMobUxxyUQMup_Ng");
            CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(100000).build()).build();
            client.execute(httpPut);
        }catch (Exception e){
            return "fail";
        }


        return "success";
    }
}
