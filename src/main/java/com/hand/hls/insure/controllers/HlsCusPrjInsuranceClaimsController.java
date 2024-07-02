package com.hand.hls.insure.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.insure.dto.HlsCusPrjInsuranceClaims;
import com.hand.hls.insure.service.HlsCusPrjInsuranceClaimsService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusPrjInsuranceClaimsController extends BaseController {

    @Autowired
    private HlsCusPrjInsuranceClaimsService service;


    @RequestMapping(value = "/prj/insurance/claims/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjInsuranceClaims dto = param.toJavaObject(HlsCusPrjInsuranceClaims.class);
        List<HlsCusPrjInsuranceClaims> list = service.select(requestContext, dto, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/insurance/claims/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjInsuranceClaims> list = param.toJavaList(HlsCusPrjInsuranceClaims.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/prj/insurance/claims/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjInsuranceClaims> list = param.toJavaList(HlsCusPrjInsuranceClaims.class);
        service.batchDelete(list);
        return new ResponseData(list);
    }

    @RequestMapping(value ="/prj/insurance/claims/file/query")
    @ResponseBody
    public ResponseData queryAllFile(final HlsCusPrjInsuranceClaims dto, final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjInsuranceClaims> dtoList = service.queryAllFile(requestContext,dto);
        String securityKey = TokenUtils.getSecurityKey(request.getSession());
        SysFile file = new SysFile();
        for (HlsCusPrjInsuranceClaims f : dtoList) {
            file.setFileId(Long.parseLong(f.getFileId()));
            f.set_token(TokenUtils.generateToken(securityKey, file));
        }
        return new ResponseData(dtoList);
    }

}