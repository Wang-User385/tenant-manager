package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusWriteOffMatch;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.exception.WriteOffTypeNullException;
import com.hand.hls.csh.service.HlsCusIWriteOffMatchService;
import com.hand.hls.utils.AttachmentHttpUtils;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

@Controller
public class HlsCusWriteOffMatchController extends BaseController {

    @Autowired
    private HlsCusIWriteOffMatchService service;


    @RequestMapping(value = "/csh/write/off/match/query")
    @ResponseBody
    public ResponseData query(HlsCusWriteOffMatch dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/csh/write/off/match/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusWriteOffMatch> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/csh/write/off/match/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusWriteOffMatch> dto) {
        service.batchDelete(dto);
        return new ResponseData(new ArrayList<>(dto));
    }


    @RequestMapping({"/csh/write/off/match/submitMatch"})
    @ResponseBody
    public ResponseData submitWriteOffMatch(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request, HttpSession session) throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException {
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            JSONArray parameter = (JSONArray) requestData.get("parameter");
            List<HlsCusCshWriteOff> hlsCusCshWriteOffs = parameter.toJavaList(HlsCusCshWriteOff.class);

            this.service.saveWriteOffMatch(RequestHelper.getCurrentRequest(), hlsCusCshWriteOffs, session);
            return new ResponseData();
        }
    }

    @RequestMapping(value = "/csh/write/off/match/queryHaveMatch")
    @ResponseBody
    public ResponseData queryHaveMatch(HlsCusWriteOffMatch dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectWriteOffMatch(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/csh/write/off/match/backMatch")
    @ResponseBody
    public ResponseData backMatch(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshTransaction cshTransaction = param.toJavaObject(HlsCusCshTransaction.class);
        service.updateWriteOffMatchBack(requestContext, cshTransaction);
        return new ResponseData();
    }
    @RequestMapping(value = "/csh/attach/file/upload")
    public void detail(HttpServletRequest request, HttpServletResponse response) throws FileReadIOException, TokenException {
        IRequest requestContext = createRequestContext(request);
        try {
            String fileName = request.getSession().getServletContext().getRealPath("/")+ "resources/excel/CSH/收款导入模板.xlsx";
            File file = new File(fileName);
            String name =  "收款导入模板.xlsx";
            boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
            if (isMSIE) {
                name = URLEncoder.encode(name, "UTF-8");
            } else {
                name = new String(name.getBytes("UTF-8"), "ISO-8859-1");
            }
            if (file.exists()) {
                response.addHeader("Content-Disposition","attachment;filename=\"" + name + "\"");
                response.setContentType("EXCEL;charset=UTF-8");
                response.setHeader("Accept-Ranges", "bytes");
                int fileLength = (int) file.length();
                response.setContentLength(fileLength);
                if (fileLength > 0) {
                    writeFileToResp(response, file);
                }
            } else {
                response.getWriter().write("文件不存在！");
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }
    }
    private void writeFileToResp(HttpServletResponse response, File file) throws FileNotFoundException, IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        try (InputStream inStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();
        }
    }
}