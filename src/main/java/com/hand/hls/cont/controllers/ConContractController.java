package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractAttachment;
import com.hand.hls.cont.dto.HlsCusConContractIncept;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractLeaseItemService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.IPrjQuotationService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.JsonUtils;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class ConContractController extends BaseController {

    @Autowired
    private IConContractService service;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    /*  @Autowired
     private HlsBeanRefUtilService beanRefUtilService;
     @Autowired
     private IPrjQuotationService prjQuotationService;
     @Autowired
     private HlsCusPrjQuotationMapper prjQuotationMapper;*/
    @Autowired
    private IConContractLeaseItemService conContractLeaseItemService;




    private Logger logger = LoggerFactory.getLogger(getClass());


    /*@RequestMapping(value = "/con/contract/query")
    @ResponseBody
    public ResponseData query(HlsCusConContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/search/contract/home/query")
    @ResponseBody
    public ResponseData searchContractHome(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract metadataRelation = param.toJavaObject(HlsCusConContract.class);

        return new ResponseData(service.searchContractHome(requestContext, metadataRelation, page, pageSize));
    }

    @RequestMapping(value = "/con/contract/queryBasicInfo")
    @ResponseBody
    public ResponseData queryBasicInfoByContractId(HlsCusConContract dto,
                                                   HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if (dto.getContractId() == null) {
            return new ResponseData(false, "合同号不存在!");
        }
        return new ResponseData(Arrays.asList(service.queryContractBasicInfo(requestContext, dto.getContractId())));
    }

    @RequestMapping(value = "/con/contract/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusConContract> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/con/contract/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusConContract> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/test")
    @ResponseBody
    public ResponseData testetss(HttpServletRequest request, @RequestParam Long contractId) {
        IRequest iRequest = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(contractId);

        service.saveConContractFromPrjProject(iRequest, hlsCusPrjProject);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/sending/management/selectForLovIf")
    @ResponseBody
    public ResponseData conContractSendingManagementSelectForLovIf(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectProNumberAndBusinessType(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping("/con/contract/selectContractByStatus")
    public ResponseData selectContractByStatus(HlsCusConContract dto,
                                               @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                               HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        Map parameter = JSON.parseObject(JSON.parseObject(request.getParameter("_request_data")).get("parameter").toString(), Map.class);
        if (StringUtils.isBlank(dto.getContractStatus())) {
            return new ResponseData(false, "合同状态不能为空!");
        }

        if (parameter.get("contractNumber") != null) {
            dto.setContractNumber(parameter.get("contractNumber").toString());
        }
        if (parameter.get("contractName") != null) {
            dto.setContractName(parameter.get("contractName").toString());
        }
        return new ResponseData(service.select(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping("/con/contract/lease")
    @ResponseBody
    public ResponseData startLease(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        IRequest iRequest = createRequestContext(request);
        Map map = (Map) requestData.get("parameter");
        map = JSONObject.parseObject(JsonUtils.toCamelJsonString(map));
        HlsCusConContractIncept contractIncept = new HlsCusConContractIncept();
        List<Long> quotationIds = JSON.parseArray(map.get("quotationIds").toString(), Long.class);
        List<Long> contractAttachmentIds = JSON.parseArray(map.get("contractAttachmentIds").toString(), Long.class);
        beanRefUtilService.setFieldValue(contractIncept, map);
        if (quotationIds != null) {
            contractIncept.setQuotationIds(quotationIds);
        }
        if (contractAttachmentIds != null) {
            contractIncept.setContractAttatchmentIds(contractAttachmentIds);
        }
        try {
            service.startLease(iRequest, contractIncept);
            for (int i = 0; i < quotationIds.size(); i++) {
                HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectByPrimaryKey(quotationIds.get(i));
                prjQuotation.setInceptionOfLease(((JSONObject) map).getDate("inceptionOfLease"));
                prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotation);
            }
        } catch (ResMessageException e) {
            logger.error("起租报错：", e);
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    *//**
     * 合同文本生成(单条)
     *
     * @param contractAttachment
     * @param request
     * @return
     * @throws TokenException
     * @throws FileReadIOException
     * @throws Docx4JException
     *//*
    @RequestMapping(value = "con/contract/content/single/create")
    @ResponseBody
    public ResponseData contentCreate(HlsCusConContractAttachment contractAttachment, final HttpServletRequest request) {
        if (contractAttachment.getContractId() == null || contractAttachment.getContractAttachmentId() == null) {
            return new ResponseData(false, "合同文本生成失败,请确认信息已经保存完毕!");
        }

        IRequest requestContext = createRequestContext(request);
        try {
            service.contextCreateSingle(requestContext, contractAttachment);
        } catch (Exception e) {
            return new ResponseData(false, "合同文本生成失败");
        }

        return new ResponseData(true, "生成合同文本成功!");
    }

    *//**
     * 合同文本生成(一键生成)
     *
     * @return
     *//*
    @RequestMapping(value = "con/contract/content/create")
    @ResponseBody
    public ResponseData contentCreate(HlsCusConContract conContract,
                                      final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);

        try {
            service.contextCreateMultiple(requestContext, conContract);
        } catch (Exception e) {
            return new ResponseData(false, "生成合同文本失败!");
        }

        return new ResponseData(true, "生成合同文本成功!");
    }


    @RequestMapping(value = "/con/contract/wfl/status/query")
    @ResponseBody
    public ResponseData queryContractAllStatus(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        Map map = (Map) requestData.get("parameter");
        HlsCusConContract contract = JSONObject.parseObject(JsonUtils.toCamelJsonString(map), HlsCusConContract.class);
        contract = service.queryContractAllStatus(iRequest, contract.getContractId());
        List<HlsCusConContract> list = new ArrayList<>();
        list.add(contract);
        return new ResponseData(list);
    }


    *//**
     * 合同结束
     */
    @RequestMapping(value = "/con/contract/terminate")
    @ResponseBody
    public ResponseData terminate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        service.terminate(iRequest, hlsCusConContract);
        return new ResponseData();
    }

    /**
     * 支付表打印
     *//*
    @RequestMapping(value = "/con/contract/payment/docx")
    public void paymentDocx(HttpServletRequest request, HttpServletResponse response, String contractIdsStr) throws Exception {
        IRequest iRequest = createRequestContext(request);
        String[] contractIds = contractIdsStr.split(",");
        List<HlsCusConContract> list = new ArrayList<>(contractIds.length);
        for (int i = 0; i < contractIds.length; i++) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(Long.valueOf(contractIds[i]));
            list.add(hlsCusConContract);
        }
        service.paymentDocx(iRequest, request, response, list);
    }

    *//**
     * 同步失败的再次同步
     *//*
    @RequestMapping(value = "/con/contract/send/sap")
    public void sendSap(HttpServletRequest request, HttpServletResponse response, String contractIdsStr) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        String[] contractIds = contractIdsStr.split(",");
        List<HlsCusConContract> list = new ArrayList<>(contractIds.length);
        for (int i = 0; i < contractIds.length; i++) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(Long.valueOf(contractIds[i]));
            list.add(hlsCusConContract);
        }
        service.sendSap(iRequest, list);
    }

    *//**
     * 产权转移书打印
     *//*
    @RequestMapping(value = "/con/contract/output/transfer")
    public void outputTransferOfPropertyRights(HttpServletRequest request, HttpServletResponse response, String contractIdsStr) throws Exception {
        IRequest iRequest = createRequestContext(request);
        String[] contractIds = contractIdsStr.split(",");
        List<HlsCusConContract> list = new ArrayList<>(contractIds.length);
        for (int i = 0; i < contractIds.length; i++) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(Long.valueOf(contractIds[i]));
            list.add(hlsCusConContract);
        }
        service.outputTransferOfPropertyRights(iRequest, request, response, list);
    }
*/
    @RequestMapping(value = "/contract/change/req/pre/payment/query")
    public ResponseData prePaymentQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag","N");
        JSONObject jsonObject = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusConContract hlsCusConContract = jsonObject.toJavaObject(HlsCusConContract.class);
        List<Map> list = new ArrayList<>();
        Map map = new HashMap<String,Object>();
        if (Objects.nonNull(hlsCusConContract) && Objects.nonNull(hlsCusConContract.getChangeReqId())) {
            map = contractMapper.selectPrePaymentChangeReqInfo(hlsCusConContract);
        }
        if (Objects.nonNull(map)) {
            map.put("period_interest_change",map.get("period_interest"));
        }
        list.add(map);
        return new ResponseData(list);
    }
