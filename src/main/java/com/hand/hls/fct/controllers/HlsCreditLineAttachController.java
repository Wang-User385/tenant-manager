package com.hand.hls.fct.controllers;

import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceAttachMapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.service.IHlsCreditLineAttachService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsCreditLineAttachController extends BaseController{

    @Autowired
    private IHlsCreditLineAttachService service;

    @Autowired
    private HlsCusHlsCreditLineChanceAttachMapper hlsCusHlsCreditLineChanceAttachMapper;

    @RequestMapping(value = "/hls/credit/line/attach/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCreditLineAttach dto = param.toJavaObject(HlsCreditLineAttach.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/credit/line/attach/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCreditLineAttach> list = param.toJavaList(HlsCreditLineAttach.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/credit/line/attach/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCreditLineAttach> dto = parameter.toJavaList(HlsCreditLineAttach.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/credit/download")
    @ResponseBody
    public void download(@RequestParam("chanceId") Long chanceId,
                         @RequestParam("attachmentCategory") String attachmentCategory,
                         HttpServletResponse response, HttpServletRequest request) throws ResMessageException {
        HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach = new HlsCusHlsCreditLineChanceAttach();
        hlsCusHlsCreditLineChanceAttach.setChanceId(chanceId);
        hlsCusHlsCreditLineChanceAttach.setAttachmentCategory(attachmentCategory);
        //获取所有的数据
        List<HlsCusHlsCreditLineChanceAttach> hlsCusHlsCreditLineChanceAttaches = hlsCusHlsCreditLineChanceAttachMapper.findListHlsCusHlsCreditLineChanceAttach(hlsCusHlsCreditLineChanceAttach);
        if (!CollectionUtils.isEmpty(hlsCusHlsCreditLineChanceAttaches)) {
            String zipFilePath = "";
            String fileName = "";
            List<HlsCusSysFile> hlsCusSysFiles = new ArrayList<>();
            for (HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach1 : hlsCusHlsCreditLineChanceAttaches) {
                HlsCusSysFile hlsCusSysFile = new HlsCusSysFile();
                String filePath = hlsCusHlsCreditLineChanceAttach1.getFilePath();
                String fileName1 = hlsCusHlsCreditLineChanceAttach1.getFileName();
                hlsCusSysFile.setFilePath(filePath);
                hlsCusSysFile.setFileName(fileName1);
                hlsCusSysFiles.add(hlsCusSysFile);
            }
            if (!CollectionUtils.isEmpty(hlsCusSysFiles)) {
                File zipFilePath1 = new File(zipFilePath);
                //拼接文件名,用户名+系统时间,避免出现重复
                fileName = "downloadZip_" + System.currentTimeMillis();
                //String zipFile = "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1") + ".zip";
                String zipFile = zipFilePath1 + fileName + ".zip";
                try {
                    FileOutputStream outStream = new FileOutputStream(zipFile);
                    ZipOutputStream toClient = new ZipOutputStream(outStream);
                    //打包转换为zip文件
                    HlsCusZipUtil.zipFile(hlsCusSysFiles, toClient);
                    toClient.close();
                    outStream.close();
                    //下载zip文件
                    HlsCusZipUtil.downloadZip(new File(zipFile), response);
                } catch (Exception e) {
                    throw new ResMessageException("一键下载异常");
                }
            }

        }
    }
}