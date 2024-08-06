package hls.core.hls.service.impl;

import cn.hutool.core.date.DateUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusConDebtExemptionReqCfMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionParaMapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import hls.core.fnd.dto.HlsPenaltyProfileDtl;
import hls.core.fnd.mapper.HlsPenaltyProfileDtlMapper;
import hls.core.hls.service.HlsDayEndCommon;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import static com.hand.hls.sys.utils.OracleUtils.nvl;
import java.util.*;

@Service
public class HlsPenaltyCalServiceImpl implements HlsDayEndCommon {
    @Autowired
    HlsCusConContractMapper conContractMapper;
    @Autowired
    HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    HlsPenaltyProfileDtlMapper hlsPenaltyProfileDtlMapper;
    @Autowired
    HlsCusConDebtExemptionReqCfMapper conDebtExemptionReqCfMapper;
    private static final String DayEndType = "PENALTY";
    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;
    @Autowired
    private HlsCusPrjQuotationMapper quotationMapper;

    public HlsPenaltyCalServiceImpl() {
    }

    public String getDayEndType() {
        return "PENALTY";
    }

    public void process(Map map) {
        HlsCusConContract contract;
        if (map.get("contractId") != null && map.get("contractId") != "") {
            Long contractId = Long.valueOf(map.get("contractId").toString());
            contract = new HlsCusConContract();
            contract.setContractId(contractId);
            contract = (HlsCusConContract)this.conContractMapper.selectByPrimaryKey(contract);
            if (contract != null && !contract.getContractStatus().equals("INCEPT")) {
                throw new IllegalArgumentException("合同状态错误不能计算罚息！");
            }

            this.calculatePenalty(contract, (Date)map.get("dayEndDate"));
        } else {
            List<HlsCusConContract> conContractList = this.conContractMapper.selectNeedCalcPenaltyContract(Long.parseLong(map.get("companyId").toString()));
            contract = new HlsCusConContract();
            contract.setCompanyId(Long.parseLong(map.get("companyId").toString()));
            contract.setDayEndDate((Date)map.get("dayEndDate"));
            List<HlsCusConContract> overContractList = this.conContractMapper.selectOverContract(contract);
            Iterator var5;
            HlsCusConContract con = new HlsCusConContract();
            if (overContractList != null && overContractList.size() > 0) {
                /*var5 = overContractList.iterator();

                while(var5.hasNext()) {
                    con = (HlsCusConContract)var5.next();
                    con.setOverdueStatus("Y");
                    con.setCompanyId(1L);
                    this.conContractMapper.updateByPrimaryKey(con);
                }*/
                for (HlsCusConContract conContract : overContractList) {
                    HlsCusConContract conContract1 = this.conContractMapper.selectByPrimaryKey(conContract);
                    conContract1.setOverdueStatus("Y");
                    this.conContractMapper.updateByPrimaryKey(conContract1);
                }
            }

            if (conContractList != null && conContractList.size() > 0) {
                var5 = conContractList.iterator();

                while(var5.hasNext()) {
                    con = (HlsCusConContract)var5.next();
                    this.calculatePenalty(con, (Date)map.get("dayEndDate"));
                }
            }
        }

    }

