package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.DepositManageHdMapper;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.add;
import static com.hand.hls.utils.HlsCusMathUtil.sub;

/**
 * Created by junL on 2022年8月6日.
 * 期中租金代付流程
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCshDepositManageHdSubmitServiceTask implements JavaDelegate, IActivitiBean,IHlsCusActivitiBean {
    @Autowired
    HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;
    @Autowired
    HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private DepositManageHdMapper depositManageHdMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private CshTransactionService cshTransactionService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private IConContractCashflowService cashflowService;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    private static final String JE_CON_CONTRACT = "CON_CONTRACT";

    private Logger logger = LoggerFactory.getLogger(getClass());

    public HlsCshDepositManageHdSubmitServiceTask() {
    }


    /**
     * 结束监听
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");

        //解决跳转时结束监听器多次被调用问题
        if(((ExecutionEntityImpl) delegateExecution).getStartTime()==null){
            return;
        }

        Long manageHdId = Long.valueOf(delegateExecution.getProcessInstanceBusinessKey());
        DepositManageHd depositManage = depositManageHdMapper.selectByPrimaryKey(manageHdId);
        //设置审批中的状态(执行结果(NEW-新建，APPROVING-审批中，APPROVED-审批通过，WRITE_OFF-已核销，CANCEL-取消，REJECTED-拒绝))
        //业务数据处理
        if("APPROVED".equals(result) || "REJECTED".equals(result) || "APPROVED_RETURN".equals(result)) {
            updateCon(requestCtx, result,depositManage);
        }

    }


    /*获取inputDate日期number个月之后的日期*/
    private static Date getAfterMonth(Date inputDate, int number) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(inputDate);//设置日历时间
        c.add(Calendar.MONTH, number);//在日历的月份上增加6个月
        return c.getTime();
    }

    /**
     * @Title: setCshTransaction
     * @Discription: 构建现金事务
     * @Param: [requestCtx, hlsCusCshTransaction]
     * @Return: com.hand.hls.csh.dto.HlsCusCshTransaction
     */
    private HlsCusCshTransaction setCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {

        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                "PAYMENT", "PAYMENT", null));
        hlsCusCshTransaction.setTransactionDate(getAfterMonth(new Date(),0));
        hlsCusCshTransaction.setPenaltyCalcDate(getAfterMonth(new Date(),0));
        hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");
