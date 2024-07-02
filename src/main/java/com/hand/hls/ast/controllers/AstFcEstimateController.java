//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.service.IAstFcEstimateResultService;
import com.hand.hls.ast.service.IAstFcEstimateService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.hn.service.IPrjCheckPlanService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AstFcEstimateController extends BaseController {
    @Autowired
    private IAstFcEstimateService astFcEstimateService;
    @Autowired
    private IAstFcEstimateResultService astFcEstimateResultService;
    @Autowired
    private PrjCheckPlanMapper prjCheckPlanMapper;
    @Autowired
    private IPrjCheckPlanService prjCheckPlanService;
    public AstFcEstimateController() {
    }

    @RequestMapping({"/ast/fc/estimate/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Long companyId = requestContext.getCompanyId();
        return companyId == null ? new ResponseData(false, "请求参数有误，请联系管理员:company_id不能为空") : this.astFcEstimateService.queryByRequestData(requestContext, requestData, pagenum, pagesize);
    }

    @RequestMapping({"/ast/fc/estimate/submit"})
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<AstFcEstimate> list = parameter.toJavaList(AstFcEstimate.class);
        IRequest requestContext = this.createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        if (companyId == null) {
            return new ResponseData(false, "请求参数有误，请联系管理员:未找到companyId");
        } else {
            Iterator var8 = list.iterator();

            while(var8.hasNext()) {
                AstFcEstimate dto = (AstFcEstimate)var8.next();
                if (dto.getCompanyId() == null) {
                    dto.setCompanyId(companyId);
                }

                if (dto.getEstimateDate() == null) {
                    dto.setEstimateDate(new Date());
                }
            }

            this.getValidator().validate(list, result);
            if (result.hasErrors()) {
                ResponseData responseData = new ResponseData(false);
                responseData.setMessage(this.getErrorMessage(result, request));
                return responseData;
            } else {
                return new ResponseData(this.astFcEstimateService.batchUpdate(requestContext, list));
            }
        }
    }

    @RequestMapping({"/ast/fc/estimate/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<AstFcEstimate> dto = parameter.toJavaList(AstFcEstimate.class);
        this.astFcEstimateService.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping({"/ast/fc/estimate/approve"})
    @ResponseBody
    public ResponseData fcEstimateApprove(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        Map map = (Map)requestData.get("parameter");
        Long fcEstimateId = Long.parseLong(map.get("fc_estimate_id").toString());
        this.astFcEstimateService.fcEstimateApprove(iRequest, fcEstimateId);
        return new ResponseData();
    }

    @RequestMapping({"/ast/fc/estimate/approvable"})
    @ResponseBody
    public ResponseData fcEstimateApprovable(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        List<AstFcEstimate> astFcEstimates = this.astFcEstimateService.selectAll(iRequest);
        astFcEstimates.removeIf((f) -> {
            return !"APPROVING".equalsIgnoreCase(f.getStatus());
        });
        return new ResponseData(astFcEstimates);
    }

    @RequestMapping(value = "/ast/fc/estimate/create")
    @ResponseBody
    public ResponseData assetsDisposalCreate(AstFcEstimate dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param1 = (JSONObject)requestData.get("parameter");
        JSONObject param = (JSONObject)param1.get("param");
        String estimate_num =(String.valueOf(param.get("estimate_num")));
        String five_class_plan = (String.valueOf(param.get("five_class_plan")));
        String contract_number = (String.valueOf(param.get("contract_number")));
        Long project_id = Long.valueOf(String.valueOf(param.get("project_id")));
        Long employee_id = Long.valueOf(String.valueOf(param.get("employee_id")));
        //company_id
        Long company_id = Long.valueOf(String.valueOf(param.get("company_id")));
        Long unit_id = Long.valueOf(String.valueOf(param.get("unit_id")));
        String estimate_date = (String.valueOf(param.get("estimate_date")));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dto.setEstimateDate(formatter.parse(estimate_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dto.setEmployeeId(employee_id);
        dto.setProjectId(project_id);
        dto.setEstimateNum(estimate_num);
        dto.setFiveClassPlan(five_class_plan);
        dto.setContractNumber(contract_number);
        dto.setCompanyId(company_id);
        dto.setUnitId(unit_id);
        AstFcEstimate assetsDisposal = this.astFcEstimateService.astFcEstimateCreate(requestContext,dto);
        List<AstFcEstimate> astFcEstimateList = new ArrayList<>();
        astFcEstimateList.add(assetsDisposal);
        return new ResponseData(astFcEstimateList);
    }



//    @PostMapping(value = "/ast/fc/estimate/result/assessment")
//    public ResponseData assessment(final HttpServletRequest request,
//                                   @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
//                                   @RequestParam(value = "fcEstimateId") Long fcEstimateId,
//                                   @RequestParam(value = "fiveClassPlan") String fiveClassPlan) {
//        IRequest requestContext = createRequestContext(request);
//        JSONArray para = (JSONArray) requestData.get("parameter");
//        List<AstFcEstimate> resources = para.toJavaList((AstFcEstimate.class));
//
//        AstFcEstimate astFcEstimate = new AstFcEstimate();
//        astFcEstimate.setFcEstimateId(fcEstimateId);
//        astFcEstimate.setFiveClassPlan(fiveClassPlan);
//        ResponseData data = new ResponseData();
//        resources.forEach(resource -> {
//            if (resource.get__status().equals("update")) {
//                resource.set__status("insert");
//            }
//        });
//        astFcEstimateResultService.assessment(requestContext, astFcEstimate, resources);
//        //functionService.updateFunctionResources(requestContext, function, resources);
//        data.setSuccess(true);
//        return data;
//    }



    //
    @RequestMapping(value = "/ast/fc/estimate/approval")
    @ResponseBody
    public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AstFcEstimate astFcEstimate = param.toJavaObject(AstFcEstimate.class);
        this.astFcEstimateService.conInceptSubmit(requestContext, astFcEstimate);
        return new ResponseData();
    }


    //
    @RequestMapping(value = "/ast/fc/estimate/approval2")
    @ResponseBody
    public ResponseData conInceptSubmit2(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AstFcEstimate astFcEstimate = param.toJavaObject(AstFcEstimate.class);
        this.astFcEstimateService.conInceptSubmit2(requestContext, astFcEstimate);
        return new ResponseData();
    }




    @RequestMapping(value = "/ast/fc/estimate/update")
    @ResponseBody
    public ResponseData updateCheckPlan(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjCheckPlan dto = param.toJavaObject(PrjCheckPlan.class);
        dto.setAssignStatus("achieved");
        Date sysDate = new Date();
        dto.setActualFinishDate(sysDate);
        prjCheckPlanMapper.updatePrjCheckPlan(dto);
        //prjCheckPlanService.updateByPrimaryKey(requestContext,dto);
        return new ResponseData();
    }

}
