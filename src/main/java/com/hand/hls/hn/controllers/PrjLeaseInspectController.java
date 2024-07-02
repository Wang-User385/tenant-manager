package com.hand.hls.hn.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.BpCreditMotherline;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.service.IPrjLeaseInspectService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class PrjLeaseInspectController extends BaseController{

    @Autowired
    private IPrjLeaseInspectService service;

    @RequestMapping(value = "/prj/lease/inspect/submit")
    @ResponseBody
    public ResponseData submitPrjContractChange(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjLeaseInspect dto = param.toJavaObject(PrjLeaseInspect.class);
        //PrjLeaseInspect prjLeaseInspect=new PrjLeaseInspect();
        service.submitPrjContractChange(requestContext, dto);
        return new ResponseData();
    }

    /**
     * @author kalvin
     * @Description 生成编码规则
     * @Date 9:14 2021/4/9
     * @Param [request]
     * @return com.hand.hap.system.dto.ResponseData
     **/
    @RequestMapping("/prj/lease/generateAuthorityString")

    public ResponseData generateAuthorityString(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        String authorityRuleString = service.generateAuthorityString(iRequest);
        return new ResponseData(Arrays.asList(authorityRuleString));
    }

    @RequestMapping(value = "/prj/lease/inspect/create")
    @ResponseBody
    public ResponseData assetsDisposalCreate(PrjLeaseInspect dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param1 = (JSONObject)requestData.get("parameter");
        JSONObject param = (JSONObject)param1.get("param");
        String lease_inspect_number =(String.valueOf(param.get("lease_inspect_number")));
        String contract_number = (String.valueOf(param.get("contract_number")));
        String approve_status = (String.valueOf(param.get("approve_status")));
        Long project_id = Long.valueOf(String.valueOf(param.get("project_id")));
        Long user_id = Long.valueOf(String.valueOf(param.get("user_id")));
        String from_date = (String.valueOf(param.get("lease_inspect_date")));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            //leaseInspectDate
            dto.setLeaseInspectDate(formatter.parse(from_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dto.setUserId(user_id);
        dto.setProjectId(project_id);
        dto.setContractNumber(contract_number);
        dto.setLeaseInspectNumber(lease_inspect_number);
        dto.setApproveStatus(approve_status);
        PrjLeaseInspect prjLeaseInspect = service.prjLeaseInspectCreate(requestContext,dto);
        List<PrjLeaseInspect> prjLeaseInspects = new ArrayList<>();
        prjLeaseInspects.add(prjLeaseInspect);
        return new ResponseData(prjLeaseInspects);
    }

}