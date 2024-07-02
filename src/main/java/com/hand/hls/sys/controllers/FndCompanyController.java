//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.sys.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.gld.dto.FinancialAttribute;
import com.hand.hls.gld.service.IFinancialAttributeService;
import com.hand.hls.sys.service.IFndCompanyService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class FndCompanyController extends BaseController {
    @Autowired
    private IFndCompanyService fndCompanyService;

    @Autowired
    private IFinancialAttributeService financialAttributeService;

    public FndCompanyController() {
    }

    @RequestMapping({"/leaf/fnd/company/querydetail"})
    @ResponseBody
    public ResponseData querydetail(FndCompany company, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestCtx = this.createRequestContext(request);
        return company != null && company.getCompanyId() != null ? new ResponseData(this.fndCompanyService.selectCompanyDetail(requestCtx, company)) : new ResponseData(false);
    }

    @RequestMapping({"/leaf/fnd/company/query"})
    @ResponseBody
    public ResponseData fuzzQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndCompany company = (FndCompany)param.toJavaObject(FndCompany.class);
        IRequest requestCtx = this.createRequestContext(request);
        return new ResponseData(this.fndCompanyService.queryAll(requestCtx, company, pagenum, pagesize));
    }

    @PostMapping({"/leaf/fnd/company/submit"})
    public ResponseData leafSubmitCompany(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndCompany> resources = parameter.toJavaList(FndCompany.class);
        resources.forEach((item) -> {
            item.setCompanyShortName(item.getCompanyFullName());
            if ("update".equals(item.get__status()) && null == item.getParentCompanyId()) {
                item.setParentCompanyId("");
            }

        });
        this.getValidator().validate(resources, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestContext = this.createRequestContext(request);
            List<FndCompany> resourcesNew = this.fndCompanyService.batchUpdate(requestContext, resources);
            for(int i=0;i<resourcesNew.size();i++){
                if(resourcesNew.get(i).getWsComCode()==null){
                    resourcesNew.get(i).setWsComCode("HX"+resourcesNew.get(i).getCompanyId());
                    fndCompanyService.updateByPrimaryKeySelective(requestContext,resourcesNew.get(i));
                }

                Long setOfBooksId =resources.get(i).getSetOfBooksId();
                FinancialAttribute financialAttribute = new FinancialAttribute();
                financialAttribute.setCompanyId(resourcesNew.get(i).getCompanyId());
                financialAttribute.setSetOfBooksId(setOfBooksId);
                FinancialAttribute financialAttributeOld = financialAttributeService.selectByPrimaryKey(requestContext,financialAttribute);
                if(financialAttributeOld == null){
                    financialAttribute.set__status("add");
                    financialAttributeService.insertSelective(requestContext,financialAttribute);
                }else{
                    financialAttribute.set__status("update");
                    financialAttributeService.updateByPrimaryKeySelective(requestContext,financialAttribute);
                }
            }
            return new ResponseData(resourcesNew);
        }
    }

    @RequestMapping({"/leaf/fnd/company/remove"})
    @ResponseBody
    public ResponseData delete(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<FndCompany> dto = param.toJavaList(FndCompany.class);
        IRequest requestContext = this.createRequestContext(request);
        this.fndCompanyService.batchDelete(requestContext, dto);
        return new ResponseData(dto);
    }

    @RequestMapping({"/leaf/fnd/company/queryAllForLov"})
    @ResponseBody
    public ResponseData queryAllForLov(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        FndCompany company = (FndCompany)param.toJavaObject(FndCompany.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.fndCompanyService.queryAllForLov(requestCtx, company, pagenum, pagesize));
    }
}
