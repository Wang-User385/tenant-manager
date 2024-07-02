package com.hand.hls.interfacePlatform.utils;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.util.ActivitiSysEventUtils;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.risk.mapper.RiskAttachmentMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestController extends BaseController {
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private FinanceBaseUtils base;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private ActivitiSysEventUtils activitiSysEventUtils;
    @Autowired
    private InvoiceBaseUtils invoiceBaseUtils;
    @Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private IGldContractCashflowService gldContractCashflowService;

    @RequestMapping(value = "/test/interface/queryFlow")
    @ResponseBody
    public ResponseData prjCreateCon(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String query_date = String.valueOf(param.get("query_date"));
        List<String> ioCodeList = new ArrayList<>();
        ioCodeList.add("AL001");
        ioCodeList.add("AL002");
        base.queryFlowItfc(requestContext,ioCodeList,query_date);

        List<HlsCusConContract> list=new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/postPayment")
    @ResponseBody
    public ResponseData postPayment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String payment_number = String.valueOf(param.get("payment_number"));
        HlsCusCshPaymentReqHd payment=new HlsCusCshPaymentReqHd();
        payment.setPaymentNumber(payment_number);
        payment=cshPaymentReqHdService.select(requestContext,payment,1,999).get(0);
        base.postPaymentItfc(requestContext,payment.getPaymentReqId());
        List<HlsCusConContract> list=new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/createProjectPlan")
    @ResponseBody
    public ResponseData createProjectPlan(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String project_number = String.valueOf(param.get("project_number"));
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectNumber(project_number);
        prjProject=hlsCusPrjProjectService.select(requestContext,prjProject,1,999).get(0);
        base.createProjectPlanItfc(requestContext,prjProject.getProjectId());
        List<HlsCusConContract> list=new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/writeMatch")
    @ResponseBody
    public ResponseData writeMatch(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long cashflow_id = Long.valueOf(String.valueOf(param.get("cashflow_id")) );
        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(cashflow_id);
        base.writeOffFlowItfc(requestContext,cashflow);
        List<HlsCusConContract> list=new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/createContract")
    @ResponseBody
    public ResponseData createContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long contract_id = Long.valueOf(String.valueOf(param.get("contract_id")));
        base.paymentFlowItfc(requestContext,contract_id);
        List<HlsCusConContract> list=new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/postCashflow")
    @ResponseBody
    public ResponseData postCashflow(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long write_off_id = Long.valueOf(String.valueOf(param.get("write_off_id")));
        HlsCusCshWriteOff writeOff=new HlsCusCshWriteOff();
        writeOff.setWriteOffId(write_off_id);
        writeOff=cshWriteOffMapper.select(writeOff).get(0);
        base.postCashflowItfc(requestContext,writeOff);
        List<HlsCusConContract> list=new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/uploatToPlat")
    @ResponseBody
    public ResponseData uploatToPlat(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long attachmentId = Long.valueOf(String.valueOf(param.get("attachmentId")));
        FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentId);
        String filePath = fndAttachment.getFilePath();
        String fileName = fndAttachment.getFileName();
        activitiSysEventUtils.upLoadFileToPlat1(fileName,filePath, String.valueOf(attachmentId));
        List<FndAttachment> list =new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/queryInvoiceUrl")
    @ResponseBody
    public ResponseData queryInvoiceUrl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String invoiceDocumentNumber = String.valueOf(param.get("invoice_document_number"));
        HlsCusAcrInvoiceHd acrInvoiceHd=new HlsCusAcrInvoiceHd();
        acrInvoiceHd.setDocumentNumber(invoiceDocumentNumber);
        acrInvoiceHd=acrInvoiceHdMapper.select(acrInvoiceHd).get(0);
        invoiceBaseUtils.queryInvoiceUrlItfc(requestContext,acrInvoiceHd);
        List<FndAttachment> list =new ArrayList<>();
        return new ResponseData(list);
    }

    @RequestMapping(value = "/test/interface/prjCheck")
    @ResponseBody
    public ResponseData prjCheck(@RequestParam Long contractId, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        //查询最新的合同数据 起租时进行分摊计算
        HlsCusConContract ct = new HlsCusConContract();
        ct.setContractId(contractId);
        ct = hlsCusConContractService.selectByPrimaryKey(requestContext,ct);
        gldContractCashflowService.clacFinanceIncomeRetail(requestContext, ct.getContractId(), ct.getVatRate(),ct.getIrr());
        return new ResponseData();
    }
}
