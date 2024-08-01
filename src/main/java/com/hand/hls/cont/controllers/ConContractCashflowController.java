package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.IConDebtExemptionReqService;
import com.hand.hls.utils.JsonUtils;
import com.hand.hls.utils.service.HlsConstantUtil;
import leaf.bean.LeafRequestData;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ConContractCashflowController extends BaseController {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private IConContractCashflowService service;

    @Autowired
    private HlsCusConContractCashflowService cusConContractCashflowService;

    @Autowired
    private IConDebtExemptionReqService iConDebtExemptionReqService;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @RequestMapping(value = "/contract/cashflow/queryLov")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsCusConContractCashflow hlsCusConContractCashflow,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");

        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }


        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);
        metadataRelation.setCreditFlag(param.getString("creditFlag"));
        //二期功能：接收多选伪LOV页面的查询参数
        String multiNotCashflowIdsStr = metadataRelation.getNotCashflowIdsStr();
        String multiInCashflowIdsStr = metadataRelation.getInCashflowIdsStr();
        if (multiNotCashflowIdsStr != null && !"".equals(multiNotCashflowIdsStr)) {
            List<Long> notCashflowIds = new ArrayList<>();

            String[] str = multiNotCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notCashflowIds.add(Long.parseLong(str[i]));
                }
            }
            if (notCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(notCashflowIds);
            }
        }

        if (multiInCashflowIdsStr != null && !"".equals(multiInCashflowIdsStr)) {
            List<Long> inCashflowIds = new ArrayList<>();
            String[] str = multiInCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }
            metadataRelation.setInCashflowIds(inCashflowIds);
        }

        //用来接收setLovPara 参数
        String notCashflowIdsStr = hlsCusConContractCashflow.getNotCashflowIdsStr();
        String inCashflowIdsStr = hlsCusConContractCashflow.getInCashflowIdsStr();
        if (notCashflowIdsStr != null && !"".equals(notCashflowIdsStr)) {
            List<Long> notCashflowIds = new ArrayList<>();

            String[] str = notCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notCashflowIds.add(Long.parseLong(str[i]));
                }
            }
            if (notCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(notCashflowIds);
            }
        }

        if (inCashflowIdsStr != null && !"".equals(inCashflowIdsStr)) {
            List<Long> inCashflowIds = new ArrayList<>();
            String[] str = inCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }
            metadataRelation.setInCashflowIds(inCashflowIds);
        }
        if (hlsCusConContractCashflow.getContractId() != null) {
            metadataRelation.setContractId(hlsCusConContractCashflow.getContractId());
        }

        if (hlsCusConContractCashflow.getCfStatus() != null) {
            metadataRelation.setCfStatus(hlsCusConContractCashflow.getCfStatus());
        }

        if (hlsCusConContractCashflow.getCfDirection() != null) {
            metadataRelation.setCfDirection(hlsCusConContractCashflow.getCfDirection());
        }
        if (hlsCusConContractCashflow.getSurplusAmountFlag() != null) {
            metadataRelation.setSurplusAmountFlag(hlsCusConContractCashflow.getSurplusAmountFlag());
        }
        if (hlsCusConContractCashflow.getCfItem() != null) {
            metadataRelation.setCfItem(hlsCusConContractCashflow.getCfItem());
        }
        /*if (hlsCusConContractCashflow.getNCfItem() != null) {
            metadataRelation.setNCfItem(hlsCusConContractCashflow.getNCfItem());
        }*/
        if (hlsCusConContractCashflow.getCfType() != null) {
            metadataRelation.setCfType(hlsCusConContractCashflow.getCfType());
        }
        /*if (hlsCusConContractCashflow.getNCfType() != null) {
            metadataRelation.setNCfType(hlsCusConContractCashflow.getNCfType());
        }*/

        if (hlsCusConContractCashflow.getDueDateFrom() != null) {
            metadataRelation.setDueDateFrom(hlsCusConContractCashflow.getDueDateFrom());
        }

        if (hlsCusConContractCashflow.getDueDateTo() != null) {
            metadataRelation.setDueDateTo(hlsCusConContractCashflow.getDueDateTo());
        }

        if (hlsCusConContractCashflow.getCfItemN() != null) {
            metadataRelation.setCfItemN(hlsCusConContractCashflow.getCfItemN());
        }

        if (hlsCusConContractCashflow.getDepositFlag() != null) {
            metadataRelation.setDepositFlag(hlsCusConContractCashflow.getDepositFlag());
        }

        IRequest requestCtx = createRequestContext(request);
       // List<HlsCusConContractCashflow> list = service.queryContractCashflowLov(requestCtx, metadataRelation, pagenum, pagesize);

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

