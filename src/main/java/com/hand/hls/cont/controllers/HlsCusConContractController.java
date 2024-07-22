package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ast.dto.VirtualConContractLov;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class HlsCusConContractController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(HlsCusConContractController.class);

    @Autowired
    private HlsCusConContractService service;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper HlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;

    @Autowired
    private IGldContractCashflowService gldContractCashflowService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;


    @RequestMapping(value = "/test/this/query")
    @ResponseBody
    public ResponseData query(HlsCusFctQuotationCashflow dto, HttpServletRequest request){
        List<HlsCusFctQuotationCashflow> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(list);
    }
    @RequestMapping(value = "/test/that/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryThat(@RequestBody HlsCusFctQuotationCashflow dto, HttpServletRequest request){
        List<HlsCusFctQuotationCashflow> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/that/query", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData queryThat2(HlsCusFctQuotationCashflow dto, HttpServletRequest request){
        List<HlsCusFctQuotationCashflow> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(list);
    }


    @RequestMapping(value = "/ct/prj/quotation/query/payment/change/info/lov")
    @ResponseBody
    public ResponseData queryPaymentChangeInfoLov(HlsCusConContract hlsCusConContract, HttpServletRequest request,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize){
        IRequest req = createRequestContext(request);
        return new ResponseData(service.queryPaymentChangeInfoLov(req, hlsCusConContract, page, pageSize));
    }

    @RequestMapping(value = "/con/contract/preRepayment/change/submit")
    @ResponseBody
    public ResponseData submitConContractPreRepaymentChange(@RequestBody HlsCusConContract hlsCusConContract, HttpServletRequest request){
        IRequest req = createRequestContext(request);
        return new ResponseData(service.submitConContractPreRepaymentChange(req, hlsCusConContract));
    }

    @RequestMapping(value = "/con/contract/rentplan/change/submit")
    @ResponseBody
    public ResponseData submitConContractRentplanChange(@RequestBody HlsCusConContract hlsCusConContract, HttpServletRequest request){
        IRequest req = createRequestContext(request);
        return new ResponseData(service.submitConContractRentplanChange(req, hlsCusConContract));
    }

    @RequestMapping(value = "/con/contract/change/submit")
    @ResponseBody
    public ResponseData submitConContractChange(@RequestBody HlsCusConContract hlsCusConContract, HttpServletRequest request){
        IRequest req = createRequestContext(request);
        return new ResponseData(service.submitConContractChange(req, hlsCusConContract));
    }

    @RequestMapping(value = "/prj/contract/save/change")
    @ResponseBody
    public ResponseData saveConContractChange(@RequestBody HlsCusConContract hlsCusConContract, HttpServletRequest request){
        IRequest req = createRequestContext(request);
        return new ResponseData(service.saveConContractChange(req, hlsCusConContract));
    }

    @RequestMapping(value = "/prj/contract/cancel/change")
    @ResponseBody
    public ResponseData backConContractChange(@RequestBody HlsCusConContract hlsCusConContract, HttpServletRequest request){
        IRequest req = createRequestContext(request);
        return new ResponseData(service.backConContractChange(req, hlsCusConContract));
    }

    @RequestMapping(value = "/ct/con/contract/query")
    @ResponseBody
    public ResponseData query(HlsCusConContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/con/contract/home/status/query")
    @ResponseBody
    public ResponseData conHomePageGetAllStatusContractCount(HlsCusConContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setCompanyId(requestContext.getCompanyId());
        return new ResponseData(service.conHomePageGetAllStatusContractCount(requestContext,dto));
    }
    @RequestMapping(value = "/ct/con/contract/home/info/query")
    @ResponseBody
    public ResponseData conHomePageContractInfoGrid(HlsCusConContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setCompanyId(requestContext.getCompanyId());
        return new ResponseData(service.conHomePageContractInfoGrid(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/ct/con/contract/submit")
    @ResponseBody
    public ResponseData conContractSave(@RequestBody List<HlsCusPrjQuotation> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusConContract> list=new ArrayList<>();
        list=service.conContractSave(requestCtx,dto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/con/contract/submit/wfl")
    @ResponseBody
    public ResponseData conContractSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInfo dto = new HlsCusPrjProjectInfo();
        dto.setHlsCusPrjProject(param.toJavaObject(HlsCusPrjProject.class));
        IRequest requestCtx = createRequestContext(request);
        service.conContractSubmit(requestCtx, dto);

        return new ResponseData();
    }

    @RequestMapping(value = "/ct/con/contract/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusConContract> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/con/contract/payment/table/make/status")
    @ResponseBody
    public String paymentTableMakeStatus(HttpServletRequest request,@RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.paymentTableMakeStatus(requestCtx,projectId);
    }

    @RequestMapping(value = "/con/contract/payment/request/status")
    @ResponseBody
    public String paymentReqStatus(HttpServletRequest request,@RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.paymentReqStatus(requestCtx,projectId);
    }

    @RequestMapping(value = "/con/contract/payment/table/confirm/status")
    @ResponseBody
    public String paymentTableConfirmStatus(HttpServletRequest request,@RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.paymentTableConfirmStatus(requestCtx,projectId);
    }

    @RequestMapping(value = "/con/contract/change/status")
    @ResponseBody
    public String conContractChangeStatus(HttpServletRequest request,@RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.conContractChangeStatus(requestCtx,projectId);
    }

    @RequestMapping(value = "/con/contract/et/status")
    @ResponseBody
    public String conContractEtStatus(HttpServletRequest request,@RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.conContractEtStatus(requestCtx,projectId);
    }

    @RequestMapping(value = "/con/contract/cancel/status")
    @ResponseBody
    public String conContractCancelStatus(HttpServletRequest request,@RequestParam Long projectId) {
        IRequest requestCtx = createRequestContext(request);
        return service.conContractCancelStatus(requestCtx,projectId);
    }

    @RequestMapping(value = "/ct/con/contract/payment/info/query")
    @ResponseBody
    public ResponseData queryPaymentInfoList(HlsCusConContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setCompanyId(requestContext.getCompanyId());
        return new ResponseData(service.queryPaymentInfoList(requestContext,dto,page,pageSize));
    }

    /**
     * abs 入池合同选择
     * ferry
     */
    @RequestMapping(value = "/abs/contract/select")
    @ResponseBody
    public ResponseData queryContractForABS(HlsCusConContract hlsCusConContract, HttpServletRequest httpServletRequest,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(httpServletRequest);
        return new ResponseData(service.selectContractForABS(requestContext, hlsCusConContract, page, pagesize));
    }

    /**
     * 租赁合同页面 查询汇总的放款信息
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/con/csh/payment/req/list/queryForSummary")
    @ResponseBody
    public ResponseData queryForSummary(HlsCusConContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusConContract> hlsCusConContracts = service.selectConCshPaymentReqForSummary(requestContext, dto, page, pageSize);
        return new ResponseData(hlsCusConContracts);
    }

    @RequestMapping(value = "/hls/cus/con/contract/detail/query")
    @ResponseBody
    public ResponseData conContractCshReqDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                final HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        IRequest requestContext = createRequestContext(request);
        dto.setCompanyId(requestContext.getCompanyId());
        List<HlsCusConContract> hlsCusConContracts = service.conContractCshReqDetail(dto);
        return new ResponseData(hlsCusConContracts);
    }


    /**
     * 租赁付款保存前校验
     *
     * @param request
     * @return
     * @throws
     */
    @RequestMapping(value = "/hls/cus/con/contract/csh/validate")
    @ResponseBody
    public ResponseData conContractValidate(@RequestBody HlsCusContractPkg hlsCusContractPkg,
                                            HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        service.conContractValidate(requestCtx, hlsCusContractPkg);
        return new ResponseData();
    }

    /**
     * 租赁付款保存
     *
     * @param request
     * @return
     * @throws
     */
    @RequestMapping(value = "/hls/cus/con/contract/csh/submit")
    @ResponseBody
    public ResponseData conContractSave(@RequestBody HlsCusContractPkg hlsCusContractPkg, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        service.conContractSave(requestCtx, hlsCusContractPkg);
        return new ResponseData();
    }




    @RequestMapping(value = "/hls/cus/con/loan/submit/wfl")
    @ResponseBody
    public ResponseData loanSubmitWfl(Long paymentReqId, HttpServletRequest request) throws ResMessageException, HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        ResponseData rd = new ResponseData(true);
        // 校验采购合同是否在审批中
        service.validatePurchaseContractStatus(requestCtx,paymentReqId);
        HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
        hlsCusBpMaster.setPaymentReqId(paymentReqId);
        List<HlsCusBpMaster> hlsCusBpMasterList= hlsCusBpMasterMapper.paymentFundTenantInfo(hlsCusBpMaster);
        //字段必输校验
//        Boolean  flag=true;
//        for(HlsCusBpMaster item:hlsCusBpMasterList){
//            flag=false;
//            rd.setSuccess(false);
//            rd.setMessage("承租人："+item.getBpName()+"——基本信息——SWIFT BIC不能为空！");
//            return rd;
//        }

//        if(flag){
        service.conLoanSubmit(requestCtx, paymentReqId);
//        }
        return rd;
    }

    @RequestMapping(value = "/hls/cus/con/contract/incept/date/update")
    @ResponseBody
    public ResponseData conSaveInceptDate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        try {
            service.contractUpdateCashflowDueDate(requestContext, hlsCusConContract);
        } catch (Exception e) {
            logger.error("计算出错", e);
            return new ResponseData(false, "计算出错");
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/cus/con/contract/incept/sumbit")
    @ResponseBody
    public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        HlsCusConContract cusConContract = new HlsCusConContract();
        if(hlsCusConContract.getContractId() != null){
            cusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContract);
        }
        cusConContract.setLeaseDateAdjust(hlsCusConContract.getLeaseDateAdjust());

        service.conInceptSubmit(requestContext, cusConContract);
        return new ResponseData();
    }


    @RequestMapping(value = "/hls/cus/con/contract/incept/save")
    @ResponseBody
    public ResponseData conInceptSave(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
//        service.contractUpdateCashflowDueDate(requestContext, hlsCusConContract);
        service.conInceptSave(requestContext, hlsCusConContract);

        return new ResponseData();

    }


    @RequestMapping(value = "/hls/cus/con/project/id/query")
    @ResponseBody
    public ResponseData selectByProjectId(HlsCusConContract dto, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) throws ParseException {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        ResponseData responseData = new ResponseData();
        List<HlsCusConContract> list = new ArrayList<>();
        try {
            list.add(service.selectByProjectId(dto.getProjectId()));
            responseData.setRows(list);
            responseData.setSuccess(true);
        } catch (Exception ex) {
            responseData.setSuccess(false);
            responseData.setMessage("未找到合同");
        }
        return responseData;
    }

    /**
     * 合同结束工作流
     *
     * @param request
     */
    @RequestMapping(value = "/hls/cus/con/contract/end/submit/wfl")
    @ResponseBody
    public ResponseData conContractEndSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        service.conContractEndSubmitWfl(requestCtx, param.toJavaObject(HlsCusContractTermination.class));
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/cus/con/contract/id/query")
    @ResponseBody
    public ResponseData queryFctContractByContractId(HlsCusConContract dto, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) throws ParseException {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        dto.setCompanyId(requestContext.getCompanyId());
        ResponseData responseData = new ResponseData();
        List<HlsCusConContract> list = new ArrayList<>();
        try {
            list.add(service.selectByPrimaryKey(requestContext, dto));
            responseData.setRows(list);
            responseData.setSuccess(true);
        } catch (Exception ex) {
            responseData.setSuccess(false);
            responseData.setMessage("未找到合同");
        }
        return responseData;
    }

    @RequestMapping(value = "/ct/con/contract/change/save")
    @ResponseBody
    public ResponseData conContractChangeSave(@RequestBody HlsCusContractPkg hlsCusContractPkg, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusConContract> list = new ArrayList<>();
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        try {
            hlsCusConContract = service.conContractChangeSave(requestCtx, hlsCusContractPkg);
            hlsCusConContract.setSuccess(true);
        } catch (Exception e) {
            hlsCusConContract.setSuccess(false);
            hlsCusConContract.setMessage(e.getMessage());
        }


        return new ResponseData(list.add(hlsCusConContract));
    }

    @RequestMapping(value = "/ct/con/contract/incept/save")
    @ResponseBody
    public ResponseData conInceptSave(@RequestBody HlsCusPrjProjectInfo dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        try {
            service.contractUpdateCashflowDueDate(requestCtx, dto.getHlsCusConContract());
            service.conInceptSave(requestCtx, dto);
            responseData.setSuccess(true);
        } catch (Exception e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @RequestMapping(value = "/ct/con/contract/change/submit/wfl")
    @ResponseBody
    public HlsCusPrjProject conContractChangeSubmit(@RequestBody HlsCusPrjProjectInfo dto, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        ResponseData responseData = new ResponseData();
        try {
            hlsCusPrjProject = service.conContractChangeSubmit(requestCtx, dto);
            hlsCusPrjProject.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            hlsCusPrjProject.setSuccess(false);
            hlsCusPrjProject.setMessage("提交工作流失败，请联系管理员");
        }
        return hlsCusPrjProject;
    }

    /**
     * 合同保险信息查询
     * ferry
     */
    @RequestMapping(value = "/insure/con/contract/list/query")
    @ResponseBody
    public ResponseData queryInsureContractList(@ModelAttribute("_request_data") LeafRequestData requestData,HttpServletRequest httpServletRequest,
                                                @RequestParam(defaultValue = "1") int pagenum,
                                                @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        IRequest requestContext = createRequestContext(httpServletRequest);
        return new ResponseData(service.queryInsureContractList(requestContext, dto, pagenum, pagesize));
    }

    /**
     * 保险合同信息查询
     * Jeffery
     * 2020年1月19日17点45分
     */
    @RequestMapping(value = "/insure/con/contract/info/query")
    @ResponseBody
    public ResponseData queryInsureContractInfo(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        return new ResponseData(service.queryInsureContractList(requestContext, dto, 1, 1));
    }

    /*
     * 租金支付表制作,根据传入的项目，拆分成对应金额的合同，自动计算报价
     *
     * */
    @RequestMapping(value = "/create/contract/batch/by/project")
    @ResponseBody
    public ResponseData createContractByProject(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> list = param.toJavaList(HlsCusConContract.class);

        list = service.createContractBatchByContract(requestContext,list);

        return new ResponseData(list);
    }

    /**
     * 租金支付表-退出前校验
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/contract/rentmake/exit/check")
    @ResponseBody
    public ResponseData exitCheckBp(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> list = param.toJavaList(HlsCusConContract.class);

        List<String> responseList = new ArrayList<>();
        responseList.add(service.exitCheckBp(requestContext,list));
        return new ResponseData(responseList);
    }


    /*
     * 租金支付表制作,删除合同
     *
     * */
    @RequestMapping(value = "/delete/contract/batch")
    @ResponseBody
    public ResponseData deleteContract(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> list = param.toJavaList(HlsCusConContract.class);

        service.deleteContract(requestContext,list);

        return new ResponseData(true);
    }

    @RequestMapping(value = "/hls/cus/con/cashflow/update/amortization/method")
    @ResponseBody
    public ResponseData updateCashflowAmortizationMethod(Long contractId, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        if (contractId != null) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(contractId);
            hlsCusConContract = service.selectByPrimaryKey(requestCtx, hlsCusConContract);
            hlsCusConContractMapper.updateCashflowAmortizationMethod(hlsCusConContract);

            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(contractId);
            List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.selectSelective(requestCtx, hlsCusConContractCashflow);
            List<HlsCusConContractCashflow> list = cashflowList.stream().filter(cashflow -> !(cashflow.getCfItem().equals(1L) || cashflow.getCfItem().equals(10L))).collect(Collectors.toList());
            for(HlsCusConContractCashflow item : list){
                if(item.getAmortizationMethod() != null){
                    item.setQuotationId(hlsCusConContract.getQuotationId());
                    HlsCusPrjQuotationCashflowMapper.updateQuotationAmortizationMethod(item);
                }
            }
        }
        return new ResponseData();
    }


    @RequestMapping(value = "/hls/cus/con/payment/submit/wfl")
    @ResponseBody
    public ResponseData paymentSubmitWfl(Long paymentReqId, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        ResponseData rd = new ResponseData(true);
        HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
        hlsCusBpMaster.setPaymentReqId(paymentReqId);
        List<HlsCusBpMaster> hlsCusBpMasterList= hlsCusBpMasterMapper.paymentFundTenantInfo(hlsCusBpMaster);
        //字段必输校验
//        Boolean  flag=true;
//        for(HlsCusBpMaster item:hlsCusBpMasterList){
//            flag=false;
//            rd.setSuccess(false);
//            rd.setMessage("承租人："+item.getBpName()+"——基本信息——SWIFT BIC不能为空！");
//            return rd;
//        }

//        if(flag){
        service.conPaymentSubmit(requestCtx, paymentReqId);
//        }
        return rd;
    }


    @RequestMapping(value = "/hls/csh/virtual/list/query")
    @ResponseBody
    public ResponseData queryVirtualContractLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        VirtualConContractLov dto = param.toJavaObject(VirtualConContractLov.class);

        return new ResponseData(service.queryVirtualContractLov(requestContext, dto, pagenum, pageSize));
    }

    /**
     * 查询项目报价
     * @param requestData
     * @param pagenum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/query/prj/quotation/info")
    @ResponseBody
    public ResponseData queryPrjQuotationInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");

        HlsCusPrjQuotation prjQuotation =  param.toJavaObject(HlsCusPrjQuotation.class);
        return new ResponseData(hlsCusPrjQuotationService.queryPrjQuotationInfoForContractPlan(prjQuotation));
    }


    @RequestMapping(value = "/con/contract/rentApportioned")
    @ResponseBody
    public ResponseData rentApportioned(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");

        Long contractId = Long.valueOf(String.valueOf(param.get("contract_id")) );
        HlsCusConContract ct = new HlsCusConContract();
        ct.setContractId(contractId);
        ct = service.selectByPrimaryKey(requestContext,ct);

        gldContractCashflowService.clacFinanceIncome(requestContext,contractId,ct.getVatRate(),ct.getIrr());

        return new ResponseData();
    }



    /**
     *租金支付表制作,根据传入的项目，拆分成对应金额的合同，自动计算报价,此时并没有创建真正的合同
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/create/contract/plan/by/project")
    @ResponseBody
    public ResponseData createContractPlanByProject(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjQuotation> list = param.toJavaList(HlsCusPrjQuotation.class);

        list = service.createContractPlanByContract(requestContext,list);

        return new ResponseData(list);
    }



    /**
     * 租金支付表制作,删除合同投放计划
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delete/contract/plan")
    @ResponseBody
    public ResponseData deleteContractPlan(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjQuotation> list = param.toJavaList(HlsCusPrjQuotation.class);

        service.deleteContractPlan(requestContext,list);

        return new ResponseData(true);
    }

    /**
     * 投放计划确认
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/contract/plan/confirm")
    @ResponseBody
    public ResponseData ContractPlanConfirm(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjQuotation> list = param.toJavaList(HlsCusPrjQuotation.class);

        service.confirmContractPlan(requestContext,list);

        return new ResponseData(true);
    }

    /**
     * 支付表确认新建
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "save/contract/confirm")
    @ResponseBody
    public ResponseData createContractConfirm(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm = param.toJavaObject(HlsCusConContractRentPaymentConfirm.class);

        List<HlsCusConContractRentPaymentConfirm> listR = service.createContractConfirm(requestContext,conContractRentPaymentConfirm)  ;

        return new ResponseData(listR);
    }

    /**
     * 支付表确认提交审批
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "submit/contract/confirm/wfl")
    @ResponseBody
    public ResponseData submitContractConfirmWfl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm = param.toJavaObject(HlsCusConContractRentPaymentConfirm.class);

        List<HlsCusConContractRentPaymentConfirm> listR = service.submitContractConfirmWfl(requestContext,conContractRentPaymentConfirm);

        return new ResponseData(listR);
    }

    //确认起租
    @RequestMapping(value = "/con/contract/incept/confirm")
    @ResponseBody
    public ResponseData conInceptConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        service.conInceptconfirm(requestContext, param);
        return new ResponseData();
    }

    //支付表确认更新现金流日期
    @RequestMapping(value = "/con/contract/updateCshDate")
    @ResponseBody
    public ResponseData updateCshDate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        service.updateCshDate(requestContext, param);
        return new ResponseData();
    }

    //支付表确认保存成功
    @RequestMapping(value = "/con/contract/updateQuotationInfo")
    @ResponseBody
    public ResponseData updateQuotationInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        service.updateQuotationInfo(requestContext, param);
        return new ResponseData();
    }

    //合同结束
    @RequestMapping(value = "/con/contract/terminate/confirm")
    @ResponseBody
    public ResponseData conTerminateConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        service.conTerminateConfirm(requestContext, param);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/change/submit/wfl")
    @ResponseBody
    public ResponseData conContractChangeSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        /*JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInfo dto = new HlsCusPrjProjectInfo();

        dto.setHlsCusPrjProject(param.toJavaObject(HlsCusPrjProject.class));
        IRequest requestCtx = createRequestContext(request);
        service.conContractSubmit(requestCtx, dto);*/

        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto = new HlsCusPrjProject();
        dto = param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestCtx = createRequestContext(request);
        service.conContractChangeSubmitWfl(requestCtx, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/repayment/calculate")
    @ResponseBody
    public ResponseData contractChangeRepaymentCalculate(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData){
        IRequest req = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        return new ResponseData(service.contractChangeRepaymentCalculate(req, hlsCusConContract));
    }

    @RequestMapping(value = "/con/contract/et/calculate")
    @ResponseBody
    public ResponseData contractChangeEtCalculate(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData){
        IRequest req = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        return new ResponseData(service.contractChangeEtCalculate(req, hlsCusConContract));
    }

    @RequestMapping(value = "/con/contract/change/cancel")
    @ResponseBody
    public ResponseData conContractChangeCancel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto =param.toJavaObject(HlsCusPrjProject.class);
        IRequest requestCtx = createRequestContext(request);
        service.conContractChangeCancel(requestCtx, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/compareinfoGenerate")
    @ResponseBody
    public ResponseData compareinfoGenerate(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData){
        IRequest req = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract hlsCusConContract = param.toJavaObject(HlsCusConContract.class);
        return new ResponseData(service.compareinfoGenerate(req, hlsCusConContract));
    }
    @RequestMapping(value = "/prj/creditLine/query")
    @ResponseBody
    public ResponseData queryCreditLine(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData){
        IRequest req = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ResponseData responseData =new ResponseData();
        if("MANUFACTURER".equals(param.get("bp_type"))){
            HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = param.toJavaObject(HlsCusHlsCreditLineChance.class);
            Long bpId=hlsCusHlsCreditLineChance.getBpId();
            hlsCusHlsCreditLineChance.setBpId(null);
            List<HlsCusHlsCreditLineChance> hlsCusHlsCreditLineChances = hlsCusHlsCreditLineChanceMapper.selectCreditLineChanceByStatus(hlsCusHlsCreditLineChance);
            for (HlsCusHlsCreditLineChance cusHlsCreditLineChance : hlsCusHlsCreditLineChances) {
                if(bpId.equals(cusHlsCreditLineChance.getBpId())&&(cusHlsCreditLineChance.getCreditLineStatus().equals("APPROVED")||cusHlsCreditLineChance.getCreditLineStatus().equals("APPROVING"))){
                    responseData.setRows(hlsCusHlsCreditLineChances);
                }
            }
        }else if("TENANT".equals(param.get("bp_type"))){
            Map prjRpModifyMap = new HashMap();
            prjRpModifyMap.put("bpName",param.get("bp_name"));
            List<Map> prjRpModifyEntranceQuery = hlsCusPrjProjectMapper.prjRpModifyEntranceQuery(prjRpModifyMap);
            for (Map map : prjRpModifyEntranceQuery) {
                if(prjRpModifyEntranceQuery.size()>0&&("APPROVED".equals(map.get("project_status"))||"APPROVING".equals(map.get("project_status")))){
                    responseData.setRows(prjRpModifyEntranceQuery);
                    break;
                }
            }
        }
        return responseData;
    }
    /**
     * 根据合同编号查找合同id
     * @param contractNumber 合同编号
     * @return 合同id
     */
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
    /**
     * 进件合同起租列表页面查询
     */
    @RequestMapping(value = "/contract/queryContractInceptList")
    @ResponseBody
    public ResponseData queryContractInceptList(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProject dto1 = param.toJavaObject(HlsCusPrjProject.class);
        List<HlsCusConContract> list = service.queryContractInceptInfoMain(requestContext,dto1, pagenum, pagesize);
        return new ResponseData(list);
    }


}