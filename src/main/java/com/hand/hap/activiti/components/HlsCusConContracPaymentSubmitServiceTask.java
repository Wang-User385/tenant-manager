package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.ast.service.INoticeManageService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.CshPaymentReqLnBankAccountMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.*;
import com.hand.hls.fct.dto.HlsCusFctContractWithdrawCf;
import com.hand.hls.fct.mapper.HlsCusFctContractWithdrawCfMapper;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.MathUtil;
import hls.core.sys.event.service.SysEventService;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContracPaymentSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusActVarMapper hlsCusActVarMapper;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private IHlsCusPrjQuotationHistoryService hlsCusPrjQuotationHistoryService;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @Autowired
    private IHlsCusPrjQuotationCashflowHistoryService hlsCusPrjCashflowHistoryService;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private IHlsCusPrjQuotationDetailsHistoryService hlsCusPrjQuotationDetailsHistoryService;
    @Autowired
    private HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;

    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;

    @Autowired
    private EasVenderBasicSycnExecutor easVenderBasicSycnExecutor;
    @Autowired
    private CshPaymentReqLnBankAccountMapper cshPaymentReqLnBankAccountMapper;
    @Autowired
    CshTransactionService cshTransactionService;
    @Autowired
    private IMainCshWriteOffService mainCshWriteOffService;
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
        Map<String, String> params = new HashMap<String, String>();
    @Autowired
    private HlsCusFctContractWithdrawCfMapper fctContractWithdrawCfMapper;

    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;

    @Autowired
    private JeTrxCommonService commonService;
    @Autowired
    private HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;
    @Autowired
    HlsCusConContractMapper conContractMapper;
    @Autowired
    private IConContractCashflowService contractCashflowService;
    @Autowired
    private INoticeManageService noticeService;
    @Autowired
    private HlsCusPrjQuotationPaymentService hlsCusPrjQuotationPaymentService;
    @Autowired
    private HlsCusPrjQuotationService hlscusprjquotationservice;
    @Autowired
    private CshPaymentReqLnService hlscuscshpaymentreqlnService;
    @Autowired
    HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private IConContractCashflowService conContractCashflowService;
    @Autowired
    private SysEventService sysEventService;
    private static final String WRITEOFF_TYPE_PAYMENT_DEBT = "PAYMENT_DEBT";

    private static final String DOCUMENT_TYPE_FCT_PAYMENT_REQ = "FCT_PAYMENT_REQ";
    private static final String WRITEOFF_TYPE_FCT_PAYMENT_DEBT = "FCT_PAYMENT_DEBT";

    private static final String WRITEOFF_DOC_CATEGORY_FCT_CONTRACT = "FCT_CONTRACT";
    private static final Long FCT_CONTRACT_WITHDRAW_CF_ITEM_40 = 40L;
    private static final String WRITEOFF_DOC_CATEGORY_CON_CONTRACT = "CON_CONTRACT";
    private static final String JE_CON_CONTRACT = "CON_CONTRACT";
    private static final String WRITEOFF_TYPE_RECEIPT_CREDIT = "RECEIPT_CREDIT";
    public static final String LOAN_INITIAL_Y = "Y";
    public   String CHECK_FLAG = "N";
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    private void getTransactionNum(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionType("PAYMENT");
        hlsCusCshTransaction.setBusinessType("PAYMENT");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(), hlsCusCshTransaction.getBusinessType(), params));
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(hlsCusCshTransaction.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");

    }
    private void getTransactionNumDudect(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionType("DEDUCTION");
        hlsCusCshTransaction.setBusinessType("DEDUCTION");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(), hlsCusCshTransaction.getBusinessType(), params));
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(hlsCusCshTransaction.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");

    }

    boolean checkReceivedPrincipalAndInterest(HlsCusConContractCashflow cashflow) {
        boolean flag = false;
        if (cashflow.getCashflowId() != null) {
            HlsCusConContractCashflow cusConContractCashflow = new HlsCusConContractCashflow();
            cusConContractCashflow.setCashflowId(cashflow.getCashflowId());
            cusConContractCashflow = cashflowMapper.selectByPrimaryKey(cusConContractCashflow);
            List<Integer> cfItem = Arrays.asList(0, 2, 3, 5, 8, 9, 10);
            if (cfItem.contains(Integer.valueOf(cusConContractCashflow.getCfItem().toString()))) {
                flag = true;
            }
        }

        return flag;
    }
    public void payment(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws Exception {
            CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount =new CshPaymentReqLnBankAccount();
            cshPaymentReqLnBankAccount.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            List<CshPaymentReqLnBankAccount> cshPaymentReqLnBankAccounts = cshPaymentReqLnBankAccountMapper.selectCshPaymentReqLnBankAccount(cshPaymentReqLnBankAccount);
            List<HlsCusCshWriteOff> cshWriteOffList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < cshPaymentReqLnBankAccounts.size(); i++) {
                //生成现金事务
                HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();

                cshTransaction.setTransactionDate(hlsCusCshPaymentReqHd.getActualPayDate());
                cshTransaction.setCompanyId(iRequest.getCompanyId());
                cshTransaction.setTransactionAmount(Double.valueOf(cshPaymentReqLnBankAccounts.get(i).getPaymentAmount()));
                cshTransaction.setCurrencyCode(cshPaymentReqLnBankAccounts.get(i).getCurrency());
                getTransactionNum(iRequest, cshTransaction);
                cshTransaction.setContractId(cshPaymentReqLnBankAccounts.get(i).getSourceDocId());
                cshTransaction.setSourceDocCategory("CSH_PAYMENT_REQ");
                cshTransaction.setSourceDocId(cshPaymentReqLnBankAccounts.get(i).getPaymentReqId());
                cshTransaction.setBusinessType("PAYMENT");
                cshTransaction.setTransactionType("PAYMENT");

                cshTransaction.setTransactionCategory("CSH_TRANSACTION");

                cshTransaction = cshTransactionService.insertSelective(iRequest, cshTransaction);


                //构造核销数据

                HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                cshWriteOff.setCompanyId(cshPaymentReqLnBankAccounts.get(i).getCompanyId());
                if (DOCUMENT_TYPE_FCT_PAYMENT_REQ.equals(cshPaymentReqLnBankAccounts.get(i).getDocumentType())) {
                    cshWriteOff.setWriteOffType(WRITEOFF_TYPE_FCT_PAYMENT_DEBT);
                    cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_FCT_CONTRACT);
                    //设置cashFlowId时 需要设置为现金流的主键（WithdrawCfId） 但保理cshPaymentReqLn.getSourceDocLineId()获取的是提款id
                    HlsCusFctContractWithdrawCf hlsCusFctContractWithdrawCf = new HlsCusFctContractWithdrawCf();
                    hlsCusFctContractWithdrawCf.setWithdrawId(cshPaymentReqLnBankAccounts.get(i).getSourceDocLineId());
                    hlsCusFctContractWithdrawCf.setCfItem(FCT_CONTRACT_WITHDRAW_CF_ITEM_40);
                    List<HlsCusFctContractWithdrawCf> cashFlowList = fctContractWithdrawCfMapper.select(hlsCusFctContractWithdrawCf);
                    if (cashFlowList != null && cashFlowList.size() != 0) {
                        hlsCusFctContractWithdrawCf = cashFlowList.get(0);
                    }
                    cshWriteOff.setCashflowId(hlsCusFctContractWithdrawCf.getWithdrawCfId());
                } else {
                    cshWriteOff.setWriteOffType(WRITEOFF_TYPE_PAYMENT_DEBT);
                    cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_CON_CONTRACT);
                    //租赁的cashFlowId正常设置即可
                    cshWriteOff.setCashflowId(cshPaymentReqLnBankAccounts.get(i).getSourceDocLineId());
                }
                cshWriteOff.setWriteOffDate(sdf.parse(sdf.format(cshPaymentReqLnBankAccounts.get(i).getActualPayDate())));
                cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
                //减去抵扣金额作为核销金额
                cshWriteOff.setCshWriteOffAmount(Double.valueOf(cshPaymentReqLnBankAccounts.get(i).getPaymentAmount()));
                cshWriteOff.setReversedFlag("N");
                cshWriteOff.setCashflowId(cshPaymentReqLnBankAccounts.get(i).getSourceDocLineId());
                cshWriteOff.setContractId(cshPaymentReqLnBankAccounts.get(i).getSourceDocId());
                cshWriteOff.setCurrencyCode(cshPaymentReqLnBankAccounts.get(i).getCurrency());
                cshWriteOff.setTimes(Long.valueOf(cshPaymentReqLnBankAccounts.get(i).getTimes()));
                cshWriteOff.setCfItem(Long.valueOf(cshPaymentReqLnBankAccounts.get(i).getCfItem()));
                cshWriteOff.setCfType(Long.valueOf(cshPaymentReqLnBankAccounts.get(i).getCfType()));
                cshWriteOff.setWriteOffDueAmount(Double.valueOf(cshPaymentReqLnBankAccounts.get(i).getPaymentAmount()));
                cshWriteOff.setPaymentReqId(cshPaymentReqLnBankAccounts.get(i).getPaymentReqId());
                cshWriteOff.setPaymentReqLineId(cshPaymentReqLnBankAccounts.get(i).getPaymentReqLnId());
