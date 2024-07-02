//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.sys.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.service.IFndOrgUnitService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FndOrgUnitController extends BaseController {
    @Autowired
    private IFndOrgUnitService service;

    public FndOrgUnitController() {
    }

    @RequestMapping({"/leaf/fnd/org/unit/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndOrgUnit dto = (FndOrgUnit)param.toJavaObject(FndOrgUnit.class);
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectUnit(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping({"/leaf/fnd/org/unit/query/modify"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam("company_id") Long companyId, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndOrgUnit dto = (FndOrgUnit)param.toJavaObject(FndOrgUnit.class);
        dto.setCompanyId(companyId);
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectUnit(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping({"/leaf/fnd/org/unit/query/queryByUnitId"})
    @ResponseBody
    public ResponseData queryByUnitId(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam("unit_id") Long unitId, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndOrgUnit dto = (FndOrgUnit)param.toJavaObject(FndOrgUnit.class);
        dto.setUnitId(unitId);
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectUnit(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping({"/leaf/fnd/org/unit/queryByCompany"})
    @ResponseBody
    public ResponseData queryByCompany(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam("companyId") Long companyId, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndOrgUnit dto = (FndOrgUnit)param.toJavaObject(FndOrgUnit.class);
        dto.setCompanyId(companyId);
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectUnit(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping({"/leaf/fnd/org/unit/submit"})
    @ResponseBody
    public ResponseData update(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<FndOrgUnit> dto = param.toJavaList(FndOrgUnit.class);
        this.getValidator().validate(dto, result);
        if(result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestCtx = this.createRequestContext(request);

            List<FndOrgUnit> dtoList=this.service.batchUpdate(requestCtx, dto);

            for(FndOrgUnit item:dtoList){
                        if(item.getEasCode()==null){
                            item.setEasCode("HX"+item.getUnitId());
                            this.service.updateByPrimaryKeySelective(requestCtx,item);
                        }
            }


            return new ResponseData();
        }
    }

    @RequestMapping({"/leaf/fnd/org/unit/remove"})
    @ResponseBody
    public ResponseData delete(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<FndOrgUnit> dto = param.toJavaList(FndOrgUnit.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        this.service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping({"/leaf/fnd/org/unit/queryForLovByCompanyCode"})
    @ResponseBody
    public ResponseData queryForLov(@RequestParam(required = false) String companyCode, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        List<FndOrgUnit> dto = null;
        if(companyCode != null) {
            dto = this.service.queryForLovByComapnyCode(requestContext, companyCode, pagenum, pagesize);
        }

        return new ResponseData(dto);
    }

    @RequestMapping({"/fnd/org/unit/mgr/query"})
    @ResponseBody
    public ResponseData unitMgrQuery(@RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        Map para = requestData.getParameter();
        IRequest requestContext = this.createRequestContext(request);
        return para.get("lease_organization") != null?new ResponseData(this.service.queryUnitMgr(requestContext, para.get("lease_organization").toString(), pagenum, pagesize)):new ResponseData();
    }
}
