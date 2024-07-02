package com.hand.hls.cont.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqLnMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.*;
import com.hand.hls.fnd.mapper.FndBaseRateMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.service.HlsQuotationCalcService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.sys.utils.SysDocumentHistoryUtils;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.rmi.NoSuchObjectException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConFloatingRateReqLnServiceImpl extends BaseServiceImpl<HlsCusConFloatingRateReqLn> implements ConFloatingRateReqLnService {

    private static final Logger logger = LoggerFactory.getLogger(ConFloatingRateReqLnServiceImpl.class);

    private static final String LEVEL_PMT = "HLS_EQUAL_RENTAL";//等额本息
    private static final String EQUAL_PRINCIPAL = "General_Calculator_LP";//等额本金

    public static final String FLOATING = "FLOATING";//浮动利率类型
    public static final String FAILURE = "FAILURE";//失败
    public static final String CALCULATED = "CALCULATED";//已计算
    public static final String CONFIRMED = "CONFIRMED";//确认
    public static final String UNCONFIRMED = "UNCALCULATED";//未计算
    public static final String CON_CONTRACT = "con_contract";
    public static final String ENABLED_FLAG_Y = "Y";
    public static final String PENDING = "PENDING";
    public static final String DOCUMENT_CATEGORY = "CON_FLOATING_RATE_REQ";
    public static final String DOCUMENT_CATEGORY_ORIGIN = "CON_FLOATING_RATE_REQ";
    public static final String DOCUMENT_TYPE = "STD";
    public static final String BUSINESS_TYPE = "CON_FLOATING_RATE_REQ";
    public static final String CON_CONTRACT_CASHFLOW = "con_contract_cashflow";

    public static final String CON_FLOATING_RATE_REQ_LN = "CON_FLOATING_RATE_REQ_LN";

    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private ConFloatingRateReqService conFloatingRateReqService;
    @Autowired
    private HlsCusConFloatingRateReqMapper conFloatingRateReqMapper;
    @Autowired
    private HlsCusConFloatingRateReqLnMapper conFloatingRateReqLnMapper;
    @Autowired
    private HlsCusConFloatingRateReqLnService lnService;
    @Autowired
    private IConContractService conContractService;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private IConFloatingCalcService iConFloatingCalcService;
    @Autowired
    private IConContractCashflowService conContractCashflowService;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;


    private List<Map<String, Object>> getFloatingReq(Long fltReqId) throws ParameterNullException, ResMessageException {
        HlsCusConFloatingRateReq conFloatingRateReq = new HlsCusConFloatingRateReq();
        conFloatingRateReq.setFltReqId(fltReqId);
        List<HlsCusConFloatingRateReq> conFloatingRateReqList = conFloatingRateReqMapper.queryConFloatingRateReq(conFloatingRateReq);
        List<Map<String, Object>> list;
        try {
            list = SysDocumentHistoryUtils.initRecords("con_floating_rate_req", "flt_req_id", conFloatingRateReqList);
        } catch (ResMessageException e) {
            throw new ResMessageException(e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getFloatingReqLn(Long fltReqId) throws ParameterNullException, ResMessageException {
        HlsCusConFloatingRateReqLn conFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        conFloatingRateReqLn.setFltReqId(fltReqId);
        List<HlsCusConFloatingRateReqLn> conFloatingRateReqLnList = conFloatingRateReqLnMapper.queryConFloatingRateReqLn(conFloatingRateReqLn);
        List<Map<String, Object>> list;
        try {
            list = SysDocumentHistoryUtils.initRecords("con_floating_rate_req_ln", "flt_req_ln_id", "con_floating_rate_req", fltReqId.toString(), conFloatingRateReqLnList);
        } catch (ResMessageException e) {
            throw new ResMessageException(e.getMessage());
        }
        return list;
    }


    private List<Map<String, Object>> getContract(List<Long> contractIds) throws ParameterNullException, ResMessageException {

        if (CollectionUtils.isEmpty(contractIds)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Long contractId : contractIds) {
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractId(contractId);
            List<HlsCusConContract> contracts = new ArrayList<>();
            HlsCusConContract hlsCusConContract = conContractMapper.selectByPrimaryKey(contract);
            contracts.add(hlsCusConContract);
            List<Map<String, Object>> list = new ArrayList<>();
            try {
                list = SysDocumentHistoryUtils.initRecords("con_contract", "contract_id", contracts);
            } catch (ResMessageException e) {
                throw new ResMessageException(e.getMessage());
            }
            mapList.addAll(list);
        }
        return mapList;
    }

    private List<Map<String, Object>> getContractCashflow(List<Long> quotationIds) throws ParameterNullException, ResMessageException {
        if (CollectionUtils.isEmpty(quotationIds)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Long quotationId : quotationIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("generatedSource", ConContractCashflow.SOURCE_PRJ_QUOTATION);
            map.put("generatedSourceDocId", quotationId);
            List<HlsCusConContractCashflow> contractCashflows = conContractCashflowMapper.queryConContractCashflowDetail(map);
            if (CollectionUtils.isEmpty(contractCashflows)) {
                continue;
            }
            List<Map<String, Object>> list = new ArrayList<>();
            try {
                //现金流是报价为度，一个合同有多个报价对应多套现金流
                list = SysDocumentHistoryUtils.initRecords("con_contract_cashflow", "cashflow_id", "prj_quotation", quotationId.toString(), contractCashflows);
            } catch (ResMessageException e) {
                throw new ResMessageException(e.getMessage());
            }
            mapList.addAll(list);
        }
        return mapList;
    }

    public List<Map<String, Object>> getDatas(Long fltReqId, List<Long> contractIds, List<Long> quotationIds) throws ParameterNullException, ResMessageException {
        //开始数据组装
        List<Map<String, Object>> datas = new ArrayList<>();
        //调息头表
        datas.addAll(getFloatingReq(fltReqId));
        //调息行表
        datas.addAll(getFloatingReqLn(fltReqId));
        //合同信息
        datas.addAll(getContract(contractIds));
        //现金流
        datas.addAll(getContractCashflow(quotationIds));
        return datas;
    }

    private void createHistory(Long fltReqId, String documentCategory, List<Long> contractIds, List<Long> quotationIds) throws ParameterNullException, ResMessageException {
        List<Map<String, Object>> datas = getDatas(fltReqId, contractIds, quotationIds);
        //通用创建历史留痕接口
        sysDocumentHistoryService.createHistory(documentCategory, fltReqId, datas);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<HlsCusConFloatingRateReqLn> queryConFloatingRateReqLn(IRequest iRequest, HlsCusConFloatingRateReqLn conFloatingRateReqLn, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return conFloatingRateReqLnMapper.queryConFloatingRateReqLn(conFloatingRateReqLn);
    }

    @Override
    public List<HlsCusConFloatingRateReqLn> queryFloatlnCalcDetail(IRequest iRequest, HlsCusConFloatingRateReqLn conFloatingRateReqLn, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return conFloatingRateReqLnMapper.queryFloatlnCalcDetail(conFloatingRateReqLn);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<HlsCusConContractCashflow> queryCompare(IRequest iRequest, HlsCusConFloatingRateReqLn conFloatingRateReqLn, int pagenum, int pagesize) throws NoSuchObjectException {

        HlsCusConFloatingRateReqLn reqLn = conFloatingRateReqLnMapper.selectByPrimaryKey(conFloatingRateReqLn.getFltReqLnId());
        List<JSONObject> oldContractCashFlows = getContractCashFlows(iRequest, reqLn.getFltReqId(), DOCUMENT_CATEGORY_ORIGIN, reqLn.getQuotationId());
        List<JSONObject> newContractCashFlows = getContractCashFlows(iRequest, reqLn.getFltReqId(), DOCUMENT_CATEGORY, reqLn.getQuotationId());


        List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<>();

        for (JSONObject newContractCashflow : newContractCashFlows) {
            JSONObject newCashflow = newContractCashflow.getJSONObject("data");
            if (newCashflow.getLong("cf_item").compareTo(1L) == 0) {
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();

                conContractCashflow.setCashflowId(newCashflow.getLong("cashflow_id"));
                conContractCashflow.setContractId(newCashflow.getLong("contract_id"));
                conContractCashflow.setDueDate(newCashflow.getDate("due_date"));
                conContractCashflow.setTimes(newCashflow.getLong("times"));


                oldContractCashFlows.forEach(item -> {
                    if (item.getJSONObject("data").getLong("cashflow_id").compareTo(newCashflow.getLong("cashflow_id")) == 0) {
                        conContractCashflow.setOldDueAmount(item.getJSONObject("data").getDouble("due_amount"));
                        conContractCashflow.setOldInterest(item.getJSONObject("data").getDouble("interest"));
                    }

                });
                conContractCashflow.setNewDueAmount(newCashflow.getDouble("due_amount"));
                conContractCashflow.setNewInterest(newCashflow.getDouble("interest"));
                conContractCashflow.setInterestAdjAmount(HlsCusMathUtil.sub(conContractCashflow.getNewInterest(), conContractCashflow.getOldInterest()));
                conContractCashflowList.add(conContractCashflow);
            }
        }

        return conContractCashflowList;
    }


    private List<JSONObject> getContractCashFlows(IRequest iRequest, Long documentId, String
            documentCategory, Long quotationId) throws NoSuchObjectException {
        List<JSONObject> contractCashflows = new ArrayList<>();
        sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "con_contract_cashflow", false)
                .forEach(item -> {
                    if (quotationId.equals(item.getJSONObject("data").getLong("generated_source_doc_id"))) {
                        contractCashflows.add(item);
                    }
                });
        return contractCashflows;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelFloatingRateReq(IRequest iRequest, HlsCusConFloatingRateReq floatingRateReq) {

        HlsCusConFloatingRateReq conFloatingRateReq = conFloatingRateReqMapper.selectByPrimaryKey(floatingRateReq.getFltReqId());
        conFloatingRateReq.setStatus("CANCEL");
        conFloatingRateReqService.updateByPrimaryKeySelective(iRequest, conFloatingRateReq);

        HlsCusConFloatingRateReqLn conFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        conFloatingRateReqLn.setFltReqId(floatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> conFloatingRateReqLns = conFloatingRateReqLnMapper.select(conFloatingRateReqLn);

        //将头状态置为取消 并将合同状态修改回来
        conFloatingRateReqLns.forEach(item -> {
            HlsCusConContract conContract =
                    conContractMapper.selectByPrimaryKey(item.getContractId());
            if (conContract != null) {
                conContract.setContractStatus(item.getContractStatus());
                conContractService.updateByPrimaryKeySelective(iRequest, conContract);
            }
        });

    }


    private void confirmConFloating(IRequest request, HlsCusConFloatingRateReqLn item) throws NoSuchObjectException {
        //更新合同和现金流
        if (item.getStatus() == CALCULATED) {

            HlsCusConContract conContract = conContractMapper.selectByPrimaryKey(item.getContractId());
            conContract.setIntRate(item.getNewIntRate());
            conContract.setContractStatus(item.getContractStatus());
            conContract.setBaseRate(item.getNewBaseRate());
            conContractService.updateByPrimaryKeySelective(request, conContract);

            List<JSONObject> cashflows = getContractCashFlows(request, item.getFltReqId(), DOCUMENT_CATEGORY, item.getQuotationId());
            //租本利 剩余本金 含税不含税

            for (JSONObject cashflow : cashflows) {
                cashflow = cashflow.getJSONObject("data");
                if (cashflow.getLong("cf_item").compareTo(1L) == 0) {
                    HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                    contractCashflow.setCashflowId(cashflow.getLong("cashflow_id"));
                    contractCashflow = conContractCashflowMapper.selectByPrimaryKey(contractCashflow);

                    contractCashflow.setDueAmount(cashflow.getDouble("due_amount"));
                    contractCashflow.setNetDueAmount(cashflow.getDouble("net_due_amount"));
                    contractCashflow.setVatDueAmount(cashflow.getDouble("vat_due_amount"));
                    contractCashflow.setPrincipal(cashflow.getDouble("principal"));
                    contractCashflow.setNetPrincipal(cashflow.getDouble("net_principal"));
                    contractCashflow.setVatPrincipal(cashflow.getDouble("vat_principal"));
                    contractCashflow.setInterest(cashflow.getDouble("interest"));
                    contractCashflow.setNetInterest(cashflow.getDouble("net_interest"));
                    contractCashflow.setVatInterest(cashflow.getDouble("vat_interest"));
                    contractCashflow.setOutstandingPrincipal(cashflow.getDouble("outstanding_principal"));
                    conContractCashflowService.updateByPrimaryKeySelective(request, contractCashflow);
                }
            }
            item.setStatus(CONFIRMED);
            this.self().updateByPrimaryKeySelective(request, item);
        }


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void confirmFloatingRate(IRequest request, HlsCusConFloatingRateReq floatingRateReq) throws Exception {

        HlsCusConFloatingRateReq conFloatingRateReq = conFloatingRateReqMapper.selectByPrimaryKey(floatingRateReq.getFltReqId());

        HlsCusConFloatingRateReqLn conFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        conFloatingRateReqLn.setFltReqId(conFloatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> reqLnListAll = conFloatingRateReqLnMapper.select(conFloatingRateReqLn);


        reqLnListAll.forEach((item) -> {
            try {
                confirmConFloating(request, item);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            }

        });

        int confirmInt = reqLnListAll.stream().filter(l -> l.getStatus().equals(CONFIRMED)).collect(Collectors.toList()).size();
        HlsCusConFloatingRateReq req = new HlsCusConFloatingRateReq();
        if (confirmInt == reqLnListAll.size()) {
            req.setFltReqId(reqLnListAll.get(0).getFltReqId());
            req.setStatus("APPROVED");
            conFloatingRateReqMapper.updateByPrimaryKeySelective(req);
        }
    }

    @Override
    public List<HlsCusConFloatingRateReq> queryHistory(IRequest iRequest, HlsCusConFloatingRateReq conFloatingRateReq, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return conFloatingRateReqMapper.queryHistory(conFloatingRateReq);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public HlsCusConFloatingRateReq createFloatingRateChangeList(IRequest request) {
        //插入申请头表
        HlsCusConFloatingRateReq answer = new HlsCusConFloatingRateReq();
        answer.setStatus("NEW");//状态
        answer.setDescription(request.getAttribute("description"));
        answer.setFltReqDate(new Date());//日期
        answer.setFltReqUserId(request.getUserId());//人员
        answer.setDocumentCategory(DOCUMENT_CATEGORY);
        answer.setDocumentType(DOCUMENT_TYPE);
        answer.setBusinessType(BUSINESS_TYPE);
        Map<String, String> params = new HashMap<>();
        //申请单号
        answer.setFltReqNumber(fndCodingRuleValuesService.getCodeRuleValue(request, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params));//生成单据编号
        answer.setCompanyId(request.getCompanyId());
        HlsCusConFloatingRateReq hlsCusConFloatingRateReq = conFloatingRateReqService.insertSelective(request, answer);
        //获得要调息的合同的集合
       /* List<HlsCusConContract> floatingRateContracts = conContractMapper.selectFloatingRateContract();
        for (HlsCusConContract item : floatingRateContracts) {
            HlsCusConFloatingRateReqLn floatingRateReqLn = new HlsCusConFloatingRateReqLn();
            floatingRateReqLn.setContractId(item.getContractId());        //合同 ID
            floatingRateReqLn.setQuotationId(item.getQuotationId());      //报价id
            floatingRateReqLn.setFltReqId(hlsCusConFloatingRateReq.getFltReqId());
            floatingRateReqLn.setBaseRateType(item.getBaseRateType());    //基准利率类型
            floatingRateReqLn.setNewBaseRate(item.getNewBaseRate());      //新基准利率
            floatingRateReqLn.setOldBaseRate(item.getBaseRate());         //老基准利率
            floatingRateReqLn.setOldIntRate(item.getIntRate());           //原租赁利率
            floatingRateReqLn.setContractStatus(item.getContractStatus());//合同状态
            floatingRateReqLn.setStatus(UNCONFIRMED);                     //未计算
            floatingRateReqLn.setPriceList(item.getPriceList());
            floatingRateReqLn.setFloatingRangeMethod(item.getFloatingRangeMethod()); //还款方法 FLOATING_RANGE_METHOD
            insertSelective(request, floatingRateReqLn);
        }*/
        return answer;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public  List<HlsCusConFloatingRateReqLn>  createFloatingRateQuotation(IRequest iRequest ,List<HlsCusPrjQuotation> prjQuotationList) throws Exception {


        //插入申请头表
        HlsCusConFloatingRateReq answer = new HlsCusConFloatingRateReq();
        answer.setStatus("NEW");//状态
//        answer.setDescription(iRequest.getAttribute("description"));
        answer.setFltReqDate(new Date());//日期
        answer.setFltReqUserId(iRequest.getUserId());//人员
        answer.setDocumentCategory(DOCUMENT_CATEGORY);
        answer.setDocumentType(DOCUMENT_TYPE);
        answer.setBusinessType(BUSINESS_TYPE);
        Map<String, String> params = new HashMap<>();
        //申请单号
        answer.setFltReqNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params));//生成单据编号
        answer.setCompanyId(iRequest.getCompanyId());
        HlsCusConFloatingRateReq hlsCusConFloatingRateReq = conFloatingRateReqService.insertSelective(iRequest, answer);

        StringBuilder contractNameTotal = new StringBuilder();;
        List<HlsCusConFloatingRateReqLn> rateReqLns = new ArrayList<>();
        //获得要调息的合同的集合
        for (HlsCusPrjQuotation item : prjQuotationList) {
            //获取当前日期下的基准利率 小于20号取上月否则取当月
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getNextAdjustmentDate());
            int day = cal.get(Calendar.DATE);
            String baseRateType = item.getBaseRateType();
            Date validFrom ;
            Date validTo;
            if(day<20){
                cal.add(Calendar.MONTH, -1);
                validFrom = cal.getTime();
                validTo = date;
            }else{
                validFrom = cal.getTime();
            }
            item.setValidFrom(validFrom);
            List<HlsCusPrjQuotation> quotationList = hlsCusPrjQuotationMapper.queryBaseRateNowReq(item);
            if(quotationList.size()== 0 || quotationList == null ){
                throw new IllegalArgumentException("请先维护lpr利率，再做调息！");
            }
            Double newBaseRate = quotationList.get(0).getBaseRate();

            //判断 如果新旧利率相等 则不调休
            if(newBaseRate != item.getBaseRate()){
                HlsCusConFloatingRateReqLn floatingRateReqLn = new HlsCusConFloatingRateReqLn();
                Double percent = 10000D;
                Double floatWayRate = MathUtil.div(item.getFloatingWayRate() , percent);
                Double newIntRate = MathUtil.add(quotationList.get(0).getBaseRate() ,floatWayRate);
                floatingRateReqLn.setContractId(item.getContractId());        //合同 ID
                floatingRateReqLn.setQuotationId(item.getQuotationId());      //报价id
                floatingRateReqLn.setFltReqId(hlsCusConFloatingRateReq.getFltReqId());
                floatingRateReqLn.setBaseRateType(item.getBaseRateType());    //基准利率类型
                floatingRateReqLn.setNewBaseRate(newBaseRate);      //新基准利率
//                floatingRateReqLn.setLprAdjustmentPeriod(item.getLprAdjustmentPeriod());
                floatingRateReqLn.setNewBaseRateCollect(quotationList.get(0).getBaseRateCollect());//新基准利率集
                floatingRateReqLn.setNewIntRate(newIntRate);
                floatingRateReqLn.setOldBaseRate(item.getBaseRate());         //老基准利率
                floatingRateReqLn.setOldIntRate(item.getIntRate());           //原租赁利率
                floatingRateReqLn.setNextAdjustmentDate(item.getNextAdjustmentDate());//调息日
                floatingRateReqLn.setContractStatus(item.getContractStatus());//合同状态
                floatingRateReqLn.setStatus(UNCONFIRMED);                     //未计算
                floatingRateReqLn.setPriceList(item.getPriceList());
                floatingRateReqLn.setAdjustRatePrice(item.getAdjustRatePrice());
                floatingRateReqLn.setCreationDate(new Date());
                contractNameTotal.append(item.getContractName() + ";");
                lnService.insertSelective(iRequest, floatingRateReqLn);
                rateReqLns.add(floatingRateReqLn);
            }else{
               HlsCusConContract cusConContract = new HlsCusConContract();
                Calendar cals = Calendar.getInstance();
                cals.setTime(item.getNextAdjustmentDate());
                //张栋铭哥哥说写死加一年为下一调息日 20210607
                cals.add(Calendar.YEAR,1);
                cusConContract.setNextAdjustmentDate(cals.getTime());
                cusConContract.setContractId(item.getContractId());
                //更新下一调息日
                conContractMapper.updateByPrimaryKey(cusConContract);
            }
        }

        hlsCusConFloatingRateReq.setDescription(String.valueOf(contractNameTotal));
        conFloatingRateReqService.updateByPrimaryKeySelective(iRequest , hlsCusConFloatingRateReq);

        return rateReqLns;

    }

    @Autowired
    private FndBaseRateMapper fndBaseRateMapper;
    @Autowired
    private IHlsCusPrjQuotationCashflowHistoryService hlsCusPrjCashflowHistoryService;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;
    @Autowired
    private IHlsCusPrjQuotationDetailsHistoryService hlsCusPrjQuotationDetailsHistoryService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @Autowired
    private IHlsCusPrjQuotationHistoryService hlsCusPrjQuotationHistoryService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    /**
     * 保存报价版本
     * @param requestContext
     * @param hlsCusPrjQuotationList
     */
    public void copyQuotationVersion(IRequest requestContext , List<HlsCusPrjQuotation> hlsCusPrjQuotationList){
        for(HlsCusPrjQuotation dt:hlsCusPrjQuotationList){
            JSONObject jsonDTO = (JSONObject) JSONObject.toJSON(dt);
            HlsCusPrjQuotationHistory hlsCusPrjQuotationHistory = jsonDTO.toJavaObject(HlsCusPrjQuotationHistory.class);
            hlsCusPrjQuotationHistory.setQuotationId(null);
//            hlsCusPrjQuotationHistory.setVersionId(version_count);
            hlsCusPrjQuotationHistory.setSourceQuotationId(dt.getQuotationId());
            hlsCusPrjQuotationHistory.setChangeCategory("CON_FLOATING_RATE_REQ");
            hlsCusPrjQuotationHistoryService.insertSelective(requestContext,hlsCusPrjQuotationHistory);
            //复制现金流
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.queryQuotationCashFlowById(dt);
            for(HlsCusPrjQuotationCashflow dt2:hlsCusPrjQuotationCashflowList){
                JSONObject jsonCashflowDTO = (JSONObject) JSONObject.toJSON(dt2);
                HlsCusPrjQuotationCashflowHistory hlsCusPrjQuotationCashflowHistory = jsonCashflowDTO.toJavaObject(HlsCusPrjQuotationCashflowHistory.class);
                hlsCusPrjQuotationCashflowHistory.setQuotationCashflowId(null);
                hlsCusPrjQuotationCashflowHistory.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                if(dt2.getCfItem() != 90 && dt2.getCfItem() != 91){
                    hlsCusPrjCashflowHistoryService.insertSelective(requestContext, hlsCusPrjQuotationCashflowHistory);
                }else{
                    //对承兑汇票现金流单独处理
                    if(dt2.getCfItem() == 91){
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow2 = new HlsCusPrjQuotationCashflow();
                        hlsCusPrjQuotationCashflow2.setQuotationCashflowId(dt2.getSourceCashflowId());
                        hlsCusPrjQuotationCashflow2 = hlsCusPrjQuotationCashflowService.selectByPrimaryKey(requestContext,hlsCusPrjQuotationCashflow2);
                        JSONObject jsonCashflowDTO2 = (JSONObject) JSONObject.toJSON(hlsCusPrjQuotationCashflow2);
                        HlsCusPrjQuotationCashflowHistory hlsCusPrjQuotationCashflowHistory2 = jsonCashflowDTO2.toJavaObject(HlsCusPrjQuotationCashflowHistory.class);
                        hlsCusPrjQuotationCashflowHistory2.setQuotationCashflowId(null);
                        hlsCusPrjQuotationCashflowHistory2.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                        hlsCusPrjQuotationCashflowHistory2 = hlsCusPrjCashflowHistoryService.insertSelective(requestContext, hlsCusPrjQuotationCashflowHistory2);
                        hlsCusPrjQuotationCashflowHistory.setSourceCashflowId(hlsCusPrjQuotationCashflowHistory2.getQuotationCashflowId());
                        hlsCusPrjCashflowHistoryService.insertSelective(requestContext, hlsCusPrjQuotationCashflowHistory);
                    }
                }

            }

            //复制details
            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
            List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
            for(HlsCusPrjQuotationDetails dt3:hlsCusPrjQuotationDetailsList){
                JSONObject jsonDetailDTO = (JSONObject) JSONObject.toJSON(dt3);
                HlsCusPrjQuotationDetailsHistory hlsCusPrjQuotationDetailsHistory = jsonDetailDTO.toJavaObject(HlsCusPrjQuotationDetailsHistory.class);
                hlsCusPrjQuotationDetailsHistory.setQuotationDeatilId(null);
                hlsCusPrjQuotationDetailsHistory.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                hlsCusPrjQuotationDetailsHistoryService.insertSelective(requestContext,hlsCusPrjQuotationDetailsHistory);
            }

        }
    }

    private static String ADJUST_RATE_PMT_PRICE_LIST = "ADJUST_RATE_PMT";
    @Autowired
    private HlsCalcConfigMapper priceListMapper;

    @Autowired
    private HlsQuotationCalcService hlsQuotationCalcService;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsService prjQuotationDetailsService;

    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";


    @Override
    public Map<String, Object> calRateChange(IRequest requestContext, Long fltReqId, List<Long> quotationIds) throws Exception  {
        ResponseData answer = new ResponseData(true);
        Map<String, Object> response = new HashMap<String, Object>();
        HlsCusConFloatingRateReqLn param = new HlsCusConFloatingRateReqLn();
        param.setFltReqId(fltReqId);
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();

        //复制原来的报价到历史版本
        for(Long quotationId : quotationIds){
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setQuotationId(quotationId);
            HlsCusPrjQuotation cusPrjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(requestContext , hlsCusPrjQuotation);
            hlsCusPrjQuotationList.add(cusPrjQuotation);
        }
        copyQuotationVersion(requestContext , hlsCusPrjQuotationList );


        //找到对应的行
        List<HlsCusConFloatingRateReqLn> select = conFloatingRateReqLnMapper.select(param);
        if (CollectionUtils.isNotEmpty(quotationIds)) {
            //筛选出指定合同 ID
            select = select.stream().filter(con -> quotationIds.contains(con.getQuotationId())).collect(Collectors.toList());
            //logger.info(String.valueOf(select));
        }
        if (CollectionUtils.isEmpty(select)) {
            response.put("false" , "false");
            response.put("false" , "调息计算失败");
            return response;
        }
        StringBuilder infoMessage = new StringBuilder();
        String successFlag = "Y";
        for (HlsCusConFloatingRateReqLn reqLn : select) {
            //复制原来的quotation出来 , 保存新的quotationId
            HlsCusPrjQuotation hlsCusPrjQuotationOld = new HlsCusPrjQuotation();
            HlsCusPrjQuotation hlsCusPrjQuotationNew = new HlsCusPrjQuotation();
            HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
            quotation.setQuotationId(reqLn.getQuotationId());
            hlsCusPrjQuotationOld = hlsCusPrjQuotationService.selectByPrimaryKey(requestContext , quotation);
            Map<String, String> map2 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationOld);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationNew , map2);
            hlsCusPrjQuotationNew.setQuotationId(null);
            hlsCusPrjQuotationNew.setSourceDocumentCategory("CON_FLOATING_RATE_REQ");
            hlsCusPrjQuotationNew.setAltBaseRate(reqLn.getNewBaseRate());
            hlsCusPrjQuotationNew.setAltIntRate(reqLn.getNewIntRate());
            hlsCusPrjQuotationNew.setSourceDocumentId(reqLn.getContractId());
            hlsCusPrjQuotationNew.setNextAdjustmentDate(reqLn.getNextAdjustmentDate());
            hlsCusPrjQuotationNew.setPriceList(reqLn.getAdjustRatePrice());
            hlsCusPrjQuotationNew.setBaseRateCollect(reqLn.getNewBaseRateCollect());
            HlsCusPrjQuotation cusPrjQuotation = hlsCusPrjQuotationService.insertSelective(requestContext , hlsCusPrjQuotationNew );
            reqLn.setQuotationIdNew(cusPrjQuotation.getQuotationId());

            Long quotationIdOld = reqLn.getQuotationId();
            Long quotationIdNew = reqLn.getQuotationIdNew();
            Long contractId = reqLn.getContractId();

            logger.info( "quotationIdOld :" +quotationIdOld);
            logger.info( "quotationIdNew :" +quotationIdNew);

            //复制新的quotationDetatil
            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetailsOld = new HlsCusPrjQuotationDetails();
            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetailsNew = new HlsCusPrjQuotationDetails();
            HlsCusPrjQuotationDetails cusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            cusPrjQuotationDetails.setQuotationId(quotationIdOld);
            List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList =  hlsCusPrjQuotationDetailsMapper.queryDetailsById(cusPrjQuotationDetails);
            if(hlsCusPrjQuotationDetailsList.size()>0){
                hlsCusPrjQuotationDetailsOld = hlsCusPrjQuotationDetailsList.get(0);
                Map<String, String> map3 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationDetailsOld);
                hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationDetailsNew , map3);
                hlsCusPrjQuotationDetailsNew.setQuotationId(quotationIdNew);
                hlsCusPrjQuotationDetailsNew.setQuotationDeatilId(null);
                HlsCusPrjQuotationDetails prjQuotationDetails = hlsCusPrjQuotationDetailsService.insertSelective(requestContext , hlsCusPrjQuotationDetailsNew );
            }

            //获取 新的基准利率值  君成基准利率在创建调息时存入调息行表
//            String baseRateType = reqLn.getBaseRateType();
//            List<FndBaseRate> fndBaseRates = fndBaseRateMapper.selectByBaseRateType(baseRateType);
//            List<FndBaseRate> collect = (List)fndBaseRates.stream().sorted(Comparator.comparing(FndBaseRate::getValidFrom).reversed()).collect(Collectors.toList());
//            List<FndBaseRate> fndBaseRateList = new ArrayList<>();
//            HlsCusConContract  hlsCusConContract = conContractMapper.selectByPrimaryKey(reqLn.getContractId());
//            Date nextAdjustmentDate =  hlsCusConContract.getNextAdjustmentDate();
//            if(nextAdjustmentDate == null){
//                throw new IllegalArgumentException("未获取到租金变更日！");
//            }
//            for(FndBaseRate baseRate : collect){
//                if(baseRate.getValidFrom().compareTo(nextAdjustmentDate) <= 0 ){
//                    fndBaseRateList.add(baseRate);
//                }
//            }
//            List<FndBaseRate> baseRateList =  (List)fndBaseRateList.stream().sorted(Comparator.comparing(FndBaseRate::getValidFrom).reversed()).collect(Collectors.toList());
//            FndBaseRate fndBaseRate = baseRateList.get(0);
//            Double baseRate =  fndBaseRate.getBaseRate();
//            reqLn.setNewBaseRate(baseRate);
//            Double percent = 10000D;
//            Double floatWayRate = MathUtil.div(hlsCusPrjQuotationOld.getFloatingWayRate() , percent);

//            Double newIntRate =MathUtil.add(baseRate , floatWayRate) ;
//            reqLn.setNewIntRate(newIntRate);
              Double newIntRate = reqLn.getNewIntRate();

            //调息生效日-nextadjustment ; 新基准利率值 - newBaseRate ; ，新合同年利率=新基准利率+bp 值 ;
            //计算逻辑 - 传新的quotation_id , 新的基准利率值 , 现金流行 进行计算

            String etPriceList = reqLn.getAdjustRatePrice();
            if(etPriceList != null){
                //构造一个空的workbook
                HlsCalcConfig config = priceListMapper.selectByPrimaryKey(etPriceList);
                String sheets = hlsQuotationCalcService.unzipSheet(config.getSheets());
                JSONArray array = JSONArray.parseArray(sheets);
                XSSFWorkbook wb = new XSSFWorkbook();
                hlsQuotationCalcService.readSheets(wb, array);

                //构造头需要赋值的字段
                HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setQuotationId(quotationIdNew);
                HlsCusPrjQuotation quotationLn =  hlsCusPrjQuotationMapper.queryRateReqCalc(hlsCusPrjQuotation).get(0);
                quotationLn.setNextAdjustmentRate(newIntRate);


                //excel头配置需要的
                //调息日更换 剩余本金
                quotationLn.setChangeDate(quotationLn.getNextAdjustmentDate());
//                quotationLn.setResidualPrinAtCd(quotationLn.getResidualPrinAtCdNew());
//                JSONArray modifiedHd = hlsQuotationCalcService.extractHeadDataFromObject(quotationLn, etPriceList, "INPUT");

                //构造行需要赋值的字段 , 现金流 -- cf_item = 1 /10 ,根据times , due_date ,cf_item 排序
                HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                contractCashflow.setQuotationId(quotationIdOld);
                contractCashflow.setContractId(contractId);
                List<HlsCusConContractCashflow> conContractCashflowList = conContractCashflowMapper.queryCashflowCalcPMT(contractCashflow) ;



                for(int i = 0; i < conContractCashflowList.size(); i++){
                    HlsCusConContractCashflow hlsCusConContractCashflow = conContractCashflowList.get(i);
                    if(hlsCusConContractCashflow.getDueDate().equals(quotationLn.getNextAdjustmentDate())){
                        quotationLn.setChangeDate(conContractCashflowList.get(i+1).getDueDate());
                        break;
                    }
                }
                quotationLn.setResidualPrinAtCd(quotationLn.getResidualPrinAtCdNew());
                JSONArray modifiedHd = hlsQuotationCalcService.extractHeadDataFromObject(quotationLn, etPriceList, "INPUT");


                JSONArray modifiedLines = hlsQuotationCalcService.extractLineDataFromObject(conContractCashflowList, etPriceList, "INPUT");

                //将字段赋值到sheet
                hlsQuotationCalcService.updateSheet(modifiedHd, wb.getSheet("INPUT"), etPriceList);
                hlsQuotationCalcService.updateSheetLines(modifiedLines, wb.getSheet("INPUT"), etPriceList);
                hlsQuotationCalcService.writeBack(wb, array, etPriceList);

                //回写prj_quotation 相关表
                updateQuotationFromSheet(requestContext, quotationIdNew, etPriceList, array);
                //更新 报价
                HlsCusPrjQuotation etPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationIdNew);
                HlsCusPrjQuotationDetails etPrjQuotationDetail = new HlsCusPrjQuotationDetails();
                etPrjQuotationDetail.setQuotationId(quotationIdNew);
                List<HlsCusPrjQuotationDetails> etPrjQuotationDetailList = hlsCusPrjQuotationDetailsMapper.select(etPrjQuotationDetail);
                etPrjQuotationDetail = etPrjQuotationDetailList.get(0);
                etPrjQuotationDetail.setSheets(hlsQuotationCalcService.getCompressSheets(JSON.toJSONString(array)));
                prjQuotationDetailsService.updateByPrimaryKeySelective(requestContext, etPrjQuotationDetail);

                //回写prj_quotation_cashflow
                etPrjQuotation.setSheets(JSON.toJSONString(array));
                etPrjQuotation.setCompressSheets(etPrjQuotationDetail.getSheets());

                HlsCalcConfig calcConfig = new HlsCalcConfig();
                calcConfig.setPriceList("TEST");
                calcConfig.setSheets(etPrjQuotationDetail.getSheets());
                priceListMapper.updateByPrimaryKeySelective(calcConfig);

                if(quotationLn.getLprLinkDate() != null){
                    reqLn.setLprLinkDate(quotationLn.getLprLinkDate() );
                }
                if(quotationLn.getNextAdjustmentDate() != null){
                    reqLn.setNextAdjustmentDate(quotationLn.getNextAdjustmentDate());
                }
                reqLn.setStatus(CALCULATED);
                reqLn.setMessage("操作成功");
                conFloatingRateReqLnMapper.updateByPrimaryKey(reqLn);

                try {
                    hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(requestContext, etPrjQuotation);
                    hlsCusPrjQuotationService.updateXirr(requestContext,etPrjQuotation);
                } catch (Exception e) {
                    reqLn.setStatus(FAILURE);
                    reqLn.setMessage(e.getMessage());
                    conFloatingRateReqLnMapper.updateByPrimaryKey(reqLn);
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setContractId(reqLn.getContractId() );
                    HlsCusConContract cusConContract = conContractMapper.selectByPrimaryKey(conContract);
                    logger.info("error when calculating rate Change ,contractID = " +  reqLn.getContractId()  +  e.getMessage());
                    successFlag = "N";
                    infoMessage.append("合同编号为 ： " + cusConContract.getContractNumber() + "," +  e.getMessage() + "；");
                }


            }

        }

        if("N".equalsIgnoreCase(successFlag)){
            response.put("success", false);
            response.put("message", infoMessage);
        }else{
            response.put("success", true);
            response.put("message", "调息计算成功");
        }

        return response;
    }
    //回写报价表
    public void updateQuotationFromSheet(IRequest iRequest, Long quotationId, String priceList, JSONArray array) throws Exception {
        //回写prj_quotation
        HlsCusPrjQuotation prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationId);
        String jsonStr = JSON.toJSONString(hlsCusCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, JSON.toJSONString(array), priceList, "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);

        String sheets = JSON.toJSONString(array);
        String compressSheets = getCompressSheets(sheets);

        prjQuotationDto.setPriceList(priceList);
        prjQuotationDto.setSheets(sheets);
        prjQuotationDto.setCompressSheets(compressSheets);
        prjQuotationDto.setSourceDocumentCategory(prjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setSourceDocumentId(prjQuotation.getSourceDocumentId());
        prjQuotationDto.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationDto.setDataClass("CONTRACT_PLAN");
        prjQuotationDto.setLeaseTimes(prjQuotation.getAltLeaseTimes());
        prjQuotationDto.setStatus("NEW");
        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        //回写prj_quotation_details
        HlsCusPrjQuotationDetails prjQuotationDetail = new HlsCusPrjQuotationDetails();
        prjQuotationDetail.setQuotationId(quotationId);
        List<HlsCusPrjQuotationDetails> prjQuotationDetailList = hlsCusPrjQuotationDetailsMapper.select(prjQuotationDetail);
        prjQuotationDetail = prjQuotationDetailList.get(0);
        prjQuotationDetail.setSheets(compressSheets);
        prjQuotationDetailsService.updateByPrimaryKeySelective(iRequest, prjQuotationDetail);


        HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setPriceList("TEST");
        calcConfig.setSheets(compressSheets);
        priceListMapper.updateByPrimaryKeySelective(calcConfig);

        //回写prj_quotation_cashflow
        prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationId);
        prjQuotation.setSheets(sheets);
        prjQuotation.setCompressSheets(compressSheets);
        hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotation);
    }

    private static String getHex(byte buf[]) {
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (int i = 0; i < buf.length; i++) {
            int n = (int) buf[i] & 0xff;
            o.append("%");
            if (n < 0x10) {
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }

    public static String encodeURIComponent(String input) {
        if (null == input || "".equals(input.trim())) {
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        try {
            for (int i = 0; i < l; i++) {
                String e = input.substring(i, i + 1);
                if (ALLOWED_CHARS.indexOf(e) == -1) {
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return input;
    }
    /**
     * @Title: getCompressSheets
     * @Discription: excel sheet 压缩
     * @Param: [array]
     * @Return: java.lang.String
     */
    public String getCompressSheets(String array) throws UnsupportedEncodingException {
        String sheetsArray = encodeURIComponent((array));
        String zipSheets = new String(GzipUtil.compress(sheetsArray), "iso-8859-1");
        String compressSheets = GzipUtil.btoa(zipSheets);
        return compressSheets;
    }



}