/*
                mainCshWriteOffService.insertSelective(iRequest, cshWriteOff);
*/
                cshWriteOffList.add(cshWriteOff);
            }
            Double actPaymentAmount =  OracleUtils.nvl(cshPaymentReqLnBankAccountMapper.queryActualPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId()), 0.0);

            List<HlsCusCshWriteOff> hlsCusCshWriteOffLists = new ArrayList<>();
            hlsCusCshWriteOffLists = mainCshWriteOffService.cshWriteOffMainPay(iRequest, cshWriteOffList);
            HlsCusPrjQuotation hlsCusPrjQuotations = new HlsCusPrjQuotation();
            hlsCusPrjQuotations.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryQuotationByConId(hlsCusPrjQuotations);
            HlsCusPrjQuotation cusPrjQuotations = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotations);
            cusPrjQuotations.setLeaseItemAmount(actPaymentAmount);
            cusPrjQuotations.setExchangeRate(cshPaymentReqLnBankAccounts.get(0).getRate());
        if (cshPaymentReqLnBankAccounts.get(0).getCfItem().equalsIgnoreCase("0")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            cusPrjQuotations.setLeaseStartDate(hlsCusCshPaymentReqHd.getActualPayDate());
            Calendar cal = Calendar.getInstance();
            cal.setTime(hlsCusCshPaymentReqHd.getActualPayDate());
            cal.add(Calendar.YEAR, 1);
            Date nextAdjustmentDate = cal.getTime();
            cusPrjQuotations.setNextAdjustmentDate(nextAdjustmentDate);
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            hlsCusConContract.setNextAdjustmentDate(nextAdjustmentDate);
          hlsCusConContractService.updateByPrimaryKeySelective(iRequest, hlsCusConContract);

        }
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, cusPrjQuotations);
            System.out.println(cusPrjQuotations.getLeaseItemAmount());
            hlsCusPrjQuotationService.quotationReCalc(iRequest, cusPrjQuotations.getQuotationId(),true);
            HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
            prjQuotationCashflowParameter.setQuotationId(cusPrjQuotations.getQuotationId());
            List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
