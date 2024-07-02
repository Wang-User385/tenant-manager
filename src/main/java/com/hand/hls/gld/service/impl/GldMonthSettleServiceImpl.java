package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractRiskFund;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConContractRiskFundMapper;
import com.hand.hls.cont.service.HlsCusConContractRiskFundService;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusFinanceIncomeH;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.mapper.HlsCusGldLonContractFinCostMapper;
import com.hand.hls.gld.service.*;
import com.hand.hls.utils.ResMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Auther: hxh
 * @Date: 2020年6月1日
 * @Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GldMonthSettleServiceImpl extends BaseServiceImpl<HlsCusContractFinanceIncome> implements IGldMonthSettleService {

    // 预月结凭证事务
    public static final String PRE_FIN_INCOME_RECOGNITION = "PRE_FIN_INCOME_RECOGNITION";
    // 月结凭证事务
    public static final String FIN_INCOME_RECOGNITION = "FIN_INCOME_RECOGNITION";
    // 印花税计提凭证事务
    public static final String STAMP_DUTY_CONFIRM = "STAMP_DUTY_CONFIRM";
    // 单据类型
    public static final String CON_CONTRACT = "CON_CONTRACT";

    @Autowired
    private JeTrxCommonService jeTrxCommonService;

    @Autowired
    private IContractFinanceIncomeService contractFinanceIncomeService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private IHlsCusFinanceIncomeHService hlsCusFinanceIncomeHService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusLonContractWithdrawMapper hlsCusLonContractWithdrawMapper;
    @Autowired
    private HlsCusConContractRiskFundMapper hlsCusConContractRiskFundMapper;
    @Autowired
    private HlsCusConContractRiskFundService hlsCusConContractRiskFundService;

    @Autowired
    private HlsCusGldLonContractFinCostMapper gldLonContractFinCostMapper;
    @Autowired
    private GldLonContractFinCostService gldLonContractFinCostService;
    @Override
    public List<HlsCusContractFinanceIncome> preFinIncomeRecognition(IRequest requestCtx, List<HlsCusContractFinanceIncome> contractFinanceIncomeList,String periodName) throws ResMessageException {
        Map params = new HashMap();
        final String documentCategory = "PRE_FIN_INCOME_RECOGNITION";
        final String documentType = "PRE_FIN_INCOME_RECOGNITION";
        final String businessType = "PRE_FIN_INCOME_RECOGNITION";
        //获取批次编码
        String code = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, documentCategory, documentType, businessType, params);
        for(HlsCusContractFinanceIncome dt:contractFinanceIncomeList){
            if(dt.getCeaseInteresstFlag().equals("N")){
                if(dt.getPrePostFlag().equals("N") && dt.getPostFlag().equals("N")){
                    AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get(PRE_FIN_INCOME_RECOGNITION);
                    HlsCusContractFinanceIncome dt2 = contractFinanceIncomeService.selectByPrimaryKey(requestCtx,dt);
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(dt2.getContractId());
                    hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestCtx,hlsCusConContract);
                    Map transactionParams = new HashMap<>();
                    transactionParams.put("jeTrxId", dt2.getFinanceIncomeId());
                    transactionParams.put("periodName", periodName);
                    transactionParams.put("cfItem", dt2.getCfItem());
                    transactionParams.put("companyId", requestCtx.getCompanyId());
                    transactionParams.put("contractId",dt2.getContractId());
                    transactionParams.put("sourceDoc", CON_CONTRACT);
                    transactionJeTrxService.process(requestCtx, transactionParams);
                    dt2.setPrePostFlag("Y");
                    contractFinanceIncomeService.updateByPrimaryKeySelective(requestCtx,dt2);
                    //存预月结快照
                    Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
                    HlsCusFinanceIncomeH hlsCusFinanceIncomeH = new HlsCusFinanceIncomeH();
                    hlsBeanRefUtilService.setFieldValue(hlsCusFinanceIncomeH , map);
                    hlsCusFinanceIncomeH.setFinanceIncomeId(null);
                    hlsCusFinanceIncomeH.setSourceFinanceIncomeId(dt.getFinanceIncomeId());
                    //获取下一个版本id
                    Long versionId = hlsCusFinanceIncomeHService.selectVersionCount(hlsCusFinanceIncomeH) +1;
                    hlsCusFinanceIncomeH.setVersionId(versionId);
                    hlsCusFinanceIncomeH.setPreMonthDate(new Date());
                    hlsCusFinanceIncomeH.setPreMonthBy(requestCtx.getUserId());
                    hlsCusFinanceIncomeH.setOperation("预月结");
                    hlsCusFinanceIncomeH.setOperationDate(new Date());
                    hlsCusFinanceIncomeH.setOperationBy(requestCtx.getUserId());
                    hlsCusFinanceIncomeH.setPrePostFlag("Y");
                    hlsCusFinanceIncomeH.setBatch(code);
                    hlsCusFinanceIncomeH.set__status("add");
                    hlsCusFinanceIncomeH = hlsCusFinanceIncomeHService.insertSelective(requestCtx,hlsCusFinanceIncomeH);

                }
            }

        }
        return contractFinanceIncomeList;
    }

    @Override
    public List<HlsCusContractFinanceIncome> reversePreFinIncome(IRequest requestCtx, List<HlsCusContractFinanceIncome> contractFinanceIncomeList,String periodName) throws ResMessageException {
        for(HlsCusContractFinanceIncome dt:contractFinanceIncomeList){
            if(dt.getCeaseInteresstFlag().equals("N")){
                if(dt.getPrePostFlag().equals("Y") && dt.getPostFlag().equals("N")){
                    AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get(PRE_FIN_INCOME_RECOGNITION);
                    dt = contractFinanceIncomeService.selectByPrimaryKey(requestCtx,dt);
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(dt.getContractId());
                    hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestCtx,hlsCusConContract);
                    Map transactionParams = new HashMap<>();
                    transactionParams.put("jeTrxId", dt.getFinanceIncomeId());
                    transactionParams.put("periodName", periodName);
                    transactionParams.put("cfItem", dt.getCfItem());
                    transactionParams.put("companyId", requestCtx.getCompanyId());
                    transactionParams.put("contractId",dt.getContractId());
                    transactionParams.put("sourceDoc", CON_CONTRACT);

                    //获取当月最后一天
                    Calendar cale = Calendar.getInstance();
                    cale.add(Calendar.MONTH, 1);
                    cale.set(Calendar.DAY_OF_MONTH, 0);

                    transactionParams.put("reverseJeDate", cale.getTime());
                    transactionParams.put("reverseJeTrxId",dt.getFinanceIncomeId());
                    transactionJeTrxService.process(requestCtx, transactionParams);
                    dt.setPrePostFlag("N");
                    contractFinanceIncomeService.updateByPrimaryKeySelective(requestCtx,dt);
                    //更新预月结快照（反冲）
                    HlsCusFinanceIncomeH hlsCusFinanceIncomeH = new HlsCusFinanceIncomeH();
                    hlsCusFinanceIncomeH.setSourceFinanceIncomeId(dt.getFinanceIncomeId());
                    //获取当前版本id，默认是表中最大的version_id
                    Long versionId = hlsCusFinanceIncomeHService.selectVersionCount(hlsCusFinanceIncomeH);
                    List<HlsCusFinanceIncomeH> hlsCusFinanceIncomeHList = hlsCusFinanceIncomeHService.select(requestCtx,hlsCusFinanceIncomeH,1,10);
                    if(hlsCusFinanceIncomeHList.size()>0){
                        hlsCusFinanceIncomeH = hlsCusFinanceIncomeHList.get(0);
                        hlsCusFinanceIncomeH.setVersionId(versionId);
                        hlsCusFinanceIncomeH.setPreMonthRecoilDate(new Date());
                        hlsCusFinanceIncomeH.setPreMonthRecoilBy(requestCtx.getUserId());
                        hlsCusFinanceIncomeH.setOperation("预月结反冲");
                        hlsCusFinanceIncomeH.setOperationDate(new Date());
                        hlsCusFinanceIncomeH.setOperationBy(requestCtx.getUserId());
                        hlsCusFinanceIncomeH.setPrePostFlag("N");
                        hlsCusFinanceIncomeH.set__status("update");
                        hlsCusFinanceIncomeH = hlsCusFinanceIncomeHService.updateByPrimaryKeySelective(requestCtx,hlsCusFinanceIncomeH);
                    }

                }
            }

        }
        return contractFinanceIncomeList;
    }

    @Override
    public List<HlsCusConContract> stampDutyAccrual(IRequest requestCtx, List<HlsCusConContract> hlsCusConContractList) throws ResMessageException {
        for(HlsCusConContract dt:hlsCusConContractList){
            if(dt.getStampDutyFlag().equals("N")){
                //不生成凭证
                //AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get(STAMP_DUTY_CONFIRM);
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(dt.getContractId());
                hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestCtx,hlsCusConContract);
                /*Map transactionParams = new HashMap<>();
                transactionParams.put("jeTrxId", dt.getContractId());
                transactionParams.put("periodName", dt.getPeriodName());
                transactionParams.put("companyId", requestCtx.getCompanyId());
                transactionParams.put("contractId",dt.getContractId());
                transactionParams.put("sourceDoc", CON_CONTRACT);
                transactionJeTrxService.process(requestCtx, transactionParams);*/
                hlsCusConContract.setStampDutyFlag("Y");
                hlsCusConContractService.updateByPrimaryKeySelective(requestCtx,hlsCusConContract);
            }
        }
        return hlsCusConContractList;
    }

    @Override
    public List<HlsCusContractFinanceIncome> finIncomeRecognition(IRequest requestCtx, List<HlsCusContractFinanceIncome> contractFinanceIncomeList,String periodName) throws ResMessageException {
        for(HlsCusContractFinanceIncome dt:contractFinanceIncomeList){
            if(dt.getPostFlag().equals("N")){
                dt = contractFinanceIncomeService.selectByPrimaryKey(requestCtx,dt);
                dt.setPostFlag("Y");
                dt.setRefD01(new Date());
                contractFinanceIncomeService.updateByPrimaryKeySelective(requestCtx,dt);

                //利息收入确认凭证
                AbstractJeTrxService finIncomeRecognitionTrxService = (AbstractJeTrxService) JeTrxCommonService.map.get("FIN_INCOME_RECOGNITION");
                Map params = new HashMap();
                params.put("jeTrxId", dt.getFinanceIncomeId());
                params.put("companyId", requestCtx.getCompanyId());
                params.put("contractId", dt.getContractId());
                params.put("sourceDoc", "CON_CONTRACT");
                finIncomeRecognitionTrxService.process(requestCtx, params);
            }

        }
        return contractFinanceIncomeList;
    }

    @Override
    public List<HlsCusGldLonContractFinCost> lonFinCostRecognition(IRequest requestCtx, List<HlsCusGldLonContractFinCost> gldLonContractFinCostsList, String periodName, String contractType) throws ResMessageException {

        for (HlsCusGldLonContractFinCost finCost : gldLonContractFinCostsList) {
            if ("N".equals(finCost.getPostFlag())) {
                finCost = gldLonContractFinCostMapper.selectByPrimaryKey(finCost.getFinanceCostId());

                HlsCusGldLonContractFinCost cost = new HlsCusGldLonContractFinCost();
                cost.setFinanceCostId(finCost.getFinanceCostId());
                cost.setPostFlag("Y");
                gldLonContractFinCostService.updateByPrimaryKeySelective(requestCtx, cost);
            }
        }
        return gldLonContractFinCostsList;
    }

    @Override
    public List<HlsCusConContract> stampDutyAccrualLease(List<HlsCusConContract> hlsCusConContractList) {
        for(HlsCusConContract dt:hlsCusConContractList){
            if(dt.getPostFlag().equals("N")){
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(dt.getContractId());
                if (dt.getStampDuty().equals("0.00005")) {
                    hlsCusConContract.setStampDutyAccruedAmount(dt.getStampDutyAccruedAmount());
                    hlsCusConContract.setPostFlag("Y");
                }
                if (dt.getStampDuty().equals("0.0003")) {
                    hlsCusConContract.setPurStampDutyAccruedAmount(dt.getStampDutyAccruedAmount());
                    hlsCusConContract.setPurPostFlag("Y");
                }
                hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
//                hlsCusConContractMapper.stampDutyAccrualLease(hlsCusConContract);
            }
        }
        return hlsCusConContractList;
    }

    @Override
    public List<HlsCusLonContractWithdraw> stampDutyAccrualFinancing(List<HlsCusLonContractWithdraw> cusLonContractWithdraws) {
        for(HlsCusLonContractWithdraw dt:cusLonContractWithdraws){
            if(dt.getPostFlag().equals("N")){
                HlsCusLonContractWithdraw hlsCusLonContractWithdraw = new HlsCusLonContractWithdraw();
                hlsCusLonContractWithdraw.setWithdrawId(dt.getWithdrawId());
                hlsCusLonContractWithdraw.setPostFlag("Y");
                hlsCusLonContractWithdrawMapper.stampDutyAccrualFinancing(hlsCusLonContractWithdraw);
            }
        }
        return cusLonContractWithdraws;
    }

    @Override
    public List<HlsCusConContractRiskFund> riskFundAccrual(IRequest requestCtx, List<HlsCusConContractRiskFund> conContractRiskFundList, String periodName) {
        for(HlsCusConContractRiskFund dt:conContractRiskFundList){
            //HlsCusConContractRiskFund hlsCusConContractRiskFund = new HlsCusConContractRiskFund();
            hlsCusConContractRiskFundService.insert(requestCtx, dt);
            if(dt.getPostFlag().equals("N")){
                dt = hlsCusConContractRiskFundService.selectByPrimaryKey(requestCtx, dt);
                dt.setPostFlag("Y");
                hlsCusConContractRiskFundService.updateByPrimaryKeySelective(requestCtx, dt);

                //风险金计提凭证
                AbstractJeTrxService finIncomeRecognitionTrxService = (AbstractJeTrxService) JeTrxCommonService.map.get("TRE_ACCRUED_INTEREST");
                Map params = new HashMap();
                params.put("jeTrxId", dt.getRiskId());
                params.put("companyId", requestCtx.getCompanyId());
                params.put("contractId", dt.getContractId());
                params.put("sourceDoc", "CON_CONTRACT");
                finIncomeRecognitionTrxService.process(requestCtx, params);
            }
        }
        return conContractRiskFundList;
    }
}

