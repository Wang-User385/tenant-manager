//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.BusinessCategory;
import com.hand.hls.bp.dto.BusinessTypeWork;
import com.hand.hls.bp.service.IBusinessCgService;
import com.hand.hls.bp.service.IBusinessTyService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BusinessCgController extends BaseController {
    @Autowired
    private IBusinessCgService iBusinessCgService;
    @Autowired
    private IBusinessTyService iBusinessTyService;

    public BusinessCgController() {
    }

    @RequestMapping(
            value = {"/hls_businessCg/query"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData selectList(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(value = "enabled_flag",required = false) String enabledFlag, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        BusinessCategory businessCategory = (BusinessCategory)param.toJavaObject(BusinessCategory.class);
        if (StringUtils.isNotEmpty(enabledFlag)) {
            businessCategory.setEnabledFlag(enabledFlag);
        }

        return new ResponseData(this.iBusinessCgService.select(requestContext, businessCategory, pagenum, pagesize));
    }

    @RequestMapping(
            value = {"/hls_businessCg/queryEnable"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData selectEnableList(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(value = "enabled_flag",required = false) String enabledFlag, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        BusinessCategory businessCategory = (BusinessCategory)param.toJavaObject(BusinessCategory.class);
        businessCategory.setEnabledFlag("Y");
        return new ResponseData(this.iBusinessCgService.select(requestContext, businessCategory, pagenum, pagesize));
    }

    @RequestMapping(
            value = {"/hls_businessCg/query/modify"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData selectTypeList(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        BusinessTypeWork businessCategory = (BusinessTypeWork)param.toJavaObject(BusinessTypeWork.class);
        return new ResponseData(this.iBusinessTyService.select(requestContext, businessCategory, pagenum, pagesize));
    }

    @RequestMapping(
            value = {"/hls_businessCg/submit"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        ResponseData rd = null;
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<BusinessCategory> businessCategory = parameter.toJavaList(BusinessCategory.class);
        this.getValidator().validate(businessCategory, result);

        try {
            IRequest requestContext = this.createRequestContext(request);
            return new ResponseData(this.iBusinessCgService.batchUpdate(requestContext, businessCategory));
        } catch (Exception var8) {
            rd = new ResponseData(false);
            rd.setMessage("商业伙伴类型不可重复！");
            return rd;
        }
    }

    @RequestMapping(
            value = {"/hls_businessCg/delete"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<BusinessCategory> businessCategory = parameter.toJavaList(BusinessCategory.class);
        IRequest requestContext = this.createRequestContext(request);
        this.iBusinessCgService.batchDelete(businessCategory);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(businessCategory);
    }

    @RequestMapping({"/usage/category/query"})
    @ResponseBody
    public ResponseData usageCategory(HttpServletRequest request) {
        List<BusinessCategory> datas = this.iBusinessCgService.usageCategory();
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(datas);
    }

    @RequestMapping({"/bp/master/other/query"})
    @ResponseBody
    public ResponseData bpMasterOtherQuery(HttpServletRequest request, BusinessCategory businessCategory, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        this.createRequestContext(request);
        List<BusinessCategory> datas = this.iBusinessCgService.bpMasterOtherQuery(businessCategory, page, pagesize);
        return new ResponseData(datas);
    }
}