/*
        hlscusprjquotationservice.quotationReCalc(iRequest, cusPrjQuotations.getQuotationId(),true);
*/
       /* HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflowParameter.setQuotationId(cusPrjQuotations.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);

        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(cshPaymentReqLnBankAccounts.get(0).getSourceDocId());
        hlsCusConContractCashflow.setWriteOffFlag("NOT");
        List<HlsCusConContractCashflow> deleteCashflows = cashflowMapper.select(hlsCusConContractCashflow);
        deleteCashflows.forEach(item->{
                hlsCusConContractCashflowService.deleteByPrimaryKey(item);
        });*/

      /*  for (int j = 0; j < prjQuotationCashflowList.size(); j++) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();

            BeanRefUtils.beanToBean(prjQuotationCashflowList.get(j), conContractCashflow, hlsBeanRefUtilService);

            conContractCashflow.setTimes(prjQuotationCashflowList.get(j).getTimes().longValue());
            conContractCashflow.setContractId(cshPaymentReqLnBankAccounts.get(0).getSourceDocId());
*//*
            conContractCashflow.setCashflowId(cshPaymentReqLnBankAccounts.get(j).getSourceDocLineId());
*//*            conContractCashflow = conContractCashflowService.insertSelective(iRequest, conContractCashflow);
     if (conContractCashflow.getCfItem()==0){
       HlsCusCshPaymentReqLn  hlscuscshpaymentreqln=new HlsCusCshPaymentReqLn();
       hlscuscshpaymentreqln.setSourceDocLineId(conContractCashflow.getCashflowId());
       hlscuscshpaymentreqln.setSourceDocId(cshPaymentReqLnBankAccounts.get(0).getSourceDocId());
       hlscuscshpaymentreqlnService.updateByPrimaryKeySelective(iRequest, hlscuscshpaymentreqln);

     }
        }*/
       /* for(int j = 0; j < cshWriteOffList.size(); j++){
            HlsCusCshWriteOff csh = new HlsCusCshWriteOff();
            csh=cshWriteOffList.get(j);
            mainCshWriteOffService.updateConCashflowAfter(csh);

        }*/


        for (int j = 0; j < prjQuotationCashflowList.size(); j++) {
          if(prjQuotationCashflowList.get(j).getCfItem()==0) {
              HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
              BeanRefUtils.beanToBean(prjQuotationCashflowList.get(j), conContractCashflow, hlsBeanRefUtilService);

              // prjQuotationCashflow times double -> conContractCashflow times long
              conContractCashflow.setTimes(prjQuotationCashflowList.get(j).getTimes().longValue());
              conContractCashflow.setContractId(cshPaymentReqLnBankAccounts.get(0).getSourceDocId());
              conContractCashflow.setCfStatus("RELEASE");
              conContractCashflow.setWriteOffFlag("NOT");
              conContractCashflow.setBillingStatus("NOT");
              conContractCashflow.setOverdueStatus("N");
              conContractCashflow.setPenaltyProcessStatus("N");
              conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
              conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
              conContractCashflow.setGeneratedSourceDocId(cusPrjQuotations.getQuotationId());
              conContractCashflow.setGeneratedSourceDocLineId(cshPaymentReqLnBankAccounts.get(0).getSourceDocLineId());
              conContractCashflow.setCashflowId(cshPaymentReqLnBankAccounts.get(0).getSourceDocLineId());

              HlsCusConContractCashflow cashflowNew = new HlsCusConContractCashflow();
              cashflowNew = cashflowMapper.selectConContractCashflow(cshPaymentReqLnBankAccounts.get(0).getSourceDocLineId());
              this.databaseLockProvider.lock(cashflowNew);
              Double receivedAmount = MathUtil.add(cashflowNew.getReceivedAmount(),actPaymentAmount);
              if (cashflowNew.getReceivedPrincipal() == null) {
                  conContractCashflow.setReceivedPrincipal(0.0D);
              }

              if (cashflowNew.getReceivedInterest() == null) {
                  conContractCashflow.setReceivedInterest(0.0D);
              }

              if (cshWriteOffList.get(0).getWriteOffPrincipal() == null) {
                  cshWriteOffList.get(0).setWriteOffPrincipal(0.0D);
              }

              if (cshWriteOffList.get(0).getWriteOffInterest() == null) {
                  cshWriteOffList.get(0).setWriteOffInterest(0.0D);
              }
              Double receivedPrincipal = MathUtil.add(cashflowNew.getReceivedPrincipal(), cshWriteOffList.get(0).getWriteOffPrincipal());
              Double receivedInterest = MathUtil.add(cashflowNew.getReceivedInterest(), cshWriteOffList.get(0).getWriteOffInterest());
              conContractCashflow.setReceivedAmount(receivedAmount);
              conContractCashflow.setDueAmount(receivedAmount);
              conContractCashflow.setReceivedPrincipal(receivedPrincipal);
              conContractCashflow.setReceivedInterest(receivedInterest);
              conContractCashflow.setWriteOffFlag("FULL");
              conContractCashflow.setFullWriteOffDate(cshWriteOffList.get(0).getWriteOffDate());


              if (checkReceivedPrincipalAndInterest(cashflowNew)) {
                  conContractCashflow.setReceivedPrincipal(0.0D);
                  conContractCashflow.setReceivedInterest(0.0D);
              }
              conContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);

          }

        }