    public void calculatePenalty(HlsCusConContract contract, Date dayEndDate) {
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setDayEndDate(dayEndDate);
        contractCashflow.setContractId(contract.getContractId());
        List<HlsCusConContractCashflow> contractCashflowsList = this.conContractCashflowMapper.selectCalcPenaltyCashFlowByConId(contractCashflow);
        if (contractCashflowsList != null && contractCashflowsList.size() > 0) {
            Iterator var5 = contractCashflowsList.iterator();

            while(true) {
                while(var5.hasNext()) {
                    HlsCusConContractCashflow ccc = (HlsCusConContractCashflow)var5.next();
                    ccc.setOverdueStatus("Y");
                    this.conContractCashflowMapper.updateByPrimaryKeySelective(ccc);
                    /*HlsPenaltyProfileDtl hppd = new HlsPenaltyProfileDtl();
                    hppd.setCfItem(ccc.getCfItem());
                    hppd.setPenaltyProfile(contract.getPenaltyProfile());
                    hppd = (HlsPenaltyProfileDtl)this.hlsPenaltyProfileDtlMapper.selectByPrimaryKey(hppd);*/
                    HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                    cshWriteOff.setCashflowId(ccc.getCashflowId());
                    cshWriteOff.setDueDate(ccc.getDueDate());
                    List<HlsCusCshWriteOff> cshWriteOffList = this.cshWriteOffMapper.selectPenaltyWriteOff(cshWriteOff);
                    double received = 0.0D;
                    double writeOffDueAmount = 0.0D;

                    HlsCusCshWriteOff cwo;
                    for(Iterator var14 = cshWriteOffList.iterator(); var14.hasNext(); received += cwo.getWriteOffDueAmount() * (double)((int)((cwo.getPenaltyCalcDate().getTime() - ccc.getDueDate().getTime()) / 86400000L - 0)) * contract.getPenaltyRate()) {
                        cwo = (HlsCusCshWriteOff)var14.next();
                        writeOffDueAmount += cwo.getWriteOffDueAmount();
                    }

                    double derateAmount = 0.0D;
                    HlsCusConDebtExemptionReqCf conDebtExemptionReqCf = new HlsCusConDebtExemptionReqCf();
                    conDebtExemptionReqCf.setCashflowId(ccc.getCashflowId());
                    List<HlsCusConDebtExemptionReqCf> list = this.conDebtExemptionReqCfMapper.selectReqCfByCashflowId(conDebtExemptionReqCf);
                    Iterator var18 = list.iterator();

                    while(var18.hasNext()) {
                        HlsCusConDebtExemptionReqCf cf = (HlsCusConDebtExemptionReqCf)var18.next();
                        if (cf.getExemptionAmount() != null) {
                            derateAmount += cf.getExemptionAmount();
                        }
                    }

                    double unreceived = (ccc.getDueAmount() - writeOffDueAmount) * (double)((int)((dayEndDate.getTime() - ccc.getDueDate().getTime()) / 86400000L - 0)) * contract.getPenaltyRate();
                    double profileTotalAmount = unreceived + received - derateAmount;
                    HlsCusConContractCashflow contractCashflow2 = new HlsCusConContractCashflow();
                    contractCashflow2.setGeneratedSourceDocId(ccc.getCashflowId());
                    contractCashflow2.setGeneratedSource("DAYEND");
                    contractCashflow2.setOverdueStatus("N");
                    contractCashflow2.setContractId(contract.getContractId());
                    List<HlsCusConContractCashflow> contractCashflowsList2 = this.conContractCashflowMapper.selectCalcPenaltyCashFlowBySourceId(contractCashflow2);
                    HlsCusConContractCashflow c;
                    if (contractCashflowsList2.size() > 0) {
                        for(Iterator var26 = contractCashflowsList2.iterator(); var26.hasNext(); this.conContractCashflowMapper.updateByPrimaryKeySelective(c)) {
                            c = (HlsCusConContractCashflow)var26.next();
                            c.setDueDate(dayEndDate);
                            c.setFinIncomeDate(dayEndDate);
                            c.setDueAmount(profileTotalAmount);
                            if ((new BigDecimal(c.getReceivedAmount())).compareTo(new BigDecimal(0)) == 0) {
                                c.setWriteOffFlag("NOT");
                            } else if ((new BigDecimal(c.getReceivedAmount())).compareTo(new BigDecimal(c.getDueAmount())) >= 0) {
                                c.setWriteOffFlag("FULL");
                            } else {
                                c.setWriteOffFlag("PARTIAL");
                            }
                        }
                    } else {
                        HlsCusConContractCashflow contractCashflowIns = new HlsCusConContractCashflow();
                        contractCashflowIns.setContractId(ccc.getContractId());
                        contractCashflowIns.setCfItem(9L);
                        contractCashflowIns.setCfType(9L);
                        contractCashflowIns.setCfDirection("INFLOW");
                        contractCashflowIns.setCfStatus("RELEASE");
                        contractCashflowIns.setTimes(ccc.getTimes());
                        contractCashflowIns.setDueDate(dayEndDate);
                        contractCashflowIns.setFinIncomeDate(dayEndDate);
                        contractCashflowIns.setDueAmount(profileTotalAmount);
                        contractCashflowIns.setWriteOffFlag("NOT");
                        contractCashflowIns.setPenaltyProcessStatus("NORMAL");
                        contractCashflowIns.setBillingStatus("NOT");
                        contractCashflowIns.setOverdueStatus("N");
                        contractCashflowIns.setGeneratedSource("DAYEND");
                        contractCashflowIns.setGeneratedSourceDocId(ccc.getCashflowId());
                        this.conContractCashflowMapper.insertSelective(contractCashflowIns);
                    }
                }

                return;
            }
        }
    }

