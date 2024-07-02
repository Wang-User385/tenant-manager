package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.dto.*;
import com.hand.hls.gld.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 19:02
 * @description copy from gd
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCtDocumentFinIncomeServiceImpl extends BaseServiceImpl<HlsCusCtDocumentFinIncome> implements HlsCusCtDocumentFinIncomeService {

    @Autowired
    private GldLonContractFinCostService finCostService;

    @Autowired
    private IGldFctContractFinIncomeService fctContractFinIncomeService;

    @Autowired
    private HlsCusCtDocumentFinIncomeService hlsCusCtDocumentFinIncomeService;

    @Autowired
    private IContractFinanceIncomeService contractFinanceIncomeService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;



    @Override
    public void insertCtFin(IRequest requestCtx, HlsCusFinIncomePkg hlsCusFinIncomePkg) {
        List<HlsCusContractFinanceIncome> dto = new ArrayList<>();
        List<HlsCusContractFinanceIncome> cons = hlsCusFinIncomePkg.getHlsCusContractFinanceIncomeList();
        List<HlsCusGldFctContractFinIncome> fcts = hlsCusFinIncomePkg.getHlsCusGldFctContractFinIncomeList();
        List<HlsCusGldLonContractFinCost> lons = hlsCusFinIncomePkg.getHlsCusGldLonContractFinCostList();
        String abstractJeTrx = "";
        if ("FCT_CONTRACT".equalsIgnoreCase(hlsCusFinIncomePkg.getFinType())) {
            for (HlsCusGldFctContractFinIncome fct : fcts) {
                fct.set__status("update");
                fct.setPostFlag("Y");
                fctContractFinIncomeService.updateByPrimaryKeySelective(requestCtx, fct);
                HlsCusContractFinanceIncome con = new HlsCusContractFinanceIncome();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(fct);
                hlsBeanRefUtilService.setFieldValue(con, map);
                con.setSourceType("FCT_CONTRACT");
                dto.add(con);
                abstractJeTrx = "FCT_INCOME_RECOGNITION";
            }
        } else if ("CON_CONTRACT".equalsIgnoreCase(hlsCusFinIncomePkg.getFinType())) {
            for (HlsCusContractFinanceIncome conOld : cons) {
                conOld.setPostFlag("Y");
                conOld.set__status("update");
                contractFinanceIncomeService.updateByPrimaryKeySelective(requestCtx,conOld);
                HlsCusContractFinanceIncome con = new HlsCusContractFinanceIncome();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(conOld);
                hlsBeanRefUtilService.setFieldValue(con, map);
                con.setSourceType("CON_CONTRACT");
                dto.add(con);
                abstractJeTrx = "FIN_INCOME_RECOGNITION";
            }
        } else if ("LON_CONTRACT".equalsIgnoreCase(hlsCusFinIncomePkg.getFinType())) {
            for (HlsCusGldLonContractFinCost lon : lons) {
                lon.setPostFlag("Y");
                lon.set__status("update");
                finCostService.updateByPrimaryKeySelective(requestCtx, lon);
                HlsCusContractFinanceIncome con = new HlsCusContractFinanceIncome();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(lon);
                hlsBeanRefUtilService.setFieldValue(con, map);
                con.setContractId(lon.getWithdrawId());
                con.setFinanceIncome(lon.getFinanceCost());
                con.setSourceType("LON_CONTRACT_WITHDRAW");
                con.setFinanceIncomeVat(lon.getFinanceIncomeVat());
                con.setFinanceIncomeInclud(lon.getFinanceIncomeInclud());
                dto.add(con);
                abstractJeTrx = "TRE_ACCRUED_INTEREST";
            }
        }
        for (HlsCusContractFinanceIncome dt : dto) {
            List<HlsCusCtDocumentFinIncome> incomes = new ArrayList<>();
            HlsCusCtDocumentFinIncome hlsCusCtDocumentFinIncome = new HlsCusCtDocumentFinIncome();
            hlsCusCtDocumentFinIncome.setSourceId(dt.getContractId());
            hlsCusCtDocumentFinIncome.setCfItem(dt.getCfItem());
            hlsCusCtDocumentFinIncome.setPeriodName(dt.getPeriodName());
            /*相同contractId和cf_item、period_name合并插入ct表*/
            incomes = contractFinanceIncomeService.queryCtDocumentFinIncomeByCondition(requestCtx, hlsCusCtDocumentFinIncome, 1, 99999);
            if (incomes.size() > 0) {
                /*更新日期，天数，金额*/
                hlsCusCtDocumentFinIncome = incomes.get(0);
                if (hlsCusCtDocumentFinIncome.getStartDate().after(dt.getStartDate())) {
                    hlsCusCtDocumentFinIncome.setStartDate(dt.getStartDate());
                }
                if (hlsCusCtDocumentFinIncome.getEndDate().before(dt.getEndDate())) {
                    hlsCusCtDocumentFinIncome.setEndDate(dt.getEndDate());
                }
                hlsCusCtDocumentFinIncome.setDays(hlsCusCtDocumentFinIncome.getDays() + dt.getDays());
                hlsCusCtDocumentFinIncome.setFinanceIncome(hlsCusCtDocumentFinIncome.getFinanceIncome() + dt.getFinanceIncome());
                hlsCusCtDocumentFinIncome.setFinanceIncomeInclud(hlsCusCtDocumentFinIncome.getFinanceIncomeInclud() + dt.getFinanceIncomeInclud());
                hlsCusCtDocumentFinIncome.setFinanceIncomeVat(hlsCusCtDocumentFinIncome.getFinanceIncomeVat() + dt.getFinanceIncomeVat());
                hlsCusCtDocumentFinIncomeService.updateByPrimaryKeySelective(requestCtx, hlsCusCtDocumentFinIncome);
            } else {
                HlsCusCtDocumentFinIncome income = new HlsCusCtDocumentFinIncome();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
                hlsBeanRefUtilService.setFieldValue(income, map);
                income.setSourceId(dt.getContractId());
                income.setSourceType(dt.getSourceType());
                income = self().insertSelective(requestCtx, income);
                //出表的租赁合同不再插入凭证流水，不出凭证
                boolean insertFlag;
                if ("CON_CONTRACT".equalsIgnoreCase(dt.getSourceType())) {
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(dt.getContractId());
                    hlsCusConContract = hlsCusConContractService.queryConContractByKey(requestCtx, hlsCusConContract);
                    if ("Y".equalsIgnoreCase(hlsCusConContract.getAbsShowFlag())) {
                       insertFlag = false;
                    } else {
                       insertFlag = true;
                   }
                } else {
                insertFlag = true;
                }
                if (insertFlag) {
                //插入凭证事物流水表
                    AbstractJeTrxService repaymentService = commonService.map.get(abstractJeTrx);
                    Map params = new HashMap<>();
                    params.put("jeTrxId", income.getFinIncomeId());
                    params.put("companyId", income.getCompanyId());
                    params.put("contractId", income.getFinIncomeId());
                    params.put("sourceDoc", "CT_DOCUMENT_FIN_INCOME");
                    repaymentService.process(requestCtx, params);
                }
            }
        }
    }
}
