package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.fin.dto.CreditContract;
import com.hand.hls.fin.dto.HlsCusCreditContract;
import com.hand.hls.fin.dto.HlsCusCreditContractLine;
import com.hand.hls.fin.dto.HlsCusLonCreditContractAttachment;
import com.hand.hls.fin.exception.AmoutOverdueException;
import com.hand.hls.fin.service.HlsCusICreditContractService;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusCreditContractController extends BaseController {

    @Autowired
    private HlsCusICreditContractService service;
    @Autowired
    IExportService excelService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/hlsLon/credit/contract/beforeQuery")
    @ResponseBody
    public ResponseData unitSelect(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
        Long companyId = (Long) session.getAttribute("companyId");
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCreditContract creditContract = param.toJavaObject(HlsCusCreditContract.class);
        creditContract.setCompanyId(companyId);
        List<HlsCusCreditContract> list = service.unitSelect(creditContract, page, pageSize);
        return new ResponseData(list);
    }


    /**
     * 授信额度保存
     *
     * @param request
     * @param session
     * @return
     * @throws AmoutOverdueException
     */
    @RequestMapping(value = "/hlsLon/credit/contract/update")
    @ResponseBody
    public ResponseData batchupdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws AmoutOverdueException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCreditContract creditContract = param.toJavaObject(HlsCusCreditContract.class);
        IRequest requestContext = createRequestContext(request);
        HlsCusCreditContract hlsCusCreditContract = service.save(requestContext, creditContract);
        List<HlsCusCreditContract> list = new ArrayList<>();
        list.add(hlsCusCreditContract);
        return new ResponseData(list);
    }


    @RequestMapping(value = "/hlsLon/credit/contract/queryByCreditBpId")
    @ResponseBody
    public ResponseData queryByCreditBpId(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                     HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCreditContract hlsCusCreditContract = param.toJavaObject(HlsCusCreditContract.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusCreditContract> list = new ArrayList();
        //for (HlsCusCreditContract item : hlsCusCreditContract) {
            HlsCusCreditContract record = new HlsCusCreditContract();
            record.setCreditBpId(hlsCusCreditContract.getCreditBpId());
            list.addAll(service.selectSelective(requestCtx, record));
        //}
        return new ResponseData(list);
    }
}