//        hlsCusCshTransaction.setBankAccountId(deposit.getBankAccountId());
//        hlsCusCshTransaction.setBpBankAccountId(deposit.getBpBankAccountId());

        return hlsCusCshTransaction;
    }

    /**
     * @Title: setCshWriteOff
     * @Discription: 构建核销事务
     * @Param: [ln, cshWriteOff, cshTransactionId, writeOffType]
     * @Return: com.hand.hls.csh.dto.HlsCusCshWriteOff
     */
    private HlsCusCshWriteOff setCshWriteOff(HlsCusConContractCashflow ln, HlsCusCshWriteOff cshWriteOff, Long cshTransactionId, String writeOffType){

        cshWriteOff.setWriteOffType(writeOffType);
        cshWriteOff.setWriteOffDate(getAfterMonth(new Date(),0));
        cshWriteOff.setCshTransactionId(cshTransactionId);
        cshWriteOff.setReversedFlag("N");
        cshWriteOff.setCashflowId(ln.getCashflowId());
        cshWriteOff.setContractId(ln.getContractId());
        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(ln.getCashflowId());
        cshWriteOff.setTimes(cashflow.getTimes());
        cshWriteOff.setCfItem(cashflow.getCfItem());
        cshWriteOff.setCfType(cashflow.getCfType());
        cshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
        cshWriteOff.setImportFlag("N");
        cshWriteOff.setFirstLeasePayFlag("N");

        return cshWriteOff;
    }

    //更新现金流
    private void updateCashflow(IRequest requestCtx, HlsCusCshWriteOff cshWriteOff) {

        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        //剩余可核销金额 < 本次核销金额
        Double residualAmount = sub(cashflow.getDueAmount(), nvl(cashflow.getReceivedAmount(), 0.0));
        /*if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == -1) {
            throw new ResMessageException("抵扣金额:" + cshWriteOff.getWriteOffDueAmount() + " 大于剩余可核销金额: " + residualAmount);
        }

        Double residualPrincipal = sub(nvl(cashflow.getPrincipal(), 0.0), nvl(cashflow.getReceivedPrincipal(), 0.0));
        if (residualPrincipal.compareTo(nvl(cshWriteOff.getWriteOffPrincipal(), 0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffPrincipal(), 0.0) + " 大于剩余可核销本金: " + residualPrincipal);
        }

        Double residualInterest = sub(nvl(cashflow.getInterest(), 0.0), nvl(cashflow.getReceivedInterest(), 0.0));
        if (residualInterest.compareTo(nvl(cshWriteOff.getWriteOffInterest(), 0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffInterest(), 0.0) + " 大于剩余可核销本金: " + residualInterest);
        }*/

        cashflow.setReceivedAmount(add(nvl(cashflow.getReceivedAmount(), 0.0), cshWriteOff.getWriteOffDueAmount()));
        cashflow.setReceivedPrincipal(add(nvl(cashflow.getReceivedPrincipal(), 0.0), nvl(cshWriteOff.getWriteOffPrincipal(), 0.0)));
        cashflow.setReceivedInterest(add(nvl(cashflow.getReceivedInterest(), 0.0), nvl(cshWriteOff.getWriteOffInterest(), 0.0)));

        if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == 0) {
            cashflow.setWriteOffFlag("FULL");
        } else {
            cashflow.setWriteOffFlag("PARTIAL");
        }
        cashflowService.updateByPrimaryKeySelective(requestCtx, cashflow);


    }


    //业务逻辑
    private void updateCon(IRequest request, String result,DepositManageHd depositManage) {

        //step0-1:查询NORMAL合同
        HlsCusConContract hlsCusConContractNormal = new HlsCusConContract();
        hlsCusConContractNormal.setContractId(depositManage.getContractId());
        hlsCusConContractNormal = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);
        //step0-2:还原NORMAL合同状态为起租并更新
        if("PENDING".equals(hlsCusConContractNormal.getContractStatus())) {
            hlsCusConContractNormal.setContractStatus("INCEPT");//暂挂NORMAL合同修改回起租状态
        }
        hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContractNormal);

        if("APPROVED".equals(result)){
            //step1:查询当期代付期中租金现金流
            List<DepositManageHd> lists = depositManageHdMapper.conRentCashQueryEnd(depositManage);
            for (DepositManageHd dmh : lists) {
                //step2:插入一个月后的保证金流入51现金流
                HlsCusConContractCashflow cashflowRental = hlsCusConContractCashflowMapper.selectConContractCashflow(dmh.getCashflowId());
    //            logger.debug("现金流条数[{}]",cashflowList.size());
                cashflowRental.setCashflowId(null);
                cashflowRental.setCfItem(51L);
                cashflowRental.setCfType(5L);
                if (cashflowRental.getReceivedAmount() != null && cashflowRental.getReceivedAmount() > 0) {
                    cashflowRental.setDueAmount(cashflowRental.getDueAmount() - cashflowRental.getReceivedAmount());
                } else {
                    cashflowRental.setDueAmount(cashflowRental.getDueAmount());
                }
                cashflowRental.setPrincipal(null);
                cashflowRental.setInterest(null);
                cashflowRental.setNetPrincipal(null);
                cashflowRental.setNetInterest(null);
                cashflowRental.setNetDueAmount(null);
                cashflowRental.setWriteOffFlag("NOT");
                cashflowRental.setVatDueAmount(null);
                cashflowRental.setVatInterest(null);
                cashflowRental.setVatPrincipal(null);
                cashflowRental.setReceivedAmount(null);
                cashflowRental.setReceivedInterest(null);
                cashflowRental.setReceivedPrincipal(null);
                cashflowRental.setDueDate(getAfterMonth(cashflowRental.getDueDate(), 1));
                cashflowRental.setFinIncomeDate(getAfterMonth(cashflowRental.getFinIncomeDate(), 1));
                cashflowRental.setCalcDate(getAfterMonth(cashflowRental.getCalcDate(), 1));
                cashflowRental.setCreationDate(new Date());
                hlsCusConContractCashflowService.insertSelective(request, cashflowRental);
                //step3:虚拟收款核销当期租金现金流
                HlsCusConContractCashflow cashflowRental2 = hlsCusConContractCashflowMapper.selectConContractCashflow(dmh.getCashflowId());
                //step3-1:插入deduction现金事务 （一个事务 对应租金收款核销）
                HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
                hlsCusCshTransaction.setTransactionType("RECEIPT");//收款现金事务
                hlsCusCshTransaction.setBusinessType("VIRTUAL_RECEIPT");//虚拟收款业务
                if (cashflowRental2.getReceivedAmount() != null && cashflowRental2.getReceivedAmount() > 0) {
                    hlsCusCshTransaction.setTransactionAmount(cashflowRental2.getDueAmount() - cashflowRental2.getReceivedAmount());
                } else {
                    hlsCusCshTransaction.setTransactionAmount(cashflowRental2.getDueAmount());
                }
                hlsCusCshTransaction.setCurrencyCode("CNY");
                hlsCusCshTransaction.setCreationDate(new Date());
                hlsCusCshTransaction = setCshTransaction(request, hlsCusCshTransaction);
                cshTransactionService.insertSelective(request, hlsCusCshTransaction);

                //step4-1:核销数据产生（退还类型的保证金 收款）
                String deductionWriteOffType = "RECEIPT_CREDIT";
                HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                if (cashflowRental2.getReceivedAmount() != null && cashflowRental2.getReceivedAmount() > 0) {
                    cshWriteOff.setCshWriteOffAmount(cashflowRental2.getDueAmount() - cashflowRental2.getReceivedAmount());
                    cshWriteOff.setWriteOffDueAmount(cashflowRental2.getDueAmount() - cashflowRental2.getReceivedAmount());
                } else {
                    cshWriteOff.setCshWriteOffAmount(cashflowRental2.getDueAmount());
                    cshWriteOff.setWriteOffDueAmount(cashflowRental2.getDueAmount());
                }
                if (cashflowRental2.getReceivedPrincipal() != null && cashflowRental2.getReceivedPrincipal() > 0) {
                    cshWriteOff.setWriteOffPrincipal(cashflowRental2.getPrincipal() - cashflowRental2.getReceivedPrincipal());
                } else {
                    cshWriteOff.setWriteOffPrincipal(cashflowRental2.getPrincipal());
                }
                if (cashflowRental2.getReceivedInterest() != null && cashflowRental2.getReceivedInterest() > 0) {
                    cshWriteOff.setWriteOffInterest(cashflowRental2.getInterest() - cashflowRental2.getReceivedInterest());
                } else {
                    cshWriteOff.setWriteOffInterest(cashflowRental2.getInterest());
                }
                cshWriteOff.setCompanyId(request.getCompanyId());
                cshWriteOff.setCreationDate(new Date());
                cshWriteOff = setCshWriteOff(cashflowRental2, cshWriteOff, hlsCusCshTransaction.getTransactionId(), deductionWriteOffType);
                cshWriteOff = cshWriteOffService.insertSelective(request, cshWriteOff);
                //step5:更新现金流
                updateCashflow(request, cshWriteOff);
                //step6-1:凭证 租金, csh_write_off
                /*AbstractJeTrxService paymentEqimentJeTrx = JeTrxCommonService.map.get("CSH_DEDUCTION");
                Map paramDeducts = new HashMap<>();
                paramDeducts.put("jeTrxId", cshWriteOff.getWriteOffId());
                paramDeducts.put("companyId", request.getCompanyId());
                paramDeducts.put("contractId", cshWriteOff.getContractId());
                paramDeducts.put("jeSourceDoc", JE_CON_CONTRACT);
                paramDeducts.put("jeSourceId", cshWriteOff.getContractId());
                paymentEqimentJeTrx.process(request, paramDeducts);*/
            }
            //更新为已核销WRITE_OFF
            depositManage.setExecutionResult("WRITE_OFF");
        }else{
            depositManage.setExecutionResult(result);
        }

        //step7:更新保证金管理数据
        depositManageHdMapper.updateByPrimaryKey(depositManage);

    }


    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        Long bussinessKey = Long.valueOf(hlsCusProcess.getBussinessKey());
        Long processInstanceId = Long.parseLong(hlsCusProcess.getProcessMap().get(0).get("proc_inst_id_").toString());
        DepositManageHd depositManage = depositManageHdMapper.selectByPrimaryKey(bussinessKey);
        String result = "";
        //设置审批中的状态(执行结果(NEW-新建，APPROVING-审批中，APPROVED-审批通过，WRITE_OFF-已核销，CANCEL-取消，REJECTED-拒绝))
        if (PASS.equals(hlsCusProcess.getType())) {
            result = "APPROVED";
            //业务数据处理
            updateCon(iRequest, result,depositManage);
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            result = "REJECTED";
            //业务数据处理
            updateCon(iRequest, result,depositManage);
        }else{
            result = hlsCusProcess.getType();
        }
    }
}