    /**
     * 获取罚息现金流集合
     *
     * @param contractId   需要计算罚息的合同
     * @param penaltyDate 逾期计算日期(默认为当前系统时间)
     */
    public List<HlsCusConContractCashflow> getPenaltyCashflows(Long contractId,Date penaltyDate){
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract = conContractMapper.selectByPrimaryKey(contract);
        List<HlsCusConContractCashflow> list = new ArrayList<>();

        HlsCusConContractCashflow cash =new HlsCusConContractCashflow();
        cash.setContractId(contractId);
        cash.setDayEndDate(penaltyDate);
        List<HlsCusConContractCashflow> cusConContractCashflowList = conContractCashflowMapper.selectForOverDueDayEndNew(cash);

        for (HlsCusConContractCashflow cashflow : cusConContractCashflowList) {
            //计算罚息并更新
            HlsCusConContractCashflow conContractCashflow = updatePenaltyCashfow(contract, cashflow, penaltyDate,false);
            if(conContractCashflow != null){
                list.add(conContractCashflow);
            }
        }

        return list;
    }

    private double calcReceivedAmt(long cashflowId, Date dueDate, double penaltyRate, long gracePeriod){
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCashflowId(cashflowId);
        cshWriteOff.setDueDate(dueDate);
        List<HlsCusCshWriteOff> cshWriteOffList = cshWriteOffMapper.selectPenaltyWriteOff(cshWriteOff);
        double received = 0d;
        if(CollectionUtils.isNotEmpty(cshWriteOffList)){
            for (HlsCusCshWriteOff cusCshWriteOff : cshWriteOffList) {
                // 逾期天数
                long overdueDays = DateUtil.betweenDay(cusCshWriteOff.getPenaltyCalcDate(), dueDate, true);
                long overDueDaysWithoutGracePeriod = overdueDays - gracePeriod;
                if(overDueDaysWithoutGracePeriod > 0){
                    double receiving = MathUtil.mul(
                            MathUtil.mul(cusCshWriteOff.getWriteOffDueAmount(), penaltyRate),
                            overdueDays
                    );
                    received = MathUtil.add(received, receiving);
                }
            }
        }
        return received;
    }
    private double calcUnReceivedAmt(double surplusAmount, long overdueDays, double penaltyRate, long gracePeriod){
        long overDueDaysWithoutGracePeriod = overdueDays - gracePeriod;
        double unreceived = 0d;
        if (overDueDaysWithoutGracePeriod > 0) {
            unreceived = MathUtil.mul(
                    MathUtil.mul(surplusAmount, penaltyRate),
                    overdueDays
            );
        }
        return unreceived;
    }

    private HlsCusConContractCashflow initCashflow(long contractId, long cfItem, long times, Date dueDate,
                                                   double dueAmount, double vatRate, long generatedSourceDocId,
                                                   long generatedSourceDocLineId){
        HlsCusConContractCashflow contractCashflowIns = new HlsCusConContractCashflow();
        contractCashflowIns.setContractId(contractId);
        contractCashflowIns.setCfType(9L);
        contractCashflowIns.setCfItem(cfItem);
        contractCashflowIns.setCfDirection("INFLOW");
        contractCashflowIns.setCfStatus("RELEASE");
        contractCashflowIns.setTimes(times);
        contractCashflowIns.setDueDate(dueDate);
        contractCashflowIns.setCalcDate(dueDate);
        if(Double.compare(dueAmount, 0.0) == 0){
            contractCashflowIns.setDueAmount(0.0);
            contractCashflowIns.setNetDueAmount(0.0);
            contractCashflowIns.setVatDueAmount(0.0);
            contractCashflowIns.setWriteOffFlag(HlsConstantUtil.WriteOffFlag.FULL);
        }else{
            contractCashflowIns.setDueAmount(dueAmount);
            contractCashflowIns.setNetDueAmount(MathUtil.div(dueAmount, MathUtil.add(1,vatRate), 2));
            contractCashflowIns.setVatDueAmount(MathUtil.sub(dueAmount, contractCashflowIns.getNetDueAmount(), 2));
            contractCashflowIns.setWriteOffFlag(HlsConstantUtil.WriteOffFlag.NOT);
        }
        /*contractCashflowIns.setFixPrincipalFlag(N);
        contractCashflowIns.setFixRentalFlag(N);
        contractCashflowIns.setInterestOnlyFlag(N);
        contractCashflowIns.setEqualFlag(N);
        contractCashflowIns.setManualFlag(N);
        contractCashflowIns.setBeginningOfLeaseYear(beginningOfLeaseYear);*/
        contractCashflowIns.setGeneratedSource("DAY_END");
        contractCashflowIns.setBillingStatus("NOT");
        contractCashflowIns.setOverdueStatus("N");
        contractCashflowIns.setPenaltyProcessStatus("N");
        contractCashflowIns.setGeneratedSource("DAY_END");
        contractCashflowIns.setGeneratedSourceDocId(generatedSourceDocId);
        contractCashflowIns.setGeneratedSourceDocLineId(generatedSourceDocLineId);
        contractCashflowIns.setCreationDate(new Date());
        contractCashflowIns.setLastUpdateDate(new Date());
        return contractCashflowIns;
    }

