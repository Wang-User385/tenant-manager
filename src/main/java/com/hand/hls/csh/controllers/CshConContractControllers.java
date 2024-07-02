package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.service.ICshConContractService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class CshConContractControllers extends BaseController {

    @Autowired
    private ICshConContractService conContractService;

    @RequestMapping(value = "/csh/contract/home/secord/query")
    @ResponseBody
    public ResponseData contractHomeSecondQuery(@ModelAttribute("_request_data") LeafRequestData requestData,
                                                HttpServletRequest request, final HttpSession session,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        Map map = param.toJavaObject(Map.class);
        if(param.get("payment_approved_status")!=null){
            map.put("paymentApprovedStatus",param.get("payment_approved_status"));
        }
        if(param.get("bp_name")!=null){
            map.put("bpName",param.get("bp_name"));
        }
        if(param.get("contract_number")!=null){
            map.put("contractNumber",param.get("contract_number"));
        }
        Long companyId = Long.valueOf(String.valueOf(session.getAttribute("companyId")));
        map.put("companyId", companyId);
        return new ResponseData(conContractService.contractHomeSecondQuery(map, pagenum, pagesize));
    }

    @RequestMapping(value = "/csh/contract/home/secordQuery/query")
    @ResponseBody
    public ResponseData Csh001contractHomeSecondQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, final HttpSession session, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }
        Map map = param.toJavaObject(Map.class);
        if(param.get("payment_approved_status")!=null){
            map.put("paymentApprovedStatus",param.get("payment_approved_status"));
        }
        if(param.get("bp_name")!=null){
            map.put("bpName",param.get("bp_name"));
        }
        if(param.get("contract_number")!=null){
            map.put("contractNumber",param.get("contract_number"));
        }
        if(param.get("proposed_launch_date_from")!=null){
            map.put("proposedLaunchDateFrom",param.get("proposed_launch_date_from"));
        }
        if(param.get("proposed_launch_date_to")!=null){
            map.put("proposedLaunchDateTo",param.get("proposed_launch_date_to"));
        }
        Long companyId = Long.valueOf(String.valueOf(session.getAttribute("companyId")));
        map.put("companyId", companyId);
        return new ResponseData(conContractService.csh001contractHomeSecondQuery(map, pagenum, pagesize,sortName,sortOrder));
    }

    @RequestMapping(value = "/csh/contract/home/third/query")
    @ResponseBody
    public ResponseData contractHomeThirdQuery(@ModelAttribute("_request_data") LeafRequestData requestData, final HttpSession session) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        Map map = param.toJavaObject(Map.class);
        Long companyId = Long.valueOf(String.valueOf(session.getAttribute("companyId")));
        map.put("companyId", companyId);
        return new ResponseData(conContractService.contractHomeThirdQuery(map));
    }

}
