package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqLnMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqLnService;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.*;
import com.hand.hls.fin.mapper.HlsCusLonContractAttachmentMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractRepaymentMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fin.service.HlsCusCtLonContractBankAccountService;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.fnd.dto.FndBaseRate;
import com.hand.hls.fnd.dto.FndBaseRateSet;
import com.hand.hls.fnd.mapper.FndBaseRateSetMapper;
import com.hand.hls.fnd.service.FndBaseRateService;
import com.hand.hls.fnd.service.FndBaseRateSetService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.mapper.QuotationSubsectionMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConFloatingRateReqLnServiceImpl extends BaseServiceImpl<HlsCusConFloatingRateReqLn> implements HlsCusConFloatingRateReqLnService {

    public static final String FLOATING = "FLOATING";//浮动利率类型
    public static final String UN_CALCULATED = "UN_CALCULATED";//未计算
    public static final String CON_CONTRACT = "con_contract";
    public static final String PENDING = "PENDING";
    public static final String DOCUMENT_CATEGORY = "CON_FLOATING_RATE_REQ";
    public static final String CON_DOCUMENT_TYPE = "CON_CONTRACT";
    public static final String LON_CON_DOCUMENT_TYPE = "LON_CONTRACT";
    public static final String SOURCE_DOCUMENT_CATEGORY = "PRJ_PROJECT";
    /**
     * 还租频率
     * "MONTH" 月付
     * "QUARTER" 季付
     * "HALF_A_YEAR" 半年付
     * "YEAR" 年付
     */
    private static final String MONTH = "MONTH";
    private static final String QUARTER = "QUARTER";
    private static final String HALF_A_YEAR = "HALF_A_YEAR";
    private static final String YEAR = "YEAR";
    //public static final String BUSINESS_TYPE = "CON_FLOATING_RATE_REQ";

    @Autowired
    private HlsCusConFloatingRateReqLnMapper mapper;@Autowired
    private HlsCusConFloatingRateReqMapper hlsCusConFloatingRateReqMapper;

    @Autowired
    private HlsCusConFloatingRateReqService conFloatingRateReqService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;

    @Autowired
    private HlsCusLonContractRepaymentMapper lonContractRepaymentMapper;

    @Autowired
    private FndBaseRateSetMapper fndBaseRateSetMapper;

    @Autowired
    private FndBaseRateService fndBaseRateService;

    @Autowired
    private FndBaseRateSetService fndBaseRateSetService;

    @Autowired
    private HlsCusLonContractWithdrawService lonContractWithdrawService;

    @Autowired
    private HlsCusLonContractRepaymentService lonContractRepaymentService;

    @Autowired
    private HlsCusCtLonContractBankAccountService hlsCusCtLonContractBankAccountService;

    @Autowired
    private HlsCusLonContractAttachmentMapper hlsCusLonContractAttachmentMapper;

    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;

    @Autowired
    private IFndAttachmentMultiService iFndAttachmentMultiService;

    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IFndAttachmentService iFndAttachmentService;


    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;

    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsCusLonContractQuotationService hlsCusLonContractQuotationService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private QuotationSubsectionMapper quotationSubsectionMapper;

    /*调息明细页面-调息列表查询*/
    @Override
    public List<HlsCusConFloatingRateReqLn> rateChangeListQuery(IRequest irequest, HlsCusConFloatingRateReq dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.ctRateChangeListQuery(dto);
    }

    /*调息单据明细页面*/
    @Override
    public HlsCusConFloatingRateReqLn rateChangeDetailQuery(IRequest irequest, HlsCusConFloatingRateReqLn dto) {
        return mapper.ctRateChangeDetailQuery(dto);
    }

    /*调息明细页面-调息总体信息查询*/
    @Override
    public HlsCusConFloatingRateReqLn ctRateChangeTotalQuery(IRequest irequest, HlsCusConFloatingRateReq dto) {
        List<HlsCusConFloatingRateReqLn> list = mapper.ctRateChangeListQuery(dto);
        HlsCusConFloatingRateReqLn result = new HlsCusConFloatingRateReqLn();
        Double amount = 0D;
        for (HlsCusConFloatingRateReqLn item : list) {
            if (item.getInterestAdjAmount() != null) {
                amount = CalculateUtil.add(amount, item.getInterestAdjAmount());
            }
        }
        result.setInterestAdjAmountSum(amount);
        result.setContractCount(list.size());
        return result;
    }

    /*调息明细页面-删除待调息记录*/
    @Override
    public void deleteFltReqLn(IRequest requestContext, List<HlsCusConFloatingRateReqLn> conFloatingRateReqLnList) {
        HlsCusConFloatingRateReqLn conFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        conFloatingRateReqLn.setFltReqLnId(conFloatingRateReqLnList.get(0).getFltReqLnId());
        conFloatingRateReqLn = self().selectByPrimaryKey(requestContext, conFloatingRateReqLn);

        HlsCusConFloatingRateReq conFloatingRateReq = new HlsCusConFloatingRateReq();
        conFloatingRateReq.setFltReqId(conFloatingRateReqLn.getFltReqId());
        conFloatingRateReq = conFloatingRateReqService.selectByPrimaryKey(requestContext, conFloatingRateReq);

        for (HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn : conFloatingRateReqLnList
        ) {
            HlsCusConFloatingRateReqLn cusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
            cusConFloatingRateReqLn.setFltReqLnId(hlsCusConFloatingRateReqLn.getFltReqLnId());
            cusConFloatingRateReqLn = self().selectByPrimaryKey(requestContext, cusConFloatingRateReqLn);

            //融资调息
            if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(conFloatingRateReq.getDocumentType())) {
                HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
                lonContractWithdraw.setWithdrawId(cusConFloatingRateReqLn.getContractId());
                lonContractWithdraw = lonContractWithdrawService.selectByPrimaryKey(requestContext, lonContractWithdraw);

                lonContractWithdraw.set__status("update");
                lonContractWithdraw.setWithdrawStatus("APPROVED");
                lonContractWithdrawService.updateByPrimaryKeySelective(requestContext, lonContractWithdraw);
            }
            //租赁支付表调息
            if (CON_DOCUMENT_TYPE.equalsIgnoreCase(conFloatingRateReq.getDocumentType())) {
                HlsCusConContract conContract = new HlsCusConContract();
                conContract.setContractId(cusConFloatingRateReqLn.getContractId());
                conContract = hlsCusConContractService.selectByPrimaryKey(requestContext, conContract);

                conContract.set__status("update");
                conContract.setContractStatus("INCEPT");
                hlsCusConContractService.updateByPrimaryKeySelective(requestContext, conContract);
            }

            self().deleteByPrimaryKey(cusConFloatingRateReqLn);
        }
    }

    /**
     * 创建调息申请
     *
     * @param request
     * @param floatingRateReq
     */
    @Override
    public HlsCusConFloatingRateReq createFloatingRateChange(IRequest request, HlsCusConFloatingRateReq floatingRateReq) throws HlsCusException {
        //插入申请头表
        floatingRateReq.setStatus("NEW");//状态
        floatingRateReq.setFltReqDate(new Date());//日期
        floatingRateReq.setFltReqUserId(request.getUserId());//人员
        floatingRateReq.setDocumentCategory("CON_FLOATING_RATE_REQ");
        Map<String, String> params = new HashMap<String, String>();
        if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(floatingRateReq.getDocumentType())) {
            floatingRateReq.setBusinessType("CON_FLOATING_RATE_REQ");
        }
        if (CON_DOCUMENT_TYPE.equalsIgnoreCase(floatingRateReq.getDocumentType())) {
            floatingRateReq.setBusinessType("LON_CON_FLT_REQ");
        }
        //floatingRateReq.setFltReqNumber(fndCodingRuleValuesService.getCodeRuleValue(request, DOCUMENT_CATEGORY, floatingRateReq.getDocumentType(), floatingRateReq.getBusinessType(), params));//生成单据编号
        floatingRateReq.setFltReqNumber("BAT201910240030");//生成单据编号

        HlsCusConFloatingRateReq hlsCusConFloatingRateReq = conFloatingRateReqService.insertSelective(request, floatingRateReq);

        //融资调息
        if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(hlsCusConFloatingRateReq.getDocumentType())) {
            insertLonContractFltLnReq(hlsCusConFloatingRateReq, request);
        }
        //租赁支付表调息
        if (CON_DOCUMENT_TYPE.equalsIgnoreCase(hlsCusConFloatingRateReq.getDocumentType())) {
            insertConContractFltLnReq(hlsCusConFloatingRateReq, request);
        }
        return hlsCusConFloatingRateReq;
    }

    //匹配融资合同(提款)插入待调息列表
    public void insertLonContractFltLnReq(HlsCusConFloatingRateReq hlsCusConFloatingRateReq, IRequest request) throws HlsCusException {
        List<HlsCusLonContractWithdraw> lonContractWithdrawList = lonContractWithdrawMapper.selectLonConWithdrawFlt();
        Calendar startDateCal = Calendar.getInstance();
        Calendar endDateCal = Calendar.getInstance();
        int lonMonth;
        Double newBaseRate;
        hlsCusConFloatingRateReq = conFloatingRateReqService.selectByPrimaryKey(request, hlsCusConFloatingRateReq);

        FndBaseRateSet fndBaseRateSet = new FndBaseRateSet();
        fndBaseRateSet.setBaseRateSet(hlsCusConFloatingRateReq.getBaseRateSet());
        fndBaseRateSet = fndBaseRateSetService.selectByPrimaryKey(request, fndBaseRateSet);
        if (lonContractWithdrawList.size() > 0) {
            for (HlsCusLonContractWithdraw lonContractWithdraw : lonContractWithdrawList
            ) {
                startDateCal.setTime(lonContractWithdraw.getDueDate());
                endDateCal.setTime(lonContractWithdraw.getWithdrawEndDate());
                lonMonth = endDateCal.get(Calendar.MONTH) - startDateCal.get(Calendar.MONTH)
                        + (endDateCal.get(Calendar.YEAR) - startDateCal.get(Calendar.YEAR)) * 12;

                newBaseRate = getBaseRate(request, hlsCusConFloatingRateReq.getBaseRateSet(), lonMonth);
                if (!newBaseRate.equals(lonContractWithdraw.getBaseRate())) {
                    Long copyWithdrawId = copyWithdrawRepayment(request, lonContractWithdraw, "CHANGE_REQ", "", null);
                    HlsCusConFloatingRateReqLn floatingRateReqLn = new HlsCusConFloatingRateReqLn();
                    floatingRateReqLn.setContractId(lonContractWithdraw.getWithdrawId());
                    floatingRateReqLn.setFltReqId(hlsCusConFloatingRateReq.getFltReqId());
                    floatingRateReqLn.setBaseRateType(lonContractWithdraw.getBaseRateType());
                    floatingRateReqLn.setOldBaseRate(lonContractWithdraw.getBaseRate());
                    floatingRateReqLn.setNewBaseRate(newBaseRate);
                    floatingRateReqLn.setFloatingRangeMethod(lonContractWithdraw.getFloatingRangeMethod());
                    floatingRateReqLn.setStatus(UN_CALCULATED);
                    floatingRateReqLn.setChangeReqId(copyWithdrawId);
                    self().insertSelective(request, floatingRateReqLn);
                    //获取调息开始日
                    Date fltStartDate = getFltStartDate(request, hlsCusConFloatingRateReq.getDocumentType(), fndBaseRateSet.getValidFrom(), lonContractWithdraw.getFloatingRangeMethod(), lonContractWithdraw.getWithdrawId());

                    //提款置为暂挂状态
                    lonContractWithdraw.set__status("update");
                    lonContractWithdraw.setWithdrawStatus("PENDING");
                    lonContractWithdraw.setFloatingRateStartDate(fltStartDate);//更新除手工调整外的调息开始日期
                    lonContractWithdrawService.updateByPrimaryKeySelective(request, lonContractWithdraw);
                }
            }
        }
    }

    //匹配租赁合同(提款)插入待调息列表
    public void insertConContractFltLnReq(HlsCusConFloatingRateReq hlsCusConFloatingRateReq, IRequest request) {
        List<HlsCusConContract> conContractList = hlsCusConContractMapper.selectConContractFlt();
        int conMonth;
        Double newBaseRate;

        if (conContractList.size() > 0) {
            for (HlsCusConContract conContract : conContractList) {
                conMonth = conContract.getLeaseTerm().intValue();//获取总的月数
                newBaseRate = getBaseRate(request, hlsCusConFloatingRateReq.getBaseRateSet(), conMonth);
                if (newBaseRate != conContract.getBaseRate()) {
                    Long copyContractId = copyContractRepayment(request, conContract, "CHANGE_REQ", "", null);
                    HlsCusConFloatingRateReqLn floatingRateReqLn = new HlsCusConFloatingRateReqLn();
                    floatingRateReqLn.setContractId(conContract.getContractId());
                    floatingRateReqLn.setFltReqId(hlsCusConFloatingRateReq.getFltReqId());
                    floatingRateReqLn.setBaseRateType(conContract.getBaseRateType());
                    floatingRateReqLn.setOldBaseRate(conContract.getBaseRate());
                    floatingRateReqLn.setNewBaseRate(newBaseRate);
                    floatingRateReqLn.setFloatingRangeMethod(conContract.getFloatingRangeMethod());
                    floatingRateReqLn.setStatus(UN_CALCULATED);
                    floatingRateReqLn.setChangeReqId(copyContractId);
                    self().insertSelective(request, floatingRateReqLn);

                    //提款置为暂挂状态
                    conContract.set__status("update");
                    conContract.setContractStatus("PENDING");
                    hlsCusConContractService.updateByPrimaryKeySelective(request, conContract);
                }
            }
        }
    }


    //复制融资提款以及提款项下还款计划
    @Override
    public Long copyWithdrawRepayment(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, String usageCode, String historyReason, Long changeReqId) throws HlsCusException {
        Long sourceWithdrawId = lonContractWithdraw.getWithdrawId();


        //复制提款
        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
        Map<String, String> mapWithdrawNormal = hlsBeanRefUtilService.getFieldValueMap(lonContractWithdraw);
        hlsBeanRefUtilService.setFieldValue(hlsCusLonContractWithdraw, mapWithdrawNormal);
        hlsCusLonContractWithdraw.set__status("add");
        hlsCusLonContractWithdraw.setDataClass(usageCode);
        if ("HISTORY".equalsIgnoreCase(usageCode)) {
            hlsCusLonContractWithdraw.setHistoryReason(historyReason);
        }
        hlsCusLonContractWithdraw.setChangeReqId(changeReqId);
        hlsCusLonContractWithdraw.setWithdrawId(null);
        hlsCusLonContractWithdraw = lonContractWithdrawService.insertSelective(request, hlsCusLonContractWithdraw);

        Long copyWithdrawId = hlsCusLonContractWithdraw.getWithdrawId();

        HlsCusLonContractQuotation sourceLonContractQuotation = new HlsCusLonContractQuotation();
        sourceLonContractQuotation.setWithdrawId(sourceWithdrawId);
        //一对一关系 所以只会有一条数据 数据库层有唯一性校验
        List<HlsCusLonContractQuotation> cusLonContractQuotations = hlsCusLonContractQuotationService.select(request, sourceLonContractQuotation, 1, 1);
        if (cusLonContractQuotations.size() > 0) {
            //复制生成提款的交易信息
            HlsCusLonContractQuotation lonContractQuotationCopy = new HlsCusLonContractQuotation();
            Map<String, String> mapQuotationNormal = hlsBeanRefUtilService.getFieldValueMap(cusLonContractQuotations.get(0));
            hlsBeanRefUtilService.setFieldValue(lonContractQuotationCopy, mapQuotationNormal);
            lonContractQuotationCopy.setWithdrawId(copyWithdrawId);
            lonContractQuotationCopy.setQuotationId(null);
            hlsCusLonContractQuotationService.insertSelective(request, lonContractQuotationCopy);
        }
        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
        lonContractRepayment.setWithdrawId(sourceWithdrawId);
        List<HlsCusLonContractRepayment> lonContractRepaymentList = lonContractRepaymentService.select(request, lonContractRepayment, 1, 999999);
        //复制提款项下还款计划
        if (lonContractRepaymentList.size() > 0) {
            for (HlsCusLonContractRepayment lonContractRepaymentSource : lonContractRepaymentList
            ) {
                HlsCusLonContractRepayment lonContractRepaymentCopy = new HlsCusLonContractRepayment();
                Map<String, String> mapRepaymentNormal = hlsBeanRefUtilService.getFieldValueMap(lonContractRepaymentSource);
                hlsBeanRefUtilService.setFieldValue(lonContractRepaymentCopy, mapRepaymentNormal);
                lonContractRepaymentCopy.set__status("add");
                lonContractRepaymentCopy.setWithdrawId(copyWithdrawId);
                lonContractRepaymentCopy.setRepaymentId(null);
                lonContractRepaymentService.insertSelective(request, lonContractRepaymentCopy);
            }
        }


        //复制提款下的账户信息
        HlsCusCtLonContractBankAccount lonContractBankAccount = new HlsCusCtLonContractBankAccount();
        lonContractBankAccount.setWithdrawId(sourceWithdrawId);
        List<HlsCusCtLonContractBankAccount> bankAccountList = hlsCusCtLonContractBankAccountService.select(request, lonContractBankAccount, 1, 999999);
        if (bankAccountList.size() > 0) {
            for (HlsCusCtLonContractBankAccount lonContractBankAccountSource : bankAccountList) {
                HlsCusCtLonContractBankAccount lonContractBankAccountCopy = new HlsCusCtLonContractBankAccount();
                Map<String, String> mapRepaymentNormal = hlsBeanRefUtilService.getFieldValueMap(lonContractBankAccountSource);
                hlsBeanRefUtilService.setFieldValue(lonContractBankAccountCopy, mapRepaymentNormal);
                lonContractBankAccountCopy.set__status("add");
                lonContractBankAccountCopy.setWithdrawId(copyWithdrawId);
                hlsCusCtLonContractBankAccountService.insertSelective(request, lonContractBankAccountCopy);
            }
        }

        //复制提款下的附件信息
        HlsCusLonContractAttachment hlsCusLonContractAttachment = new HlsCusLonContractAttachment();
        hlsCusLonContractAttachment.setSourceId(sourceWithdrawId);
        List<HlsCusLonContractAttachment> hlsCusLonContractAttachmentList = hlsCusLonContractAttachmentMapper.select(hlsCusLonContractAttachment);
        if (hlsCusLonContractAttachmentList.size() > 0) {
            for (HlsCusLonContractAttachment dt : hlsCusLonContractAttachmentList) {
                HlsCusLonContractAttachment lonContractAttachmentCopy = new HlsCusLonContractAttachment();
                Map<String, String> mapAttachmentNormal = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(lonContractAttachmentCopy, mapAttachmentNormal);
                lonContractAttachmentCopy.set__status("add");
                lonContractAttachmentCopy.setSourceId(copyWithdrawId);
                hlsCusLonContractAttachmentMapper.insertSelective(lonContractAttachmentCopy);
                //复制系统附件表
                copyFndAtmFile(request, String.valueOf(dt.getContractAttachmentId()), String.valueOf(lonContractAttachmentCopy.getContractAttachmentId()), "LON_CONTRACT_ATTACHMENT");
            }
        }


        return copyWithdrawId;
    }

    //复制系统附件表
    void copyFndAtmFile(IRequest iRequest,String oldPkValue,String newPkValue,String tableName) throws HlsCusException {

        if(StringUtils.isEmpty(oldPkValue) || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(newPkValue)){
            throw new HlsCusException("数据异常，请联系管理员!");
        }

        //复制fnd_atm_attachment_multi
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName(tableName);
        fndAttachmentMulti.setTablePkValue(oldPkValue);

        List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);
        if(CollectionUtils.isNotEmpty(multiList)){
            for(FndAttachmentMulti multi : multiList){

                FndAttachmentMulti attachmentMulti = new FndAttachmentMulti();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(multi);

                hlsBeanRefUtilService.setFieldValue(attachmentMulti, map);
                attachmentMulti.setTablePkValue(newPkValue);
                iFndAttachmentMultiService.insertSelective(iRequest, attachmentMulti);

                //复制fnd_atm_attachment
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(multi.getAttachmentId());
                fndAttachment = fndAttachmentMapper.selectByPrimaryKey(fndAttachment);

                fndAttachment.setAttachmentId(attachmentMulti.getAttachmentId());
                fndAttachment.setAttachmentId(null);
                iFndAttachmentService.insert(iRequest,fndAttachment);

                attachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
                iFndAttachmentMultiService.updateByPrimaryKeySelective(iRequest,attachmentMulti);
            }
        }

    }

    //复制租赁租金支付表以及报价项下付款计划
    public Long copyContractRepayment(IRequest request, HlsCusConContract conContract, String usageCode, String historyReason, Long changeReqId) {
        Long sourceContractId = conContract.getContractId();

        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        conContractCashflow.setContractId(sourceContractId);
        List<HlsCusConContractCashflow> conContractCashflows = hlsCusConContractCashflowMapper.select(conContractCashflow);


        HlsCusConContract copyConContract = new HlsCusConContract();
        Map<String, String> mapCon = hlsBeanRefUtilService.getFieldValueMap(conContract);
        hlsBeanRefUtilService.setFieldValue(copyConContract, mapCon);
        copyConContract.set__status("add");
        copyConContract.setDataClass(usageCode);
        if ("HISTORY".equalsIgnoreCase(usageCode)) {
            copyConContract.setHistoryReason(historyReason);
            copyConContract.setRefContractId(changeReqId);
        }
        copyConContract.setContractId(null);
        copyConContract.setContractStatus("INCEPT");
        copyConContract = hlsCusConContractService.insertSelective(request, copyConContract);

        Long copyConContractId = copyConContract.getContractId();


        if (conContractCashflows.size() > 0) {
            for (HlsCusConContractCashflow dt : conContractCashflows) {
                HlsCusConContractCashflow newConContractCash = new HlsCusConContractCashflow();
                Map<String, String> mapOld = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(newConContractCash, mapOld);
                newConContractCash.set__status("add");
                newConContractCash.setContractId(copyConContractId);
                newConContractCash.setCashflowId(null);
                hlsCusConContractCashflowService.insertSelective(request, newConContractCash);
            }
        }

        return copyConContractId;
    }

    /**
     * 进行调息计算
     *
     * @param request
     * @param cusConFloatingRateReq
     */
   /* @Override
    public void floatingRate(IRequest request, HlsCusConFloatingRateReq cusConFloatingRateReq) {
        cusConFloatingRateReq = conFloatingRateReqService.selectByPrimaryKey(request, cusConFloatingRateReq);

        FndBaseRateSet fndBaseRateSet = new FndBaseRateSet();
        fndBaseRateSet.setBase_rate_set(cusConFloatingRateReq.getBaseRateSet());
        fndBaseRateSet = fndBaseRateSetService.selectByPrimaryKey(request, fndBaseRateSet);

        HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        hlsCusConFloatingRateReqLn.setFltReqId(cusConFloatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> hlsCusConFloatingRateReqLnList = self().select(request, hlsCusConFloatingRateReqLn, 1, 999999);

        //循环待调息列表
        if (hlsCusConFloatingRateReqLnList.size() > 0) {
            //融资提款调息调用
            if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(cusConFloatingRateReq.getDocumentType())) {
                lonConWithdrawFltCalc(request, cusConFloatingRateReq.getDocumentType(), fndBaseRateSet.getValid_from(), hlsCusConFloatingRateReqLnList);
            }
            //租赁支付表调息调用
            if (CON_DOCUMENT_TYPE.equalsIgnoreCase(cusConFloatingRateReq.getDocumentType())) {
                conContractCshFltCalc(request, cusConFloatingRateReq.getDocumentType(), fndBaseRateSet.getValid_from(), hlsCusConFloatingRateReqLnList);
            }
        }
    }
*/
    //融资合同调息算法入口
    /*public void lonConWithdrawFltCalc(IRequest request, String documentType, Date validFrom, List<HlsCusConFloatingRateReqLn> hlsCusConFloatingRateReqLnList) {
        for (HlsCusConFloatingRateReqLn conFloatingRateReqLn : hlsCusConFloatingRateReqLnList
                ) {
            //获取提款单据
            HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
            hlsCusLonContractWithdraw.setWithdrawId(conFloatingRateReqLn.getChangeReqId());
            hlsCusLonContractWithdraw = lonContractWithdrawService.selectByPrimaryKey(request, hlsCusLonContractWithdraw);

            Calendar startDateCal = Calendar.getInstance();
            Calendar endDateCal = Calendar.getInstance();
            startDateCal.setTime(hlsCusLonContractWithdraw.getDueDate());
            endDateCal.setTime(hlsCusLonContractWithdraw.getWithdrawEndDate());
            int lonMonth;
            lonMonth = endDateCal.get(Calendar.MONTH) - startDateCal.get(Calendar.MONTH)
                    + (endDateCal.get(Calendar.YEAR) - startDateCal.get(Calendar.YEAR)) * 12;
            Double intRateNew = calcIntRateByBaseRate(conFloatingRateReqLn.getNewBaseRate(), hlsCusLonContractWithdraw.getFloatingWay(), hlsCusLonContractWithdraw.getFloatingWayRange());

            //获取调息开始日
            //  Date fltStartDate = getFltStartDate(request, documentType, validFrom, hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId());
            Date fltStartDate = hlsCusLonContractWithdraw.getFloatingRateStartDate();
            //获取调息开始期数
            Long times = getFltStartTimes(request, documentType, fltStartDate, hlsCusLonContractWithdraw.getWithdrawId());

            //循环所有还息计划
            HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
            hlsCusLonContractRepayment.setCfItem(302L);
            hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
            List<HlsCusLonContractRepayment> lonContractRepaymentList = lonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);
            Date lastRepaymentDate = hlsCusLonContractWithdraw.getDueDate();
            Double oldSumRepaymentAmount = 0D;
            Double newSumRepaymentAmount = 0D;
            hlsCusLonContractRepayment.setWithdrawId(conFloatingRateReqLn.getContractId());
            List<HlsCusLonContractRepayment> oldLonContractRepaymentList = lonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);
            for (HlsCusLonContractRepayment oldLonContractRepayment : oldLonContractRepaymentList
                    ) {
                oldSumRepaymentAmount = oldSumRepaymentAmount + oldLonContractRepayment.getPlannedDueAmount();
            }

            for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList
                    ) {
                Double repaymentAmount = 0D;

                //已确认的还息计划不做调整
                if ("N".equalsIgnoreCase(lonContractRepayment.getConfirmFlag())) {
                    if (lonContractRepayment.getTimes() == times) {
                        List<FndBaseRateSet> fndBaseRateSetList = fndBaseRateSetMapper.selectAll();
                        Date lastValidFrom = fndBaseRateSetList.get(0).getValid_from();
                        Double baseRate = getBaseRate(request, fndBaseRateSetList.get(0).getBase_rate_set(), lonMonth);
                        Double intRate = calcIntRateByBaseRate(baseRate, hlsCusLonContractWithdraw.getFloatingWay(), hlsCusLonContractWithdraw.getFloatingWayRange());
                        int i = 0;
                        for (FndBaseRateSet fndBaseRateSet : fndBaseRateSetList) {
                            if (getFltStartDate(request, documentType, fndBaseRateSet.getValid_from(), hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId()).getTime() <= getFltStartDate(request, documentType, validFrom, hlsCusLonContractWithdraw.getFloatingRangeMethod()
                                    , hlsCusLonContractWithdraw.getWithdrawId()).getTime() && getFltStartDate(request, documentType, fndBaseRateSet.getValid_from(),
                                    hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId()).getTime() >= lastRepaymentDate.getTime()) {
//                                if (validFrom.compareTo(fndBaseRateSet.getValid_from()) <= 0) {
//                                    continue;
//                                }
                                if (i == 0) {
                                    repaymentAmount = repaymentAmount + lonContractWithdrawService.getRepaymentAmount(request, lastRepaymentDate,
                                            getFltStartDate(request, documentType, fndBaseRateSet.getValid_from(), hlsCusLonContractWithdraw.getFloatingRangeMethod()
                                                    , hlsCusLonContractWithdraw.getWithdrawId()), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), intRate, hlsCusLonContractWithdraw.getCalcInterestYearDays(), null);
                                } else {
//                                    if (getFltStartDate(request, documentType, lastValidFrom, hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId()).compareTo(getFltStartDate(request, documentType, fndBaseRateSet.getValid_from(), hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId())) == 0) {
//                                        repaymentAmount = lonContractWithdrawService.getRepaymentAmount(request, lastRepaymentDate,
//                                                getFltStartDate(request, documentType, fndBaseRateSet.getValid_from(), hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId()), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), intRate, hlsCusLonContractWithdraw.getCalcInterestYearDays());
//                                    } else {
                                    repaymentAmount = repaymentAmount + lonContractWithdrawService.getRepaymentAmount(request, getFltStartDate(request, documentType, lastValidFrom, hlsCusLonContractWithdraw.getFloatingRangeMethod(), hlsCusLonContractWithdraw.getWithdrawId()),
                                            getFltStartDate(request, documentType, fndBaseRateSet.getValid_from(), hlsCusLonContractWithdraw.getFloatingRangeMethod(),
                                                    hlsCusLonContractWithdraw.getWithdrawId()), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), intRate, hlsCusLonContractWithdraw.getCalcInterestYearDays(), null);
                                    //}
                                }
                                i = i + 1;
                            }
                            lastValidFrom = fndBaseRateSet.getValid_from();
                            baseRate = getBaseRate(request, fndBaseRateSet.getBase_rate_set(), lonMonth);
                            intRate = calcIntRateByBaseRate(baseRate, hlsCusLonContractWithdraw.getFloatingWay(), hlsCusLonContractWithdraw.getFloatingWayRange());
                        }
                        if (repaymentAmount == 0) {
                            repaymentAmount = lonContractWithdrawService.getRepaymentAmount(request, lastRepaymentDate, lonContractRepayment.getPlannedCalcDate(), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), intRateNew, hlsCusLonContractWithdraw.getCalcInterestYearDays(), null);
                        } else {
                            repaymentAmount = repaymentAmount + lonContractWithdrawService.getRepaymentAmount(request, getFltStartDate(request, documentType, validFrom, hlsCusLonContractWithdraw.getFloatingRangeMethod(),
                                    hlsCusLonContractWithdraw.getWithdrawId()), lonContractRepayment.getPlannedCalcDate(), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), intRateNew, hlsCusLonContractWithdraw.getCalcInterestYearDays(), null);
                        }
                        lonContractRepayment.set__status("update");
                        lonContractRepayment.setPlannedDueAmount(repaymentAmount);
                        lonContractRepaymentService.updateByPrimaryKeySelective(request, lonContractRepayment);
                        newSumRepaymentAmount = newSumRepaymentAmount + repaymentAmount;
                    } else if (lonContractRepayment.getTimes() > times) {
                        repaymentAmount = lonContractWithdrawService.getRepaymentAmount(request, lastRepaymentDate, lonContractRepayment.getPlannedCalcDate(), hlsCusLonContractWithdraw.getWithdrawId(), hlsCusLonContractWithdraw.getDueAmount(), intRateNew, hlsCusLonContractWithdraw.getCalcInterestYearDays(), null);
                        lonContractRepayment.set__status("update");
                        lonContractRepayment.setPlannedDueAmount(repaymentAmount);
                        lonContractRepaymentService.updateByPrimaryKeySelective(request, lonContractRepayment);
                        newSumRepaymentAmount = newSumRepaymentAmount + repaymentAmount;
                    } else {
                        newSumRepaymentAmount = newSumRepaymentAmount + lonContractRepayment.getPlannedDueAmount();
                    }
                } else {
                    newSumRepaymentAmount = newSumRepaymentAmount + lonContractRepayment.getPlannedDueAmount();
                }
                lastRepaymentDate = lonContractRepayment.getPlannedCalcDate();
            }
            conFloatingRateReqLn.set__status("update");
            conFloatingRateReqLn.setStatus("CALCULATED");
            conFloatingRateReqLn.setInterestAdjAmount(newSumRepaymentAmount - oldSumRepaymentAmount);
            self().updateByPrimaryKeySelective(request, conFloatingRateReqLn);
        }
    }
*/
    /**
     * 租赁合同调息算法入口
     *
     * @param request
     * @param documentType                   调息单据类型
     * @param validFrom                      选择的基准利率的起始日期
     * @param hlsCusConFloatingRateReqLnList 调息行信息
     */
   /* public void conContractCshFltCalc(IRequest request, String documentType, Date validFrom, List<HlsCusConFloatingRateReqLn> hlsCusConFloatingRateReqLnList) {
        DecimalFormat df = new DecimalFormat("#.00");
        //循环调息行
        for (HlsCusConFloatingRateReqLn conFloatingRateReqLn : hlsCusConFloatingRateReqLnList) {
            //获取合同信息
            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setContractId(conFloatingRateReqLn.getChangeReqId());
            conContract = hlsCusConContractService.selectByPrimaryKey(request, conContract);
            //获取报价信息hlsCusPrjQuotationService
            HlsCusPrjQuotation hlsCusPrjQuotationTemp = new HlsCusPrjQuotation();
            hlsCusPrjQuotationTemp.setSourceDocumentCategory("CON_CONTRACT");
            hlsCusPrjQuotationTemp.setSourceDocumentId(conFloatingRateReqLn.getContractId());
            List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(request, hlsCusPrjQuotationTemp, 1, 999999);
            if (hlsCusPrjQuotationList.size() != 1) {
                throw new IllegalArgumentException("报价数据异常,请联系管理员");
            }
            hlsCusPrjQuotationTemp = hlsCusPrjQuotationList.get(0);
            //合同回写字段
            Double totalInterest = 0D;//利息总额
            Double totalRental = 0D; //租金总额
            Double vatRate = conContract.getVatRate();

            //获取期限(年)
            int conMonth;
            conMonth = conContract.getLeaseTerm().intValue() * 12;
            //获取新的租赁利率
            Double intRateNew = calcIntRateByBaseRate(conFloatingRateReqLn.getNewBaseRate(), conContract.getFloatingWay(), conContract.getFloatingWayRate() * 100);
            if ("DAILY_INTEREST".equals(hlsCusPrjQuotationTemp.getPaymentMethod())) {
                intRateNew = (double) Math.round(intRateNew / 360 * 365 * 10000) / 10000;
            }
            //获取调息开始日
            Date fltStartDate = getFltStartDate(request, documentType, validFrom, conContract.getFloatingRangeMethod(), conContract.getContractId());
            //获取调息开始期数
            Long times = getFltStartTimes(request, documentType, fltStartDate, conContract.getContractId());
            //获取调息后现金流
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCfItem(1L);
            conContractCashflow.setContractId(conFloatingRateReqLn.getChangeReqId());
            List<HlsCusConContractCashflow> newList = new ArrayList<>();
            newList = hlsCusConContractCashflowMapper.select(conContractCashflow);
            //获取调息前现金流
            HlsCusConContractCashflow oldConCashFlow = new HlsCusConContractCashflow();
            oldConCashFlow.setCfItem(1L);
            oldConCashFlow.setContractId(conFloatingRateReqLn.getContractId());
            List<HlsCusConContractCashflow> oldList = new ArrayList<>();
            oldList = hlsCusConContractCashflowMapper.select(oldConCashFlow);
            Date lastRepaymentDate = conContract.getEstimateRentingDate();//初始为预计起租日
            Double oldSumDueAmount = 0D;
            Double newSumDueAmount = 0D;

            for (HlsCusConContractCashflow dt : oldList) {
                oldSumDueAmount = oldSumDueAmount + dt.getDueAmount();
//                if("NOT".equals(dt.getWriteOffFlag())){
//                    outstandingPrincpl=outstandingPrincpl+dt.getPrincipal();
//                }
            }
            //获取调息开始剩余本金
            Long fltCalcTimes = times > 0 ? times - 1 : times;
            Long fltCalcCfItem = 1L;
            if (fltCalcTimes.equals(0L)) {
                fltCalcCfItem = 0L;
            }
            HlsCusConContractCashflow conCashFlowFltCalc = new HlsCusConContractCashflow();
            conCashFlowFltCalc.setCfItem(fltCalcCfItem);
            conCashFlowFltCalc.setTimes(fltCalcTimes);
            conCashFlowFltCalc.setContractId(conFloatingRateReqLn.getContractId());
            List<HlsCusConContractCashflow> fltCalcList = new ArrayList<>();
            fltCalcList = hlsCusConContractCashflowMapper.select(conCashFlowFltCalc);
            if (fltCalcList.size() != 1) {
                throw new IllegalArgumentException("处理数据失败，请检查");
            }
            Double outstandingPrincpl = fltCalcList.get(0).getOutstandingPrincipal();
            Double sumOutStandPrincel = 0D;//前期累计本金
            //等额本息
            if ("LEVEL_PMT_TAX_INC_CT".equalsIgnoreCase(conContract.getPriceList())) {
                //Double outstandingPrincpl = conContract.getFinanceAmount();//当期剩余本金
                Long rentFrequency = 0L;//还租频率换算
                Double interest = 0D;//当期利息
                Double netInterest = 0D;//不含稅利息
                Double vatInterest = 0D;//利息税额
                rentFrequency = hlsCusConContractCashflowService.getRentingFrequency(conContract.getRentingFrequency());

                Double dueAmount = hlsCusConContractCashflowService.PMT(outstandingPrincpl, intRateNew / 100, rentFrequency, (conContract.getLeaseTimes() - times + 1), 0);//每期租金
                Double netAmount = 0D;
                Double vatAmount = 0D;
                Double netPrincipal = 0D;
                Double vatPrincipal = 0D;
                for (HlsCusConContractCashflow newCsh : newList) {
                    //调息日往后的期数
                    if (newCsh.getTimes() >= times) {

                        //计算当期利息
                        interest = outstandingPrincpl * intRateNew / 100 / rentFrequency;
                        interest = (double) Math.round(interest * 100) / 100;
                        netInterest = (double) Math.round(interest / (1 + vatRate) * 100) / 100;
                        vatInterest = interest - netInterest;
                        newCsh.setInterest(interest);
                        newCsh.setNetInterest(netInterest);
                        newCsh.setVatInterest(vatInterest);

                        //计算当期租金
                        newCsh.setDueAmount((double) Math.round(dueAmount * 100) / 100);
                        netAmount = (double) Math.round(newCsh.getDueAmount() / (1 + vatRate) * 100) / 100;
                        vatAmount = newCsh.getDueAmount() - netAmount;
                        newCsh.setNetDueAmount(netAmount);
                        newCsh.setVatDueAmount(vatAmount);

                        //计算当期本金 and    //计算当期剩余本金
                        if (newCsh.getTimes() == conContract.getLeaseTimes()) {
                            //最后一期
                            newCsh.setPrincipal((double) Math.round(outstandingPrincpl * 100) / 100);
                            newCsh.setOutstandingPrincipal(0.0D);
                        } else {
                            newCsh.setPrincipal((double) Math.round(newCsh.getDueAmount() * 100) / 100 - (double) Math.round(newCsh.getInterest() * 100) / 100);
                            newCsh.setOutstandingPrincipal(outstandingPrincpl - newCsh.getPrincipal());
                        }
                        netPrincipal = newCsh.getPrincipal() / (1 + vatRate);
                        vatPrincipal = newCsh.getPrincipal() - netPrincipal;
                        newCsh.setNetPrincipal(netPrincipal);
                        newCsh.setVatPrincipal(vatPrincipal);
                        newCsh.set__status("update");
                        hlsCusConContractCashflowService.updateByPrimaryKeySelective(request, newCsh);
                        newSumDueAmount = newSumDueAmount + (double) Math.round(newCsh.getDueAmount() * 100) / 100;
                    } else {
                        newSumDueAmount = newSumDueAmount + (double) Math.round(newCsh.getDueAmount() * 100) / 100;
                    }
                    outstandingPrincpl = newCsh.getOutstandingPrincipal();//获取当期剩余本金，用于下一期的计算
                    if (newCsh.getTimes() == (times - 1)) {
                        dueAmount = hlsCusConContractCashflowService.PMT(outstandingPrincpl, intRateNew / 100, rentFrequency, (conContract.getLeaseTimes() - times + 1), 0);
                        dueAmount = (double) Math.round(dueAmount * 100) / 100;
                    }
                }
            } else if ("LEVEL_RATE_TAX_INC_CT".equalsIgnoreCase(conContract.getPriceList())) {
                // Double outstandingPrincpl = conContract.getFinanceAmount();//当期剩余本金
                //  Double sumOutStandPrincel = 0D;//前期累计本金
                Long rentFrequency = 0L;//还租频率换算
                rentFrequency = hlsCusConContractCashflowService.getRentingFrequency(conContract.getRentingFrequency());
                for (HlsCusConContractCashflow newCsh : newList) {
                    //已支付的现金流不做调整
                    if ("NOT".equalsIgnoreCase(newCsh.getWriteOffFlag())) {
                        Double dueInterset = 0D;
                        Double principal = 0D;
                        Double dueAmount = 0D;

                        //找到调息起始期数
                        if (newCsh.getTimes() == times) {
                            //剩余本金
                            principal = outstandingPrincpl / (conContract.getLeaseTimes() - newCsh.getTimes() + 1);
                            newCsh.setPrincipal((double) Math.round(principal * 100) / 100);
                            if ("NEXT_TIMES".equalsIgnoreCase(conContract.getFloatingRangeMethod())) {
                                //次期
                                dueInterset = outstandingPrincpl * intRateNew / rentFrequency / 100;
                                dueInterset = (double) Math.round(dueInterset * 100) / 100;
                            } else {
                                //非次期
                                //获取所有的基准利率
                                List<FndBaseRateSet> fndBaseRateSetList = fndBaseRateSetMapper.selectAll();
                                //获取基准利率起始日期
                                Date lastValidFrom = fndBaseRateSetList.get(0).getValid_from();
                                //获取对应的基准利率
                                Double baseRate = getBaseRate(request, fndBaseRateSetList.get(0).getBase_rate_set(), conMonth);
                                //获取调息后租赁利率
                                Double intRate = calcIntRateByBaseRate(baseRate, conContract.getFloatingWay(), conContract.getFloatingWayRate() * 100);
                                Date endValidFrom = fndBaseRateSetList.get(0).getValid_from();
                                for (FndBaseRateSet fndBaseRateSet : fndBaseRateSetList) {
                                    lastValidFrom = fndBaseRateSet.getValid_from();
                                    //预估起租日<基准利率起始日
                                    Date validFromDate = getFltStartDate(request, documentType, lastValidFrom, conContract.getFloatingRangeMethod(), conContract.getContractId());
                                    if (validFromDate.getTime() < newCsh.getDueDate().getTime() && validFromDate.getTime() >= lastRepaymentDate.getTime()) {
                                        dueInterset = dueInterset + hlsCusConContractCashflowService.getDueAmount(request, lastRepaymentDate, validFromDate, outstandingPrincpl, intRate, conContract.getInterestYearDays());
                                        endValidFrom = validFromDate;
                                        lastRepaymentDate = validFromDate;
                                    }
                                    baseRate = getBaseRate(request, fndBaseRateSet.getBase_rate_set(), conMonth);
                                    intRate = calcIntRateByBaseRate(baseRate, conContract.getFloatingWay(), conContract.getFloatingWayRate());
                                }
                                if (dueInterset == 0) {
                                    //当期利息
                                    dueInterset = hlsCusConContractCashflowService.getDueAmount(request, lastRepaymentDate, newCsh.getDueDate(), outstandingPrincpl, intRateNew, conContract.getInterestYearDays());
                                    dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                } else {
                                    //利息：调息日期----下一期结束日期
                                    dueInterset = dueInterset + hlsCusConContractCashflowService.getDueAmount(request, endValidFrom, newCsh.getDueDate(), outstandingPrincpl, intRateNew, conContract.getInterestYearDays());
                                    dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                }
                            }
                        } else if (newCsh.getTimes() > times) {
                            if (newCsh.getTimes() == conContract.getLeaseTimes()) {
                                //最后一期
                                principal = conContract.getFinanceAmount() - sumOutStandPrincel;
                                newCsh.setPrincipal((double) Math.round(principal * 100) / 100);
                            } else {
                                //算出每期本金
                                principal = outstandingPrincpl / (conContract.getLeaseTimes() - newCsh.getTimes() + 1);
                                newCsh.setPrincipal((double) Math.round(principal * 100) / 100);
                            }
                            if ("NEXT_TIMES".equalsIgnoreCase(conContract.getFloatingRangeMethod())) {
                                //次期
                                dueInterset = outstandingPrincpl * intRateNew / rentFrequency / 100;
                                dueInterset = (double) Math.round(dueInterset * 100) / 100;
                            } else {
                                //dueInterset = hlsCusConContractCashflowService.getDueAmount(request, lastRepaymentDate, newCsh.getDueDate(), outstandingPrincpl, intRateNew);
                                dueInterset = outstandingPrincpl * intRateNew / rentFrequency / 100;
                                dueInterset = (double) Math.round(dueInterset * 100) / 100;
                            }

                        } else if (newCsh.getTimes() < times) {
                            dueInterset = newCsh.getInterest();
                        }
                        newCsh.set__status("update");
                        newCsh.setInterest(dueInterset);
                        outstandingPrincpl = conContract.getFinanceAmount() - sumOutStandPrincel - newCsh.getPrincipal();//获取当期剩余本金，用于下一期的计算
                        newCsh.setOutstandingPrincipal(outstandingPrincpl);
                        newCsh.setDueAmount(dueInterset + newCsh.getPrincipal());
                        hlsCusConContractCashflowService.updateByPrimaryKey(request, newCsh);
                        newSumDueAmount = newSumDueAmount + newCsh.getDueAmount();
                    } else {
                        newSumDueAmount = newSumDueAmount + newCsh.getDueAmount();
                    }
                    lastRepaymentDate = newCsh.getDueDate();
                    totalInterest = totalInterest + newCsh.getInterest();
                    totalRental = totalRental + newCsh.getDueAmount();
                    sumOutStandPrincel = sumOutStandPrincel + newCsh.getPrincipal();//累加本金，用于计算最后一期本金
                }
            } else if ("COMBINATION_QUOTATION".equalsIgnoreCase(conContract.getPriceList())) {

                HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setSourceDocumentId(conContract.getProjectId());
                hlsCusPrjQuotation.setSourceDocumentCategory(SOURCE_DOCUMENT_CATEGORY);
                List<HlsCusPrjQuotation> hlsCusPrjQuotationLists = hlsCusPrjQuotationService.select(request, hlsCusPrjQuotation, 1, 9999999);
                if (hlsCusPrjQuotationLists.size() > 1) {
                    throw new IllegalArgumentException("获取到多行报价，请检查数据");
                }
                hlsCusPrjQuotation = hlsCusPrjQuotationLists.get(0);
                QuotationSubsection quotationSubsection = new QuotationSubsection();
                quotationSubsection.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                quotationSubsection.setTimes(times);
                List<QuotationSubsection> quotationSubsectionLists = quotationSubsectionMapper.quotationSubsectionByFloatingRateStartTimes(quotationSubsection);
                HlsCusConContractCashflow contractCashflowFln = new HlsCusConContractCashflow();
                contractCashflowFln.setCfItem(1L);
                contractCashflowFln.setContractId(conFloatingRateReqLn.getChangeReqId());
                contractCashflowFln.setTimes(times);
                Double floatingRateBefore = hlsCusConContractCashflowMapper.querySubsectionCashflowDueAmount(contractCashflowFln);
                //获取调息开始前期次的租金总和
                newSumDueAmount = newSumDueAmount + floatingRateBefore;
                if (quotationSubsectionLists.size() == 0) {
                    throw new IllegalArgumentException("未取到分段信息，请检查数据");
                }

                for (int i = 0; i < quotationSubsectionLists.size(); i++) {
                    Long pmtTimes = conContract.getLeaseTimes() / getRentingFrequency(conContract.getRentingFrequency()) - times + 1;

                    Long writeOffCount = 0L;
                    QuotationSubsection subsection = quotationSubsectionLists.get(i);
                    Long startTimes;
                    Long endTimes;
                    if (i == 0) {
                        startTimes = times;
                    } else {
                        startTimes = subsection.getStartTime().longValue();
                    }
                    endTimes = subsection.getEndTime().longValue();
                    HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                    contractCashflow.setCfItem(1L);
                    contractCashflow.setSubsectionStartTime(startTimes);
                    contractCashflow.setSubsectionEndTime(endTimes);
                    contractCashflow.setTimes(times);
                    contractCashflow.setContractId(conFloatingRateReqLn.getChangeReqId());
                    List<HlsCusConContractCashflow> quotationSubsectionList = hlsCusConContractCashflowMapper.querySubsectionCashflow(contractCashflow);

//                    for (HlsCusConContractCashflow hlsCusConContractCashflow : quotationSubsectionList) {
//                        if (!"NOT".equals(hlsCusConContractCashflow.getWriteOffFlag())) {
//                            writeOffCount = writeOffCount + 1;
//                        }
//                    }
                    if ("EQUAL_INTEREST".equalsIgnoreCase(subsection.getCalcWay()) || "DESIGNATED_RENT".equalsIgnoreCase(subsection.getCalcWay())) {
                        Long rentFrequency = 0L;//还租频率换算
                        Double interest = 0D;//当期利息
                        Double netInterest = 0D;//不含稅利息
                        Double vatInterest = 0D;//利息税额
                        //pmt期数

                        rentFrequency = hlsCusConContractCashflowService.getRentingFrequency(conContract.getRentingFrequency());

                        Double dueAmount = hlsCusConContractCashflowService.PMT(outstandingPrincpl, intRateNew / 100, rentFrequency, pmtTimes, 0);//每期租金
                        Double netAmount = 0D;
                        Double vatAmount = 0D;
                        Double netPrincipal = 0D;
                        Double vatPrincipal = 0D;
                        for (HlsCusConContractCashflow newCsh : quotationSubsectionList) {
                            //调息日往后的期数
                            if (newCsh.getTimes() >= times) {

                                //计算当期利息
                                interest = outstandingPrincpl * intRateNew / 100 / rentFrequency;
                                interest = (double) Math.round(interest * 100) / 100;
                                netInterest = (double) Math.round(interest / (1 + vatRate) * 100) / 100;
                                vatInterest = interest - netInterest;
                                newCsh.setInterest(interest);
                                newCsh.setNetInterest(netInterest);
                                newCsh.setVatInterest(vatInterest);

                                //计算当期租金
                                if ("EQUAL_INTEREST".equalsIgnoreCase(subsection.getCalcWay())) {
                                    newCsh.setDueAmount((double) Math.round(dueAmount * 100) / 100);
                                    netAmount = (double) Math.round(newCsh.getDueAmount() / (1 + vatRate) * 100) / 100;
                                    vatAmount = newCsh.getDueAmount() - netAmount;
                                    newCsh.setNetDueAmount(netAmount);
                                    newCsh.setVatDueAmount(vatAmount);
                                }

                                //计算当期本金 and    //计算当期剩余本金
                                if (newCsh.getTimes() == conContract.getLeaseTimes()) {
                                    //最后一期
                                    newCsh.setPrincipal((double) Math.round(outstandingPrincpl * 100) / 100);
                                    newCsh.setOutstandingPrincipal(0.0D);
                                } else {
                                    newCsh.setPrincipal((double) Math.round(newCsh.getDueAmount() * 100) / 100 - (double) Math.round(newCsh.getInterest() * 100) / 100);
                                    newCsh.setOutstandingPrincipal(outstandingPrincpl - newCsh.getPrincipal());
                                }
                                netPrincipal = newCsh.getPrincipal() / (1 + vatRate);
                                vatPrincipal = newCsh.getPrincipal() - netPrincipal;
                                newCsh.setNetPrincipal(netPrincipal);
                                newCsh.setVatPrincipal(vatPrincipal);
                                newCsh.set__status("update");
                                hlsCusConContractCashflowService.updateByPrimaryKeySelective(request, newCsh);
                                newSumDueAmount = newSumDueAmount + (double) Math.round(newCsh.getDueAmount() * 100) / 100;
                            } else {
                                newSumDueAmount = newSumDueAmount + (double) Math.round(newCsh.getDueAmount() * 100) / 100;
                            }
                            outstandingPrincpl = newCsh.getOutstandingPrincipal();//获取当期剩余本金，用于下一期的计算
                            if (newCsh.getTimes() == (times - 1)) {
                                dueAmount = hlsCusConContractCashflowService.PMT(outstandingPrincpl, intRateNew / 100, rentFrequency, pmtTimes, 0);
                                dueAmount = (double) Math.round(dueAmount * 100) / 100;
                            }
                        }
                    } else if ("DESIGNATED_PRINCIPAL".equalsIgnoreCase(subsection.getCalcWay()) || "EQUAL_PRINCIPAL".equalsIgnoreCase(subsection.getCalcWay())) {
                        // Double outstandingPrincpl = conContract.getFinanceAmount();//当期剩余本金


                        Long rentFrequency = 0L;//还租频率换算
                        rentFrequency = hlsCusConContractCashflowService.getRentingFrequency(conContract.getRentingFrequency());
                        //   Long pmtTimes = conContract.getLeaseTimes()/getRentingFrequency(conContract.getRentingFrequency())-writeOffCount;
                        for (HlsCusConContractCashflow newCsh : quotationSubsectionList) {
                            //已支付的现金流不做调整
                            if ("NOT".equalsIgnoreCase(newCsh.getWriteOffFlag())) {
                                Double dueInterset = 0D;
                                Double principal = 0D;
                                Double dueAmount = 0D;
                                //找到调息起始期数
                                if (newCsh.getTimes() == times) {
                                    //剩余本金
                                    if ("DESIGNATED_PRINCIPAL".equalsIgnoreCase(subsection.getCalcWay())) {
                                        principal = newCsh.getPrincipal();
                                    } else {
                                        principal = outstandingPrincpl / (conContract.getLeaseTimes() - newCsh.getTimes() + 1);
                                    }


                                    newCsh.setPrincipal((double) Math.round(principal * 100) / 100);
                                    if ("NEXT_TIMES".equalsIgnoreCase(conContract.getFloatingRangeMethod())) {
                                        //次期
                                        dueInterset = outstandingPrincpl * intRateNew / rentFrequency / 100;
                                        dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                    } else {
                                        //非次期
                                        //获取所有的基准利率
                                        List<FndBaseRateSet> fndBaseRateSetList = fndBaseRateSetMapper.selectAll();
                                        //获取基准利率起始日期
                                        Date lastValidFrom = fndBaseRateSetList.get(0).getValid_from();
                                        //获取对应的基准利率
                                        Double baseRate = getBaseRate(request, fndBaseRateSetList.get(0).getBase_rate_set(), conMonth);
                                        //获取调息后租赁利率
                                        Double intRate = calcIntRateByBaseRate(baseRate, conContract.getFloatingWay(), conContract.getFloatingWayRate() * 100);
                                        Date endValidFrom = fndBaseRateSetList.get(0).getValid_from();
                                        for (FndBaseRateSet fndBaseRateSet : fndBaseRateSetList) {
                                            lastValidFrom = fndBaseRateSet.getValid_from();
                                            //预估起租日<基准利率起始日
                                            Date validFromDate = getFltStartDate(request, documentType, lastValidFrom, conContract.getFloatingRangeMethod(), conContract.getContractId());
                                            if (validFromDate.getTime() < newCsh.getDueDate().getTime() && validFromDate.getTime() >= lastRepaymentDate.getTime()) {
                                                dueInterset = dueInterset + hlsCusConContractCashflowService.getDueAmount(request, lastRepaymentDate, validFromDate, outstandingPrincpl, intRate, conContract.getInterestYearDays());
                                                endValidFrom = validFromDate;
                                                lastRepaymentDate = validFromDate;
                                            }
                                            baseRate = getBaseRate(request, fndBaseRateSet.getBase_rate_set(), conMonth);
                                            intRate = calcIntRateByBaseRate(baseRate, conContract.getFloatingWay(), conContract.getFloatingWayRate());
                                        }
                                        if (dueInterset == 0) {
                                            //当期利息
                                            dueInterset = hlsCusConContractCashflowService.getDueAmount(request, lastRepaymentDate, newCsh.getDueDate(), outstandingPrincpl, intRateNew, conContract.getInterestYearDays());
                                            dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                        } else {
                                            //利息：调息日期----下一期结束日期
                                            dueInterset = dueInterset + hlsCusConContractCashflowService.getDueAmount(request, endValidFrom, newCsh.getDueDate(), outstandingPrincpl, intRateNew, conContract.getInterestYearDays());
                                            dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                        }
                                    }
                                } else if (newCsh.getTimes() > times) {
                                    if (newCsh.getTimes() == conContract.getLeaseTimes()) {
                                        //最后一期
                                        principal = outstandingPrincpl;
                                        newCsh.setPrincipal((double) Math.round(principal * 100) / 100);
                                    } else {
                                        //算出每期本金
                                        if ("DESIGNATED_PRINCIPAL".equalsIgnoreCase(subsection.getCalcWay())) {
                                            principal = newCsh.getPrincipal();
                                        } else {
                                            principal = outstandingPrincpl / (conContract.getLeaseTimes() - newCsh.getTimes() + 1);

                                        }
                                        // principal = outstandingPrincpl / (conContract.getLeaseTimes() - newCsh.getTimes() + 1);
                                        newCsh.setPrincipal((double) Math.round(principal * 100) / 100);
                                    }
                                    if ("NEXT_TIMES".equalsIgnoreCase(conContract.getFloatingRangeMethod())) {
                                        //次期
                                        dueInterset = outstandingPrincpl * intRateNew / rentFrequency / 100;
                                        dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                    } else {
                                        //dueInterset = hlsCusConContractCashflowService.getDueAmount(request, lastRepaymentDate, newCsh.getDueDate(), outstandingPrincpl, intRateNew);
                                        dueInterset = outstandingPrincpl * intRateNew / rentFrequency / 100;
                                        dueInterset = (double) Math.round(dueInterset * 100) / 100;
                                    }

                                } else if (newCsh.getTimes() < times) {
                                    dueInterset = newCsh.getInterest();
                                }
                                newCsh.set__status("update");
                                newCsh.setInterest(dueInterset);
                                outstandingPrincpl = outstandingPrincpl - newCsh.getPrincipal();//获取当期剩余本金，用于下一期的计算
                                // outstandingPrincpl=newCsh.getOutstandingPrincipal();
                                newCsh.setOutstandingPrincipal(outstandingPrincpl);
                                newCsh.setDueAmount(dueInterset + newCsh.getPrincipal());
                                hlsCusConContractCashflowService.updateByPrimaryKey(request, newCsh);
                                newSumDueAmount = newSumDueAmount + newCsh.getDueAmount();
                            } else {
                                newSumDueAmount = newSumDueAmount + newCsh.getDueAmount();
                            }
                            lastRepaymentDate = newCsh.getDueDate();
                            totalInterest = totalInterest + newCsh.getInterest();
                            totalRental = totalRental + newCsh.getDueAmount();
                            sumOutStandPrincel = sumOutStandPrincel + newCsh.getPrincipal();//累加本金，用于计算最后一期本金
                        }
                    }

                }


            }
            conFloatingRateReqLn.set__status("update");
            conFloatingRateReqLn.setStatus("CALCULATED");
            conFloatingRateReqLn.setInterestAdjAmount(newSumDueAmount - oldSumDueAmount);
            self().updateByPrimaryKeySelective(request, conFloatingRateReqLn);

            //更新合同表
            conContract.setIntRate(intRateNew);//新租赁利率
            conContract.setBaseRate(conFloatingRateReqLn.getNewBaseRate());//基准利率
            conContract.setTotalInterest(totalInterest);//利息总额
            conContract.setTotalRental(totalRental);//租金总额


        }
    }
*/

    //获取基准利率
    public Double getBaseRate(IRequest request, String baseRateSet, int lonMonth) {
        Double result = 0D;
        FndBaseRate fndBaseRate = new FndBaseRate();
        fndBaseRate.setBaseRateSet(baseRateSet);
        List<FndBaseRate> fndBaseRateList = fndBaseRateService.select(request, fndBaseRate, 1, 999999);

        if (fndBaseRateList.size() > 0) {
            for (FndBaseRate baseRate : fndBaseRateList
            ) {
                if (baseRate.getMonthsFrom() < lonMonth && baseRate.getMonthsTo() >= lonMonth) {
                    result = baseRate.getBaseRate();
                }
            }
        }
        return result;
    }

    //计算执行利率
    public Double calcIntRateByBaseRate(Double baseRate, String floatingWay, Double floatingWayRange) {
        Double result = 0D;
        if ("PLUS".equalsIgnoreCase(floatingWay) || "INCREASE".equalsIgnoreCase(floatingWay)) {
            result = (double) Math.round((baseRate + floatingWayRange / 100) * 1000000) / 10000;
        } else if ("MUTIPLY".equalsIgnoreCase(floatingWay)) {
            result = (double) Math.round((baseRate * (1 + floatingWayRange / 100)) * 1000000) / 10000;
        } else if ("DECREASE".equalsIgnoreCase(floatingWay)) {
            result = (double) Math.round((baseRate - floatingWayRange / 100) * 1000000) / 10000;
        }
        return result;
    }

    //获取调息开始日,如果为次期，则具体从业务现金流表中获取计划还款日
    public Date getFltStartDate(IRequest request, String documentType, Date validFrom, String floatingMethod, Long documentId) {
        Date result = validFrom;
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(validFrom);
        int year = dateCal.get(Calendar.YEAR);
        int month = dateCal.get(Calendar.MONTH) + 1;
        int day = dateCal.get(Calendar.DAY_OF_MONTH);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//当日
        if ("SAME_DAY".equalsIgnoreCase(floatingMethod)) {
            result = new Date();
        }
//次期本金
        if ("SUBPRIME_PRINCIPAL".equalsIgnoreCase(floatingMethod)) {
            if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(documentType)) {
                HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
                hlsCusLonContractRepayment.setWithdrawId(documentId);
                hlsCusLonContractRepayment.setCfItem(301L);
                List<HlsCusLonContractRepayment> lonContractRepaymentList = lonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);
                String firstFlag = "N";
                for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList
                ) {
                    if (lonContractRepayment.getPlannedDueDate().getTime() > validFrom.getTime()) {
                        if ("N".equalsIgnoreCase(firstFlag)) {
                            result = lonContractRepayment.getPlannedDueDate();
                            firstFlag = "Y";
                        }
                    }
                }
            }
        }
        //次日，央行利率变化日的后一天
        if ("NEXT_DAY".equalsIgnoreCase(floatingMethod)) {
            dateCal.add(Calendar.DATE, 1);
            result = dateCal.getTime();
            return result;
        }
        //次年，央行利率变化日下一年一月一日
        else if ("NEXT_YEAR".equalsIgnoreCase(floatingMethod)) {
            year = year + 1;
            month = 1;
            day = 1;
        }
        //次自然年，央行利率变化日后一年的同一天
        else if ("NEXT_YEAR_DAY".equalsIgnoreCase(floatingMethod)) {
            year = year + 1;
        }
        //次季，央行利率变化日下一个季度的一号
        else if ("NEXT_QUARTER".equalsIgnoreCase(floatingMethod)) {
            if (month >= 10) {
                month = 1;
                year = year + 1;
            } else {
                while (month != 4 && month != 7 && month != 10) {
                    month = month + 1;
                }
            }
            day = 1;
        }
        //次自然季，央行利率变化日后一个季度的同一天