    private void updateCashflow(HlsCusConContractCashflow cashflow, Date dueDate, double dueAmount, double vatRate, long generatedSourceDocId,
                                long generatedSourceDocLineId){
        cashflow.setDueDate(dueDate);
        cashflow.setCalcDate(dueDate);
        if(Double.compare(dueAmount, 0.0) == 0 || Double.compare(dueAmount, nvl(cashflow.getReceivedAmount(),0.0)) == 0){
            cashflow.setWriteOffFlag(HlsConstantUtil.WriteOffFlag.FULL);
        }else{
            if(Double.compare(nvl(cashflow.getReceivedAmount(),0.0), 0.0) > 0){
                cashflow.setWriteOffFlag(HlsConstantUtil.WriteOffFlag.PARTIAL);
            }else{
                cashflow.setWriteOffFlag(HlsConstantUtil.WriteOffFlag.NOT);
            }
        }
        cashflow.setDueAmount(dueAmount);
        cashflow.setNetDueAmount(MathUtil.div(dueAmount, MathUtil.add(1,vatRate), 2));
        cashflow.setVatDueAmount(MathUtil.sub(dueAmount, cashflow.getNetDueAmount(), 2));
        cashflow.setGeneratedSource("DAY_END");
        cashflow.setGeneratedSourceDocId(generatedSourceDocId);
        cashflow.setGeneratedSourceDocLineId(generatedSourceDocLineId);
        cashflow.setLastUpdateDate(new Date());
    }