//        List<HlsCusConContractCashflow> list=hlsCusConContractCashflowMapper.queryContractCashflowLov(metadataRelation);
        List<HlsCusConContractCashflow> list=hlsCusConContractCashflowMapper.queryContractCashflowLovNew(metadataRelation);
        //格式化日期
        /*if (list.size() > 0) {
            list.stream().forEach(conContractCashflow -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                conContractCashflow.setDueDateFormat(sdf.format(conContractCashflow.getDueDate()));
            });
        }*/

        return new ResponseData(list);
    }

    /**
     * 二期功能：收款管理-业务功能 核销为保证金 LOV 查询
     * @param requestData
     * @param request
     * @param response
     * @param hlsCusConContractCashflow
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/contract/cashflow/deposit/queryLov")
    @ResponseBody
    public ResponseData depositQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsCusConContractCashflow hlsCusConContractCashflow,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");

        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }


        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);
        //二期功能：接收多选伪LOV页面的查询参数
        String multiNotCashflowIdsStr = metadataRelation.getNotCashflowIdsStr();
        String multiInCashflowIdsStr = metadataRelation.getInCashflowIdsStr();
        if (multiNotCashflowIdsStr != null && !"".equals(multiNotCashflowIdsStr)) {
            List<Long> notCashflowIds = new ArrayList<>();

            String[] str = multiNotCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notCashflowIds.add(Long.parseLong(str[i]));
                }
            }
            if (notCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(notCashflowIds);
            }
        }

        if (multiInCashflowIdsStr != null && !"".equals(multiInCashflowIdsStr)) {
            List<Long> inCashflowIds = new ArrayList<>();
            String[] str = multiInCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }
            metadataRelation.setInCashflowIds(inCashflowIds);
        }

        //用来接收setLovPara 参数
        String notCashflowIdsStr = hlsCusConContractCashflow.getNotCashflowIdsStr();
        String inCashflowIdsStr = hlsCusConContractCashflow.getInCashflowIdsStr();
        if (notCashflowIdsStr != null && !"".equals(notCashflowIdsStr)) {
            List<Long> notCashflowIds = new ArrayList<>();

            String[] str = notCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notCashflowIds.add(Long.parseLong(str[i]));
                }
            }
            if (notCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(notCashflowIds);
            }
        }

        if (inCashflowIdsStr != null && !"".equals(inCashflowIdsStr)) {
            List<Long> inCashflowIds = new ArrayList<>();
            String[] str = inCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }
            metadataRelation.setInCashflowIds(inCashflowIds);
        }
        if (hlsCusConContractCashflow.getContractId() != null) {
            metadataRelation.setContractId(hlsCusConContractCashflow.getContractId());
        }

        if (hlsCusConContractCashflow.getCfStatus() != null) {
            metadataRelation.setCfStatus(hlsCusConContractCashflow.getCfStatus());
        }

        if (hlsCusConContractCashflow.getCfDirection() != null) {
            metadataRelation.setCfDirection(hlsCusConContractCashflow.getCfDirection());
        }
        if (hlsCusConContractCashflow.getSurplusAmountFlag() != null) {
            metadataRelation.setSurplusAmountFlag(hlsCusConContractCashflow.getSurplusAmountFlag());
        }
        if (hlsCusConContractCashflow.getCfItem() != null) {
            metadataRelation.setCfItem(hlsCusConContractCashflow.getCfItem());
        }

        if (hlsCusConContractCashflow.getCfType() != null) {
            metadataRelation.setCfType(hlsCusConContractCashflow.getCfType());
        }


        if (hlsCusConContractCashflow.getDueDateFrom() != null) {
            metadataRelation.setDueDateFrom(hlsCusConContractCashflow.getDueDateFrom());
        }

        if (hlsCusConContractCashflow.getDueDateTo() != null) {
            metadataRelation.setDueDateTo(hlsCusConContractCashflow.getDueDateTo());
        }

        if (hlsCusConContractCashflow.getCfItemN() != null) {
            metadataRelation.setCfItemN(hlsCusConContractCashflow.getCfItemN());
        }

        IRequest requestCtx = createRequestContext(request);

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        List<HlsCusConContractCashflow> list=hlsCusConContractCashflowMapper.queryContractCashflowForDepositLov(metadataRelation);


        return new ResponseData(list);
    }

    /**
     * 二期功能：查询现金流lov
     *      - 收付抵扣
     */
    @RequestMapping({"/contract/cashflow/deduct/queryLov"})
    @ResponseBody
    @SuppressWarnings("all")
    public ResponseData queryForDeduct(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                       HttpServletRequest request, HttpServletResponse response,
                                       HlsCusConContractCashflow hlsCusConContractCashflow,
                                       @RequestParam(defaultValue = "1") int pagenum,
                                       @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get(com.hand.hls.utils.HlsConstantUtil.BaseController.PARAMETER);
        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);
        String notCashflowIdsStr = hlsCusConContractCashflow.getNotCashflowIdsStr();
        String inCashflowIdsStr = hlsCusConContractCashflow.getInCashflowIdsStr();
        ArrayList inCashflowIds;
        String[] str;
        int i;
        if (null != notCashflowIdsStr && !"".equals(notCashflowIdsStr)) {
            inCashflowIds = new ArrayList();
            str = notCashflowIdsStr.split(",");

            for(i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    inCashflowIds.add(Long.parseLong(str[i]));
                }
            }

            if (inCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(inCashflowIds);
            }
        }

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(inCashflowIdsStr)) {
            inCashflowIds = new ArrayList();
            str = inCashflowIdsStr.split(",");

            for(i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }

            metadataRelation.setInCashflowIds(inCashflowIds);
        }

        if (null != hlsCusConContractCashflow.getQuotationId()) {
            metadataRelation.setQuotationId(hlsCusConContractCashflow.getQuotationId());
        }

        if (null != hlsCusConContractCashflow.getContractId()) {
            metadataRelation.setContractId(hlsCusConContractCashflow.getContractId());
        }

        if (null != hlsCusConContractCashflow.getCfStatus()) {
            metadataRelation.setCfStatus(hlsCusConContractCashflow.getCfStatus());
        }

        if (null != hlsCusConContractCashflow.getCfDirection()) {
            metadataRelation.setCfDirection(hlsCusConContractCashflow.getCfDirection());
        }

        if (null != hlsCusConContractCashflow.getSurplusAmountFlag()) {
            metadataRelation.setSurplusAmountFlag(hlsCusConContractCashflow.getSurplusAmountFlag());
        }

        if (null != hlsCusConContractCashflow.getCfItem()) {
            metadataRelation.setCfItem(hlsCusConContractCashflow.getCfItem());
        }

        if (null != hlsCusConContractCashflow.getnCfItem()) {
            metadataRelation.setnCfItem(hlsCusConContractCashflow.getnCfItem());
        }

        if (null != hlsCusConContractCashflow.getCfType()) {
            metadataRelation.setCfType(hlsCusConContractCashflow.getCfType());
        }

        if (null != hlsCusConContractCashflow.getnCfType()) {
            metadataRelation.setnCfType(hlsCusConContractCashflow.getnCfType());
        }

        if (null != hlsCusConContractCashflow.getQuotationNumber()) {
            metadataRelation.setQuotationNumber(hlsCusConContractCashflow.getQuotationNumber());
        }

        if(null != hlsCusConContractCashflow.getBpIdTenant()){
            metadataRelation.setBpIdTenant(hlsCusConContractCashflow.getBpIdTenant());
        }

        if(null != hlsCusConContractCashflow.getBpIdVender()){
            metadataRelation.setBpIdVender(hlsCusConContractCashflow.getBpIdVender());
        }

        if(org.apache.commons.lang3.StringUtils.isNotEmpty(hlsCusConContractCashflow.getCfItemN())){
            metadataRelation.setCfItemN(hlsCusConContractCashflow.getCfItemN());
        }

        IRequest requestCtx = this.createRequestContext(request);

        //角色为管理公司的用户，在指定功能移除权限，并将user_id做为参数传入mapper中
        /*if(conContractService.isCompanyManageRole(requestCtx,"EXTERNAL_MANAGE_USER")){
            metadataRelation.setManageUserId(requestCtx.getUserId());
            requestCtx.setAttribute("authorityRuleFlag", "N");
        }*/

        List<HlsCusConContractCashflow> list = hlsCusConContractCashflowMapper.queryContractCashflowLovForDeduct(metadataRelation);
        if (list.size() > 0) {
            list.stream().forEach((conContractCashflow) -> {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                conContractCashflow.setDueDateFormat(sdf.format(conContractCashflow.getDueDate()));
            });
        }

        return new ResponseData(list);
    }

    @RequestMapping("/con/contract/cashflow/selectOutFlowByContractId")
    public ResponseData selectByContract(HttpServletRequest request, HttpServletResponse response,
                                         Long contractId,
                                         @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        if (contractId == null) {
            return new ResponseData(false, "请先选择合同!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return new ResponseData(service.queryOutFlowCashFlowDetailByContractId(contractId, pagenum, pagesize));
    }


    @RequestMapping(value = "/contract/cashflow/csh/payment/create/query")
    @ResponseBody
    public ResponseData queryCshPaymentCreateInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                  HttpServletRequest request, HttpServletResponse response,
                                                  HlsCusConContractCashflow hlsCusConContractCashflow,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject)requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(hlsCusConContractCashflow.getNotCashflowIdsStr())) {
            String[] notInCashflowIds = hlsCusConContractCashflow.getNotCashflowIdsStr().split(",");
            List<Long> ids = new ArrayList<>(notInCashflowIds.length);
            for(int i = 0; i < notInCashflowIds.length; i++){
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(notInCashflowIds[i])){
                    ids.add(Long.valueOf(notInCashflowIds[i]));
                }
            }
            if (ids.size() > 0) {
                metadataRelation.setNotCashflowIds(ids);
            }
        }
        if(null != hlsCusConContractCashflow.getBpIdTenant()){
            metadataRelation.setBpIdTenant(hlsCusConContractCashflow.getBpIdTenant());
        }
        if(null != hlsCusConContractCashflow.getBpIdVender()){
            metadataRelation.setBpIdVender(hlsCusConContractCashflow.getBpIdVender());
        }
        if(null != hlsCusConContractCashflow.getPaymentBpId()){
            metadataRelation.setPaymentBpId(hlsCusConContractCashflow.getPaymentBpId());
        }
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(hlsCusConContractCashflow.getCfItemN())){
            metadataRelation.setCfItemN(hlsCusConContractCashflow.getCfItemN());
        }
        if(null != hlsCusConContractCashflow.getCfItem()){
            metadataRelation.setCfItem(hlsCusConContractCashflow.getCfItem());
        }
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(hlsCusConContractCashflow.getCondition())){
            metadataRelation.setCondition(hlsCusConContractCashflow.getCondition());
        }

        IRequest requestCtx = this.createRequestContext(request);
        metadataRelation.setEmployeeCode(requestCtx.getEmployeeCode());
        List<HlsCusConContractCashflow> list = cusConContractCashflowService.queryCshPaymentCreateInfo(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/contract/cashflow/cont/payment/query")
    @ResponseBody
    public ResponseData queryPaymentContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);

        IRequest requestCtx = createRequestContext(request);
        List<HlsCusConContractCashflow> list = service.queryCshPaymentCreateInfo(requestCtx, metadataRelation);
        return new ResponseData(list);
    }


    @RequestMapping(value = "/contract/cashflow/et/amount/query")
    @ResponseBody
    public ResponseData queryEtAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        String param = JsonUtils.toCamelJsonString(requestData.get("parameter"));
        Map map = (Map) JSON.parse(param);
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.queryEtAmount(iRequest, map));

    }

    @RequestMapping(value = "/search/csh/payment/home/query")
    @ResponseBody
    public ResponseData searchCshPaymentHomeInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                 HttpServletRequest request, HttpServletResponse response,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshPaymentReqHd metadataRelation = param.toJavaObject(HlsCusCshPaymentReqHd.class);

        IRequest requestCtx = createRequestContext(request);
        List<HlsCusCshPaymentReqHd> list =
                service.searchCshPaymentHomeInfo(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/csh/penalty/reduce/sumbit")
    @ResponseBody
    public ResponseData penaltyReduceSumbit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                            HttpServletRequest request, HttpSession session) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq = param.toJavaObject(HlsCusConDebtExemptionReq.class);
        IRequest requestCtx = createRequestContext(request);
        iConDebtExemptionReqService.hlsCusConDebtExemptionReqSave(session,requestCtx,hlsCusConDebtExemptionReq);


        return new ResponseData();
    }

    @RequestMapping(value = "/csh/penalty/reduce/sumbit2")
    @ResponseBody
    public ResponseData penaltyReduceSumbit2(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                            HttpServletRequest request, HttpSession session) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq = param.toJavaObject(HlsCusConDebtExemptionReq.class);
        IRequest requestCtx = createRequestContext(request);
        iConDebtExemptionReqService.hlsCusConDebtExemptionReqSave2(session,requestCtx,hlsCusConDebtExemptionReq);


        return new ResponseData();
    }


    @RequestMapping(value="/hls/con/overdue/query")
    @ResponseBody
    public ResponseData queryContractOverdueDetailInfo(HlsCusConContractCashflow hlsCusConContractCashflow,
                                         @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){

        PageHelper.startPage(page,pagesize);
        List<HlsCusConContractCashflow> list=hlsCusConContractCashflowMapper.queryContractOverdueDetailInfo(hlsCusConContractCashflow);
        return new ResponseData(list);
    }


    /**
     * 收款管理-业务功能 核销为预收款：代偿
     */
    @RequestMapping(value = "/contract/cashflow/comp/queryLov")
    @ResponseBody
    public ResponseData compQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                     HttpServletRequest request, HttpServletResponse response,
                                     HlsCusConContractCashflow hlsCusConContractCashflow,
                                     @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");

        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }


        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);
        //二期功能：接收多选伪LOV页面的查询参数
        String multiNotCashflowIdsStr = metadataRelation.getNotCashflowIdsStr();
        String multiInCashflowIdsStr = metadataRelation.getInCashflowIdsStr();
        if (multiNotCashflowIdsStr != null && !"".equals(multiNotCashflowIdsStr)) {
            List<Long> notCashflowIds = new ArrayList<>();

            String[] str = multiNotCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notCashflowIds.add(Long.parseLong(str[i]));
                }
            }
            if (notCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(notCashflowIds);
            }
        }

        if (multiInCashflowIdsStr != null && !"".equals(multiInCashflowIdsStr)) {
            List<Long> inCashflowIds = new ArrayList<>();
            String[] str = multiInCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }
            metadataRelation.setInCashflowIds(inCashflowIds);
        }

        //用来接收setLovPara 参数
        String notCashflowIdsStr = hlsCusConContractCashflow.getNotCashflowIdsStr();
        String inCashflowIdsStr = hlsCusConContractCashflow.getInCashflowIdsStr();
        if (notCashflowIdsStr != null && !"".equals(notCashflowIdsStr)) {
            List<Long> notCashflowIds = new ArrayList<>();

            String[] str = notCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notCashflowIds.add(Long.parseLong(str[i]));
                }
            }
            if (notCashflowIds.size() > 0) {
                metadataRelation.setNotCashflowIds(notCashflowIds);
            }
        }

        if (inCashflowIdsStr != null && !"".equals(inCashflowIdsStr)) {
            List<Long> inCashflowIds = new ArrayList<>();
            String[] str = inCashflowIdsStr.split(",");

            for (int i = 0; i < str.length; i++) {
                inCashflowIds.add(Long.parseLong(str[i]));
            }
            metadataRelation.setInCashflowIds(inCashflowIds);
        }
        if (hlsCusConContractCashflow.getContractId() != null) {
            metadataRelation.setContractId(hlsCusConContractCashflow.getContractId());
        }

        if (hlsCusConContractCashflow.getCfStatus() != null) {
            metadataRelation.setCfStatus(hlsCusConContractCashflow.getCfStatus());
        }

        if (hlsCusConContractCashflow.getCfDirection() != null) {
            metadataRelation.setCfDirection(hlsCusConContractCashflow.getCfDirection());
        }
        if (hlsCusConContractCashflow.getSurplusAmountFlag() != null) {
            metadataRelation.setSurplusAmountFlag(hlsCusConContractCashflow.getSurplusAmountFlag());
        }
        if (hlsCusConContractCashflow.getCfItem() != null) {
            metadataRelation.setCfItem(hlsCusConContractCashflow.getCfItem());
        }

        if (hlsCusConContractCashflow.getCfType() != null) {
            metadataRelation.setCfType(hlsCusConContractCashflow.getCfType());
        }


        if (hlsCusConContractCashflow.getDueDateFrom() != null) {
            metadataRelation.setDueDateFrom(hlsCusConContractCashflow.getDueDateFrom());
        }

        if (hlsCusConContractCashflow.getDueDateTo() != null) {
            metadataRelation.setDueDateTo(hlsCusConContractCashflow.getDueDateTo());
        }

        if (hlsCusConContractCashflow.getCfItemN() != null) {
            metadataRelation.setCfItemN(hlsCusConContractCashflow.getCfItemN());
        }

        IRequest requestCtx = createRequestContext(request);

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        List<HlsCusConContractCashflow> list=hlsCusConContractCashflowMapper.queryContractCashflowForCompLov(metadataRelation);


        return new ResponseData(list);
    }


}