//        else if ("NEXT_QUARTER_DAY".equalsIgnoreCase(floatingMethod)) {
//
//        }
        //次月，央行利率变化日的下一个月的一号
        else if ("NEXT_MONTH".equalsIgnoreCase(floatingMethod)) {
            if (month == 12) {
                month = 1;
                year = year + 1;
            } else {
                month = month + 1;
            }
            day = 1;
        }
        try {
            result = df.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //租赁次期
        if ("NEXT_TIMES".equalsIgnoreCase(floatingMethod)) {
            //融资提款
            if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(documentType)) {
                HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
                hlsCusLonContractRepayment.setWithdrawId(documentId);
                hlsCusLonContractRepayment.setCfItem(302L);
                List<HlsCusLonContractRepayment> lonContractRepaymentList = lonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);
                String firstFlag = "N";
                for (HlsCusLonContractRepayment lonContractRepayment : lonContractRepaymentList
                ) {
                    if (lonContractRepayment.getPlannedDueDate().getTime() > validFrom.getTime()) {
                        if ("N".equalsIgnoreCase(firstFlag)) {
                            result = lonContractRepayment.getPlannedDueDate();
                            firstFlag = "Y";
                        }
                    }
                }
            }//租赁支付表
            else if (CON_DOCUMENT_TYPE.equalsIgnoreCase(documentType)) {
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setContractId(documentId);
                conContractCashflow.setCfItem(1L);
                List<HlsCusConContractCashflow> conContractCashflows = hlsCusConContractCashflowService.select(request, conContractCashflow, 1, 10000);
                String firstFlag = "N";
                Long times = 0L;
                for (HlsCusConContractCashflow contractCashflow : conContractCashflows) {
                    if (contractCashflow.getDueDate().getTime() >= validFrom.getTime()) {
                        //落在当期
                        if ("N".equalsIgnoreCase(firstFlag)) {
                            result = contractCashflow.getDueDate();
                            firstFlag = "Y";
                            times = contractCashflow.getTimes();
                        }
                    }
                }
                conContractCashflow.setTimes(times);
                List<HlsCusConContractCashflow> newList = hlsCusConContractCashflowService.select(request, conContractCashflow, 1, 10000);
                if (newList.size() != 0) {
                    result = newList.get(0).getDueDate();
                }
            }
        }


        return result;
    }


    //获取调息开始期数
    public Long getFltStartTimes(IRequest request, String documentType, Date fltStartDate, Long documentId) {
        Long result = 0L;

        //融资提款
        if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(documentType)) {
            HlsCusLonContractRepayment hlsCusLonContractRepayment = new HlsCusLonContractRepayment();
            hlsCusLonContractRepayment.setWithdrawId(documentId);
            hlsCusLonContractRepayment.setCfItem(302L);
            List<HlsCusLonContractRepayment> hlsCusLonContractRepaymentList = lonContractRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);
            String firstFlag = "N";
            for (HlsCusLonContractRepayment lonContractRepayment : hlsCusLonContractRepaymentList
            ) {
                if (lonContractRepayment.getPlannedDueDate().getTime() > fltStartDate.getTime()) {
                    if ("N".equalsIgnoreCase(firstFlag)) {
                        result = lonContractRepayment.getTimes();
                        firstFlag = "Y";
                    }
                }
            }
        }
        //租赁支付表
        else if (CON_DOCUMENT_TYPE.equalsIgnoreCase(documentType)) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setContractId(documentId);
            conContractCashflow.setCfItem(1L);
            List<HlsCusConContractCashflow> hlsCusConContractCashflowList = new ArrayList<>();
            hlsCusConContractCashflowList = hlsCusConContractCashflowMapper.select(conContractCashflow);
            String firstFlag = "N";
            for (HlsCusConContractCashflow contractCashflow : hlsCusConContractCashflowList
            ) {
                if (contractCashflow.getDueDate().getTime() > fltStartDate.getTime()) {
                    //落在当期
                    if ("N".equalsIgnoreCase(firstFlag)) {
                        result = contractCashflow.getTimes();
                        firstFlag = "Y";
                    }
                }
            }
        }
        return result;
    }

    /**
     * 取消调息
     *
     * @param iRequest
     * @param floatingRateReq
     */
    @Override
    public void ctCancelFloatingRateReq(IRequest iRequest, @StdWho HlsCusConFloatingRateReq floatingRateReq) {
        floatingRateReq = conFloatingRateReqService.selectByPrimaryKey(iRequest, floatingRateReq);
        HlsCusConFloatingRateReqLn conFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        conFloatingRateReqLn.setFltReqId(floatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> conFloatingRateReqLns = mapper.select(conFloatingRateReqLn);
        if (conFloatingRateReqLns.size() > 0) {
            for (HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn : conFloatingRateReqLns
            ) {
                //融资提款
                if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(floatingRateReq.getDocumentType())) {
                    HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
                    lonContractWithdraw.setWithdrawId(hlsCusConFloatingRateReqLn.getContractId());
                    lonContractWithdraw = lonContractWithdrawService.selectByPrimaryKey(iRequest, lonContractWithdraw);

                    lonContractWithdraw.set__status("update");
                    lonContractWithdraw.setWithdrawStatus("APPROVED");
                    lonContractWithdrawService.updateByPrimaryKeySelective(iRequest, lonContractWithdraw);
                }
                //租赁支付表
                else if (CON_DOCUMENT_TYPE.equalsIgnoreCase(floatingRateReq.getDocumentType())) {
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setContractId(hlsCusConFloatingRateReqLn.getContractId());
                    conContract = hlsCusConContractService.selectByPrimaryKey(iRequest, conContract);

                    conContract.set__status("update");
                    conContract.setContractStatus("INCEPT");
                    conContract = hlsCusConContractService.updateByPrimaryKeySelective(iRequest, conContract);
                }
            }
        }


        floatingRateReq.set__status("update");
        floatingRateReq.setStatus("CANCEL");
        conFloatingRateReqService.updateByPrimaryKeySelective(iRequest, floatingRateReq);
    }

    //调息确认
    @Override
    public void confirmFltReq(IRequest request, HlsCusConFloatingRateReq conFloatingRateReq) throws HlsCusException {
        conFloatingRateReq = conFloatingRateReqService.selectByPrimaryKey(request, conFloatingRateReq);

        HlsCusConFloatingRateReqLn conFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        conFloatingRateReqLn.setFltReqId(conFloatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> conFloatingRateReqLnList = self().select(request, conFloatingRateReqLn, 1, 999999);

        if (conFloatingRateReqLnList.size() > 0) {
            for (HlsCusConFloatingRateReqLn floatingRateReqLn : conFloatingRateReqLnList
            ) {
                //融资提款
                if (LON_CON_DOCUMENT_TYPE.equalsIgnoreCase(conFloatingRateReq.getDocumentType())) {
                    HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
                    lonContractWithdraw.setWithdrawId(floatingRateReqLn.getContractId());
                    lonContractWithdraw = lonContractWithdrawService.selectByPrimaryKey(request, lonContractWithdraw);

                    HlsCusLonContractWithdraw lonContractWithdrawChangeReq = new HlsCusLonContractWithdraw();
                    lonContractWithdrawChangeReq.setWithdrawId(floatingRateReqLn.getChangeReqId());
                    lonContractWithdrawChangeReq = lonContractWithdrawService.selectByPrimaryKey(request, lonContractWithdrawChangeReq);

                    //复制NORMAL备份为历史版本
                    copyWithdrawRepayment(request, lonContractWithdraw, "HISTORY", "FLOATING_REQ", lonContractWithdrawChangeReq.getWithdrawId());

                    //删除未确认还息计划
                    HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
                    lonContractRepayment.setContractId(lonContractWithdraw.getContractId());
                    lonContractRepayment.setWithdrawId(lonContractWithdraw.getWithdrawId());
                    lonContractRepayment.setCfItem(302L);
                    lonContractRepayment.setConfirmFlag("N");
                    List<HlsCusLonContractRepayment> lonContractRepaymentList = lonContractRepaymentService.select(request, lonContractRepayment, 1, 999999);
                    if (lonContractRepaymentList.size() > 0) {
                        lonContractRepaymentService.batchDelete(lonContractRepaymentList);
                    }

                    //复制调息CHANGE_REQ还息计划插入至NORMAL提款项下
                    HlsCusLonContractRepayment lonContractRepaymentChangeReq = new HlsCusLonContractRepayment();
                    lonContractRepaymentChangeReq.setContractId(lonContractWithdrawChangeReq.getContractId());
                    lonContractRepaymentChangeReq.setWithdrawId(lonContractWithdrawChangeReq.getWithdrawId());
                    lonContractRepaymentChangeReq.setCfItem(302L);
                    lonContractRepayment.setConfirmFlag("N");
                    List<HlsCusLonContractRepayment> lonContractRepaymentChangeReqList = lonContractRepaymentService.select(request, lonContractRepaymentChangeReq, 1, 999999);
                    if (lonContractRepaymentChangeReqList.size() > 0) {
                        for (HlsCusLonContractRepayment hlsCusLonContractRepayment : lonContractRepaymentChangeReqList
                        ) {
                            hlsCusLonContractRepayment.set__status("add");
                            hlsCusLonContractRepayment.setWithdrawId(lonContractWithdraw.getWithdrawId());
                            hlsCusLonContractRepayment.setRepaymentId(null);
                            lonContractRepaymentService.insertSelective(request, hlsCusLonContractRepayment);
                        }
                    }

                    lonContractWithdraw.set__status("update");
                    lonContractWithdraw.setBaseRate(floatingRateReqLn.getNewBaseRate());
                    lonContractWithdraw.setLoanRate(calcIntRateByBaseRate(floatingRateReqLn.getNewBaseRate(), lonContractWithdraw.getFloatingWay(), lonContractWithdraw.getFloatingWayRange()));
                    lonContractWithdraw.setWithdrawStatus("APPROVED");
                    lonContractWithdrawService.updateByPrimaryKeySelective(request, lonContractWithdraw);

                    //重新计提
                    lonContractWithdrawService.calcLonConWithdrawFinCost(request, lonContractWithdraw);
                }
                //租赁支付表
                else if (CON_DOCUMENT_TYPE.equalsIgnoreCase(conFloatingRateReq.getDocumentType())) {
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setContractId(floatingRateReqLn.getContractId());
                    conContract = hlsCusConContractService.selectByPrimaryKey(request, conContract);

                    //调息后合同
                    HlsCusConContract conContractChangeReq = new HlsCusConContract();
                    conContractChangeReq.setContractId(floatingRateReqLn.getChangeReqId());
                    conContractChangeReq = hlsCusConContractService.selectByPrimaryKey(request, conContractChangeReq);


                    //复制NORMAL备份为历史版本
                    copyContractRepayment(request, conContract, "HISTORY", "FLOATING_REQ", floatingRateReqLn.getChangeReqId());

                    //删除租赁合同现金流
                    HlsCusConContractCashflow oldConCash = new HlsCusConContractCashflow();
                    oldConCash.setContractId(conContract.getContractId());
                    oldConCash.setWriteOffFlag("NOT");
                    List<HlsCusConContractCashflow> oldList = hlsCusConContractCashflowService.select(request, oldConCash, 1, 10000);
                    if (oldList.size() > 0) {
                        hlsCusConContractCashflowService.batchDelete(oldList);
                    }

                    //复制调息CHANGE_REQ现金流插入至NORMAL合同下
                    HlsCusConContractCashflow newConCash = new HlsCusConContractCashflow();
                    newConCash.setContractId(conContractChangeReq.getContractId());
                    newConCash.setWriteOffFlag("NOT");
                    List<HlsCusConContractCashflow> newList = hlsCusConContractCashflowService.select(request, newConCash, 1, 10000);
                    if (newList.size() > 0) {
                        for (HlsCusConContractCashflow dt : newList) {
                            dt.set__status("add");
                            dt.setContractId(conContract.getContractId());
                            dt.setCashflowId(null);
                            dt = hlsCusConContractCashflowService.insertSelective(request, dt);
                        }
                    }

                    //更新合同
                    conContract.set__status("update");
                    conContract.setBaseRate(floatingRateReqLn.getNewBaseRate());
                    conContract.setContractStatus("INCEPT");
                    conContract = hlsCusConContractService.updateByPrimaryKeySelective(request, conContract);

                    //重算未实现融资收益
                    hlsCusConContractService.calcConFinIncome(request, conContract);

                    //插入合同调息凭证流水
                    /*AbstractJeTrxService floatingRateReqLnService = commonService.map.get("FLOATING_INTEREST");
                    Map params = new HashMap<>();
                    params.put("jeTrxId", floatingRateReqLn.getFltReqLnId());
                    params.put("companyId", conContract.getCompanyId());
                    params.put("contractId", floatingRateReqLn.getFltReqLnId());
                    params.put("sourceDoc", "CON_FlOATING_RATE_REQ_LN");
                    floatingRateReqLnService.process(request, params);*/
                }

                floatingRateReqLn.set__status("update");
                floatingRateReqLn.setStatus("DONE");
                self().updateByPrimaryKeySelective(request, floatingRateReqLn);
            }
        }

        conFloatingRateReq.set__status("update");
        conFloatingRateReq.setStatus("APPROVED");
        conFloatingRateReqService.updateByPrimaryKeySelective(request, conFloatingRateReq);
    }

    /**
     * 得到付款频率
     */
    public Long getRentingFrequency(String rentingFrequency) {
        Long rf = 1L;
        if (StringUtils.equals(MONTH, rentingFrequency)) {
            rf = 1L;
        } else if (StringUtils.equals(QUARTER, rentingFrequency)) {
            rf = 3L;
        } else if (StringUtils.equals(HALF_A_YEAR, rentingFrequency)) {
            rf = 6L;
        } else if (StringUtils.equals(YEAR, rentingFrequency)) {
            rf = 12L;
        }
        return rf;
    }
    @Override
    public List<HlsCusConFloatingRateReq> queryBaseRateSet(IRequest iRequest,HlsCusConFloatingRateReq hlsCusConFloatingRateReq, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return hlsCusConFloatingRateReqMapper.queryBaseRateSet(hlsCusConFloatingRateReq);
    }
}