    public HlsCusConContractCashflow dealCashflowForPenaltyAmt(double total, double vatRate, long overdueDays, long cfItem, HlsCusConContractCashflow cashflow,
                                                               Date dayEndDate,boolean flag){
        // 查询该条现金流是否已经插入罚息
        HlsCusConContractCashflow cashFlowUpdateParam = new HlsCusConContractCashflow();
        cashFlowUpdateParam.setContractId(cashflow.getContractId());
        cashFlowUpdateParam.setCfItem(cfItem);
        cashFlowUpdateParam.setTimes(cashflow.getTimes());
        cashFlowUpdateParam.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
        cashFlowUpdateParam.setGeneratedSourceDocLineId(cashflow.getCashflowId());
        cashFlowUpdateParam.setGeneratedSource("DAY_END");
        cashFlowUpdateParam.setOverdueStatus("N");
        List<HlsCusConContractCashflow> updateCashflowList = conContractCashflowMapper.select(cashFlowUpdateParam);

        if (CollectionUtils.isEmpty(updateCashflowList)) {
            if(total == 0.0){  // 罚息金额为0不需要生成罚息现金流
                return null;
            }
            HlsCusConContractCashflow penalty = initCashflow(cashflow.getContractId(), cfItem, cashflow.getTimes(),
                    dayEndDate, total, vatRate, cashflow.getGeneratedSourceDocId(),
                    cashflow.getCashflowId());
            if(!flag) {
                return penalty;
            }
            conContractCashflowMapper.insertSelective(penalty);
        } else {
            HlsCusConContractCashflow penalty = updateCashflowList.get(0);
            if(StringUtils.equals(HlsConstantUtil.WriteOffFlag.FULL, penalty.getWriteOffFlag()) &&
                    Double.compare(MathUtil.round(penalty.getDueAmount(), 2), total) == 0){
                if(!flag) {
                    return penalty;
                }else{
                    return null;
                }
            }
            updateCashflow(penalty, dayEndDate, total, vatRate, cashflow.getGeneratedSourceDocId(), cashflow.getCashflowId());
            if(!flag) {
                return penalty;
            }
            conContractCashflowMapper.updateByPrimaryKey(penalty);
            //更新
            if (updateCashflowList.size() > 1) {
                for (HlsCusConContractCashflow delete : updateCashflowList) {
                    if (!delete.getCashflowId().equals(penalty.getCashflowId())) {
                        conContractCashflowMapper.deleteByPrimaryKey(delete);
                    }
                }
            }
        }
       return null;
    }
    /**
     * 计算罚息
     * con_contract_cashflow.penalty_process_status<> 'NORMAL' 跳过 (其他两种方式:SUSPEND 暂停计算/EXEMPT 豁免在罚息减免处考虑)
     * 已核销金额罚息 = 已核销金额*(核销日期-应还日期-宽限期)*罚息率 =write_off_amount*( write_off_date- due_date - grace_period )* penalty_rate
     * 备注: 已核销金额罚息累加(每次核销的已核销金额都会有两天的宽限期)
     * 未核销金额罚息=(应还金额-已还金额)*(sysdate-应还日期-宽限期)*罚息率 =(due_amount-received_amount)*(sysdate-due_date- grace_period)*penalty_rate
     *
     * @param contract   合同 访问器模式
     * @param cashflow   现金流 访问器模式
     * @param dayEndDate 计算日期
     * @param flag 是否更新罚息的现金流
     */
    public HlsCusConContractCashflow updatePenaltyCashfow(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate,boolean flag) {
        if (cashflow == null){
            return null;
        }
        if (!"NORMAL".equals(cashflow.getPenaltyProcessStatus()) && !"N".equals(cashflow.getPenaltyProcessStatus())) {
            return null;
        }
        // 宽限期天数
        long gracePeriod = 0L;
        //罚息率
        HlsCusPrjQuotation prjQuotation=new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(contract.getQuotationId());
        prjQuotation=quotationMapper.selectByPrimaryKey(prjQuotation.getQuotationId());
        Double penaltyRate=nvl(hlsProductDefinitionParaMapper.getDefaulValue(prjQuotation.getPlanId(),"PENALTY_RATE"),0d);
        // 逾期天数
        long overdueDays = DateUtil.betweenDay(dayEndDate, cashflow.getDueDate(), true);
        if(overdueDays <= gracePeriod){
            return null;
        }
        // 已收金额产生的罚息
        double received = calcReceivedAmt(cashflow.getCashflowId(), cashflow.getDueDate(), penaltyRate, gracePeriod);
        double unreceived = calcUnReceivedAmt(MathUtil.sub(cashflow.getDueAmount(), nvl(cashflow.getReceivedAmount(), 0d)),
                overdueDays, penaltyRate, gracePeriod);
        double total = MathUtil.add(received, unreceived, 2);
        return dealCashflowForPenaltyAmt(total, contract.getVatRate(), overdueDays, 9L, cashflow, dayEndDate, flag);
    }

/*    public HlsCusConContractCashflow updateManuFactoryPenaltyCashfow(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate,boolean flag) {
        if (!"NORMAL".equals(cashflow.getPenaltyProcessStatus()) && !"N".equals(cashflow.getPenaltyProcessStatus())) {
            return null;
        }
        HlsCusPrjQuotation quotation = prjQuotationMapper.selectByPrimaryKey(contract.getQuotationId());
        // 宽限期天数
        long manufacturerGraceDay = Optional.of(quotation.getManufacturerGraceDay()).get();
        // 罚息率
        double manufacturerPenaltyRate = Optional.of(quotation.getManufacturerPenaltyRate()).get();
        // 逾期天数
        long overdueDays = DateUtil.betweenDay(dayEndDate, cashflow.getDueDate(), true);
        if(overdueDays <= manufacturerGraceDay){
            return null;
        }
        // 已收金额产生的罚息
        double received = calcReceivedAmtManuFactory(cashflow.getContractId(), cashflow.getDueDate(), manufacturerPenaltyRate, manufacturerGraceDay);
        double unreceived = calcUnReceivedAmtManuFactory(cashflow.getContractId(), overdueDays, manufacturerPenaltyRate);
        double total = MathUtil.add(received, unreceived, 2);
        return dealCashflowForPenaltyAmt(total, contract.getVatRate(), overdueDays, 99L, cashflow, dayEndDate, flag);
    }*/

    /**
     * 获取厂商罚息现金流集合
     *
     * @param contractId   需要计算罚息的合同
     * @param penaltyDate 逾期计算日期(默认为当前系统时间)
     */
    /*public List<HlsCusConContractCashflow> getManuFactoryPenaltyCashflows(Long contractId,Date penaltyDate){
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract = conContractMapper.selectByPrimaryKey(contract);
        List<HlsCusConContractCashflow> list = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("contractId", contractId);
        param.put("dayEndDate", penaltyDate);
        List<HlsCusConContractCashflow> cashflowList = conContractCashflowMapper.selectForManuFactoryOverDueDayEnd(param);
        if(CollectionUtils.isNotEmpty(cashflowList)){
            HlsCusConContractCashflow cashflow = cashflowList.get(0);
            //计算罚息并更新
            HlsCusConContractCashflow conContractCashflow = updateManuFactoryPenaltyCashfow(contract, cashflow, penaltyDate,false);
            if(conContractCashflow != null){
                list.add(conContractCashflow);
            }
        }
        return list;
    }*/
}