/*
    @RequestMapping(value = "/contract/create/for/import")
    public ResponseData createContractForStockData(HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        service.createContractForStockData(iRequest);
        return new ResponseData();
    }
    *//**
     * 根据合同编号查找合同id
     * @param contractNumber 合同编号
     * @return 合同id
     *//*
    @RequestMapping(value = "/contract/queryContractId")
    @ResponseBody
    public ResponseData queryContractIdByContractNumber(HttpServletRequest request,String contractNumber) {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag","N");
        if (StringUtils.isEmpty(contractNumber)) {
            return new ResponseData(false, "合同编号不能为空!");
        }
        return new ResponseData(service.queryContractIdByContractNumber(contractNumber));
    }

    *//**
     * 业务确认函打印

     *//*
    @RequestMapping(value = "/con/business/confirm/download")
    public void downloadBusinessConfirm(HttpServletRequest request, HttpServletResponse response, String contractIdsStr) throws Exception {
        IRequest iRequest = createRequestContext(request);
        String[] contractIds = contractIdsStr.split(",");
        List<HlsCusConContract> list = new ArrayList<>(contractIds.length);
        for (int i = 0; i < contractIds.length; i++) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(Long.valueOf(contractIds[i]));
            list.add(hlsCusConContract);
        }
        service.downloadBusinessConfirmPdf(iRequest,response, list);
    }

    private static final String UNDEFINED="undefined";
    *//**
     * 租金支付表excel打印
     *//*
    @RequestMapping(value = "/con/contract/payment/excel")
    public void paymentExcel(HttpServletRequest request, HttpServletResponse response, String contract_ids_str,String docx,String pdfSign,String excel) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONObject params=new JSONObject();
        if(!StringUtils.equals(UNDEFINED,docx)){
            params.put("docx",docx);
        }
        if(!StringUtils.equals(UNDEFINED,contract_ids_str)){
            params.put("contract_ids_str",contract_ids_str);
        }
        if(!StringUtils.equals(UNDEFINED,pdfSign)){
            params.put("pdfSign",pdfSign);
        }
        if(!StringUtils.equals(UNDEFINED,excel)){
            params.put("excel",excel);
        }
//        JSONObject params =JSONObject.parseObject(signParam);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        RequestHelper.setCurrentRequest(requestCtx);
        service.paymentAll(iRequest,request,response,params);
    }

    *//**
     * 产权转移书打印
     *//*
    @RequestMapping(value = "/con/contract/ownership/transfer/download")
    public void ownershipTransferDownload(HttpServletRequest request, HttpServletResponse response, String contractIdsStr) throws Exception {
        IRequest iRequest = createRequestContext(request);
        service.ownershipTransferDownload(iRequest,request,response,contractIdsStr);
    }
*/
    @RequestMapping(value = "/con/contract/machine/query")
    @ResponseBody
    public ResponseData queryContractLeaseItemMachine(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        requestContext.setAttribute("authorityRuleFlag", BaseConstants.NO);
        Map<String, Object> conContract = param.toJavaObject(Map.class);
        conContractLeaseItemService.leaseItemQuery(requestContext, conContract);
        conContract.put("isMachine",BaseConstants.YES);
        List<Map<String, Object>> result = conContractLeaseItemService.queryContractLeaseItem(requestContext,conContract,pagenum,pagesize);
        return new ResponseData(result);
    }


    @RequestMapping(value = "/con/contract/vehicle/query")
    @ResponseBody
    public ResponseData queryContractLeaseItemVehicle(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        requestContext.setAttribute("authorityRuleFlag", BaseConstants.NO);
        Map<String, Object> conContract = param.toJavaObject(Map.class);
        conContract.put("isVehicle",BaseConstants.YES);
        conContractLeaseItemService.leaseItemQuery(requestContext, conContract);
        List<Map<String, Object>> result = conContractLeaseItemService.queryContractLeaseItem(requestContext,conContract,pagenum,pagesize);
        return new ResponseData(result);
    }

    @RequestMapping(value = "/con/contract/photovoltaic/query")
    @ResponseBody
    public ResponseData queryContractLeaseItemPhotovoltaic(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,  HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        requestContext.setAttribute("authorityRuleFlag", BaseConstants.NO);
        Map<String, Object> conContract = param.toJavaObject(Map.class);
        conContract.put("isPhotovoltaic",BaseConstants.YES);
        conContractLeaseItemService.leaseItemQuery(requestContext, conContract);
        List<Map<String, Object>> result = conContractLeaseItemService.queryContractLeaseItem(requestContext,conContract,pagenum,pagesize);
        return new ResponseData(result);
    }

    /**
     * 大单部分提前还本不规则报价现金流导入
     */
    @RequestMapping("/con/contract/change/prepayment/cashflow/import")
    @ResponseBody
    public ResponseData conContractChangePrepaymentCashflowExcelImport(HttpServletRequest request,Long headerId,@RequestParam HashMap params) throws Exception {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag", "N");
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        return service.conContractChangePrepaymentCashflowExcelImport(iRequest,headerId,paramJson.getLong("changeReqId"));
    }

}
