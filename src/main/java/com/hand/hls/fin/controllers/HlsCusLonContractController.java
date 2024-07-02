package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fin.service.HlsCusLonContractService;

import com.hand.hls.gld.service.IHlsCusLonContractRepaymentMergeService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Controller
public class HlsCusLonContractController extends BaseController {

    @Autowired
    private HlsCusLonContractService service;

    @Autowired
    IExportService excelService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private IHlsCusLonContractRepaymentMergeService lonContractRepaymentMergeService;
    @Autowired
    private HlsCusLonContractWithdrawMapper hlsCusLonContractWithdrawMapper;

    @RequestMapping(value = "/hlsLon/contract/save/repayment/validata")
    @ResponseBody
    public ResponseData validata(HlsCusLonContract lonContract, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        Integer result = service.validata(requestContext, lonContract);
        List<Integer> list = new ArrayList<>();
        list.add(result);
        return new ResponseData(list);
    }

    /**
     * 融资合同保存
     * @param
     * @param
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "/hlsLon/contract/create")
    @ResponseBody
    public ResponseData saveLonContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  HttpServletRequest request, HttpSession session) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContract lonContract = param.toJavaObject(HlsCusLonContract.class);
        service.save(requestContext, lonContract, session);
        List<HlsCusLonContract> lonContractList = new ArrayList<HlsCusLonContract>();
        lonContractList.add(lonContract);
        return new ResponseData(lonContractList);
    }

    @RequestMapping(value = "/hlsLon/contract/formData")
    @ResponseBody
    public HlsCusLonContract query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLonContract dto = param.toJavaObject(HlsCusLonContract.class);
        return service.selectLonContractFormData(requestContext,dto);

    }

    /*提交审批*/
    @RequestMapping(value = "/hlsLon/contract/submitApproval")
    @ResponseBody
    public ResponseData submitLonContractToWfl(@RequestBody HlsCusLonContract lonContract, HttpServletRequest request, HttpSession session) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.submitLonContractToWfl(requestCtx, lonContract, session));
    }

    /*融资合同变更*/
    @RequestMapping(value = "/hlsLon/contract/changeReqCreate")
    @ResponseBody
    public HlsCusLonContract lonContractChangeReqCreate(HlsCusLonContract lonContract, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        return service.lonContractChangeReqCreate(requestContext, lonContract, session);
    }

    /*取消变更*/
    @RequestMapping(value = "/hlsLon/contract/cancelChangeReq")
    @ResponseBody
    public void cancelLonContractChangeReq(@RequestBody HlsCusLonContract lonContract, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        service.cancelLonContractChangeReq(requestCtx, lonContract, session);
    }

    @RequestMapping(value = "/hlsLon/contract/lonContractQuery")
    @ResponseBody
    public ResponseData lonContractQuery(HlsCusLonContract lonContract, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
        lonContract.setCompanyId((Long) session.getAttribute("companyId"));
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.lonContractQuery(requestContext, lonContract, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/lonContractChangeReqQuery")
    @ResponseBody
    public ResponseData lonContractChangeReqQuery(HlsCusLonContract lonContract, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
        lonContract.setCompanyId((Long) session.getAttribute("companyId"));
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.lonContractChangeReqQuery(requestContext, lonContract, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/list")
    @ResponseBody
    public ResponseData queryLonConList(HlsCusLonContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryLonContract(requestContext, dto, page, pageSize));
    }




    @RequestMapping(value = "/hlsCus/lon/contract/changeReq/export")
    public void createLonChangeReqXLS(HttpServletRequest request, @RequestParam String config,
                                      HttpServletResponse httpServletResponse, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusLonContract.class, ColumnInfo.class);
            ExportConfig<HlsCusLonContract, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractMapper.lonContractChangeReqQuery",
                    exportConfig, request, httpServletResponse, requestContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/hlsCus/lon/contract/approval/submit")
    public void updateLonContractApproval(HttpServletRequest request, HlsCusLonContract dto) {
        IRequest requestContext = createRequestContext(request);
        service.updateLonContractApproval(requestContext, dto);
    }

    @RequestMapping(value = "/hlsCus/lon/contract/queryValidWithdraw")
    @ResponseBody
    public int queryValidWithdrawCount(HttpServletRequest request, HlsCusLonContract dto) {
        IRequest requestContext = createRequestContext(request);
        return service.selectWithdrawValidStatus(requestContext,dto);
    }

    /**
     * 合同释放
     * @param request
     * @return
     */
    @RequestMapping(value = "/hlsLon/contract/release")
    @ResponseBody
    public ResponseData releaseLonContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContract lonContract = param.toJavaObject(HlsCusLonContract.class);
        lonContract.setConReleaseFlag("Y");
        lonContract.setSettleStatus("UNCLEARED");
        service.updateByPrimaryKeySelective(requestContext,lonContract);
        return new ResponseData();
    }

    @RequestMapping(value = "/hlsLon/contract/update")
    @ResponseBody
    public void  updateLonContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  HttpServletRequest request, HttpSession session) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContract lonContract = param.toJavaObject(HlsCusLonContract.class);
        service.updateByPrimaryKeySelective(requestContext,lonContract);
    }

    @RequestMapping(value="/hlsLon/contract/check")
    @ResponseBody
    public ResponseData contractNumberCheck(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        JSONObject param = (JSONObject)requestData.get("parameter");
        String contractNumber = param.getString("contractNumber");
        List<HlsCusLonContract> lonContracts = service.contractNumberCheck(contractNumber);
        return new ResponseData(lonContracts);
    }


    @RequestMapping(value="/lon/contract/rentApportioned")
    @ResponseBody
    public ResponseData lonrentApportioned(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData){
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long withdrawId = Long.valueOf(String.valueOf(param.get("withdraw_id")) );
        HlsCusLonContractWithdraw withdraw = new HlsCusLonContractWithdraw();
        withdraw.setWithdrawId(withdrawId);
        withdraw = hlsCusLonContractWithdrawMapper.selectByPrimaryKey(withdraw);
        lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(requestContext, withdraw,null);

        return new ResponseData();
    }

}