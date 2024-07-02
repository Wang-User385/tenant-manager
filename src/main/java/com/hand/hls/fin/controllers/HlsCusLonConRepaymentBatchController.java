package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.common.components.HlsWordToPdfComponent;
import com.hand.hls.fin.dto.HlsCusLonConBankAccTransfer;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatch;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.mapper.HlsCusLonConBankAccTransferMapper;
import com.hand.hls.fin.mapper.HlsCusLonConRepaymentBatchLnMapper;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
    public class HlsCusLonConRepaymentBatchController extends BaseController{

    @Autowired
    private IHlsCusLonConRepaymentBatchService service;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private IHlsCusLonConRepaymentBatchLnService hlsCusLonConRepaymentBatchLnService;
    @Autowired
    private HlsCusLonConRepaymentBatchLnMapper hlsCusLonConRepaymentBatchLnMapper;
    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;

    @Autowired
    private HlsWordToPdfComponent hlsWordToPdfComponent;
    @Autowired
    private HlsCusLonConBankAccTransferMapper hlsCusLonConBankAccTransferMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static String DOCUMENT_CATEGORY = "LON_CONTRACT_REPAYMENT";
    private static String DOCUMENT_TYPE = "LON_CON_REPAYMENT_BATCH";
    private static String BUSINESS_TYPE = "LON_CON_REPAYMENT_BATCH";


    @RequestMapping(value = "/lon/con/repayment/batch/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLonConRepaymentBatch dto = param.toJavaObject(HlsCusLonConRepaymentBatch.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/lon/con/repayment/batch/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLonConRepaymentBatch> list = param.toJavaList(HlsCusLonConRepaymentBatch.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/lon/con/repayment/batch/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusLonConRepaymentBatch> HlsCusLonConRepaymentBatchList = parameter.toJavaList(HlsCusLonConRepaymentBatch.class);
        for(HlsCusLonConRepaymentBatch dt:HlsCusLonConRepaymentBatchList){
            if(dt.getBatchId() == null){
                throw new RuntimeException("批次id不能为空！");
            }

            //删除批次行信息
            HlsCusLonConRepaymentBatchLn batchLn = new HlsCusLonConRepaymentBatchLn();
            batchLn.setBatchId(dt.getBatchId());
            hlsCusLonConRepaymentBatchLnMapper.deleteBatchLnByBatchId(batchLn);

            //删除调拨行信息
            HlsCusLonConBankAccTransfer transfer = new HlsCusLonConBankAccTransfer();
            transfer.setBatchId(dt.getBatchId());
            hlsCusLonConBankAccTransferMapper.deleteTransferByBatchId(transfer);

            service.deleteByPrimaryKey(dt);
        }
        //service.batchDelete(HlsCusLonConRepaymentBatchList);
        return new ResponseData(HlsCusLonConRepaymentBatchList);
    }

        @RequestMapping(value = "/lon/con/repayment/batch/create")
        @ResponseBody
        public ResponseData lonConRepaymentBatchCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
            IRequest requestCtx = createRequestContext(request);
            RequestHelper.setCurrentRequest(requestCtx);
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusLonContractRepayment> list = param.toJavaList(HlsCusLonContractRepayment.class);
            List<HlsCusLonConRepaymentBatch> hlsCusLonConRepaymentBatchList = new ArrayList<>();
            if(list.size()>0){
                HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch = new HlsCusLonConRepaymentBatch();
                Map<String, String> params = new HashMap<>();
                String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
                hlsCusLonConRepaymentBatch.setBatchNumber(ruleCode);
                hlsCusLonConRepaymentBatch.setBatchStatus("NEW");
                hlsCusLonConRepaymentBatch.setUnitId(Long.parseLong(requestCtx.getAttribute("unitId").toString()));
                hlsCusLonConRepaymentBatch.setRepaymentDate(new Date(list.get(0).getPlannedCalcDate().getTime()));
                hlsCusLonConRepaymentBatch.setCreditBpId(list.get(0).getCreditBpId());
                hlsCusLonConRepaymentBatch = service.insertSelective(requestCtx,hlsCusLonConRepaymentBatch);
                if(hlsCusLonConRepaymentBatch.getBatchId() != null){
                    for(HlsCusLonContractRepayment dt:list){
                        HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn = new HlsCusLonConRepaymentBatchLn();
                        hlsCusLonConRepaymentBatchLn.setBatchId(hlsCusLonConRepaymentBatch.getBatchId());
                        hlsCusLonConRepaymentBatchLn.setRepaymentId(dt.getRepaymentId());
                        hlsCusLonConRepaymentBatchLn.setContractId(dt.getContractId());
                        hlsCusLonConRepaymentBatchLn.setWithdrawId(dt.getWithdrawId());
                        hlsCusLonConRepaymentBatchLn.setPlannedDueAmount(dt.getPlannedDueAmount());
                        hlsCusLonConRepaymentBatchLn = hlsCusLonConRepaymentBatchLnService.insertSelective(requestCtx,hlsCusLonConRepaymentBatchLn);
                    }
                }
                hlsCusLonConRepaymentBatchList.add(hlsCusLonConRepaymentBatch);
            }

            return new ResponseData(hlsCusLonConRepaymentBatchList);
        }

        @RequestMapping(value = "/lon/auto/repayment/batch/create")
        @ResponseBody
        public ResponseData autoRepaymentBatchCreate(HttpServletRequest request){
            IRequest requestCtx = RequestHelper.newEmptyRequest();
            requestCtx.setEmployeeCode("ADMIN");
            requestCtx.setUserName("admin");
            requestCtx.setCompanyId(248L);
            requestCtx.setUserId(10001L);
            requestCtx.setLocale("zh_CN");

            Long allocationId=144L;
            requestCtx.setAttribute("allocationId",allocationId);

            requestCtx.setAttribute("employeeCode","ADMIN");
            RequestHelper.setCurrentRequest(requestCtx);
            HlsCusLonContractRepayment lcr = new HlsCusLonContractRepayment();
            lcr.setAutoWrite("Y");
            List<HlsCusLonContractRepayment> list = hlsCusLonContractRepaymentService.selectLonContractRepAndFin(lcr);
            List<HlsCusLonContractRepayment> hlsCusLonContractRepayments = new ArrayList<>();
            List<Integer> ints = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    String currI = list.get(i).getPlannedCalcDate().getYear()+"-"+list.get(i).getPlannedCalcDate().getMonth();
                    String currJ = list.get(j).getPlannedCalcDate().getYear()+"-"+list.get(j).getPlannedCalcDate().getMonth();
                    if (currI.equals(currJ) && list.get(i).getCreditBpId().equals(list.get(j).getCreditBpId())
                        && list.get(i).getLonCompanyId().equals(list.get(j).getLonCompanyId()) && list.get(i).getCurrency().equals(list.get(j).getCurrency())){
                        ints.add(j);
                        hlsCusLonContractRepayments.add(list.get(j));
                    }
                    if (j == (list.size()-1) && !ints.contains(i)){
                        hlsCusLonContractRepayments.add(list.get(i));
                    }
                }
                if(hlsCusLonContractRepayments.size() == 0 && !ints.contains(i)){
                    hlsCusLonContractRepayments.add(list.get(i));
                }
                if(hlsCusLonContractRepayments.size()>0){
                    HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch = new HlsCusLonConRepaymentBatch();
                    Map<String, String> params = new HashMap<>();
                    String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
                    hlsCusLonConRepaymentBatch.setBatchNumber(ruleCode);
                    hlsCusLonConRepaymentBatch.setBatchStatus("NEW");
                    hlsCusLonConRepaymentBatch.setUnitId(104L);
                    hlsCusLonConRepaymentBatch.setRepaymentDate(new Date(hlsCusLonContractRepayments.get(0).getPlannedCalcDate().getTime()));
                    hlsCusLonConRepaymentBatch.setCreditBpId(hlsCusLonContractRepayments.get(0).getCreditBpId());
                    long time = hlsCusLonContractRepayments.get(0).getPlannedCalcDate().getTime();
                    java.sql.Date sdate = new java.sql.Date(time);
                    String s = sdate.toString();
                    String date = s.substring(0, s.lastIndexOf("-"));
                    System.out.println(date);
                    hlsCusLonConRepaymentBatch.setDescription(date.replace("-","年")+"月银行还款-系统自动核销创建批次");
                    hlsCusLonConRepaymentBatch = service.insertSelective(requestCtx,hlsCusLonConRepaymentBatch);
                    if(hlsCusLonConRepaymentBatch.getBatchId() != null){
                        for(HlsCusLonContractRepayment dt:hlsCusLonContractRepayments){
                            HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn = new HlsCusLonConRepaymentBatchLn();
                            hlsCusLonConRepaymentBatchLn.setBatchId(hlsCusLonConRepaymentBatch.getBatchId());
                            hlsCusLonConRepaymentBatchLn.setRepaymentId(dt.getRepaymentId());
                            hlsCusLonConRepaymentBatchLn.setContractId(dt.getContractId());
                            hlsCusLonConRepaymentBatchLn.setWithdrawId(dt.getWithdrawId());
                            hlsCusLonConRepaymentBatchLn.setPlannedDueAmount(dt.getPlannedDueAmount());
                            hlsCusLonConRepaymentBatchLn = hlsCusLonConRepaymentBatchLnService.insertSelective(requestCtx,hlsCusLonConRepaymentBatchLn);
                        }
                    }
                    hlsCusLonContractRepayments.clear();
                }
            }

            return new ResponseData(hlsCusLonContractRepayments);
        }

    @RequestMapping(value = "/lon/con/repayment/batch/update")
    @ResponseBody
    public ResponseData lonConRepaymentBatchUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request,Long batchId){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLonContractRepayment> list = param.toJavaList(HlsCusLonContractRepayment.class);
        if(list.size()>0){
            HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch = new HlsCusLonConRepaymentBatch();
            hlsCusLonConRepaymentBatch.setBatchId(batchId);
            hlsCusLonConRepaymentBatch = service.selectByPrimaryKey(requestCtx,hlsCusLonConRepaymentBatch);
            if(hlsCusLonConRepaymentBatch !=null && hlsCusLonConRepaymentBatch.getBatchId() != null){
                for(HlsCusLonContractRepayment dt:list){
                    HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn = new HlsCusLonConRepaymentBatchLn();
                    hlsCusLonConRepaymentBatchLn.setBatchId(hlsCusLonConRepaymentBatch.getBatchId());
                    hlsCusLonConRepaymentBatchLn.setRepaymentId(dt.getRepaymentId());
                    hlsCusLonConRepaymentBatchLn.setContractId(dt.getContractId());
                    hlsCusLonConRepaymentBatchLn.setWithdrawId(dt.getWithdrawId());
                    hlsCusLonConRepaymentBatchLn = hlsCusLonConRepaymentBatchLnService.insertSelective(requestCtx,hlsCusLonConRepaymentBatchLn);
                }
            }else{
                throw new RuntimeException("融资还款批次不存在！");
            }
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/lon/con/repayment/batch/submit/wfl")
    @ResponseBody
    public ResponseData lonConRepaymentBatchSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch = param.toJavaObject(HlsCusLonConRepaymentBatch.class);
        hlsCusLonConRepaymentBatch = service.lonConRepaymentBatchSubmitWfl(requestCtx,hlsCusLonConRepaymentBatch);
        List<HlsCusLonConRepaymentBatch> list = new ArrayList<>();
        list.add(hlsCusLonConRepaymentBatch);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/lon/con/repayment/batch/simple/doc/gen")
    @ResponseBody
    public ResponseData createEftTransferList(String code, String batchId, final HttpServletRequest request, HttpServletResponse response) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if(StringUtils.isEmpty(code)) {
            throw new HlsCusException("模板文件代码为空！");
        }
        if(StringUtils.isEmpty(batchId)) {
            throw new HlsCusException("放款单号为空！");
        }
        try {
            List<FndAttachmentMulti> multiList = service.contextCreateMultiple(requestContext, code,batchId,response);
            hlsWordToPdfComponent.wordToPdfAttachmentMuti(requestContext,multiList);
            return new ResponseData(multiList);
        } catch (Exception e) {
            logger.error("文件模版不存在!", e);
            return new ResponseData(false, "文件模版不存在!");
        }
    }

    @RequestMapping(value = "/lon/con/repayment/batch/confirm")
    @ResponseBody
    public ResponseData lonConRepaymentBatchConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request,Long batchId , final HttpSession session){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLonConRepaymentBatch> list = param.toJavaList(HlsCusLonConRepaymentBatch.class);
        if(list.size()>0){

            service.confirmBatchStatus(requestCtx , list , session);
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/lon/con/repayment/save/confirm")
    @ResponseBody
    public ResponseData lonConRepaymentBatchSaveConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request,Long batchId , final HttpSession session){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusLonConRepaymentBatch> list = param.toJavaList(HlsCusLonConRepaymentBatch.class);
        if(list.size()>0){

            service.saveConfirmBatchStatus(requestCtx , list , session);
        }
        return new ResponseData();
    }


}