/*
            cashflowMapper.updateContractCashFlow(cashflowNew);
*/
            List<HlsCusCshWriteOff> cusCshWriteOffList = new ArrayList<HlsCusCshWriteOff>();
            //遍历csh_write_off , 判断 cf_item是否为0 ，若为0 ，即设备款，修改其中一行的 FIRST_LEASE_PAY_FLAG 字段为Y
            for (HlsCusCshWriteOff cshWriteOff : hlsCusCshWriteOffLists) {
                HlsCusCshWriteOff writeOffNew = new HlsCusCshWriteOff();
                writeOffNew = cshWriteOffMapper.queryByCfItem(cshWriteOff);
                if (writeOffNew != null) {
                    cusCshWriteOffList.add(writeOffNew);
                }

            }
            //修改其中一行数据的  firstLeasePayFlag 为Y
            if (cusCshWriteOffList.size() > 0) {
                HlsCusCshWriteOff hlsCshWriteOff = cusCshWriteOffList.get(0);
                hlsCshWriteOff.setFirstLeasePayFlag("Y");
                cshWriteOffMapper.updateByPrimaryKeySelective(hlsCshWriteOff);
            }


    }
        @SneakyThrows
        @Override
    public void execute(DelegateExecution delegateExecution){
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String contractId = String.valueOf(delegateExecution.getVariable("contractId"));

        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());

        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(delegateExecution.getProcessInstanceBusinessKey()));
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);

        if ("APPROVED".equalsIgnoreCase(result)) {

            flag = "APPROVED";
            if(!hlsCusCshPaymentReqHd.getPaymentApprovedStatus().equalsIgnoreCase("PAID")) {
                Double paymentAmount =  hlsCusCshPaymentReqHdMapper.queryPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId());
                if(hlsCusCshPaymentReqHd.getLoanTotalAmount() > paymentAmount){
                    hlsCusCshPaymentReqHd.setPaymentApprovedStatus("PART_PAID");
                    hlsCusCshPaymentReqHd.setPaymentStatus("PART_PAID");
                }else if(hlsCusCshPaymentReqHd.getLoanTotalAmount().compareTo(paymentAmount) == 0 ){
                    hlsCusCshPaymentReqHd.setPaymentApprovedStatus("PAID");
                    hlsCusCshPaymentReqHd.setPaymentStatus("PAID");
                    //核销
                    payment(requestCtx, hlsCusCshPaymentReqHd);
                    Map<String, Object> paramsEvent = new HashMap<String, Object>();

                    String userName = hlsCusCshPaymentReqHdMapper.queryUserName(hlsCusCshPaymentReqHd.getPaymentReqId());
                    String msg= userName + "您好,【" + hlsCusCshPaymentReqHd.getBpName() + "】的放款支付申请【"+hlsCusCshPaymentReqHd.getPaymentReqNumber()+"】已经审批通过且支付完成，可进行后续【支付表确认】操作，望知悉，谢谢。";
                    paramsEvent.put("message", msg);
                    paramsEvent.put("noticeTitle", "款项支付审批");
                    paramsEvent.put("noticeType", "NOTICE");
                    paramsEvent.put("url", "");
                    paramsEvent.put("level", 1L);
                    requestCtx.setUserId(hlsCusCshPaymentReqHd.getCreatedBy());

                    sysEventService.eventSave(requestCtx, hlsCusCshPaymentReqHd.getPaymentReqId(), hlsCusCshPaymentReqHd.getDocumentCategory(), hlsCusCshPaymentReqHd.getDocumentType(), "CSH_PAYMENT_REQ", "PAYMENT_REQ", "P2D", paramsEvent);

                }
                Double actualPayment=  hlsCusCshPaymentReqHdMapper.queryActualPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId());

                hlsCusCshPaymentReqHd.setActualHdPayAmount(actualPayment);
                cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);

            }

            hlsCusCshPaymentReqHd.setPaymentProcessInstanceId(processInstanceId);
            cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);


        } else if ("REJECTED".equalsIgnoreCase(result)) {
            flag = "REJECTED";
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus(flag);
            hlsCusCshPaymentReqHd.setPaymentProcessInstanceId(processInstanceId);
            cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);

        }











    }
}