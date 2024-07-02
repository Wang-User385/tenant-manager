package com.hand.hls.cont.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.calc.service.impl.ConContractChangeQuatationServiceImpl;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.*;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.service.IConContractChangeReqService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.cont.service.IConContractSpinService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.CshDepositDeductReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.IConDebtExemptionReqCfService;
import com.hand.hls.csh.service.IConDebtExemptionReqService;
import com.hand.hls.csh.service.ICshDepositDeductReqHdService;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.lease.dto.LeaseItemInsurance;
import com.hand.hls.lease.mapper.LeaseItemInsuranceMapper;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.utils.IDUtils;
import com.hand.hls.sys.dto.DocumentHistoryData;
import com.hand.hls.sys.dto.SysDocumentHistory;
import com.hand.hls.sys.dto.SysDocumentHistoryBlob;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryBlobMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.sys.utils.SysDocumentHistoryUtils;
import com.hand.hls.utils.*;
import com.hand.hls.wfl.service.IActivitiStartService;

import java.rmi.NoSuchObjectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import hls.core.hls.service.impl.HlsPenaltyCalServiceImpl;
import hls.core.sys.mapper.SysCodeValueMapper;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class ConContractChangeReqServiceImpl extends BaseServiceImpl<HlsCusConContractChangeReq> implements IConContractChangeReqService {
    public static final String CHANGE_TYPE_PRE = "PRE";
    public static final String CHANGE_TYPE_RESCHEDULE = "RESCHEDULE";
    public static final String CHANGE_TYPE_EXTENSION = "EXTENSION";
    public static final String CHANGE_TYPE_PENALTY_EXEMPT = "PENALTY_EXEMPT";
    public static final String CHANGE_TYPE_PREPAYMENT = "PREPAYMENT";
    public static final String CHANGE_TYPE_REPO = "REPO";
    public static final String CHANGE_TYPE_DELAY = "DELAY";
    public static final String CHANGE_TYPE_TENANT = "TENANT";
    public static final String CHANGE_TYPE_LEASE_ITEM = "LEASE_ITEM";
    public static final String WORKFLOW_APPROVING = "APPROVING";
    public static final String WORKFLOW_UNDO = "UNDO";
    public static final String QUOTATION_STATUS_PENDING = "PENDING";
    public static final String DOCUMENT_CATEGORY_CONTRACT_CHANGE = "CONTRACT_CHANGE";
    public static final String BUSINESS_TYPE_CONTRACT_CHANGE = "CONTRACT_CHANGE";
    public static final String DOCUMENT_TYPE_STD = "STD";
    public static final String CONTRACT = "CONTRACT";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private HlsCusPrjQuotationMapper quotationMapper;
    @Autowired
    private HlsCusPrjQuotationService prjQuotationService;
    @Autowired
    private HlsCusPrjQuotationCashflowService prjQuotationCashflowService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IConContractService contractService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusConContractChangeReqMapper contractChangeReqMapper;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusConContractBpMapper contractBpMapper;
    @Autowired
    private HlsCusConContractLeaseItemMapper contractLeaseItemMapper;
    @Autowired
    private HlsCusConContractMortgageMapper mortgageMapper;
    @Autowired
    private HlsCusConContractCashflowMapper contractCashflowMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private IConContractCashflowService contractCashflowService;
    @Autowired
    private IConDebtExemptionReqService conDebtExemptionReqService;
    @Autowired
    private IConDebtExemptionReqCfService conDebtExemptionReqCfService;
    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;
    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;
    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;
    @Autowired
    private SysDocumentHistoryMapper sysDocumentHistoryMapper;
    @Autowired
    private SysDocumentHistoryBlobMapper documentHistoryBlobMapper;
    @Autowired
    private IConContractSpinService conContractSpinService;
    @Autowired
    private HlsCusContractAttachmentMapper contractAttachmentMapper;
    @Autowired
    private IFndAttachmentMultiService attachmentMultiService;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private HlsCfItemMapper hlsCfItemMapper;
    @Autowired
    private SysCodeValueMapper sysCodeValueMapper;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private ConContractChangeCashflowMapper conContractChangeCashflowMapper;
    @Autowired
    private HlsPenaltyCalServiceImpl penaltyCalServiceImpl;
    @Autowired
    private HlsCusCshTransactionMapper transactionMapper;
    @Autowired
    private ConContractChangeQuatationServiceImpl changeQuatationServiceImpl;
    @Autowired
    private CshDepositDeductReqHdMapper cshDepositDeductReqHdMapper;
    @Autowired
    private ICshDepositDeductReqHdService iCshDepositDeductReqHdService;
    @Autowired
    private LeaseItemInsuranceMapper conLeaseItemInsuranceMapper;
    @Autowired
    private HlsCusPrjQuotationDetailsMapper quotationDetailMapper;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    private Map<String, QuotationCommon> QuotationCommonRegistion = new HashMap();
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Boolean getIfQuotationPendding(IRequest iRequest, HlsCusConContract contract) {
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setEnabledFlag("Y");
        //prjQuotation.setSourceDocumentCategory("CONTRACT");
        prjQuotation.setDataClass("CON_QUOTATION");
        prjQuotation.setSourceDocumentId(contract.getContractId());
        prjQuotation.setStatus("PENDING");
        List<HlsCusPrjQuotation> list = quotationMapper.select(prjQuotation);
        return CollectionUtils.isEmpty(list);
    }

    @Override
    public ResponseData backUp(IRequest iRequest, DocumentHistoryData documentHistoryData, LeafRequestData leafRequestData, String contractId) throws ResMessageException {
        HlsCusConContract contract = new HlsCusConContract();
        if (StringUtils.isEmpty(contractId)) {
            throw new ResMessageException("contract id is null");
        } else {
            contract.setContractId(Long.valueOf(contractId));
            contract.setContractStatus("PENDING");
            contractService.updateByPrimaryKeySelective(iRequest, contract);
            return sysDocumentHistoryService.save(documentHistoryData, iRequest, leafRequestData);
        }
    }

    public void checkBeforeSave(IRequest iRequest, Long contractId,String changeType) throws ParameterNullException{
        Map<String, Long> map = new HashMap<>();
        map.put("contractId", contractId);
        List<HlsCusConContractLeaseItem> conContractLeaseItems = contractLeaseItemMapper.queryContractLeaseItemDetail(map);

        map = new HashMap<>();
        map.put("contractId", contractId);
        List<HlsCusConContractBp> contractBps = contractBpMapper.queryContractBp(map);

        if(CHANGE_TYPE_TENANT.equals(changeType) && contractBps.size()==0){
            throw new RuntimeException("合同信息有误，无承租人信息！");
        }
        if(CHANGE_TYPE_LEASE_ITEM.equals(changeType) && conContractLeaseItems.size()==0){
            throw new RuntimeException("合同信息有误，无租赁物信息！");
        }

    }

    @Override
    public List<HlsCusConContractChangeReq> submit(IRequest iRequest, HlsCusConContractChangeReq contractChangeReq) throws ParameterNullException {
        Long contractId = contractChangeReq.getContractId();
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);

        checkBeforeSave(iRequest,contractId,contractChangeReq.getChangeType());

        //查询报价id
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(contractId);
        //hlsCusPrjQuotation.setSourceDocumentCategory(CONTRACT);
        hlsCusPrjQuotation.setDataClass("CON_QUOTATION");
        hlsCusPrjQuotation = quotationMapper.selectOne(hlsCusPrjQuotation);
        contractChangeReq.setQuotationId(hlsCusPrjQuotation.getQuotationId());

        contractChangeReq.setDocumentCategory(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        contractChangeReq.setDocumentType(DOCUMENT_TYPE_STD);
        contractChangeReq.setBusinessType(BUSINESS_TYPE_CONTRACT_CHANGE);
        contractChangeReq.setStatus("NEW");
        contractChangeReq.setChangeReqUserId(iRequest.getUserId());
        if (StringUtils.isEmpty(contractChangeReq.getChangeReqNumber())) {
            Map<String, String> params = new HashMap();
            contractChangeReq.setChangeReqNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, contractChangeReq.getDocumentCategory(), contractChangeReq.getDocumentType(), contractChangeReq.getBusinessType(), params));
        }

        List<HlsCusConContractChangeReq> list = new ArrayList();
        list.add(contractChangeReq);
        Boolean isNew = contractChangeReq.getChangeReqId() == null;
        list = self().batchUpdate(iRequest, list);
        Long contractChangeReqId = list.get(0).getChangeReqId();
        if (isNew) {
            saveHistory(iRequest, contractChangeReqId, DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        }

        if (!"true".equals(contractChangeReq.getIsTryCal())) {
            contract.setContractStatus("PENDING");
            contractService.updateByPrimaryKeySelective(iRequest, contract);
        }

        return list;
    }

    @Override
    public List<HlsCusConContractChangeReq> submitPartialPrepayment(IRequest request, HlsCusConContractChangeReq cusConContractChangeReq) throws ParameterNullException {

        //大单提前部分还本申请创建
        Long contractId = cusConContractChangeReq.getContractId();
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);

        contract = contractService.selectByPrimaryKey(request, contract);

        //TODO 2023-04-04 需要修改取值逻辑
        //获取提前还本起始期数
        //Long ccrStartTimes = getCcrStartTimes(contractId);

        //设定提前还本起始期数
        //cusConContractChangeReq.setCcrStartTimes(ccrStartTimes);


        cusConContractChangeReq.setQuotationId(contract.getQuotationId());

        cusConContractChangeReq.setDocumentCategory(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        cusConContractChangeReq.setDocumentType(DOCUMENT_TYPE_STD);
        cusConContractChangeReq.setBusinessType(BUSINESS_TYPE_CONTRACT_CHANGE);
        cusConContractChangeReq.setStatus("NEW");
        cusConContractChangeReq.setChangeReqUserId(request.getUserId());
        if (StringUtils.isEmpty(cusConContractChangeReq.getChangeReqNumber())) {
            Map<String, String> params = new HashMap();
            cusConContractChangeReq.setChangeReqNumber(fndCodingRuleValuesService.getCodeRuleValue(request,
                    cusConContractChangeReq.getDocumentCategory(),
                    cusConContractChangeReq.getDocumentType(),
                    cusConContractChangeReq.getBusinessType(), params));
        }

        List<HlsCusConContractChangeReq> list = new ArrayList();
        list.add(cusConContractChangeReq);
        boolean isNew = cusConContractChangeReq.getChangeReqId() == null;
        list = self().batchUpdate(request, list);
        Long contractChangeReqId = list.get(0).getChangeReqId();
        if (isNew) {
            HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
            quotation.setQuotationId(contract.getQuotationId());

            List<HlsCusPrjQuotation> quotations = quotationMapper.select(quotation);

            createHistoryForPartialPrepayment(contractChangeReqId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, contractId, quotations,
                    cusConContractChangeReq.getChangeType());
        }

        if (!"true".equals(cusConContractChangeReq.getIsTryCal())) {
            //更新合同，项目的状态
            contract.setContractStatus("PENDING");
            contractService.updateByPrimaryKeySelective(request, contract);

            HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
            cusPrjProject.setProjectId(contract.getProjectId());
            cusPrjProject.setProjectStatus("PENDING");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(request, cusPrjProject);
        }

        return list;
    }

    @Override
    public ResponseData recalculatePartialPrepayment(IRequest iRequest, Long documentId, Long quotationId, JSONObject param) throws Exception {
        //大单提前部分还本
        HlsCusConContractChangeReq changeReq = contractChangeReqMapper.selectByPrimaryKey(documentId);
        if (changeReq == null) {
            throw new HlsCusException("未能查到对应的变更申请单");
        }
        if ("APPROVED".equals(changeReq.getStatus()) || "APPROVING".equals(changeReq.getStatus())) {
            throw new HlsCusException("当前状态不能计算！");
        }
        List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_change_req");
        if (CollectionUtils.isEmpty(jsonObjects)) {
            throw new HlsCusException("变更单据不存在");
        }
        //提前还本日检查
        checkIfWriteOffPartialPrepayment(param, changeReq);

        //计算校验标示
        JSONObject object = jsonObjects.get(0);
        Long id = object.getLong("id");
        JSONObject data = object.getJSONObject("data");
        data.put("recalculate", "Y");
        logger.info("updateHistoryDetail 更新变更历史详情");
        long start = System.currentTimeMillis();
        sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, id, data.toJSONString());
        logger.info("updateHistoryDetail 更新变更历史详情耗时：{}",System.currentTimeMillis() - start);


        //TODO 确定此方法是否需要调用
        //recoverContract(iRequest, documentId, quotationId, changeReq, changeReq.getContractId());


        SysDocumentHistory history = sysDocumentHistoryMapper.selectDocumentHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, null);

        //前台传入的参数
        JSONArray config = param.getJSONArray("config");

        changeQuatationServiceImpl.contractChangeCalcCashflow(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE,
                quotationId, history.getHistoryId(), config, param.getString("price_list"));

        //更新Xirr
        updateDocumentHistoryChangeReqPartialPrepayment(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, quotationId,changeReq.getChangeReqId(),config);


        return new ResponseData();
    }

    @Override
    public void cancelChangeReqPartialPrepayment(IRequest iRequest, Long changeReqId) throws NoSuchObjectException {
        HlsCusConContractChangeReq changeReq = new HlsCusConContractChangeReq();
        changeReq.setChangeReqId(changeReqId);
        changeReq = contractChangeReqMapper.selectByPrimaryKey(changeReq);
        if ("APPROVING".equals(changeReq.getStatus())) {
            throw new NoSuchObjectException("审批中不可以取消");
        } else if ("UNDO".equals(changeReq.getStatus())) {
            throw new NoSuchObjectException("已取消申请的变更不可以再取消!");
        } else {
            changeReq.setStatus("UNDO");
            self().updateByPrimaryKeySelective(iRequest, changeReq);
            changeReq = self().selectByPrimaryKey(iRequest, changeReq);

            recoverContractAndProject(iRequest, changeReq);
        }
    }

    /**
     * 大单还原合同与项目状态
     * @param iRequest
     * @param changeReq
     * @throws NoSuchObjectException
     */
    private void recoverContractAndProject(IRequest iRequest, HlsCusConContractChangeReq changeReq) throws NoSuchObjectException {
        List<JSONObject> list = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, changeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract");
        HlsCusConContract baseContract = JSONObject.parseObject(JsonUtils.toCamelJsonString(list.get(0).getJSONObject("data")), HlsCusConContract.class);
        String contractStatus = baseContract.getContractStatus();
        Long projectId = baseContract.getProjectId();
        Long contractId = changeReq.getContractId();
        //还原合同、项目的状态
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract.setContractStatus(contractStatus);
        contractService.updateByPrimaryKeySelective(iRequest, contract);

        HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
        cusPrjProject.setProjectId(projectId);
        cusPrjProject.setProjectStatus("APPROVED");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, cusPrjProject);
    }

    @Override
    public void approvePartialPrepaymentWfl(IRequest iRequest, HlsCusConContractChangeReq contractChangeReq) throws Exception {
        contractChangeReq = self().selectByPrimaryKey(iRequest, contractChangeReq);
        if ("APPROVED".equals(contractChangeReq.getStatus()) || WORKFLOW_APPROVING.equals(contractChangeReq.getStatus()) || WORKFLOW_UNDO.equals(contractChangeReq.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractChangeReq.getContractId());
        List<JSONObject> contractJsonList = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, contractChangeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract");
        if (contractJsonList.size() <= 0) {
            throw new ResMessageException("未找到合同变更历史，请核查数据!");
        } else {
            JSONObject contractJson = (JSONObject) contractJsonList.get(0).get("data");
            contract = contractJson.toJavaObject(HlsCusConContract.class);

            Map<String, Long> map = new HashMap();
            map.put("contractId", contractChangeReq.getContractId());
            contract = contractMapper.queryConContractDetails(map).get(0);
            if (StringUtils.isEmpty(contractChangeReq.getChangeType())) {
                throw new ResMessageException("没有变更类型");
            }

            List<HlsCusConContractChangeReq> cs = new ArrayList<>();
            cs.add(contractChangeReq);
            Map<String, Object> params = new HashMap();
            params.put("workFlowType", "CON_PARTIAL_PREPAYMENT_WFL");
            params.put("WORK_FLOW", "CON_PARTIAL_PREPAYMENT_WFL");
            params.put("DEMO", "CON_CONTRACT");
            params.put("BUSINESS_KEY", contractChangeReq.getChangeReqId());
            params.put("documentCategory", DOCUMENT_CATEGORY_CONTRACT_CHANGE);
            params.put("documentName", contract.getContractName()+"-大单提前部分还本");
            params.put("documentNumber", contract.getContractNumber());
            params.put("documentId", contractChangeReq.getChangeReqId());
            params.put("contractId", contractChangeReq.getContractId());
            params.put("contractNumber", contract.getContractNumber());
            params.put("contractName", contract.getContractName());
            params.put("contractChangeReq", JSON.toJSONString(contractChangeReq));
            params.put("contract", JSON.toJSONString(contract));
            params.put("quotationId", contract.getQuotationId());
            //业务协办复核节点审批人
            params.put("projectAssistant", contract.getAssistProjectManager());
            params.put("unitId", iRequest.getAttribute("unitId"));
            params.put("companyId", iRequest.getAttribute("companyId"));
            activitiStartService.start(iRequest, cs, params);
            contractChangeReq.setStatus("APPROVING");
            self().updateByPrimaryKeySelective(iRequest, contractChangeReq);
        }

    }

    @Override
    public Long checkTimesPartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq) throws ResMessageException {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId",changeReq.getContractId());
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = contractCashflowMapper.queryConContractCashflowDetail(map);
        Date changeStartDate = changeReq.getChangeStartDate();

        Long times = 0L;

        for (HlsCusConContractCashflow cashflow : hlsCusConContractCashflowList) {
            if (cashflow.getDueDate().after(changeStartDate)) {
                if (!"NOT".equals(cashflow.getWriteOffFlag())) {
                    throw new ResMessageException("提前还本日之后已有核销！");
                }else {
                    times = cashflow.getTimes();
                    break;
                }
            }
        }

        return times;
    }

    /**
     * 大单提前部分还本 - 修改变更起始期数，检验提前还本日是否在本期数与上一期日期之间
     *
     * @param iRequest
     * @param changeReq
     * @return
     */
    @Override
    public boolean checkDatePartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq) {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId",changeReq.getContractId());
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = contractCashflowMapper.queryConContractCashflowDetail(map);
        Date changeStartDate = changeReq.getChangeStartDate();
        Long ccrStartTimes = changeReq.getCcrStartTimes();
        Long previousTimes = ccrStartTimes - 1L;
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowList.stream()
                .filter(item -> item.getTimes().equals(ccrStartTimes) && (item.getCfItem().equals(1L) || item.getCfItem().equals(0L)))
                .collect(Collectors.toList());
        List<HlsCusConContractCashflow> previousCashflows = hlsCusConContractCashflowList.stream()
                .filter(item -> item.getTimes().equals(previousTimes) && (item.getCfItem().equals(1L) || item.getCfItem().equals(0L)))
                .collect(Collectors.toList());
        // 没有对应数据直接返回false
        if (cashflowList.isEmpty() || previousCashflows.isEmpty()) {
            return false;
        }
        Date currentDueDate = cashflowList.get(0).getDueDate();
        Date previousDueDate = previousCashflows.get(0).getDueDate();


        return changeStartDate.after(previousDueDate) && changeStartDate.before(currentDueDate);
    }

    @Override
    public String checkDepositPartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq) {
        String flag = "N";
        HlsCusConContractChangeReq contractChangeReq = new HlsCusConContractChangeReq();
        contractChangeReq.setChangeReqId(changeReq.getChangeReqId());
        contractChangeReq = self().selectByPrimaryKey(iRequest, contractChangeReq);
        HlsCusPrjQuotation hlsCusPrjQuotation = quotationMapper.selectByPrimaryKey(contractChangeReq.getQuotationId());
        Double deposit = hlsCusPrjQuotation.getDeposit();

        List<JSONObject> contractCashflows = null;
        List<HlsCusConContractCashflow> changedContractCashflows = new ArrayList<>();
        Long ccrStartTimes = contractChangeReq.getCcrStartTimes();
        try {
            contractCashflows = getNewContractCashFlows(iRequest, contractChangeReq.getChangeReqId(),
                    DOCUMENT_CATEGORY_CONTRACT_CHANGE, contractChangeReq.getQuotationId());

            for (int i = 0; i < contractCashflows.size(); i++) {
                HlsCusConContractCashflow cashflow = JSONObject.parseObject(JsonUtils.toCamelJsonString(contractCashflows.get(i).getJSONObject("data")), HlsCusConContractCashflow.class);
                if(cashflow.getCfItem() == 1 && cashflow.getCashflowId() == null && cashflow.getTimes() >= ccrStartTimes){
                    //变更当期往后的租金现金流
                    changedContractCashflows.add(cashflow);
                }
            }

            //变更后的所有租金之和
            Double totalChangeDueAmount = changedContractCashflows.stream().mapToDouble(HlsCusConContractCashflow::getDueAmount).sum();
            if (MathUtil.sub(deposit, totalChangeDueAmount) > 0){
                flag = "Y";
            }

        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 大单提前部分还本 - 审批结束 处理逻辑
     *
     * @param iRequest
     * @param result
     * @param changeReq
     * @param processInstanceId
     */
    @Override
    public void changeReqPartialPrepaymentApproved(IRequest iRequest, String result, HlsCusConContractChangeReq changeReq, String processInstanceId) throws NoSuchObjectException {
        HlsCusConContractChangeReq contractChangeReq = contractChangeReqMapper.selectByPrimaryKey(changeReq);
        //1.更新变更申请单状态
        contractChangeReq.setStatus(result);
        //2.同步审批通过的现金流到合同现金流表上
        if (StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, result)){
            try {
                //2.1调用通用方法更新
                leaveHistoryPartialPrepayment(iRequest, changeReq, Long.parseLong(processInstanceId));
            } catch (Exception e) {
                logger.error("leave history error:", e);
            }

            //2.2更新项目现金流表、报价表
            Long quotationId = changeReq.getQuotationId();
            if (changeReq.getChangeXirr() != null){
                HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
                prjQuotation.setQuotationId(quotationId);
                prjQuotation = quotationMapper.selectByPrimaryKey(prjQuotation);
                prjQuotation.setXirr(changeReq.getChangeXirr());
                prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotation);
            }
            //获取项目现金流表信息
            HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
            quotationCashflow.setQuotationId(quotationId);
            List<HlsCusPrjQuotationCashflow> quotationCashflowList = prjQuotationCashflowService.selectSelective(iRequest, quotationCashflow);

            HlsCusPrjQuotationCashflow maxPrjQuotationCashflow = prjQuotationCashflowMapper.selectPrjQuotationCashflowMaxTimeAndDate(quotationCashflow);

            Long maxTimes = maxPrjQuotationCashflow.getTimes();

            //获取更新后的合同现金流表信息
            HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
            contractCashflow.setContractId(changeReq.getContractId());
            List<HlsCusConContractCashflow> contractCashflowList = contractCashflowService.selectSelective(iRequest, contractCashflow);

            //需要更新的项目现金流表信息
            List<HlsCusPrjQuotationCashflow> updateQuotationCashflowList = new ArrayList<>();

            for (int i = 0; i < maxTimes ; i++) {
                Long currentTime = i + 1L;
                HlsCusPrjQuotationCashflow updateQuotationCashflow = quotationCashflowList.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);
                HlsCusConContractCashflow baseContractCashflow = contractCashflowList.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);

                updateQuotationCashflow.setDueAmount(baseContractCashflow.getDueAmount());
                updateQuotationCashflow.setNetDueAmount(baseContractCashflow.getNetDueAmount());

                updateQuotationCashflow.setInterest(baseContractCashflow.getInterest());
                updateQuotationCashflow.setVatInterest(baseContractCashflow.getVatInterest());
                updateQuotationCashflow.setNetInterest(baseContractCashflow.getNetInterest());

                updateQuotationCashflow.setPrincipal(baseContractCashflow.getPrincipal());
                updateQuotationCashflow.setNetPrincipal(baseContractCashflow.getNetPrincipal());
                updateQuotationCashflow.setVatPrincipal(baseContractCashflow.getVatPrincipal());

                updateQuotationCashflow.setOutstandingPrincipal(baseContractCashflow.getOutstandingPrincipal());
                updateQuotationCashflow.setCashflowIrr(baseContractCashflow.getCashflowIrr());

                updateQuotationCashflow.setCalcDate(baseContractCashflow.getCalcDate());
                updateQuotationCashflow.setFinIncomeDate(baseContractCashflow.getCalcDate());

                updateQuotationCashflow.set__status("update");

                updateQuotationCashflowList.add(updateQuotationCashflow);
            }

            if (CollectionUtils.isNotEmpty(updateQuotationCashflowList)){
                prjQuotationCashflowService.batchUpdate(iRequest, updateQuotationCashflowList);
            }

        }

        //3.还原项目表、合同表的单据状态
        recoverContractAndProject(iRequest, changeReq);

        //4.更新变更申请表
        self().updateByPrimaryKeySelective(iRequest, contractChangeReq);

    }


    private void leaveHistoryPartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq, Long processInstanceId) throws Exception {
        Long contractId = changeReq.getContractId();
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);

        contract = contractService.selectByPrimaryKey(iRequest, contract);
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setQuotationId(contract.getQuotationId());

        List<HlsCusPrjQuotation> quotations = quotationMapper.select(quotation);
        List<Map<String, Object>> list = getDatasForPartialPrepayment(changeReq.getChangeReqId(), contractId, quotations, changeReq.getChangeType());



        Long lastVersion = sysDocumentHistoryService.leaveHistoryWithData(DOCUMENT_CATEGORY_CONTRACT_CHANGE, changeReq.getChangeReqId(), list);
        sysDocumentHistoryService.leaveHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, changeReq.getChangeReqId(), lastVersion);

    }


    public void updateDocumentHistoryChangeReqPartialPrepayment(IRequest iRequest, Long documentId, String documentCategory, Long quotationId,Long changeReqId, JSONArray config) throws Exception {
        logger.info("updateDocumentHistoryChangeReq-更新变更后xirr");
        List<JSONObject> contractCashflows = getNewContractCashFlows(iRequest, documentId, documentCategory, quotationId);
        //获取前台传入变更参数
        Double leaseCharge = 0D;
        for(int i=0;i < config.size();i++){
            JSONObject configDetail = config.getJSONObject(i);
            if("lease_charge".equals(configDetail.getString("field"))){
                //手续费
                leaseCharge = configDetail.getDouble("value");
            }
        }

        Double xirr = getXirr(iRequest, quotationId, contractCashflows,leaseCharge);
        logger.info("updateDocumentHistoryChangeReq-更新变更后xirr:{}",xirr);

        HlsCusConContractChangeReq changeReq = contractChangeReqMapper.selectByPrimaryKey(changeReqId);
        if (!xirr.isInfinite() && !xirr.isNaN()){
            changeReq.setChangeXirr((double) Math.round(xirr * 1000000) / 1000000);
            SysDocumentHistory history = sysDocumentHistoryMapper.selectDocumentHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, null);
            SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
            detail.setHistoryId(history.getHistoryId());
            detail.setTableName("con_contract_change_req");
            detail.setTablePkValue(String.valueOf(documentId));
            List<SysDocumentHistoryDetail> list = sysDocumentHistoryDetailMapper.select(detail);
            detail = list.get(0);

            String historyData = detail.getHistoryData();
            JSONObject jsonObject = JSON.parseObject(historyData);
            jsonObject.put("change_xirr", xirr);
            detail.setHistoryData(jsonObject.toJSONString());
            sysDocumentHistoryDetailMapper.updateByPrimaryKeySelective(detail);

        }else {
            changeReq.setChangeXirr(null);
        }

        contractChangeReqMapper.updateByPrimaryKey(changeReq);


    }

    /**
     * 大单提前还本：计算XIRR
     */
    private Double getXirr(IRequest iRequest, Long quotationId, List<JSONObject> contractCashflows, Double leaseCharge) {
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = prjQuotationService.selectByPrimaryKey(iRequest, prjQuotation);

        HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
        quotationCashflow.setQuotationId(quotationId);
        HlsCusPrjQuotationCashflow maxPrjQuotationCashflow = prjQuotationCashflowMapper.selectPrjQuotationCashflowMaxTimeAndDate(quotationCashflow);

        Long maxTimes = maxPrjQuotationCashflow.getTimes();

        //保证金
        Double deposit = prjQuotation.getDeposit() == null ? 0 : prjQuotation.getDeposit();

        //留购金
        Double residualValue = prjQuotation.getResidualValue() == null ? 0 : prjQuotation.getResidualValue();

        List<HlsCusConContractCashflow> calcXirrContractCashflowList = new ArrayList<>();

        //第0期加上保证金、手续费
        HlsCusConContractCashflow cashflow0 = new HlsCusConContractCashflow();
        cashflow0.setTimes(0L);
        cashflow0.setDueAmount(0 - prjQuotation.getFinanceAmount() + deposit + leaseCharge);
        cashflow0.setDueDate(prjQuotation.getLeaseStartDate());
         calcXirrContractCashflowList.add(0, cashflow0);


        for (int i = 0; i < contractCashflows.size(); i++) {
            HlsCusConContractCashflow cashflow = JSONObject.parseObject(JsonUtils.toCamelJsonString(contractCashflows.get(i).getJSONObject("data")), HlsCusConContractCashflow.class);
            if(cashflow.getCfItem() == 1){
                //最后一期扣除保证金 加上留购金
                if (Objects.equals(cashflow.getTimes(), maxTimes)){
                    cashflow.setDueAmount( cashflow.getDueAmount() - deposit + residualValue);
                }
                calcXirrContractCashflowList.add(cashflow);
            }
        }
        calcXirrContractCashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes)).collect(Collectors.toList());
        return getXirr(calcXirrContractCashflowList);
    }



    private Double getXirr(List<HlsCusConContractCashflow> cashFlowList) {
        double[] payments = new double[cashFlowList.size()];
        Date[] dates = new Date[cashFlowList.size()];
        for (int i = 0; i < cashFlowList.size(); i++) {
            HlsCusConContractCashflow cashflow = cashFlowList.get(i);
            payments[i] = cashflow.getDueAmount();
            dates[i] = cashflow.getDueDate();
        }
        logger.info("payments1: {}",payments);
        logger.info("dates1: {}",dates);
        return HlsCusXirr.Newtons_method(0.1, payments, dates);
    }


    private void checkIfWriteOffPartialPrepayment(JSONObject param, HlsCusConContractChangeReq changeReq) throws HlsCusException {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId",changeReq.getContractId());
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = contractCashflowMapper.queryConContractCashflowDetail(map);
        Date changeStartDate = null;
        Double changePrincipal = 0D;
        Long ccrStartTimes = 0L;
        JSONArray config = param.getJSONArray("config");

        for (int i = 0; i < config.size(); ++i) {
            JSONObject jsonObject = config.getJSONObject(i);
            if (jsonObject.getString("field").equals("change_start_date") && jsonObject.getLong("value") != null) {
                changeStartDate = jsonObject.getDate("value");
            }
            if (jsonObject.getString("field").equals("change_principal") && jsonObject.getLong("value") != null) {
                changePrincipal = jsonObject.getDouble("value");
            }
            if (jsonObject.getString("field").equals("ccr_start_times") && jsonObject.getLong("value") != null) {
                ccrStartTimes = jsonObject.getLong("value");
            }
        }

        //检验提前还本金额是否小于剩余本金
        // 获取变更起始期前一期现金流剩余本金
        Double ccrOutstandingPrincipal = 0D;
        Long ccrStartTimes1 = ccrStartTimes - 1L;
        List<HlsCusConContractCashflow> ccrOutstandingCashflows = hlsCusConContractCashflowList.stream().filter(item->item.getTimes().equals(ccrStartTimes1)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(ccrOutstandingCashflows)){
            ccrOutstandingPrincipal = hlsCusConContractCashflowList.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
        }else{
            ccrOutstandingPrincipal = hlsCusConContractCashflowList.stream().filter(item->item.getTimes().equals(ccrStartTimes1)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
        }
        if (ccrOutstandingPrincipal - changePrincipal > 0){

        }else {
            throw new HlsCusException("提前还本金额应小于剩余本金（"+ccrOutstandingPrincipal+"）！");
        }

        for (HlsCusConContractCashflow cashflow : hlsCusConContractCashflowList) {
            if (cashflow.getDueDate().after(changeStartDate)) {
                if (!"NOT".equals(cashflow.getWriteOffFlag())) {
                    throw new HlsCusException("提前还本日之后已有核销！");
                }
            }
        }
    }


    private void createHistoryForPartialPrepayment(Long documentId, String documentCategory, Long contractId,
                                                   List<HlsCusPrjQuotation> quotations, String changeType)
            throws ParameterNullException {
        List<Map<String, Object>> datas = getDatasForPartialPrepayment(documentId, contractId, quotations, changeType);
        sysDocumentHistoryService.createHistory(documentCategory, documentId, datas);
    }

    public List<Map<String, Object>> getDatasForPartialPrepayment(Long documentId, Long contractId,
                                                                  List<HlsCusPrjQuotation> quotations, String changeType)
            throws ParameterNullException {
        List<Map<String, Object>> datas = new ArrayList<>();
        datas.addAll(getContractChangeReqForPartialPrepayment(documentId,contractId));
        datas.addAll(getContractForPartialPrepayment(contractId));
        datas.addAll(getPrjQuotationForPartialPrepayment(quotations.get(0).getQuotationId(), contractId));
        datas.addAll(getContractCashflowForPartialPrepayment(quotations, contractId));
        datas.addAll(getContractAttachment(contractId));
        return datas;
    }

    private List<Map<String, Object>> getContractChangeReqForPartialPrepayment(Long changeReqId,Long contractId) throws ParameterNullException {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        Map<String, Long> map = new HashMap<>();
        map.put("changeReqId", changeReqId);
        hlsCusConContract.setChangeReqId(changeReqId);
        List<HlsCusConContractChangeReq> changeReqs = contractChangeReqMapper.queryContractChangePartialPrepaymentReqDetail(map);

        //获取提前还本起始期数
        /*Long ccrStartTimes = getCcrStartTimes(contractId);

        for (HlsCusConContractChangeReq changeReq:
                changeReqs) {
            changeReq.setCcrStartTimes(ccrStartTimes);
        }*/

        Map<String, String> meta = SysDocumentHistoryUtils.initMeta("con_contract_change_req", "change_req_id");
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords(meta, changeReqs);
        } catch (ResMessageException e) {
            logger.warn("getContractChangeReqForPartialPrepayment: {}", e.getMessage());
        }

        return list;
    }

    //获取提前还本起始期数
    private Long getCcrStartTimes(Long contractId) {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId",contractId);
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = contractCashflowMapper.queryConContractCashflowDetail(map);
        Long ccrStartTimes = 0L;
        for (HlsCusConContractCashflow cashflow : hlsCusConContractCashflowList) {
            if (cashflow.getCfItem() == 1 && "NOT".equals(cashflow.getWriteOffFlag())) {
                ccrStartTimes = cashflow.getTimes();
                break;
            }
        }
        //设定提前还本起始期数
        return ccrStartTimes;
    }

    private List<Map<String, Object>> getContractForPartialPrepayment(Long contractId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId", contractId);
        List<HlsCusConContract> contracts = contractMapper.queryConContractPartialPrepaymentDetails(map);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords("con_contract", "contract_id", contracts);
        } catch (ResMessageException var6) {
            logger.warn("getContract {}", var6.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getPrjQuotationForPartialPrepayment(Long quotationId, Long contractId) throws ParameterNullException {
        HlsCusPrjQuotation quotaion = new HlsCusPrjQuotation();
        quotaion.setQuotationId(quotationId);
        List<HlsCusPrjQuotation> quotations = quotationMapper.prjQuotationDetailQuery(quotaion);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords("prj_quotation", "quotation_id", "con_contract", contractId.toString(), quotations);
        } catch (ResMessageException var6) {
            logger.warn("getPrjQuotation {}", var6.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getContractCashflowForPartialPrepayment(List<HlsCusPrjQuotation> quotations, Long contractId) throws ParameterNullException {
        if (CollectionUtils.isEmpty(quotations)) {
            return new ArrayList<>();
        } else {
            List<Map<String, Object>> mapList = new ArrayList<>();

            for (HlsCusPrjQuotation item : quotations) {
                Map<String, Object> map = new HashMap<>();

                map.put("contractId", contractId);
                List<HlsCusConContractCashflow> contractCashflows = contractCashflowMapper.queryConContractCashflowDetail(map);
                List<Map<String, Object>> list = new ArrayList<>();
                try {
                    list = SysDocumentHistoryUtils.initRecords("con_contract_cashflow", "cashflow_id", "con_contract", contractId.toString(), contractCashflows);
                    list.stream().forEach(e->{
                        if (null != e.get("due_date")) {
                            DateTime dueDate = DateUtil.parse(e.get("due_date").toString());
                            e.put("due_date", DateUtil.format(dueDate, "yyyy-MM-dd"));
                        }
                    });
                } catch (ResMessageException e) {
                    logger.warn("getPrjQuotation {}", e.getMessage());
                }
                mapList.addAll(list);
            }
            return mapList;
        }
    }

    public void changeReqCheckBeforeSubmit(IRequest iRequest, HlsCusConContractChangeReq contractChangeReq) throws Exception {
        HlsCusConContract hlsCusConContract = contractMapper.selectByPrimaryKey(contractChangeReq.getContractId());

        if(contractChangeReq.getChangeIrr()==null){
            throw new ResMessageException("变更后的合同IRR为空，请重新计算！");
        }

        /*if(MathUtil.compare(contractChangeReq.getChangeIrr(), hlsCusConContract.getIrr()) < 0){
            throw new ResMessageException("变更后的合同IRR小于变更前的IRR，请重新计算！");
        }*/

        /*
        HlsBpMaster hlsBpMaster = (HlsBpMaster) hlsBpMasterMapper.selectByPrimaryKey(hlsCusConContract.getTenantId());
        HlsBpMaster manufactorBp = (HlsBpMaster) hlsBpMasterMapper.selectByPrimaryKey(hlsCusConContract.getManufacturerId());
        if("NP".equals(hlsBpMaster.getBpClass())){
            //合同投放日加上合同变更后期数 减去承租人年龄 < 60
            IDUtils.Person person = IDUtils.getBirAgeSex(hlsBpMaster.getIdCardNo());
            Date contractStartDate = hlsCusConContract.getLeaseStartDate();
            Calendar calendar = Calendar.getInstance();
            Calendar calendarBp = Calendar.getInstance();
            calendarBp.setTime(person.getBirthDate());
            calendarBp.add(Calendar.YEAR,60);
            calendar.setTime(contractStartDate);
            calendar.add(Calendar.MONTH,contractChangeReq.getChangeTerm().intValue()-1);

            if(calendar.after(calendarBp)){
                throw new ResMessageException("承租人年龄与租赁期限之和不得超过60岁！");
            }
        }

        HlsCreditLine hlsCreditLine = hlsCusHlsCreditLineMapper.selectByPrimaryKey(hlsCusConContract.getCreditLineId());
        // 厂商总额度
        Double creditLineAmt = contractMapper.queryCreditLineAmt(hlsCreditLine.getpCreditLineId());

        Long[] contractIds = new Long[1];
        contractIds[0] = hlsCusConContract.getContractId();

        if(MathUtil.compare(contractChangeReq.getChangeTerm().doubleValue(),36D) > 0){
            // 3年期（不含）以上
            Double threeYearAmount = MathUtil.round(contractMapper.queryAmount(
                    contractIds,
                    hlsCusConContract.getManufacturerId(),
                    hlsCreditLine.getpCreditLineId(),
                    null,
                    null,
                    null,
                    "Y",
                    null
            ), 2);
            // 3年期（不含）以上批复比例
            PrjCreditReplyPara threeYearBpReplyPara = prjCreditReplyParaMapper.queryPara(hlsCusConContract.getContractId(), "THREE_YEARS_RATIO");
            if (null != threeYearBpReplyPara
                    && null != threeYearBpReplyPara.getVauleFrom()
                    && null != threeYearBpReplyPara.getVauleTo()) {
                double rateFrom = MathUtil.mul(MathUtil.sub(1D,threeYearBpReplyPara.getVauleTo().doubleValue()), creditLineAmt, 2);
                double rateTo = MathUtil.mul(MathUtil.sub(1D,threeYearBpReplyPara.getVauleFrom().doubleValue()), creditLineAmt, 2);
                double limitAmount = rateFrom > rateTo?rateFrom:rateTo;
                if (Double.compare(threeYearAmount, limitAmount) > 0) {
                    throw new ResMessageException(manufactorBp.getBpName() + "厂商3年期比例不满足批复要求！");
                }
            }
        }

        if(MathUtil.compare(contractChangeReq.getChangeTerm().doubleValue(),60D) >= 0){
            // 5年期（含）以上
            Double fiveYearAmount = MathUtil.round(contractMapper.queryAmount(
                    contractIds,
                    hlsCusConContract.getManufacturerId(),
                    hlsCreditLine.getpCreditLineId(),
                    null,
                    null,
                    null,
                    null,
                    "Y"
            ), 2);
            // 5年期（含）以上批复比例
            PrjCreditReplyPara fiveYearBpReplyPara = prjCreditReplyParaMapper.queryPara(hlsCusConContract.getContractId(), "FIVE_YEARS_RATIO");
            if (null != fiveYearBpReplyPara
                    && null != fiveYearBpReplyPara.getVauleFrom()
                    && null != fiveYearBpReplyPara.getVauleTo()) {
                double rateFrom = MathUtil.mul(fiveYearBpReplyPara.getVauleFrom().doubleValue(), creditLineAmt, 2);
                double rateTo = MathUtil.mul(fiveYearBpReplyPara.getVauleTo().doubleValue(), creditLineAmt, 2);
                if (Double.compare(fiveYearAmount, rateFrom) < 0 || Double.compare(fiveYearAmount, rateTo) > 0) {
                    throw new ResMessageException(manufactorBp.getBpName() + "厂商5年期比例不满足批复要求！");
                }
            }
        }*/
    }

    @Override
    public void approveWfl(IRequest iRequest, HlsCusConContractChangeReq contractChangeReq) throws Exception {
        contractChangeReq = self().selectByPrimaryKey(iRequest, contractChangeReq);
        if ("APPROVED".equals(contractChangeReq.getStatus()) || WORKFLOW_APPROVING.equals(contractChangeReq.getStatus()) || WORKFLOW_UNDO.equals(contractChangeReq.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractChangeReq.getContractId());
        List<JSONObject> contractJsonList = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, contractChangeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract");
        if (contractJsonList.size() <= 0) {
            throw new ResMessageException("未找到合同变更历史，请核查数据!");
        } else {
            JSONObject contractJson = (JSONObject) contractJsonList.get(0).get("data");
            contract = contractJson.toJavaObject(HlsCusConContract.class);
            JSONArray hlsCusConContractBps = null;
            List<JSONObject> con_contract_bps = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, contractChangeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_bp");
            if (con_contract_bps.size() > 0) {
                JSONArray jsonArrayBps = new JSONArray();
                con_contract_bps.forEach(item -> {
                    jsonArrayBps.add(item.get("data"));
                });
                hlsCusConContractBps = jsonArrayBps;
            }

            int tenantCount = 0;

            for (int i = 0; i < hlsCusConContractBps.size() && CHANGE_TYPE_PRE.equals(contractChangeReq.getChangeType()); ++i) {
                JSONObject hlsCusConContractBp = hlsCusConContractBps.getJSONObject(i);
                if ("TENANT".equalsIgnoreCase(hlsCusConContractBp.getString("bp_category")) && !"delete".equals(hlsCusConContractBp.getString("_status"))) {
                    if (!"delete".equals(hlsCusConContractBp.getString("_status"))) {
                        ++tenantCount;
                        if (!contract.getTenantId().equals(hlsCusConContractBp.getLong("bp_id"))) {
                            throw new ResMessageException("商业伙伴信息中承租人信息与合同主承租人信息不匹配，合同有且只能有一个主承租人!");
                        }
                    }
                }
            }

            if (tenantCount != 1 && CHANGE_TYPE_PRE.equals(contractChangeReq.getChangeType())) {
                throw new ResMessageException("合同有且只能有一个主承租人!");
            }

            if (CHANGE_TYPE_PRE.equals(contractChangeReq.getChangeType())) {
                checkBpDuplicate(con_contract_bps);
            }

            Map<String, Long> map = new HashMap();
            map.put("contractId", contractChangeReq.getContractId());
            contract = contractMapper.queryConContractDetails(map).get(0);
            if (StringUtils.isEmpty(contractChangeReq.getChangeType())) {
                throw new ResMessageException("没有变更类型");
            }

            switch (contractChangeReq.getChangeType()) {
                case CHANGE_TYPE_EXTENSION:
                case CHANGE_TYPE_DELAY:
                case CHANGE_TYPE_PREPAYMENT:
                case CHANGE_TYPE_REPO:
                case CHANGE_TYPE_RESCHEDULE:
                    List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, contractChangeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_change_req");
                    if (CollectionUtils.isEmpty(jsonObjects)) {
                        throw new RuntimeException("历史数据中不存在变更表的数据");
                    } else if (!"Y".equals((jsonObjects.get(0)).getJSONObject("data").getString("recalculate"))) {
                        throw new RuntimeException("尚未计算，请计算后提交");
                    }
                    break;
                default:
                    break;
            }
            switch (contractChangeReq.getChangeType()) {
                case CHANGE_TYPE_EXTENSION:
                case CHANGE_TYPE_DELAY:
                    changeReqCheckBeforeSubmit(iRequest, contractChangeReq);
                    break;
                default:
                    break;
            }
            List<HlsCusConContractChangeReq> cs = new ArrayList<>();
            cs.add(contractChangeReq);
            Map<String, Object> params = new HashMap();
            params.put("workFlowType", "CONTRACT_CHANGE");
            params.put("WORK_FLOW", "CONTRACT_CHANGE");
            params.put("DEMO", "CON");
            params.put("BUSINESS_KEY", contractChangeReq.getChangeReqId());
            params.put("documentCategory", DOCUMENT_CATEGORY_CONTRACT_CHANGE);
            params.put("documentName", contract.getContractNumber()+"-零售合同变更");
            params.put("documentNumber", contract.getContractNumber());
            params.put("documentId", contractChangeReq.getChangeReqId());
            params.put("contractId", contractChangeReq.getContractId());
            params.put("contractNumber", contract.getContractNumber());
            params.put("contractName", contract.getContractName());
            params.put("contractChangeReq", JSON.toJSONString(contractChangeReq));
            params.put("contract", JSON.toJSONString(contract));
            params.put("quotationId", contract.getQuotationId());
            activitiStartService.start(iRequest, cs, params);
            contractChangeReq.setStatus("APPROVING");
            self().updateByPrimaryKeySelective(iRequest, contractChangeReq);

        }
    }

    private void checkBpDuplicate(List<JSONObject> hlsCusConContractBps) {
        for (int i = 0; i < hlsCusConContractBps.size(); ++i) {
            JSONObject jsonObject = (JSONObject) hlsCusConContractBps.get(i);
            Long id = jsonObject.getLong("id");
            JSONObject contractBp = jsonObject.getJSONObject("data");
            if (!"delete".equals(contractBp.getString("_status"))) {
                Long bpId = contractBp.getLong("bp_id");

                for (int i1 = 0; i1 < hlsCusConContractBps.size(); ++i1) {
                    JSONObject queryObject = hlsCusConContractBps.get(i1);
                    Long queryId = queryObject.getLong("id");
                    JSONObject queryContractBp = queryObject.getJSONObject("data");
                    if (!"delete".equals(queryContractBp.getString("_status")) && !queryId.equals(id) && bpId.equals(queryContractBp.getLong("bp_id"))) {
                        throw new RuntimeException("商业伙伴重复！");
                    }
                }
            }
        }

    }

    private void saveHistory(IRequest iRequest, Long documentId, String documentCategory) throws ParameterNullException {
        HlsCusConContractChangeReq changeReq = new HlsCusConContractChangeReq();
        changeReq.setChangeReqId(documentId);
        changeReq = self().selectByPrimaryKey(iRequest, changeReq);
        Long contractId = changeReq.getContractId();
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        //quotation.setSourceDocumentCategory(CONTRACT);
        quotation.setDataClass("CON_QUOTATION");
        quotation.setSourceDocumentId(contractId);
        quotation.setEnabledFlag("Y");
        List<HlsCusPrjQuotation> quotations = quotationMapper.select(quotation);
        createHistory(documentId, documentCategory, contractId, quotations, changeReq.getChangeType());
    }

    private void createHistory(Long documentId, String documentCategory, Long contractId, List<HlsCusPrjQuotation> quotations) throws ParameterNullException {
        createHistory(documentId, documentCategory, contractId, quotations, null);
    }

    private void createHistory(Long documentId, String documentCategory, Long contractId, List<HlsCusPrjQuotation> quotations, String changeType) throws ParameterNullException {
        List<Map<String, Object>> datas = getDatas(documentId, contractId, quotations, changeType);
        sysDocumentHistoryService.createHistory(documentCategory, documentId, datas);
    }

    @Override
    public List<Map<String, Object>> getDatas(Long documentId, Long contractId, List<HlsCusPrjQuotation> quotations) throws ParameterNullException {
        return getDatas(documentId, contractId, quotations, null);
    }

    public List<Map<String, Object>> getDatas(Long documentId, Long contractId, List<HlsCusPrjQuotation> quotations, String changeType) throws ParameterNullException {
        List<Map<String, Object>> datas = new ArrayList<>();
        List<Map<String, Object>> contractChangeReq = getContractChangeReq(documentId);
        datas.addAll(contractChangeReq);
        List<Map<String, Object>> contract = getContract(contractId);
        if ("EXTENSION".equals(changeType)) {
            contract.forEach(item -> {
                item.remove("ccr_start_times");
                item.remove("ccr_outstanding_times");
                item.remove("ccr_fee");
            });
        }

        Map<String, Object> map = contractChangeReq.get(0);
        Object quotationId = map.get("quotation_id");
        if (quotationId != null) {
            HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
            cashflow.setContractId(contractId);
            cashflow.setGeneratedSourceDocId(Long.valueOf(String.valueOf(quotationId)));
            /*Double ccrTotalPenaltyAmount = contractMapper.queryCcrTotalPenaltyAmount(cashflow);
            contract.forEach((item) -> {
                item.put("ccr_total_penalty_amount", ccrTotalPenaltyAmount);
            });*/
        }

        datas.addAll(contract);
        datas.addAll(getContractBp(contractId));
        datas.addAll(getContractLeaseItem(contractId));
        // 保险
        datas.addAll(getContractLeaseItemInsurance(contractId));
        //抵押物
        //datas.addAll(getContractMortgage(contractId));
        datas.addAll(getPrjQuotation(contractId));
        List<Map<String, Object>> list = getContractCashflow(quotations, contractId);
        datas.addAll(list);
        datas.addAll(getContractAttachment(contractId));
        return datas;
    }

    private List<Map<String, Object>> getContractChangeReq(Long changeReqId) throws ParameterNullException {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        Map<String, Long> map = new HashMap<>();
        map.put("changeReqId", changeReqId);
        hlsCusConContract.setChangeReqId(changeReqId);
        List<HlsCusConContractChangeReq> changeReqs = contractChangeReqMapper.queryContractChangeReqDetail(map);
        List mapList = new ArrayList<>();
        if (changeReqs.get(0).getQuotationId() != null) {
            Map<String, Long> quotation = new HashMap<>();
            quotation.put("quotationId", changeReqs.get(0).getQuotationId());
            List<Map<String, Object>> maps = contractCashflowService.queryEtAmount(RequestHelper.getCurrentRequest(false), quotation);

            for (HlsCusConContractChangeReq item : changeReqs) {
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(item));
                //提前结清增加参数
                if (CHANGE_TYPE_PREPAYMENT.equals(changeReqs.get(0).getChangeType())) {
                    Map reqInfo = contractMapper.selectPrePaymentChangeReqInfo(hlsCusConContract);
                    reqInfo.forEach((k, v) -> {
                        jsonObject.put(k.toString(), v);
                    });
                }
                jsonObject.putAll(maps.get(0));
                mapList.add(jsonObject);
            }
        } else {
            mapList = changeReqs;
        }

        Map<String, String> meta = SysDocumentHistoryUtils.initMeta("con_contract_change_req", "change_req_id");
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords(meta, mapList);
        } catch (ResMessageException var10) {
            logger.warn("getContractChangeReq: {}", var10.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getContract(Long contractId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId", contractId);
        List<HlsCusConContract> contracts = contractMapper.queryConContractDetails(map);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords("con_contract", "contract_id", contracts);
        } catch (ResMessageException var6) {
            logger.warn("getContract {}", var6.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getContractBp(Long contractId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId", contractId);
        List<HlsCusConContractBp> contractBps = contractBpMapper.queryContractBp(map);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords("con_contract_bp", "record_id", "con_contract", contractId.toString(), contractBps);
        } catch (ResMessageException var6) {
            logger.warn("getContractBp {}", var6.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getContractLeaseItem(Long contractId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("contractId", contractId);
        List<HlsCusConContractLeaseItem> conContractLeaseItems = contractLeaseItemMapper.queryContractLeaseItemDetail(map);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords("con_contract_lease_item", "contract_lease_item_id", "con_contract", contractId.toString(), conContractLeaseItems);
        } catch (ResMessageException var6) {
            logger.warn("getContractLeaseItem {}", var6.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getContractLeaseItemInsurance(Long contractId) throws ParameterNullException {
        List<LeaseItemInsurance> leaseItemInsurances = conLeaseItemInsuranceMapper.queryContractLeaseItemInsurance(contractId);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            for(Map.Entry<Long, List<LeaseItemInsurance>> entry : leaseItemInsurances.stream().collect(Collectors.groupingBy(LeaseItemInsurance::getConLeaseItemId)).entrySet()){
                list.addAll(SysDocumentHistoryUtils.initRecords("con_lease_item_insurance", "insurance_id", "con_contract_lease_item", entry.getKey().toString(), entry.getValue()));
            }
        } catch (ResMessageException e) {
            logger.warn("getContractLeaseItemInsurance {}", e.getMessage());
        }
        return list;
    }

    /*private List<Map<String, Object>> getContractMortgage(Long contractId) throws ParameterNullException {
        Map<String, Long> map = new HashMap();
        map.put("contractId", contractId);
        List<HlsCusConContractMortgage> mortgages = mortgageMapper.queryContractMortgageDetail(map);
        List<Map<String, Object>> list = new ArrayList();

        try {
            list = SysDocumentHistoryUtils.initRecords("con_contract_mortgage", "con_mortgage_id", "con_contract", contractId.toString(), mortgages);
        } catch (ResMessageException var6) {
            logger.warn("getContractMortgage {}", var6.getMessage());
        }

        return list;
    }*/

    private List<Map<String, Object>> getPrjQuotation(Long contractId) throws ParameterNullException {
        HlsCusPrjQuotation quotaion = new HlsCusPrjQuotation();
        quotaion.setSourceDocumentId(contractId);
        //quotaion.setSourceDocumentCategory("CONTRACT");
        quotaion.setDataClass("CON_QUOTATION");
        quotaion.setEnabledFlag("Y");
        List<HlsCusPrjQuotation> quotations = quotationMapper.prjQuotationDetailQuery(quotaion);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = SysDocumentHistoryUtils.initRecords("prj_quotation", "quotation_id", "con_contract", contractId.toString(), quotations);
        } catch (ResMessageException var6) {
            logger.warn("getPrjQuotation {}", var6.getMessage());
        }

        return list;
    }

    private List<Map<String, Object>> getContractCashflow(List<HlsCusPrjQuotation> quotations, Long contractId) throws ParameterNullException {
        if (CollectionUtils.isEmpty(quotations)) {
            return new ArrayList<>();
        } else {
            List<Map<String, Object>> mapList = new ArrayList<>();

            for (HlsCusPrjQuotation item : quotations) {
                Map<String, Object> map = new HashMap<>();
                //map.put("generatedSource", ConContractCashflow.SOURCE_PRJ_QUOTATION);
                map.put("generatedSourceDocId", item.getQuotationId());
                map.put("contractId", contractId);
                List<HlsCusConContractCashflow> contractCashflows = contractCashflowMapper.queryConContractCashflowDetail(map);
                List<Map<String, Object>> list = new ArrayList<>();
                try {
                    list = SysDocumentHistoryUtils.initRecords("con_contract_cashflow", "cashflow_id", "con_contract", contractId.toString(), contractCashflows);
                } catch (ResMessageException e) {
                    logger.warn("getPrjQuotation {}", e.getMessage());
                }
                mapList.addAll(list);
            }
            return mapList;
        }
    }

    private List<Map<String, Object>> getContractAttachment(Long contractId) throws ParameterNullException {
        if (contractId == null) {
            throw new RuntimeException("合同id不存在！");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("contractId", contractId);
        map.put("contractAttachmentCategory", "NEW");
        List<HlsCusContractAttachment> conContractAttachments = contractAttachmentMapper.queryContractAttachmentDetail(map);
        List<FndAttachmentMulti> fndAttachmentMultis = new ArrayList<>();
        List<FndAttachment> fndAttachments = new ArrayList<>();

        for (HlsCusContractAttachment contractAttachment : conContractAttachments) {
            Long contractAttachmentId = contractAttachment.getContractAttachmentId();
            String tableName = "con_contract_attachment";
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTablePkValue(contractAttachmentId.toString());
            fndAttachmentMulti.setTableName(tableName);
            fndAttachmentMultis.addAll(fndAttachmentMultiMapper.select(fndAttachmentMulti));

            for (FndAttachmentMulti attachmentMulti : fndAttachmentMultis) {
                Long attachmentId = attachmentMulti.getAttachmentId();
                if (attachmentId != null) {
                    FndAttachment fndAttachment = new FndAttachment();
                    fndAttachment.setAttachmentId(attachmentId);
                    fndAttachments.addAll(fndAttachmentMapper.select(fndAttachment));
                }
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list.addAll(SysDocumentHistoryUtils.initRecords("con_contract_attachment", "contract_attachment_id", "con_contract", contractId.toString(), conContractAttachments));
            list.addAll(SysDocumentHistoryUtils.initRecords("fnd_atm_attachment_multi", "record_id", fndAttachmentMultis));
            list.addAll(SysDocumentHistoryUtils.initRecords("fnd_atm_attachment", "attachment_id", fndAttachments));
        } catch (ResMessageException var15) {
            logger.warn("getPrjQuotation {}", var15.getMessage());
        }

        return list;
    }

    private JSONArray setCalculateParameter(String position, String field, String force, Object value, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("position", position);
        jsonObject.put("field", field);
        jsonObject.put("force", force);
        jsonObject.put("value", value);
        String _status = "add";
        int removeIndex = 0;
        new JSONObject();

        for (int i = 0; i < jsonArray.size(); ++i) {
            if (jsonArray.getJSONObject(i).get("field").toString().equalsIgnoreCase(field)) {
                _status = "update";
                removeIndex = i;
            }
        }

        if ("add".equals(_status)) {
            jsonArray.add(jsonObject);
        } else {
            jsonArray.remove(removeIndex);
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    private String getSheetData(Map<String, Object> data) {
        return data == null ? null : (data.containsKey("sheets") ? String.valueOf(data.get("sheets")) : null);
    }

    @Override
    public ResponseData recalculate(IRequest iRequest, Long documentId, Long quotationId, JSONObject param) throws Exception {
        HlsCusConContractChangeReq changeReq = contractChangeReqMapper.selectByPrimaryKey(documentId);
        if (changeReq == null) {
            throw new ResMessageException("未能查到对应的变更申请单");
        }
        if ("APPROVED".equals(changeReq.getStatus()) || "APPROVING".equals(changeReq.getStatus())) {
            throw new ResMessageException("当前状态不能计算！");
        }

        List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_change_req");
        if (CollectionUtils.isEmpty(jsonObjects)) {
            throw new ResMessageException("变更单据不存在");
        }
        JSONObject object = jsonObjects.get(0);
        Long id = object.getLong("id");
        JSONObject data = object.getJSONObject("data");
        data.put("recalculate", "Y");
        sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, id, data.toJSONString());
        if ("PENALTY_EXEMPT".equals(changeReq.getChangeType())) {
            penaltyExempt(iRequest, documentId, quotationId);
            return new ResponseData();
        } else if ("PREPAYMENT".equals(changeReq.getChangeType())) {
            ResponseData responseData =  prePayment(iRequest, documentId, quotationId, changeReq);
            updateDocumentHistoryChangeReq(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, quotationId,changeReq.getChangeReqId());
            return responseData;
        } else if ("REPO".equals(changeReq.getChangeType())) {
            recoverContract(iRequest, documentId, quotationId, changeReq, changeReq.getContractId());
            ResponseData responseData = preRepo(iRequest, documentId, quotationId, changeReq);
            updateDocumentHistoryChangeReq(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, quotationId,changeReq.getChangeReqId());
            return responseData;
        }else {
            checkIfWriteOff(quotationId, param);
            recoverContract(iRequest, documentId, quotationId, changeReq, changeReq.getContractId());
            QuotationCommon quotation = QuotationCommonRegistion.get(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
            SysDocumentHistory history = sysDocumentHistoryMapper.selectDocumentHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, (Object) null);
            changeQuatationServiceImpl.updateQuotation(history.getHistoryId(), param.getJSONArray("config").toJSONString(), param.getString("price_list"), quotationId, false);
            changeQuatationServiceImpl.updateDocumentHistoryCashflow(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, quotationId);
            updateDocumentHistoryChangeReq(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, quotationId,changeReq.getChangeReqId());
            return new ResponseData();
        }

    }

    private List<JSONObject> getNewContractCashFlows(IRequest iRequest, Long documentId, String documentCategory, Long quotationId) throws NoSuchObjectException {
        List<JSONObject> contractCashflows = new ArrayList<>();
        sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "con_contract_cashflow")
                .forEach(item -> {
                    if (quotationId.equals(item.getJSONObject("data").getLong("generated_source_doc_id")) && !"delete".equals(item.getJSONObject("data").getString("_status"))) {
                        contractCashflows.add(item);
                    }
                });
        return contractCashflows;
    }

    private void updateDocumentHistoryChangeReq(IRequest iRequest, Long documentId, String documentCategory, Long quotationId,Long changeReqId) throws Exception {
        logger.info("updateDocumentHistoryChangeReq-更新变更后irr");
        long start = System.currentTimeMillis();
        List<JSONObject> contractCashflows = getNewContractCashFlows(iRequest, documentId, documentCategory, quotationId);
        conContractChangeCashflowMapper.deleteChangeCashflowByChangeReqId(changeReqId);
        for (JSONObject item : contractCashflows) {
            Long id = item.getLong("id");
            JSONObject data = item.getJSONObject("data");

            ConContractChangeCashflow conContractChangeCashflow = new ConContractChangeCashflow();
            conContractChangeCashflow.setChangeReqId(changeReqId);
            conContractChangeCashflow.setTimes(data.getLong("times"));
            conContractChangeCashflow.setCfItem(data.getLong("cf_item"));
            conContractChangeCashflow.setCfType(data.getLong("cf_type"));
            String dueDate = data.getString("due_date") == null ? data.getString("calc_date"):data.getString("due_date");
            conContractChangeCashflow.setDueDate(dueDate);
            conContractChangeCashflow.setDueAmount(data.getDouble("due_amount"));
            conContractChangeCashflow.setCreatedBy(1L);
            conContractChangeCashflow.setCreationDate(new Date());
            conContractChangeCashflow.setLastUpdatedBy(1L);
            conContractChangeCashflow.setLastUpdateDate(new Date());
            conContractChangeCashflowMapper.insert(conContractChangeCashflow);
        }
        Double irr = conContractChangeCashflowMapper.getChangeReqIrrByChangeReqId(changeReqId);

        HlsCusConContractChangeReq changeReq = contractChangeReqMapper.selectByPrimaryKey(changeReqId);
        changeReq.setChangeIrr(irr);
        contractChangeReqMapper.updateByPrimaryKey(changeReq);

        SysDocumentHistory history = sysDocumentHistoryMapper.selectDocumentHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, null);
        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(history.getHistoryId());
        detail.setTableName("con_contract_change_req");
        detail.setTablePkValue(String.valueOf(documentId));
        List<SysDocumentHistoryDetail> list = sysDocumentHistoryDetailMapper.select(detail);
        detail = list.get(0);

        String historyData = detail.getHistoryData();
        JSONObject jsonObject = JSON.parseObject(historyData);
        jsonObject.put("change_irr", irr);
        detail.setHistoryData(jsonObject.toJSONString());
        sysDocumentHistoryDetailMapper.updateByPrimaryKeySelective(detail);
    }

    private void recoverContract(IRequest iRequest, Long documentId, Long quotationId, HlsCusConContractChangeReq changeReq, Long contractId) throws ParameterNullException, NoSuchObjectException {
        List<Map<String, Object>> prjQuotation = getPrjQuotation(changeReq.getContractId());
        List<JSONObject> jsonObjectList = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "prj_quotation");
        jsonObjectList.forEach(item -> {
            sysDocumentHistoryDetailMapper.deleteByPrimaryKey(item.getLong("id"));
        });
        sysDocumentHistoryService.createHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, prjQuotation);
        List<JSONObject> contractCashflows = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_cashflow");
        contractCashflows.forEach(item -> {
            sysDocumentHistoryDetailMapper.deleteByPrimaryKey(item.getLong("id"));
        });
        HlsCusPrjQuotation hlsCusPrjQuotation = quotationMapper.selectByPrimaryKey(quotationId);
        List<HlsCusPrjQuotation> list = new ArrayList();
        list.add(hlsCusPrjQuotation);
        List<Map<String, Object>> cashflows = getContractCashflow(list, contractId);
        sysDocumentHistoryService.createHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, cashflows);
    }

    private void checkIfWriteOff(Long quotationId, JSONObject param) throws HlsCusException {
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setGeneratedSource("PRJ_QUOTATION");
        contractCashflow.setGeneratedSourceDocId(quotationId);
        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = contractCashflowMapper.select(contractCashflow);
        Long ccrStartTimes = 0L;
        JSONArray config = param.getJSONArray("config");

        for (int i = 0; i < config.size(); ++i) {
            JSONObject jsonObject = config.getJSONObject(i);
            if (jsonObject.getString("field").equals("ccr_start_times") && jsonObject.getLong("value") != null) {
                ccrStartTimes = jsonObject.getLong("value");
            }
        }

        for (HlsCusConContractCashflow cashflow : hlsCusConContractCashflowList) {
            if (cashflow.getTimes().longValue() >= ccrStartTimes) {
                if (!cashflow.getWriteOffFlag().equals("NOT")) {
                    throw new HlsCusException("起始变更日期之后已有核销！");
                }
            }
        }
    }

    Boolean checkTimesFullWirteOff(HlsCusConContract contract, JSONObject cashflow) {
        Boolean flag = false;

        //查询对应期数的租金现金流
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setContractId(cashflow.getLong("contract_id"));
        contractCashflow.setCfItem(1L);
        contractCashflow.setTimes(cashflow.getLong("times"));
        try {
            contractCashflow = contractCashflowMapper.selectOne(contractCashflow);
        } catch (Exception e) {
            return false;
        }

        //当前日期
        LocalDate now = LocalDate.now();

        if ("FULL".equalsIgnoreCase(contractCashflow.getWriteOffFlag())) {
            //获取现金流的最大核销日
            Date fullWriteOffDate = contractCashflow.getFullWriteOffDate();
            LocalDate localDate = fullWriteOffDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long days = now.toEpochDay() - localDate.toEpochDay();
            //最大核销日是前一天或者今天的需要重新计算罚息
            if (!(days == 1L || days == 0L)) {
                flag = true;
            }
        }
        return flag;
    }

    private ResponseData prePayment(IRequest iRequest, Long documentId, Long quotationId, HlsCusConContractChangeReq changeReq) throws Exception {
        return prePaymentCommon(iRequest, documentId, quotationId, changeReq, 11L, 11L);
    }

    private ResponseData preRepo(IRequest iRequest, Long documentId, Long quotationId, HlsCusConContractChangeReq changeReq) throws Exception {
        return prePaymentCommon(iRequest, documentId, quotationId, changeReq, 11L, 13L);
    }

    private ResponseData prePaymentCommon(IRequest iRequest, Long documentId, Long quotationId, HlsCusConContractChangeReq changeReq, Long cfType, Long cfItem) throws NoSuchObjectException, HlsCusException {
        HlsCusPrjQuotation prjQuotation = quotationMapper.selectByPrimaryKey(quotationId);
        List<JSONObject> reqs = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_change_req");
        if (CollectionUtils.isEmpty(reqs)) {
            throw new HlsCusException("can not find change req");
        }
        JSONObject req = reqs.get(0).getJSONObject("data");
        Double discount = req.getDouble("discount") == null ? 0D : req.getDouble("discount");
        Date dueDate = req.getDate("due_date");
        //返利
        Double replyAmount = 0D;
        //贴息
        Double discountAmount = 0D;
        //提前结清前一期
        Long ccrTimes;
        //提前结清期数
        Long terTimes;

        //期间利息
        Double periodInterestChange = 0D;
        if (req.get("period_interest_change") != null) {
            periodInterestChange = Double.valueOf(req.get("period_interest_change").toString());
        }
        //期间批复利息
        Double periodReplyInterest = 0D;
        if (req.get("period_reply_interest") != null) {
            periodReplyInterest = Double.valueOf(req.get("period_reply_interest").toString());
        }

        Double ccrRental = 0D;

        HlsCusConContractCashflow contractCashflow = getCashflowInfo(quotationId, dueDate);
        if (Objects.isNull(contractCashflow)) {
            throw new HlsCusException("提前结清(回购)日期应小于最后一期现金流的日期！");
        }
        HlsCusConContractCashflow last = new HlsCusConContractCashflow();
        last.setCfItem(1L);
        last.setTimes(contractCashflow.getTimes() + 1L);
        last.setGeneratedSource("PRJ_QUOTATION");
        last.setGeneratedSourceDocId(quotationId);
        last = contractCashflowMapper.selectOne(last);
        if (Objects.isNull(last)) {
            throw new HlsCusException("提前结清(回购)日期应小于最后一期现金流的日期！");
        }

        //获取直回租类型
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractCashflow.getContractId());
        contract = contractMapper.selectByPrimaryKey(contract);
        String bussinessType = contract.getBusinessType();

        long days = getDaySub(contractCashflow.getDueDate(), dueDate);
        //剩余本金
        Double outstandingPrincipal = contractCashflow.getOutstandingPrincipal();
        ccrTimes = contractCashflow.getTimes();
        terTimes = ccrTimes + 1;

        Double intRate = prjQuotation.getIntRate();
        Double vatRate = prjQuotation.getVatRate();
        Double vatRateRepay = prjQuotation.getVatRateRepay();
        Double mul = CalculateUtil.mul(CalculateUtil.mul(Double.valueOf(days), outstandingPrincipal), intRate);

        //加留购金
        Map map = new HashMap<>();
        map.put("times", ccrTimes);
        map.put("generatedSourceDocId", quotationId);

        ccrRental = Double.valueOf(req.get("period_interest_change").toString());

        //是否代收模式标志
        boolean financeFlag = false;

        //检查是否核销
        checkWriteOff(quotationId, ccrTimes);

        Double ccrAmount = CalculateUtil.add(ccrRental, outstandingPrincipal);

        //留购金json
        String jsonString = null;
        //罚息汇总金额
        Double ccrFee = 0D;
        List<JSONObject> cashflows = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_cashflow");
        for (JSONObject item : cashflows) {
            JSONObject cashflow = item.getJSONObject("data");
            Long id = item.getLong("id");
            if (!cashflow.containsKey("cashflow_id")) {
                sysDocumentHistoryDetailMapper.deleteByPrimaryKey(id);
            }

            cashflow.remove("_status");
            //更新提前结清对应期数返利及贴息现金流的金额
            if (quotationId.equals(cashflow.getLong("generated_source_doc_id")) && (terTimes == cashflow.getLong("times")) && (new Long(60).equals(cashflow.getLong("cf_item")) || new Long(61).equals(cashflow.getLong("cf_item")))) {

                Double dueAmount = 0D;
                Double amount = CalculateUtil.sub(periodInterestChange, periodReplyInterest);
                if ((new Long(60).equals(cashflow.getLong("cf_item")))) {
                    //厂商返利 （期间利息-期间批复利息）-[（期间利息-期间批复利息）*税率/（1+税率）-（期间利息-期间批复利息）*返利税率/（1+返利税率）]
                    dueAmount = CalculateUtil.add(CalculateUtil.sub(amount, CalculateUtil.sub(CalculateUtil.div(CalculateUtil.mul(amount, vatRate), CalculateUtil.add(vatRate, 1D)), CalculateUtil.div(CalculateUtil.mul(amount, vatRateRepay), CalculateUtil.add(vatRateRepay, 1D)))), 0D);
                } else {
                    //厂商贴息 ABS（期间利息-期间批复利息）
                    dueAmount = CalculateUtil.add(Math.abs(amount), 0D);
                }
                //不含税=金额/（1+返利税率），税额=金额-金额不含税
                Double netDueAmount = CalculateUtil.add(CalculateUtil.div(dueAmount, CalculateUtil.add(1D, vatRateRepay)), 0D);
                Double vatDueAmount = CalculateUtil.add(CalculateUtil.sub(dueAmount, netDueAmount), 0D);
                cashflow.put("due_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dueDate));
                cashflow.put("due_amount", dueAmount);
                cashflow.put("net_due_amount", netDueAmount);
                cashflow.put("vat_due_amount", vatDueAmount);
                cashflow.put("outstanding_principal", 0D);
                cashflow.put("_status", "update");
                sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, id, cashflow.toJSONString());
            }
            //删除提前结清后面期数的现金流以及罚息，更新留购金的日期
            else if (quotationId.equals(cashflow.getLong("generated_source_doc_id"))
                    && (ccrTimes < cashflow.getLong("times")) || (new Long(9).equals(cashflow.getLong("cf_item")))) {
                if (cashflow.containsKey("cashflow_id")) {
                    if (new Long(8).equals(cashflow.getLong("cf_item"))) {
                        cashflow.put("times", terTimes);
                        cashflow.put("due_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dueDate));
                        cashflow.put("outstanding_principal", 0D);
                        cashflow.put("change_flag", "Y");
                        cashflow.put("_status", "insert");
                        jsonString = cashflow.toJSONString();
                    }
                    //罚息对应的期数的现金流未完全核销才可以删除，完全核销的不会重现计算罚息
                    if ((new Long(9).equals(cashflow.getLong("cf_item"))) && checkTimesFullWirteOff(contract, cashflow)) {
                        //叠加不删除的罚息金额
                        Double penaltyInterest = MathUtil.sub(cashflow.getDouble("due_amount"), cashflow.getDouble("received_amount"), 2);
                        ccrFee = MathUtil.add(penaltyInterest, ccrFee);
                    } else {
                        cashflow.put("_status", "delete");
                        sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, id, cashflow.toJSONString());
                    }
                }
            }

            //更新返利或者贴息金额
            if (quotationId.equals(cashflow.getLong("generated_source_doc_id")) && !"delete".equals(cashflow.getString("_status"))) {
                Double dueAmount = cashflow.getDouble("due_amount") == null ? 0D : cashflow.getDouble("due_amount");
                Double receivedAmount = cashflow.getDouble("received_amount") == null ? 0D : cashflow.getDouble("received_amount");
                Double amount = CalculateUtil.sub(dueAmount, receivedAmount);
                //厂商返利
                if (new Long(60).equals(cashflow.getLong("cf_item"))) {
                    replyAmount = CalculateUtil.add(replyAmount, amount);
                }
                //厂商贴息
                if (new Long(61).equals(cashflow.getLong("cf_item"))) {
                    discountAmount = CalculateUtil.add(discountAmount, amount);
                }
            }
        }

        JSONObject changeObj = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_change_req").get(0);
        JSONObject data = changeObj.getJSONObject("data");
        Long objId = changeObj.getLong("id");
        data.remove(RecordHelper.STATUS_FIELD);
        data.put("times", terTimes);
        data.put("times_n", terTimes);
        data.put("reply_amount", replyAmount);
        data.put("discount_amount", discountAmount);


        //插入罚息现金流
        insertPenaltyInterestCashflow(documentId, prjQuotation, changeReq, data, cashflows, cfItem, ccrFee,financeFlag);

        //插入提前结清现金流
        insertRepaymentCashflow(documentId, prjQuotation, changeReq, dueDate, ccrTimes, ccrAmount, ccrRental, outstandingPrincipal, prjQuotation.getVatRate(), jsonString, bussinessType, cfType, cfItem);

        //变更手续费
        double etFee = req.getDouble("et_fee") == null ? 0D : req.getDouble("et_fee");
        if (etFee > 0) {
            //插入承租人服务费
            // 承租人服务费改为 变更服务费 70
            insertServiceChargeCashflow(documentId, prjQuotation, changeReq, dueDate, ccrTimes, etFee, ccrRental, outstandingPrincipal, prjQuotation.getVatRate(), jsonString, bussinessType, 65L, 70L);
        }

        List<Map> list = getMaps(ccrTimes, changeObj);
        sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, objId, data.toJSONString());
        return new ResponseData(list);
    }

    private void checkWriteOff(Long quotationId, Long ccrTimes) {
        HlsCusConContractCashflow f = new HlsCusConContractCashflow();
        f.setCfItem(1L);
        f.setTimes(ccrTimes + 1L);
        f.setGeneratedSource("PRJ_QUOTATION");
        f.setGeneratedSourceDocId(quotationId);
        f = contractCashflowMapper.selectOne(f);
        if (!"NOT".equals(f.getWriteOffFlag())) {
            throw new RuntimeException("不能提前结清已经核销的现金流");
        }
    }

    private Long getaCashflowId(Long quotationId, Date dueDate) {
        Map cashflow = new HashMap();
        cashflow.put("dueDate", dueDate);
        cashflow.put("generatedSource", "PRJ_QUOTATION");
        cashflow.put("generatedSourceDocId", quotationId);
        return contractCashflowMapper.selectMaxContractCashflowIdBeforeDate(cashflow);
    }

    private HlsCusConContractCashflow getCashflowInfo(Long quotationId, Date dueDate) {
        Map cashflow = new HashMap();
        cashflow.put("dueDate", dueDate);
        cashflow.put("generatedSource", ConContractCashflow.SOURCE_PRJ_QUOTATION);
        cashflow.put("generatedSourceDocId", quotationId);

        return contractCashflowMapper.selectMaxContractCashflowInfoBeforeDate(cashflow);
    }

    //插入罚息现金流
    private void insertPenaltyInterestCashflow(Long documentId, HlsCusPrjQuotation prjQuotation, HlsCusConContractChangeReq changeReq, JSONObject data, List<JSONObject> cashflows, Long cfItem, Double ccrFee ,boolean financeFlag) throws HlsCusException {
        List<Map> cfItemMap = sysCodeValueMapper.queryCodeDetails("CON.CASHFLOW_WRITE_OFF_FLAG");
        Date penaltyDate = null;
        try {
            penaltyDate = simpleDateFormat.parse(data.get("due_date").toString());
        } catch (ParseException e) {
            throw new HlsCusException("提前结清日获取失败!");
        }

        //重算罚息
        List<HlsCusConContractCashflow> list = penaltyCalServiceImpl.getPenaltyCashflows(changeReq.getContractId(), penaltyDate);

        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        history = sysDocumentHistoryMapper.select(history).get(0);
        for (int i = 0; i < list.size(); i++) {
            HlsCashflowItem cashflowItem = new HlsCashflowItem();
            cashflowItem.setCfItem(list.get(i).getCfItem().toString());
            cashflowItem.setCfType(list.get(i).getCfType().toString());
            List<HlsCashflowItem> items = hlsCfItemMapper.select(cashflowItem);
            if (CollectionUtils.isNotEmpty(items) && items.size() == 1) {
                list.get(i).setCfItemN(items.get(0).getDescription());
            }

            //计算罚息
            Double dueAmount = list.get(i).getDueAmount();
            Double receivedAmount = list.get(i).getReceivedAmount();
            if (dueAmount == null) {
                dueAmount = 0D;
            }
            if (receivedAmount == null) {
                receivedAmount = 0D;
            }
            String writeOffFlag = list.get(i).getWriteOffFlag();
            if (writeOffFlag != null) {
                int finalI = i;
                List<HlsCusConContractCashflow> finalList = list;
                cfItemMap.forEach(item -> {
                    if (item.get("code_value").equals(writeOffFlag)) {
                        finalList.get(finalI).setWriteOffFlagN(item.get("meaning").toString());
                    }
                });
            }

            //罚息重算后应收小于已收需要抛出异常
            if(MathUtil.compare(dueAmount,receivedAmount) < 0){
                throw new HlsCusException(new StringBuffer("第").append(list.get(i).getTimes()).append("期罚息应收金额小于已收金额，请检查！").toString());
            }
            ccrFee = MathUtil.add(ccrFee,MathUtil.sub(dueAmount,receivedAmount));

            JSONObject jsonObject = JSONObject.parseObject(JsonUtils.toSnakeJsonString(list.get(i)));
            jsonObject.put("_status", "insert");

            SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
            detail.setHistoryId(history.getHistoryId());
            detail.setTableName("con_contract_cashflow");
            detail.setTablePkValue(UUID.randomUUID().toString());
            detail.setParentTableName("prj_quotation");
            detail.setParentPkValue(String.valueOf(prjQuotation.getQuotationId()));
            detail.setHistoryData(jsonObject.toJSONString());

            sysDocumentHistoryDetailMapper.insertSelective(detail);
        }

        //更新罚息金额
        data.put("penalty_interest", MathUtil.round(ccrFee, 2));
    }

    //插入提前结清现金流
    private void insertRepaymentCashflow(Long documentId, HlsCusPrjQuotation prjQuotation, HlsCusConContractChangeReq changeReq, Date dueDate, Long ccrTimes, Double ccrAmount, Double ccrRental, Double lastOutstandingPrincipal, Double vatRate, String jsonString, String bussinessType, Long cfType, Long cfItem) {
        HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
        newCashflow.setContractId(changeReq.getContractId());
        newCashflow.setCfItem(cfItem);
        newCashflow.setCfType(cfType);
        HlsCashflowItem item = new HlsCashflowItem();
        item.setCfItem(String.valueOf(cfItem));
        item.setCfType(String.valueOf(cfType));
        item = hlsCfItemMapper.selectOne(item);
        if (item == null) {
            throw new RuntimeException("现金流类型不存在");
        }
        newCashflow.setCfItemN(item.getDescription());
        newCashflow.setCfDirection("INFLOW");
        newCashflow.setCfStatus("RELEASE");
        newCashflow.setTimes(ccrTimes + 1L);
        newCashflow.setDueDate(dueDate);
        newCashflow.setCalcDate(dueDate);
        newCashflow.setFinIncomeDate(dueDate);
        newCashflow.setDueAmount(ccrAmount);
        newCashflow.setReceivedAmount(0D);
        newCashflow.setGeneratedSource("PRJ_QUOTATION");
        newCashflow.setGeneratedSourceDocId(prjQuotation.getQuotationId());
        newCashflow.setOverdueStatus("N");
        newCashflow.setWriteOffFlag("NOT");
        newCashflow.setWriteOffFlagN("未核销");
        newCashflow.setPenaltyProcessStatus("N");
        newCashflow.setBillingStatus("NOT");
        newCashflow.setInterest(ccrRental);
        newCashflow.setOutstandingPrincipal(0D);
        newCashflow.setPrincipal(lastOutstandingPrincipal);
        newCashflow.setQuotationNumber(prjQuotation.getQuotationNumber());
        newCashflow.setChangeFlag("Y");
        //计算含税不含税
        calcCashflowNetAndVat(newCashflow, vatRate, bussinessType);

        JSONObject jsonObject = JSONObject.parseObject(JsonUtils.toSnakeJsonString(newCashflow));
        jsonObject.put("_status", "insert");

        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        history = sysDocumentHistoryMapper.select(history).get(0);

        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(history.getHistoryId());
        detail.setTableName("con_contract_cashflow");
        detail.setTablePkValue(UUID.randomUUID().toString());
        detail.setParentTableName("prj_quotation");
        detail.setParentPkValue(String.valueOf(prjQuotation.getQuotationId()));
        detail.setHistoryData(jsonObject.toJSONString());

        sysDocumentHistoryDetailMapper.insertSelective(detail);
        //插入留购金现金流
        if (jsonString != null) {
            SysDocumentHistoryDetail historyDetail = new SysDocumentHistoryDetail();
            historyDetail.setHistoryId(history.getHistoryId());
            historyDetail.setTableName("con_contract_cashflow");
            historyDetail.setTablePkValue(UUID.randomUUID().toString());
            historyDetail.setParentTableName("prj_quotation");
            historyDetail.setParentPkValue(String.valueOf(prjQuotation.getQuotationId()));
            historyDetail.setHistoryData(jsonString);
            sysDocumentHistoryDetailMapper.insertSelective(historyDetail);
        }
    }

    //插入服务费
    private void insertServiceChargeCashflow(Long documentId, HlsCusPrjQuotation prjQuotation, HlsCusConContractChangeReq changeReq, Date dueDate, Long ccrTimes, Double etFee, Double ccrRental, Double lastOutstandingPrincipal, Double vatRate, String jsonString, String bussinessType, Long cfType, Long cfItem) {
        HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
        newCashflow.setContractId(changeReq.getContractId());
        newCashflow.setCfItem(cfItem);
        newCashflow.setCfType(cfType);
        HlsCashflowItem item = new HlsCashflowItem();
        item.setCfItem(String.valueOf(cfItem));
        item.setCfType(String.valueOf(cfType));
        item = hlsCfItemMapper.selectOne(item);
        if (item == null) {
            throw new RuntimeException("现金流类型不存在");
        }
        newCashflow.setCfItemN(item.getDescription());
        newCashflow.setCfDirection("INFLOW");
        newCashflow.setCfStatus("RELEASE");
        newCashflow.setTimes(ccrTimes + 1L);
        newCashflow.setDueDate(dueDate);
        newCashflow.setCalcDate(dueDate);
        newCashflow.setFinIncomeDate(dueDate);
        newCashflow.setDueAmount(etFee);
        newCashflow.setReceivedAmount(0D);
        newCashflow.setGeneratedSource("PRJ_QUOTATION");
        newCashflow.setGeneratedSourceDocId(prjQuotation.getQuotationId());
        newCashflow.setOverdueStatus("N");
        newCashflow.setWriteOffFlag("NOT");
        newCashflow.setWriteOffFlagN("未核销");
        newCashflow.setPenaltyProcessStatus("N");
        newCashflow.setBillingStatus("NOT");
        newCashflow.setInterest(0D);
        newCashflow.setOutstandingPrincipal(0D);
        newCashflow.setPrincipal(0D);
        newCashflow.setQuotationNumber(prjQuotation.getQuotationNumber());
        newCashflow.setChangeFlag("Y");
        //计算含税不含税
        calcCashflowNetAndVat(newCashflow, vatRate, bussinessType);

        JSONObject jsonObject = JSONObject.parseObject(JsonUtils.toSnakeJsonString(newCashflow));
        jsonObject.put("_status", "insert");

        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        history = sysDocumentHistoryMapper.select(history).get(0);

        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(history.getHistoryId());
        detail.setTableName("con_contract_cashflow");
        detail.setTablePkValue(UUID.randomUUID().toString());
        detail.setParentTableName("prj_quotation");
        detail.setParentPkValue(String.valueOf(prjQuotation.getQuotationId()));
        detail.setHistoryData(jsonObject.toJSONString());

        sysDocumentHistoryDetailMapper.insertSelective(detail);
    }

    void calcCashflowNetAndVat(HlsCusConContractCashflow cashflow, Double vatRate, String bussinessType) {
        Double netDueAmount = 0D;
        Double vatDueAmount = 0D;
        Double netPrincipal = 0D;
        Double vatPrincipal = 0D;
        Double netInterest = 0D;
        Double vatInterest = 0D;
        if ("LEASE".equals(bussinessType)) {
            //租金不含税
            netDueAmount = CalculateUtil.div(cashflow.getDueAmount(), CalculateUtil.add(1D, vatRate));
            vatDueAmount = CalculateUtil.sub(cashflow.getDueAmount(), netDueAmount);

            //本金不含税
            netPrincipal = CalculateUtil.div(cashflow.getPrincipal(), CalculateUtil.add(1D, vatRate));
            vatPrincipal = CalculateUtil.sub(cashflow.getPrincipal(), netPrincipal);

            //利息不含税
            netInterest = CalculateUtil.sub(netDueAmount, netPrincipal);
            vatInterest = CalculateUtil.sub(cashflow.getInterest(), netInterest);


        } else if ("LEASEBACK".equals(bussinessType)) {
            // 回租项目，租金不含税=本金+利息不含税，租金税=利息税； 本金不含税=本金，本金税=0；利息不含税=利息/（1+税率）；利息税=利息不含税*税率
            //利息不含税
            netInterest = CalculateUtil.div(cashflow.getInterest(), CalculateUtil.add(1D, vatRate));
            vatInterest = CalculateUtil.sub(cashflow.getInterest(), netInterest);
            //本金不含税
            netPrincipal = cashflow.getPrincipal();
            vatPrincipal = 0D;
            //租金不含税
            netDueAmount = CalculateUtil.add(cashflow.getPrincipal(), netInterest);
            vatDueAmount = CalculateUtil.sub(cashflow.getDueAmount(), netDueAmount);
        }
        cashflow.setNetDueAmount(netDueAmount);
        cashflow.setVatDueAmount(vatDueAmount);
        cashflow.setNetPrincipal(netPrincipal);
        cashflow.setVatPrincipal(vatPrincipal);
        cashflow.setNetInterest(netInterest);
        cashflow.setVatInterest(vatInterest);
    }

    private long getDaySub(Date beginDate, Date endDate) {
        long day = 0L;
        new SimpleDateFormat("yyyy-MM-dd");

        try {
            beginDate = parseDate(beginDate);
            endDate = parseDate(endDate);
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        return day;
    }

    private Date parseDate(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(date);
        return sdf.parse(s);
    }

    private Double getAmountIfNull(Double amount) {
        return amount == null ? 0.0D : amount;
    }

    private List<Map> getMaps(Long ccrTimes, JSONObject changeObj) {
        Map queryMap = new HashMap();
        queryMap.put("generatedSourceDocId", changeObj.getJSONObject("data").getLong("quotation_id"));
        queryMap.put("cfItem", "1");
        Double ccrOverdueRental = getAmountIfNull(contractCashflowMapper.queryOverDueAmount(queryMap) == null ? null : contractCashflowMapper.queryOverDueAmount(queryMap));
        queryMap.put("cfItem", "9");
        Double ccrPenalty = getAmountIfNull(contractCashflowMapper.queryOverDueAmount(queryMap) == null ? null : contractCashflowMapper.queryOverDueAmount(queryMap));
        queryMap.put("cfItem", "100");
        Double ccrOverduePrin = getAmountIfNull(contractCashflowMapper.queryOverDueAmount(queryMap) == null ? null : contractCashflowMapper.queryOverDueAmount(queryMap));
        queryMap.put("times", ccrTimes);
        Double ccrOutstandingPrinTaxIncld = getAmountIfNull(contractCashflowMapper.queryRestAmount(queryMap) == null ? null : contractCashflowMapper.queryRestAmount(queryMap));
        queryMap.put("cfItem", "8");
        Double retention = getAmountIfNull(contractCashflowMapper.queryRestAmount(queryMap) == null ? null : contractCashflowMapper.queryRestAmount(queryMap));
        queryMap.remove("cfItem");
        queryMap.remove("times");
        queryMap.put("ccr_overdue_rental", ccrOverdueRental);
        queryMap.put("ccr_penalty", ccrPenalty);
        queryMap.put("ccr_overdue_prin", ccrOverduePrin);
        queryMap.put("ccr_outstanding_prin_tax_incld", ccrOutstandingPrinTaxIncld);
        queryMap.put("retention", retention);
        changeObj.getJSONObject("data").putAll(JSON.parseObject(JsonUtils.toSnakeJsonString4Map(queryMap), Map.class));
        List<Map> list = new ArrayList<>();
        list.add(queryMap);
        return list;
    }

    private void penaltyExempt(IRequest iRequest, Long documentId, Long quotationId) throws NoSuchObjectException, ResMessageException {
        Map<String, Object> map = new HashMap();
        map.put("cfItem", 1);
        map.put("generatedSourceDocId", quotationId);
        Double overDueAmount = contractCashflowMapper.queryOverDueAmount(map);
        if (Double.compare(overDueAmount, 0.0D) > 0) {
            throw new ResMessageException("不存在逾期的租金时才可以进行罚息减免");
        } else {
            List<JSONObject> objects = getExemptContractCashflow(iRequest, documentId, quotationId);
            List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract");
            if (CollectionUtils.isEmpty(jsonObjects)) {
                throw new ResMessageException("未找到合同信息");
            }
            JSONObject contract = (jsonObjects.get(0)).getJSONObject("data");
            Double amount = null;
            //执行类型
            switch (contract.getString("ccr_penalty_exempt_type")) {
                case "10":
                    amount = contract.getDouble("ccr_penalty_exempt_amount");
                    break;
                case "20":
                    if ("9".equals(contract.getString("ccr_penalty_exempt_item"))) {
                        amount = CalculateUtil.mul(contract.getDouble("ccr_penalty_exempt_ratio"), contract.getDouble("ccr_total_penalty_amount"));
                    } else if ("99".equals(contract.getString("ccr_penalty_exempt_item"))) {
                        amount = CalculateUtil.mul(contract.getDouble("ccr_penalty_exempt_ratio"), contract.getDouble("ccr_total_penalty_amt_manu"));
                    }
                    break;
                default:
                    break;
            }

            //执行顺序
            switch (contract.getString("ccr_penalty_exempt_order")) {
                case "10":
                    updateContractCashflow(iRequest, objects, true, amount, documentId);
                    break;
                case "20":
                    updateContractCashflow(iRequest, objects, false, amount, documentId);
                    break;
                default:
                    break;
            }
        }
    }

    private List<JSONObject> getExemptContractCashflow(IRequest iRequest, Long documentId, Long quotationId) throws NoSuchObjectException {
        List<JSONObject> objects = new ArrayList();
        sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_cashflow").forEach((item) -> {
            if (quotationId.equals(item.getJSONObject("data").getLong("generated_source_doc_id")) && "9".equals(item.getJSONObject("data").getString("cf_item"))) {
                objects.add(item);
            }

        });
        return objects;
    }

    void updateContractCashflow(IRequest iRequest, List<JSONObject> contractCashflows, Boolean isAsc, Double amount, Long documentId) throws NoSuchObjectException {
        if (!isAsc) {
            Collections.reverse(contractCashflows);
        }
        //税率
        List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract");
        Double vatRate = Double.parseDouble(jsonObjects.get(0).getJSONObject("data").get("vat_rate").toString());

        for(JSONObject item:contractCashflows){
            if (amount.compareTo(0.0D) != 0) {
                JSONObject data = item.getJSONObject("data");
                Double dueAmount = data.getDouble("due_amount");
                Double receivedAmount = data.getDouble("received_amount");
                if (receivedAmount == null) {
                    receivedAmount = 0.0D;
                }

                Double noReceivedAmount = CalculateUtil.sub(dueAmount, receivedAmount);
                if (amount.compareTo(noReceivedAmount) < 0) {
                    data.put("due_amount", CalculateUtil.sub(dueAmount, amount));
                    data.put("reduce_amount", amount);
                    amount = 0.0D;
                } else {
                    data.put("due_amount", CalculateUtil.sub(dueAmount, noReceivedAmount));
                    data.put("reduce_amount", noReceivedAmount);
                    //减免完后完全核销
                    data.put("write_off_flag", "FULL");
                    amount = CalculateUtil.sub(amount, noReceivedAmount);
                }
                //罚息减免修改due_amount时，重新拆税
                String cfItem = data.getString("cf_item");
                Double dueAmountNew = data.getDouble("due_amount");
                if("9".equals(cfItem) || "99".equals(cfItem)){
                    Double netDueAmountNew = CalculateUtil.div(dueAmountNew, MathUtil.add(vatRate,1));
                    Double vatDueAmountNew = CalculateUtil.sub(dueAmountNew, netDueAmountNew);
                    data.put("net_due_amount", netDueAmountNew);
                    data.put("vat_due_amount", vatDueAmountNew);
                }
                data.put("_status", "update");
                sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, item.getLong("id"), data.toJSONString());
            }
        }

    }

    @Override
    public void cancelChangeReq(IRequest iRequest, Long changeReqId) throws NoSuchObjectException {
        HlsCusConContractChangeReq changeReq = new HlsCusConContractChangeReq();
        changeReq.setChangeReqId(changeReqId);
        changeReq = contractChangeReqMapper.selectByPrimaryKey(changeReq);
        if ("APPROVING".equals(changeReq.getStatus())) {
            throw new NoSuchObjectException("审批中不可以取消");
        } else if ("UNDO".equals(changeReq.getStatus())) {
            throw new NoSuchObjectException("已取消申请的变更不可以再取消!");
        } else {
            changeReq.setStatus("UNDO");
            self().updateByPrimaryKeySelective(iRequest, changeReq);
            changeReq = self().selectByPrimaryKey(iRequest, changeReq);
            List<JSONObject> list = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, changeReqId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract");
            String contractStatus = (JSONObject.parseObject(JsonUtils.toCamelJsonString(list.get(0).getJSONObject("data")), HlsCusConContract.class)).getContractStatus();
            Long contractId = changeReq.getContractId();
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(contractId);
            contract.setContractStatus(contractStatus);
            contractService.updateByPrimaryKeySelective(iRequest, contract);
        }
    }

    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, QuotationCommon> map = applicationContext.getBeansOfType(QuotationCommon.class);
        map.forEach((k, v) -> {
            QuotationCommonRegistion.put(v.getSourceDocumentCategory(), v);
        });
    }

    @Override
    public void leaveHistory(IRequest iRequest, HlsCusConContractChangeReq changeReq, Long processInstanceId) throws Exception {
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        //quotation.setSourceDocumentCategory("CONTRACT");
        quotation.setDataClass("CON_QUOTATION");
        quotation.setSourceDocumentId(changeReq.getContractId());
        quotation.setEnabledFlag("Y");
        List<HlsCusPrjQuotation> quotations = quotationMapper.select(quotation);
        List<Map<String, Object>> list = self().getDatas(changeReq.getChangeReqId(), changeReq.getContractId(), quotations);
        JSONObject object = null;
        JSONObject newObj = null;

        //是否修改报价
        boolean isQuotationModified = false;
        String compressSheets= null;
        try {
            for (JSONObject jsonObject : sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, changeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "prj_quotation")) {
                String _status = jsonObject.getJSONObject("data").getString("_status");
                if ("update".equals(_status) || "insert".equals(_status)) {
                    isQuotationModified = true;

                    //获取变更后报价sheets
                    SysDocumentHistoryBlob sysDocumentHistoryBlob = new SysDocumentHistoryBlob();
                    sysDocumentHistoryBlob.setHistoryDetailId(jsonObject.getLong("id"));
                    sysDocumentHistoryBlob.setFieldName("sheets");
                    List<SysDocumentHistoryBlob> blobList = documentHistoryBlobMapper.select(sysDocumentHistoryBlob);
                    for(SysDocumentHistoryBlob item:blobList){
                        String unpressSheets= item.getFieldValue();
                        String sheetsArray = ConContractChangeQuatationServiceImpl.encodeURIComponent(unpressSheets);
                        String zipSheets = new String(GzipUtil.compress(sheetsArray),"iso-8859-1");
                        compressSheets = GzipUtil.btoa(zipSheets);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("select quotation history error:", e);
        }

        if ("TENANT".equals(changeReq.getChangeType())) {
            List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, changeReq.getChangeReqId(), DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract_bp");
            if (jsonObjects.size() <= 0) {
                throw new Exception("未取到承租人变更信息!");
            }

            for(JSONObject jsonObject:jsonObjects){
                if ("TENANT".equals(jsonObject.getJSONObject("data").getString("bp_category"))) {
                    newObj = jsonObject.getJSONObject("data");
                }
            }
        }

        if ("PENALTY_EXEMPT".equals(changeReq.getChangeType())) {
            saveConDebtExemptionReq(iRequest, changeReq, processInstanceId);
        }

        Long lastVersion = sysDocumentHistoryService.leaveHistoryWithData(DOCUMENT_CATEGORY_CONTRACT_CHANGE, changeReq.getChangeReqId(), list);
        sysDocumentHistoryService.leaveHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, changeReq.getChangeReqId(), lastVersion);
        String changeType = changeReq.getChangeType();
        switch (changeType) {
            case "PRE":
                changeContractAttachment(iRequest, changeReq);
                break;
            case "TENANT":
                HlsCusConContract contract = new HlsCusConContract();
                contract.setContractId(changeReq.getContractId());
                contract.setTenantId(newObj.getLong("bp_id"));
                contractService.updateByPrimaryKeySelective(iRequest, contract);
                break;
            case "LEASE_ITEM":

                break;
            case "PREPAYMENT":
            case "PENALTY_EXEMPT":
                break;
            case "DELAY":
            case "EXTENSION":
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(changeReq.getContractId());
                hlsCusConContract = contractService.selectByPrimaryKey(iRequest,hlsCusConContract);

                HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                //hlsCusPrjQuotation.setSourceDocumentCategory("CONTRACT");
                hlsCusPrjQuotation.setDataClass("CON_QUOTATION");
                hlsCusPrjQuotation.setSourceDocumentId(hlsCusConContract.getContractId());
                hlsCusPrjQuotation = quotationMapper.select(hlsCusPrjQuotation).get(0);

                hlsCusConContract.setIrr(changeReq.getChangeIrr());
                hlsCusConContract.setLeaseTimes(changeReq.getChangeTimes());
                hlsCusConContract.setLeaseTerm(changeReq.getChangeTerm().doubleValue());
                hlsCusConContract.setPriceList(hlsCusPrjQuotation.getPriceList());
                contractService.updateByPrimaryKey(iRequest,hlsCusConContract);

                hlsCusPrjQuotation.setLeaseTimes(changeReq.getChangeTimes());
                hlsCusPrjQuotation.setLeaseTerm(changeReq.getChangeTerm().doubleValue());
                quotationMapper.updateByPrimaryKey(hlsCusPrjQuotation);

                HlsCusPrjQuotationDetails quotationDetail =new HlsCusPrjQuotationDetails();
                quotationDetail.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                quotationDetail = quotationDetailMapper.select(quotationDetail).get(0);
                quotationDetail.setSheets(compressSheets);
                quotationDetailMapper.updateByPrimaryKeySelective(quotationDetail);
                break;
            default:
                break;
        }
    }

    @Override
    public String queryDocumentSheets(IRequest iRequest, Long documentId, String documentCategory, Long quotationId) throws NoSuchObjectException, ResMessageException {
        List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "prj_quotation");
        JSONObject quotation = null;
        for(JSONObject jsonObject:jsonObjects){
            if (quotationId.equals(jsonObject.getJSONObject("data").getLong("quotation_id"))) {
                quotation = jsonObject.getJSONObject("data");
            }
        }

        if (quotation == null) {
            throw new ResMessageException("未找到改ID的报价");
        } else {
            return quotation.getString("sheets");
        }
    }

    @Override
    public String querySheetByQuotation(IRequest requestCtx, Long quotationId, String documentCategory, Long documentId) {
        List<JSONObject> prjQuotations = null;

        try {
            prjQuotations = sysDocumentHistoryService.selectDocumentHistoryByTableName(requestCtx, documentId, documentCategory, "prj_quotation");
        } catch (NoSuchObjectException var10) {
            logger.warn("Query history quotation found.", var10);
            return "[{\"name\":\"Sheet1\",\"rows\":[]}]";
        }

        if (prjQuotations != null && prjQuotations.size() > 0) {
            HlsCusPrjQuotation hlsCusPrjQuotation = null;

            for(JSONObject prjQuotation:prjQuotations){
                JSONObject data = prjQuotation.getJSONObject("data");
                if (Objects.equals(quotationId, data.getLong("quotation_id"))) {
                    hlsCusPrjQuotation = data.toJavaObject(HlsCusPrjQuotation.class);
                    break;
                }
            }

            if (hlsCusPrjQuotation == null) {
                logger.warn("No quotation found.");
                return "[{\"name\":\"Sheet1\",\"rows\":[]}]";
            } else {
                String sheets = hlsCusPrjQuotation.getSheets();
                if (!"HLS_EQUAL_PRINCIPAL".equals(hlsCusPrjQuotation.getPriceList()) && !"HLS_NOT_EQUAL_RENTAL".equals(hlsCusPrjQuotation.getPriceList()) && !"HLS_EQUAL_RENTAL".equals(hlsCusPrjQuotation.getPriceList())) {
                    return sheets;
                } else {
                    JSONArray sheetsJson = JSON.parseArray(sheets);
                    String businessTypeDesc = "";
                    if ("LEASE".equals(hlsCusPrjQuotation.getBusinessType())) {
                        businessTypeDesc = "直租";
                    } else {
                        businessTypeDesc = "回租";
                    }

                    ((JSONObject) ((JSONArray) ((JSONObject) ((JSONArray) ((JSONObject) sheetsJson.get(0)).get("rows")).get(2)).get("cells")).get(7)).put("value", businessTypeDesc);
                    return JSON.toJSONString(sheetsJson);
                }
            }
        } else {
            return "[{\"name\":\"Sheet1\",\"rows\":[]}]";
        }
    }

    @Override
    public ResponseData queryContractBp(IRequest requestCtx, LeafRequestData requestData) {
        Map parameter = requestData.getParameter();
        Object bpCategory = parameter.get("bp_category");
        HlsCusConContractChangeReq changeReq = contractChangeReqMapper.selectByPrimaryKey(parameter.get("change_req_id"));
        boolean forQuery = "APPROVED".equals(changeReq.getStatus());
        if (forQuery) {
            SysDocumentHistory history = new SysDocumentHistory();
            Long changeReqId = Long.valueOf(String.valueOf(parameter.get("change_req_id")));
            history.setDocumentId(changeReqId);
            String documentCategory = String.valueOf(parameter.get("document_category"));
            history.setDocumentCategory(documentCategory);
            List<SysDocumentHistory> histories = sysDocumentHistoryMapper.selectHistoryCount(history);
            if (CollectionUtils.isEmpty(histories)) {
                return null;
            } else {
                SysDocumentHistory max = histories.get(0);
                SysDocumentHistory second = max;
                if (histories.size() > 1) {
                    histories.sort((a, b) -> {
                        return (int) (a.getVersion() - b.getVersion());
                    });
                    second = histories.get(histories.size() - 2);
                }

                List<JSONObject> bps = null;

                try {
                    bps = sysDocumentHistoryService.selectDocumentHistoryByTableName(requestCtx, changeReqId, documentCategory, "con_contract_bp", second.getVersion());
                    ArrayList<JSONObject> result = new ArrayList();

                    for (int i = 0; i < bps.size(); ++i) {
                        JSONObject jsonObject = bps.get(i);
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (Objects.equals(bpCategory, data.get("bp_category"))) {
                            result.add(JSON.parseObject(JsonUtils.toCamelJsonString(data)));
                            break;
                        }
                    }

                    return new ResponseData(result);
                } catch (NoSuchObjectException var18) {
                    return new ResponseData(false);
                }
            }
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("contractId", parameter.get("contract_id"));
            params.put("bpCategory", bpCategory);
            List<HlsBpMaster> hlsBpMasters = hlsBpMasterMapper.queryContractBpDetail(params);
            return new ResponseData(hlsBpMasters);
        }
    }

    private void saveConDebtExemptionReq(IRequest iRequest, HlsCusConContractChangeReq changeReq, Long processInstanceId) throws NoSuchObjectException {
        Long changeReqId = changeReq.getChangeReqId();
        Long quotationId = changeReq.getQuotationId();
        HlsCusConContract contract = contractMapper.selectByPrimaryKey(changeReq.getContractId());
        HlsCusConDebtExemptionReq debtExemptionReq = new HlsCusConDebtExemptionReq();
        BeanRefUtils.beanToBean(changeReq, debtExemptionReq, hlsBeanRefUtilService);
        BeanRefUtils.beanToBean(contract, debtExemptionReq, hlsBeanRefUtilService);
        debtExemptionReq.setDescription(changeReq.getDescription());
        debtExemptionReq.setReqStatus("APPROVED");
        debtExemptionReq.setReqDate(changeReq.getChangeReqDate());
        debtExemptionReq.setProcessInstanceId(processInstanceId);
        debtExemptionReq.setChangeReqId((Long) null);
        debtExemptionReq = conDebtExemptionReqService.insertSelective(iRequest, debtExemptionReq);
        List<JSONObject> newCashflows = new ArrayList();
        getExemptContractCashflow(iRequest, changeReqId, quotationId).forEach((item) -> {
            newCashflows.add(item.getJSONObject("data"));
        });
        HlsCusConDebtExemptionReqCf debtExemptionReqCf = new HlsCusConDebtExemptionReqCf();
        debtExemptionReqCf.setChangeReqId(debtExemptionReq.getChangeReqId());
        insertConDebtExemptionReqCf(iRequest, debtExemptionReqCf, newCashflows, quotationId);
    }

    private void insertConDebtExemptionReqCf(IRequest iRequest, HlsCusConDebtExemptionReqCf debtExemptionReqCf, List<JSONObject> newCashflows, Long quotationId) {
        HlsCusConContractCashflow queryDto = new HlsCusConContractCashflow();
        queryDto.setGeneratedSource("PRJ_QUOTATION");
        queryDto.setGeneratedSourceDocId(quotationId);
        queryDto.setCfItem(9L);
        List<HlsCusConContractCashflow> oldCashflows = contractCashflowMapper.select(queryDto);

        for(HlsCusConContractCashflow oldCashflow: oldCashflows){
            for(JSONObject newCashflow:newCashflows){
                if (oldCashflow.getCashflowId().equals(newCashflow.getLong("cashflow_id"))) {
                    Double amount = CalculateUtil.sub(oldCashflow.getDueAmount(), newCashflow.getDouble("due_amount"));
                    if (amount.compareTo(0.0D) > 0) {
                        BeanRefUtils.beanToBean(oldCashflow, debtExemptionReqCf, hlsBeanRefUtilService);
                        debtExemptionReqCf.setExemptionAmount(amount);
                        conDebtExemptionReqCfService.insertSelective(iRequest, debtExemptionReqCf);
                    }
                }
            }
        }
    }

    private void changeContractAttachment(IRequest iRequest, HlsCusConContractChangeReq changeReq) {
        HlsCusContractAttachment attachment = new HlsCusContractAttachment();
        attachment.setContractId(changeReq.getContractId());
        List<HlsCusContractAttachment> contractAttachmentList = contractAttachmentMapper.select(attachment);

        for(HlsCusContractAttachment contractAttachment:contractAttachmentList){
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTablePkValue(changeReq.getChangeReqId() + contractAttachment.getDocumentName());
            fndAttachmentMulti.setTableName("CON_CONTRACT_CHANGE_REQ");
            List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);

            for(FndAttachmentMulti attachmentMulti:multiList){
                attachmentMulti.setTableName("CON_CONTRACT");
                attachmentMulti.setTablePkValue(changeReq.getContractId().toString());
                attachmentMultiService.updateByPrimaryKey(iRequest, attachmentMulti);
            }
        }
    }

    @Override
    public List<HlsCusBpMaster> queryTenantBeforeChange(IRequest iRequest, Long changeReqId) throws NoSuchObjectException {
        SysDocumentHistory sysDocumentHistory = new SysDocumentHistory();
        sysDocumentHistory.setDocumentId(changeReqId);
        sysDocumentHistory.setDocumentCategory(DOCUMENT_CATEGORY_CONTRACT_CHANGE);
        JSONObject contract = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, changeReqId, DOCUMENT_CATEGORY_CONTRACT_CHANGE, "con_contract").get(0).getJSONObject("data");
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setBpId(contract.getLong("tenant_id"));
        hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectByPrimaryKey(hlsBpMaster);
        String bpClassN = codeService.getCodeValue(iRequest, "FND.BP_CLASS", hlsBpMaster.getBpClass()).getMeaning();
        hlsBpMaster.setBpClassN(bpClassN);
        List<HlsCusBpMaster> list = new ArrayList<>(1);
        list.add(hlsBpMaster);
        return list;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void contractChangeApproved(IRequest iRequest, String changeType, String depositDeductFlag, Long contractId) {
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract.setContractStatus("INCEPT");
        contractService.updateByPrimaryKeySelective(iRequest, contract);

        /*switch (changeType.toUpperCase()) {
            case "EXTENSION":
            case "DELAY":
            case "PREPAYMENT":
            case "REPO":
            case "TENANT":
                plusVersionWithOne(iRequest, contract.getContractId(), changeType.toUpperCase());
                break;
            default:
                break;
        }*/
    }

    @Override
    public void plusVersionWithOne(IRequest iRequest, Long contractId, String changeType) {
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract = contractService.selectByPrimaryKey(iRequest, contract);
        contract.setObjectVersionNumber(contract.getObjectVersionNumber() + 1);
        contract.setDownloadVersion(contract.getDownloadVersion() + 1);
        contractService.updateByPrimaryKey(iRequest, contract);
        Long versionId = contract.getObjectVersionNumber();

        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contract.getContractId());
        List<HlsCusConContractCashflow> cashflowList = contractCashflowService.select(iRequest, cashflow, 1, 0);
        if (StringUtils.equals("TENANT", changeType)) {
            for (HlsCusConContractCashflow hlsCusConContractCashflow : cashflowList) {
                hlsCusConContractCashflow.setObjectVersionNumber(versionId);
                hlsCusConContractCashflow.setChangeFlag("N");
                hlsCusConContractCashflow.set__status("update");
            }
        } else {
            for (HlsCusConContractCashflow hlsCusConContractCashflow : cashflowList) {
                hlsCusConContractCashflow.setObjectVersionNumber(versionId);
                hlsCusConContractCashflow.set__status("update");
            }
        }
        contractCashflowService.batchUpdate(iRequest, cashflowList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealInterfaceWithContractChangeApproved(IRequest iRequest, Long contractId, String changeType, String depositDeductFlag, Long changeReqId) {
        contractChangeApproved(iRequest, changeType, depositDeductFlag, contractId);
        contractChangeDeposit(iRequest, contractId, changeType, depositDeductFlag);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void contractChangeDeposit(IRequest iRequest, Long contractId, String changeType, String depositDeductFlag) {
        switch (changeType) {
            case "PREPAYMENT":
            case "REPO":
                //提前结清或者回购的时候判断是否自动抵扣保证金
                try {
                    autoWriteOffDeposit(iRequest, depositDeductFlag, contractId,changeType);
                } catch (HlsCusException e) {
                    logger.error("contract change error", e);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public HlsCusConContractCashflow calcFullCashflow(HlsCusConContractCashflow cashflow, List<CshDepositDeductReqLn> lnList) {
        CshDepositDeductReqLn cshDepositDeductReqLn = new CshDepositDeductReqLn();
        cshDepositDeductReqLn.setCashflowId(cashflow.getCashflowId());
        cshDepositDeductReqLn.setSurplusAmount(cashflow.getSurplusAmount());

        switch (cashflow.getCfType().intValue()) {
            case 1:
                cshDepositDeductReqLn.setWriteOffDueAmount(cashflow.getSurplusAmount());
                cshDepositDeductReqLn.setWriteOffPrincipal(cashflow.getSurplusPrincipal());
                cshDepositDeductReqLn.setWriteOffInterest(cashflow.getSurplusInterest());
                cshDepositDeductReqLn.setSurplusPrincipal(cashflow.getSurplusPrincipal());
                cshDepositDeductReqLn.setSurplusInterest(cashflow.getSurplusInterest());
                break;
            case 8:
                cshDepositDeductReqLn.setWriteOffDueAmount(cashflow.getSurplusAmount());
                cshDepositDeductReqLn.setWriteOffPrincipal(0.0);
                cshDepositDeductReqLn.setWriteOffInterest(0.0);
                cshDepositDeductReqLn.setSurplusPrincipal(0.0);
                cshDepositDeductReqLn.setSurplusInterest(0.0);
                break;
            case 9:
                cshDepositDeductReqLn.setWriteOffDueAmount(cashflow.getSurplusAmount());
                cshDepositDeductReqLn.setWriteOffPrincipal(0.0);
                cshDepositDeductReqLn.setWriteOffInterest(0.0);
                cshDepositDeductReqLn.setSurplusPrincipal(0.0);
                cshDepositDeductReqLn.setSurplusInterest(0.0);
                break;
        }
        lnList.add(cshDepositDeductReqLn);
        return cashflow;
    }

    @Override
    public HlsCusConContractCashflow calcCashflow(HlsCusConContractCashflow cashflow, List<CshDepositDeductReqLn> lnList, Double returnAmount) {
        Double canReturnAmount = returnAmount;
        CshDepositDeductReqLn cshDepositDeductReqLn = new CshDepositDeductReqLn();
        cshDepositDeductReqLn.setCashflowId(cashflow.getCashflowId());
        cshDepositDeductReqLn.setSurplusAmount(cashflow.getSurplusAmount());

        switch (cashflow.getCfType().intValue()) {
            case 1:
                cshDepositDeductReqLn.setWriteOffDueAmount(canReturnAmount);
                cshDepositDeductReqLn.setSurplusPrincipal(cashflow.getSurplusPrincipal());
                cshDepositDeductReqLn.setSurplusInterest(cashflow.getSurplusInterest());
                //先抵扣利息
                if (canReturnAmount.compareTo(cashflow.getSurplusInterest()) == 1) {
                    cshDepositDeductReqLn.setWriteOffInterest(cashflow.getSurplusInterest());
                    cshDepositDeductReqLn.setWriteOffPrincipal(MathUtil.sub(canReturnAmount, cashflow.getSurplusInterest(), 2));

                } else {
                    cshDepositDeductReqLn.setWriteOffPrincipal(0.0);
                    cshDepositDeductReqLn.setWriteOffInterest(canReturnAmount);
                }

                break;
            case 8:
                cshDepositDeductReqLn.setWriteOffDueAmount(canReturnAmount);
                cshDepositDeductReqLn.setWriteOffPrincipal(0.0);
                cshDepositDeductReqLn.setWriteOffInterest(0.0);
                cshDepositDeductReqLn.setSurplusPrincipal(0.0);
                cshDepositDeductReqLn.setSurplusInterest(0.0);
                break;
            case 9:
                cshDepositDeductReqLn.setWriteOffDueAmount(canReturnAmount);
                cshDepositDeductReqLn.setWriteOffPrincipal(0.0);
                cshDepositDeductReqLn.setWriteOffInterest(0.0);
                cshDepositDeductReqLn.setSurplusPrincipal(0.0);
                cshDepositDeductReqLn.setSurplusInterest(0.0);
                break;
        }
        lnList.add(cshDepositDeductReqLn);
        return cashflow;
    }

    @Override
    public void autoWriteOffDeposit(IRequest iRequest, String flag, Long contractId,String changeType) throws HlsCusException {
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setDepositContractId(contractId);
        cshTransaction.setReversedFlag("N");
        Map<Long,Map<String,Double>> cashflowBlockAmtMap = new HashMap<>();
        List<HlsCusCshTransaction> list = transactionMapper.queryCshTransaction(cshTransaction);
        String depositDeductMethod = "MANUAL";
        if ("Y".equalsIgnoreCase(flag)) {
            depositDeductMethod = "AUTO";
        }

        HlsCusConContract cusConContract = new HlsCusConContract();
        cusConContract.setContractId(contractId);
        cusConContract.setDepositDeductMethod(depositDeductMethod);
        contractMapper.updateByPrimaryKeySelective(cusConContract);

        if ("Y".equalsIgnoreCase(flag)) {
            //查询要抵扣的保证金
            if (CollectionUtils.isEmpty(list)) {
                logger.error("未查询到要抵扣的保证金!");
            }

            List<CshDepositDeductReqHd> cshDepositDeductReqHdList = new ArrayList<>();
            int i = 0;
            for(HlsCusCshTransaction cusCshTransaction :list){
                i++;
                Long transactionId = cusCshTransaction.getTransactionId();
                //查询抵扣头信息
                CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
                cshDepositDeductReqHd.setTransactionId(transactionId);
                cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectCshDepositDeductReqHdInit(cshDepositDeductReqHd);
                cshDepositDeductReqHd.setDeductDate(new Date());

                Double canReturnAmount = cshDepositDeductReqHd.getCanReturnAmount();
                if (canReturnAmount == null) {
                    canReturnAmount = 0D;
                }
                List<CshDepositDeductReqLn> lnList = new ArrayList<>();

                //查询抵扣现金流信息
                HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
                cashflow.setContractId(contractId);
                cashflow.setQueryTimes(i);
                List<HlsCusConContractCashflow> cashflowList = contractCashflowMapper.queryAutoDepositCashflowOrder(cashflow);
                for (HlsCusConContractCashflow contractCashflow : cashflowList) {
                    logger.info("cashflowList:{}",JSON.toJSONString(cashflowList));
                    Long cashflowId  = contractCashflow.getCashflowId();
                    if(cashflowBlockAmtMap.containsKey(cashflowId)){
                        Map<String,Double> blockAmtMap = cashflowBlockAmtMap.get(cashflowId);
                        blockAmtMap.put("BLOCK_AMOUNT_SUM",MathUtil.add(blockAmtMap.get("BLOCK_AMOUNT_SUM"),
                                MathUtil.sub(contractCashflow.getBlockAmount(),blockAmtMap.get("CURRENT_BLOCK_AMOUNT"))));
                        blockAmtMap.put("CURRENT_BLOCK_AMOUNT",nvl(contractCashflow.getBlockAmount(),0d));
                        blockAmtMap.put("INTEREST_BLOCK_AMOUNT_SUM",MathUtil.add(blockAmtMap.get("INTEREST_BLOCK_AMOUNT_SUM"),
                                MathUtil.sub(contractCashflow.getBlockInterest(),blockAmtMap.get("INTEREST_CURRENT_BLOCK_AMOUNT"))));
                        blockAmtMap.put("INTEREST_CURRENT_BLOCK_AMOUNT",nvl(contractCashflow.getBlockInterest(),0d));
                        blockAmtMap.put("PRINCIPAL_BLOCK_AMOUNT_SUM",MathUtil.add(blockAmtMap.get("PRINCIPAL_BLOCK_AMOUNT_SUM"),
                                MathUtil.sub(contractCashflow.getBlockPrincipal(),blockAmtMap.get("PRINCIPAL_CURRENT_BLOCK_AMOUNT"))));
                        blockAmtMap.put("PRINCIPAL_CURRENT_BLOCK_AMOUNT",nvl(contractCashflow.getBlockPrincipal(),0d));
                        logger.info("cashflowId:{}---{}",cashflowId,JSON.toJSONString(blockAmtMap));
                        contractCashflow.setSurplusAmount(MathUtil.sub(contractCashflow.getSurplusAmount(),blockAmtMap.get("BLOCK_AMOUNT_SUM")));
                        contractCashflow.setSurplusInterest(MathUtil.sub(contractCashflow.getSurplusInterest(),blockAmtMap.get("INTEREST_BLOCK_AMOUNT_SUM")));
                        contractCashflow.setSurplusPrincipal(MathUtil.sub(contractCashflow.getSurplusPrincipal(),blockAmtMap.get("PRINCIPAL_BLOCK_AMOUNT_SUM")));
                        if(contractCashflow.getSurplusAmount().compareTo(0D) < 0 ||
                                contractCashflow.getSurplusInterest().compareTo(0D) < 0 ||
                                contractCashflow.getSurplusPrincipal().compareTo(0D) < 0 ){
                            throw new HlsCusException("冻结金额小于0");
                        }
                    }else{
                        Map<String,Double> blockAmtMap = new HashMap<>();
                        blockAmtMap.put("BLOCK_AMOUNT_SUM",0D);
                        blockAmtMap.put("CURRENT_BLOCK_AMOUNT",nvl(contractCashflow.getBlockAmount(),0d));
                        blockAmtMap.put("INTEREST_BLOCK_AMOUNT_SUM",0D);
                        blockAmtMap.put("INTEREST_CURRENT_BLOCK_AMOUNT",nvl(contractCashflow.getBlockInterest(),0d));
                        blockAmtMap.put("PRINCIPAL_BLOCK_AMOUNT_SUM",0D);
                        blockAmtMap.put("PRINCIPAL_CURRENT_BLOCK_AMOUNT",nvl(contractCashflow.getBlockPrincipal(),0d));
                        cashflowBlockAmtMap.put(cashflowId,blockAmtMap);
                        logger.info("cashflowId:{}---{}",cashflowId,JSON.toJSONString(blockAmtMap));
                    }
                }
                for (HlsCusConContractCashflow contractCashflow : cashflowList) {
                    Double surplusAmount = contractCashflow.getSurplusAmount();
                    if (canReturnAmount.compareTo(surplusAmount) > 0) {
                        if(surplusAmount.compareTo(0D) > 0){
                            calcFullCashflow(contractCashflow, lnList);
                            canReturnAmount = CalculateUtil.sub(canReturnAmount, surplusAmount);
                        }
                    } else {
                        calcCashflow(contractCashflow, lnList, canReturnAmount);
                        canReturnAmount = 0D;
                        break;
                    }
                }
                cshDepositDeductReqHd.setCanReturnAmount(canReturnAmount);
                cshDepositDeductReqHd.setCshDepositDeductReqLnList(lnList);
                cshDepositDeductReqHd.setReqStatus(HlsConstantUtil.WorkFlowStatus.APPROVED);
                if(CollectionUtils.isEmpty(lnList)){
                    logger.info("dont have line");
                    continue;
                }
                cshDepositDeductReqHd = iCshDepositDeductReqHdService.saveDepositDeductReq(iRequest, cshDepositDeductReqHd,changeType);
                iCshDepositDeductReqHdService.blockAmountWithNewTransaction(iRequest, cshDepositDeductReqHd.getReqHdId());
                cshDepositDeductReqHdList.add(cshDepositDeductReqHd);
            }
            for(CshDepositDeductReqHd cshDepositDeductReqHd :cshDepositDeductReqHdList){
                iCshDepositDeductReqHdService.execDepositDeductReqApproved(iRequest, cshDepositDeductReqHd,changeType);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public String contractChange(IRequest iRequest, String result, HlsCusConContractChangeReq changeReq, String processInstanceId) {
        String depositDeductFlag = "N";
        if (StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, result)) {
            try {
                leaveHistory(iRequest, changeReq, Long.parseLong(processInstanceId));
            } catch (Exception e) {
                logger.error("leave history error:", e);
            }
            List<JSONObject> jsonObjects = new ArrayList<>();
            try {
                jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, changeReq.getChangeReqId(), "CONTRACT_CHANGE", "con_contract_change_req");
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            }
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(changeReq.getContractId());
            switch (changeReq.getChangeType()) {
                case "PREPAYMENT":
                    depositDeductFlag = jsonObjects.get(0).getJSONObject("data").get("deposit_deduct_flag").toString();
                    break;
                case "REPO":
                    depositDeductFlag = jsonObjects.get(0).getJSONObject("data").get("deposit_deduct_flag").toString();
                    break;
            }
        }

        switch (changeReq.getChangeType()) {
            case "PREPAYMENT":
            case "REPO":
            case "DELAY":
            case "EXTENSION":
                Map map = contractMapper.queryContractChangeUpdateInfo(changeReq.getContractId());

                HlsCusConContract conContract = JSONObject.parseObject(JSONObject.toJSONString(map), HlsCusConContract.class);
                conContract.setContractId(changeReq.getContractId());
                conContract.setObjectVersionNumber(null);
                contractMapper.updateByPrimaryKeySelective(conContract);

                HlsCusPrjQuotation prjQuotation = JSONObject.parseObject(JSONObject.toJSONString(map), HlsCusPrjQuotation.class);

                HlsCusPrjQuotation queryQuotation = new HlsCusPrjQuotation();
                queryQuotation.setSourceDocumentId(changeReq.getContractId());
                //queryQuotation.setSourceDocumentCategory("CONTRACT");
                queryQuotation.setDataClass("CON_QUOTATION");
                queryQuotation = quotationMapper.selectOne(queryQuotation);

                prjQuotation.setObjectVersionNumber(null);
                prjQuotation.setQuotationId(queryQuotation.getQuotationId());
                quotationMapper.updateByPrimaryKeySelective(prjQuotation);
                break;
        }

        HlsCusConContractChangeReq ccr = new HlsCusConContractChangeReq();
        ccr.setChangeReqId(changeReq.getChangeReqId());
        ccr.setProcessInstanceId(Long.parseLong(processInstanceId));
        ccr.setStatus(result);
        self().updateByPrimaryKeySelective(iRequest, ccr);
        return depositDeductFlag;
    }

    @Override
    public List<JSONObject> prevContractLeaseItem(IRequest iRequest, Long changeReqId) throws NoSuchObjectException {
        List<JSONObject> returnJSONList = new ArrayList<>();

        HlsCusConContractChangeReq changeReq = contractChangeReqMapper.selectByPrimaryKey(changeReqId);
        if(StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, changeReq.getStatus())){
            List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                    changeReqId, "CONTRACT_CHANGE", "con_contract_lease_item", 1L);
            if(CollectionUtils.isNotEmpty(jsonObjects)){
                jsonObjects.forEach(item -> returnJSONList.add(item.getJSONObject("data")));
            }
        }else{
            List<JSONObject> jsonObjects = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest,
                    changeReqId, "CONTRACT_CHANGE", "con_contract_lease_item");
            if(CollectionUtils.isNotEmpty(jsonObjects)){
                jsonObjects.forEach(item -> {
                    JSONObject data = item.getJSONObject("data");
                    String status = data.getString("_status");
                    if(null == status){
                        returnJSONList.add(data);
                    }else if(StringUtils.equals("delete", status) || StringUtils.equals("update", status)){
                        Map map = new HashMap();
                        map.put("contractId",data.getLong("contract_id"));
                        map.put("contractLeaseItemId", data.getLong("contract_lease_item_id"));
                        List<HlsCusConContractLeaseItem> leaseItems = contractLeaseItemMapper.queryContractLeaseItemDetail(map);
                        HlsCusConContractLeaseItem leaseItem = new HlsCusConContractLeaseItem();
                        if(CollectionUtils.isNotEmpty(leaseItems)){
                            leaseItem = leaseItems.get(0);
                        }
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(leaseItem));
                        returnJSONList.add(jsonObject);
                    }
                });
            }
        }

        return returnJSONList;
    }

    @Override
    public boolean validata(IRequest requestCtx, Long contractId, Double maxAmount) {
        requestCtx.setAttribute("authorityRuleFlag","N");
        HlsCusConContract contract =contractMapper.selectByPrimaryKey(contractId);
        Double contractLeaseTerm = Double.valueOf(contract.getLeaseTerm());
        // 免购保险期限(月)
        Double leaseTerm = contractMapper.queryReplyParamDefaultValue(contractId, "INSURANCE_FREE_PERIOD");
        // 免购买保险设备额
        Double amount = contractMapper.queryReplyParamDefaultValue(contractId, "INSURANCE_FREE_AMOUNT");
        if(leaseTerm == null || Double.compare(contractLeaseTerm, leaseTerm) > 0){
            if(amount == null || Double.compare(maxAmount, amount) > 0){
                return true;
            } else{
                return false;
            }
        }else{
            return false;
        }
    }
}
