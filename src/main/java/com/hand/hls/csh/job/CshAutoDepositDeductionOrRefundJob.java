package com.hand.hls.csh.job;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.WriteOffTempMapper;
import com.hand.hls.csh.service.*;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatch;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.dto.HlsDurationDepositAccount;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationDepositAccountMapper;
import com.hand.hls.hls.mapper.HlsDurationDepositMapper;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationDepositService;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.hls.service.HlsDurationLnService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.add;
import static com.hand.hls.utils.HlsCusMathUtil.round;
import static com.hand.hls.utils.HlsCusMathUtil.sub;

/**
 * @Author：junL
 * @Date：2022/7/26 9:43
 * @purpose：保证金抵扣、退还job
 * @PROJECT_NAME：leaf-jczl
 **/

public class CshAutoDepositDeductionOrRefundJob extends AbstractJobWithIRequest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private WriteOffTempMapper writeOffTempMapper;
    @Autowired
    private IWriteOffTempService cshHdService;
    @Autowired
    private HlsDurationDepositMapper hlsDurationDepositMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private CshTransactionService cshTransactionService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private IConContractCashflowService cashflowService;
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    private static final String JE_CON_CONTRACT = "CON_CONTRACT";


    @Override
    //执行job
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest requestCtx) throws Exception {
        //step1:查询保证金退还类型的现金流数据 (如果只是针对期末，那这只是查询期末的现金流即可，目前是针对所有的退还类型的现金流)
        WriteOffTemp temp = new WriteOffTemp();
        List<WriteOffTemp> tempLists = writeOffTempMapper.selectDepositInfo(temp);
        //step2:抵扣
        deduction(requestCtx,tempLists);

        //step3：退还创建付款支付单据
        List<WriteOffTemp> refundLists = writeOffTempMapper.selectDepositRefundInfo(temp);
        PaymentReqHdCreate(requestCtx,refundLists);
    }

    //退还创建付款支付单据
    public void PaymentReqHdCreate(IRequest iRequest, List<WriteOffTemp> lists) throws HlsCusAmountOverException, HlsCusException {
        for (int i = 0; i < lists.size(); i++) {
            /*step1:进行NORMAL合同的保证金类型判断，不符合条件直接退出
            PERIOD_FINAL_DEDUCTIBLE 保证金抵扣租金 PERIOD_FINAL_RETURN 保证金退还
            PERIOD_FINAL_DECHARGE 保证金抵扣手续费 DEPOSIT_NO 无保证金*/
            if(!"PERIOD_FINAL_RETURN".equals(lists.get(i).getDepositDeduction())){
                logger.info("合同："+lists.get(i).getContractNumber()+"不是保证金退还类型，不能进行保证金退还");
                continue;
            }
            //step2:付款申请创建
//            JSONObject param = JSONObject.parseObject(lists.get(i).toString());
            HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();//param.toJavaObject(HlsCusCshPaymentReqHd.class);
            hlsCusCshPaymentReqHd.setAuthorityRuleString("\"91440101578012710A\".\"IT_DEPT\".\"\".\"CSH_PAYMENT_REQ_HD\".\"CSH_PAYMENT_REQ_HD\".\"null\".\"ADMIN\"");
            hlsCusCshPaymentReqHd.setFundingPlanIdN(lists.get(i).getContractNumber());
            hlsCusCshPaymentReqHd.setFundingPlanId(lists.get(i).getContractId());
            hlsCusCshPaymentReqHd.setPaymentNumber(lists.get(i).getContractNumber());
            hlsCusCshPaymentReqHd.setFundingPlanNumber(lists.get(i).getContractNumber());
            hlsCusCshPaymentReqHd.setContractNumber(lists.get(i).getPpContractNumber());
            hlsCusCshPaymentReqHd.setContractName(lists.get(i).getPpContractName());
            hlsCusCshPaymentReqHd.setProposedLaunchDate(lists.get(i).getProposedLaunchDate());
            hlsCusCshPaymentReqHd.setCompanyId(lists.get(i).getCompanyId());
            hlsCusCshPaymentReqHd.setLoanTotalAmount(lists.get(i).getLoanTotalAmount());
            hlsCusCshPaymentReqHd.setProjectId(lists.get(i).getProjectId());
            hlsCusCshPaymentReqHd.setProjectName(lists.get(i).getProjectName());
            hlsCusCshPaymentReqHd.setFinanceAmount(lists.get(i).getFinanceAmount());
            hlsCusCshPaymentReqHd.setUnitName(lists.get(i).getUnitName());
            hlsCusCshPaymentReqHd.setBpName(lists.get(i).getBpName());
            hlsCusCshPaymentReqHd.setUnitId(lists.get(i).getUnitId());
            logger.info("unitid"+lists.get(i).getUnitId());
            hlsCusCshPaymentReqHd.setCurrency(lists.get(i).getCurrency());
            hlsCusCshPaymentReqHd.setContractId(lists.get(i).getContractId());
            hlsCusCshPaymentReqHd.setContractCurrency(lists.get(i).getContractCurrency());
            hlsCusCshPaymentReqHd.setContractCurrencyId(lists.get(i).getContractCurrencyId());
            hlsCusCshPaymentReqHd.setBpId(lists.get(i).getBpId());

            //获取合同的项目主办信息
            HlsCusPrjProject prjProject =new HlsCusPrjProject();
            prjProject.setProjectId(lists.get(i).getProjectId());
            List<Map> prjProjects = hlsCusPrjProjectMapper.queryPrjDetailSecond(prjProject);
            if(prjProjects.size()>0){
                hlsCusCshPaymentReqHd.setEmployeeId( Long.valueOf(prjProjects.get(0).get("hostProjectManager").toString()));
            }else{
                hlsCusCshPaymentReqHd.setEmployeeId(10001L);
            }
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
            hlsCusCshPaymentReqHd.setPaymentReqStatus("APPROVED");
            hlsCusCshPaymentReqHd.setPaymentType("REFUND_DEPOSIT");//保证金退还 款项类型
            hlsCusCshPaymentReqHd = cshHdService.cshHdCreate(iRequest, hlsCusCshPaymentReqHd);
            //插入行信息
            HlsCusCshPaymentReqLn ln = new HlsCusCshPaymentReqLn();
            ln.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            ln.setSourceDocCategory("CON_CONTRACT");
            ln.setSourceDocId(lists.get(i).getContractId());
            ln.setSourceDocLineId(lists.get(i).getCashflowId());
//            ln.setAmount();
            ln.setAmount(0D);
            ln.setPaymentMethod("TT");//电汇
            ln.setBpId(lists.get(i).getBpId());
            ln.setBpBankBranchName(lists.get(i).getBpBankBranchName());
            ln.setBpBankAccountNum(lists.get(i).getBpBankAccountNum());
            ln.setBpBankAccountName(lists.get(i).getBpBankAccountName());
            ln.setIfFromContract("Y");
            ln.setDueAmountLn(lists.get(i).getFinanceAmount());
            ln.setSumDueAmount(lists.get(i).getFinanceAmount());
            cshPaymentReqLnService.insertSelective(iRequest, ln);
        }

    }


    /**
     * @Title: setCshTransaction
     * @Discription: 构建现金事务
     * @Param: [requestCtx, hlsCusCshTransaction]
     * @Return: com.hand.hls.csh.dto.HlsCusCshTransaction
     */
    private HlsCusCshTransaction setCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) throws ParseException {

        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                "PAYMENT", "PAYMENT", null));
        hlsCusCshTransaction.setTransactionDate(df.parse(df.format(new Date())));
        hlsCusCshTransaction.setPenaltyCalcDate(df.parse(df.format(new Date())));
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
    private HlsCusCshWriteOff setCshWriteOff(WriteOffTemp ln, HlsCusCshWriteOff cshWriteOff, Long cshTransactionId, String writeOffType) throws ParseException {

        cshWriteOff.setWriteOffType(writeOffType);
        cshWriteOff.setWriteOffDate(df.parse(df.format(new Date())));
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
    private void updateCashflow(IRequest requestCtx, HlsCusCshWriteOff cshWriteOff) throws ResMessageException {

        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        //剩余可核销金额 < 本次核销金额
        Double residualAmount = sub(cashflow.getDueAmount(), nvl(cashflow.getReceivedAmount(), 0.0));
        if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == -1) {
            throw new ResMessageException("抵扣金额:" + cshWriteOff.getWriteOffDueAmount() + " 大于剩余可核销金额: " + residualAmount);
        }

        Double residualPrincipal = sub(nvl(cashflow.getPrincipal(), 0.0), nvl(cashflow.getReceivedPrincipal(), 0.0));
        if (residualPrincipal.compareTo(nvl(cshWriteOff.getWriteOffPrincipal(), 0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffPrincipal(), 0.0) + " 大于剩余可核销本金: " + residualPrincipal);
        }

        Double residualInterest = sub(nvl(cashflow.getInterest(), 0.0), nvl(cashflow.getReceivedInterest(), 0.0));
        if (residualInterest.compareTo(nvl(cshWriteOff.getWriteOffInterest(), 0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffInterest(), 0.0) + " 大于剩余可核销本金: " + residualInterest);
        }

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

    //抵扣逻辑
    private void deduction(IRequest requestCtx, List<WriteOffTemp> tempLists) throws HlsCusException,ParseException, ResMessageException {
        //批量处理退还类型现金流抵扣逻辑
        for (int i = 0; i < tempLists.size(); i++) {
            /*step1:进行NORMAL合同的保证金类型判断，不符合条件直接退出
            PERIOD_FINAL_DEDUCTIBLE 保证金抵扣租金 PERIOD_FINAL_RETURN 保证金退还
            PERIOD_FINAL_DECHARGE 保证金抵扣手续费 DEPOSIT_NO 无保证金*/
            if(!"PERIOD_FINAL_DEDUCTIBLE".equals(tempLists.get(i).getDepositDeduction())){
                logger.info("合同："+tempLists.get(i).getContractNumber()+"不是保证金抵扣租金类型，不能保证金抵扣租金");
                continue;
            }

            //step2:查询同期对应租金 1L为租金,若不符合条件直接退出
            WriteOffTemp temp = new WriteOffTemp();
            temp.setCfItem(1L);
            temp.setContractId(tempLists.get(i).getContractId());
            temp.setTimes(tempLists.get(i).getTimes());
            List<WriteOffTemp> retalTempLists = writeOffTempMapper.selectDepositInfo(temp);
            if(retalTempLists.size()==0){
                logger.info(tempLists.get(i).getContractNumber()+"第"+tempLists.get(i).getTimes()+"期租金现金流不符合条件");
                continue;
            }
            if(retalTempLists.size()>1){
                logger.info(retalTempLists.get(0).getContractNumber()+"第"+retalTempLists.get(0).getTimes()+"期租金现金流不是唯一条数据");
                continue;
            }
            if(!retalTempLists.get(0).getWriteOffDueAmount().equals(tempLists.get(i).getWriteOffDueAmount())){
                logger.info(retalTempLists.get(0).getContractNumber()+"第"+retalTempLists.get(0).getTimes()+"期租金与退还类型现金流不是足额抵扣(两个未收金额不等)");
                continue;
            }

            //step3:插入deduction现金事务 （一个事务 对应两个核销数据，一个租金收款核销，一个保证金退还类型的付款核销）
            HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
            hlsCusCshTransaction.setTransactionType("DEDUCTION");//抵扣类型现金事务
            hlsCusCshTransaction.setBusinessType("DEPOSIT");//保证金
            hlsCusCshTransaction.setTransactionAmount(tempLists.get(i).getWriteOffDueAmount());
            hlsCusCshTransaction.setCurrencyCode("CNY");
            hlsCusCshTransaction = setCshTransaction(requestCtx, hlsCusCshTransaction);
            cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);

            //step4-1:核销数据产生（退还类型的保证金 收款）
            String deductionWriteOffType = "RECEIPT_CREDIT";
            HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
            cshWriteOff.setCshWriteOffAmount(tempLists.get(i).getWriteOffDueAmount());
            cshWriteOff.setWriteOffDueAmount(tempLists.get(i).getWriteOffDueAmount());
            cshWriteOff.setWriteOffPrincipal(tempLists.get(i).getWriteOffPrincipal());
            cshWriteOff.setWriteOffInterest(tempLists.get(i).getWriteOffInterest());
            cshWriteOff.setCompanyId(requestCtx.getCompanyId());
            cshWriteOff = setCshWriteOff(tempLists.get(i), cshWriteOff, hlsCusCshTransaction.getTransactionId(), deductionWriteOffType);
            cshWriteOff = cshWriteOffService.insertSelective(requestCtx, cshWriteOff);

            //step4-2:核销数据产生（租金 付款）
            deductionWriteOffType = "PAYMENT_DEBT";
            HlsCusCshWriteOff cshWriteOffRental = new HlsCusCshWriteOff();
            cshWriteOffRental.setCshWriteOffAmount(retalTempLists.get(0).getWriteOffDueAmount());
            cshWriteOffRental.setWriteOffDueAmount(retalTempLists.get(0).getWriteOffDueAmount());
            cshWriteOffRental.setWriteOffPrincipal(retalTempLists.get(0).getWriteOffPrincipal());
            cshWriteOffRental.setWriteOffInterest(retalTempLists.get(0).getWriteOffInterest());
            cshWriteOffRental.setCompanyId(requestCtx.getCompanyId());
            cshWriteOffRental = setCshWriteOff(retalTempLists.get(0), cshWriteOffRental, hlsCusCshTransaction.getTransactionId(), deductionWriteOffType);
            cshWriteOffRental = cshWriteOffService.insertSelective(requestCtx, cshWriteOffRental);

            //step5:更新现金流
            updateCashflow(requestCtx, cshWriteOff);
            updateCashflow(requestCtx, cshWriteOffRental);

            //step6-1:凭证 租金,1 csh_write_off
            AbstractJeTrxService paymentEqimentJeTrxRental = JeTrxCommonService.map.get("CSH_DEDUCTION");
            Map paramDeductsRental = new HashMap<>();
            paramDeductsRental.put("jeTrxId", cshWriteOffRental.getWriteOffId());
            paramDeductsRental.put("companyId", requestCtx.getCompanyId());
            paramDeductsRental.put("contractId", cshWriteOffRental.getContractId());
            paramDeductsRental.put("jeSourceDoc", JE_CON_CONTRACT);
            paramDeductsRental.put("jeSourceId", cshWriteOffRental.getContractId());
            paymentEqimentJeTrxRental.process(requestCtx, paramDeductsRental);
            //step6-1:凭证 保证金 退还类型现金流52, csh_write_off
            AbstractJeTrxService paymentEqimentJeTrx = JeTrxCommonService.map.get("CSH_DEDUCTION");
            Map paramDeducts = new HashMap<>();
            paramDeducts.put("jeTrxId", cshWriteOff.getWriteOffId());
            paramDeducts.put("companyId", requestCtx.getCompanyId());
            paramDeducts.put("contractId", cshWriteOff.getContractId());
            paramDeducts.put("jeSourceDoc", JE_CON_CONTRACT);
            paramDeducts.put("jeSourceId", cshWriteOff.getContractId());
            paymentEqimentJeTrx.process(requestCtx, paramDeducts);
        }
    }


    private void deductionBackUp(IRequest requestCtx, List<WriteOffTemp> tempLists) throws ParseException, ResMessageException {

//        List<WriteOffTemp> writeOffTemps = new ArrayList<>();
//        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < tempLists.size(); i++) {

            //相同合同id进行分组处理
            /*for (int j = i + 1; j < tempLists.size(); j++) {
                if (tempLists.get(j).getContractId().equals(tempLists.get(i).getContractId()) && !ints.contains(j)){
                    ints.add(j);
                    writeOffTemps.add(tempLists.get(j));
                }
            }
            //添加原本的现金流
            if(!ints.contains(i)){
                writeOffTemps.add(tempLists.get(i));
            }*/

            //合同组处理
//            if(writeOffTemps.size()>0){
            /*PERIOD_FINAL_DEDUCTIBLE 保证金抵扣租金
            PERIOD_FINAL_RETURN 保证金退还
            PERIOD_FINAL_DECHARGE 保证金抵扣手续费
            DEPOSIT_NO 无保证金*/
            String deductionWriteOffType = "RECEIPT_CREDIT";
            if("PERIOD_FINAL_DEDUCTIBLE".equals(tempLists.get(i).getDepositDeduction())){
                deductionWriteOffType = "RECEIPT_CREDIT";
            }else{
                logger.info("合同id："+tempLists.get(i).getContractId()+"不是保证金抵扣租金类型，不进行虚拟收款抵扣");
                continue;
            }
            //插入deduction 现金事务--虚拟收款抵扣
            HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
            hlsCusCshTransaction.setTransactionType("VIRTUAL_DEDUCTION");//虚拟收款抵扣
            hlsCusCshTransaction.setBusinessType("DEPOSIT");//保证金
            hlsCusCshTransaction.setTransactionAmount(tempLists.get(i).getWriteOffDueAmount());
            hlsCusCshTransaction.setCurrencyCode("CNY");
            hlsCusCshTransaction = setCshTransaction(requestCtx, hlsCusCshTransaction);
            cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);

            //核销
            HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
            cshWriteOff.setCshWriteOffAmount(tempLists.get(i).getWriteOffDueAmount());
            cshWriteOff.setWriteOffDueAmount(tempLists.get(i).getWriteOffDueAmount());
            cshWriteOff.setWriteOffPrincipal(tempLists.get(i).getWriteOffPrincipal());
            cshWriteOff.setWriteOffInterest(tempLists.get(i).getWriteOffInterest());
            cshWriteOff.setCompanyId(requestCtx.getCompanyId());
            cshWriteOff = setCshWriteOff(tempLists.get(i), cshWriteOff, hlsCusCshTransaction.getTransactionId(), deductionWriteOffType);
            cshWriteOff = cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
            //更新现金流
            updateCashflow(requestCtx, cshWriteOff);

            //凭证 , csh_write_off
            AbstractJeTrxService paymentEqimentJeTrx = JeTrxCommonService.map.get("CSH_DEDUCTION");
            Map paramDeducts = new HashMap<>();
            paramDeducts.put("jeTrxId", cshWriteOff.getWriteOffId());
            paramDeducts.put("companyId", requestCtx.getCompanyId());
            paramDeducts.put("contractId", cshWriteOff.getContractId());
            paramDeducts.put("jeSourceDoc", JE_CON_CONTRACT);
            paramDeducts.put("jeSourceId", cshWriteOff.getContractId());
            paymentEqimentJeTrx.process(requestCtx, paramDeducts);
        }
    }
}