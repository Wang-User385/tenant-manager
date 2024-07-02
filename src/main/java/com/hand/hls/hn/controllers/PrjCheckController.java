package com.hand.hls.hn.controllers;

import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.hn.mapper.PrjCheckProjectScheduleMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.hn.service.IPrjLeaseInspectService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.FundingPlan;
import com.hand.hls.hls.service.IFundingPlanService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class PrjCheckController extends BaseController{

    @Autowired
    private IPrjCheckService service;
    @Autowired
    private PrjCheckPlanMapper prjCheckPlanMapper;

    @RequestMapping(value = "/prj/check/submit")
    @ResponseBody
    public ResponseData submitPrjContractChange(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) throws HlsCusException{
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjCheck dto = param.toJavaObject(PrjCheck.class);
        service.submitPrjContractChange(requestContext, dto);
        return new ResponseData();
    }

    /**
     * 零售业务租后检查
     */
    @RequestMapping(value = "/prj/check/ls/submit")
    @ResponseBody
    public ResponseData submitCheckLs(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) throws HlsCusException{
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjCheck dto = param.toJavaObject(PrjCheck.class);
        service.submitPrjCheckLs(requestContext, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/check/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjCheck dto = param.toJavaObject(PrjCheck.class);
        //PrjCheck<List> prjCheck= ;
        return new ResponseData(service.queryContract(dto));
    }

    @RequestMapping(value = "/prj/check/plan/update")
    @ResponseBody
    public ResponseData updateCheckPlan(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjCheckPlan dto = param.toJavaObject(PrjCheckPlan.class);
        //PrjCheck<List> prjCheck=
        dto.setAssignStatus("achieved");
        Date sysDate = new Date();
        dto.setActualFinishDate(sysDate);
        prjCheckPlanMapper.updatePrjCheckPlan(dto);
        return new ResponseData();
    }

    /**
     * @author kalvin
     * @Description 生成编码规则
     * @Date 9:14 2021/4/9
     * @Param [request]
     * @return com.hand.hap.system.dto.ResponseData
     **/
    @RequestMapping("/prj/check/generateAuthorityString")
    public ResponseData generateAuthorityString(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        String authorityRuleString = service.generateAuthorityString(iRequest);
        return new ResponseData(Arrays.asList(authorityRuleString));
    }



    @RequestMapping(value = "/prj/check/create")
    @ResponseBody
    public ResponseData assetsDisposalCreate(PrjCheck dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param1 = (JSONObject)requestData.get("parameter");
        JSONObject param = (JSONObject)param1.get("param");
        String approve_suggest =(String.valueOf(param.get("approve_suggest")));
        String contract_number = (String.valueOf(param.get("contract_number")));
        String check_number = (String.valueOf(param.get("check_number")));
        Long contract_id = Long.valueOf(String.valueOf(param.get("contract_id")));
        Long bp_id = Long.valueOf(String.valueOf(param.get("bp_id")));
        Long unit_id = Long.valueOf(String.valueOf(param.get("unit_id")));
        Long plan_id = Long.valueOf(String.valueOf(param.get("plan_id")));
        Long employee_id = Long.valueOf(String.valueOf(param.get("employee_id")));
        String check_date = (String.valueOf(param.get("check_date")));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dto.setCheckDate(formatter.parse(check_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dto.setEmployeeId(employee_id);
        dto.setBpId(bp_id);
        dto.setContractNumber(contract_number);
        dto.setApproveSuggest(approve_suggest);
        dto.setCheckNumber(check_number);
        dto.setContractId(contract_id);
        dto.setUnitId(unit_id);
        dto.setPlanId(plan_id);
        PrjCheck prjCheck = service.prjCheckCreate(requestContext,dto);
        List<PrjCheck> prjChecks = new ArrayList<>();
        prjChecks.add(prjCheck);
        return new ResponseData(prjChecks);

    }


    @RequestMapping("/prj/check/wfl/save")
    public ResponseData saveWflData(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException  {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        String product_conclusion =(String.valueOf(param.get("product_conclusion")));
        Long check_id = Long.valueOf(String.valueOf(param.get("check_id")));
        PrjCheck prjCheck =new PrjCheck();
        prjCheck.setProductConclusion(product_conclusion);
        prjCheck.setCheckId(check_id);
        prjCheck = service.updateByPrimaryKeySelective(iRequest,prjCheck);
        List<PrjCheck> prjChecks = new ArrayList<>();
        prjChecks.add(prjCheck);
        return new ResponseData(prjChecks);
    }
}