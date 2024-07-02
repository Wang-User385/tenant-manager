package com.hand.hls.cont.job;

import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/8 - 16:40
 */
public class HlsCusContractFinanceJob extends AbstractJob {
    private Logger logger = LoggerFactory.getLogger(HlsContractLeaseJob.class);

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;


    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;


    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;


    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String  currentDate = dateFormat.format(date);
        //查出每个合同的财务起租日  -- 查的是prj_quotation 表里的lease_account_date
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        List<HlsCusConContract> hlsCusConContractList = hlsCusConContractMapper.queryToFinanceInceptList(hlsCusConContract);

        List<HlsCusConContract> list = new ArrayList<>();

        List<HlsCusConContract> contractList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(hlsCusConContractList)){
            for(HlsCusConContract cusConContract : hlsCusConContractList) {
                Date leaseAccountDate = cusConContract.getLeaseAccountDate();
                if (leaseAccountDate != null) {
                    String leaseDate = dateFormat.format(leaseAccountDate);
                    //判断日期
                    int compareResult = currentDate.compareTo(leaseDate);
                    if (compareResult == 0 || compareResult > 0) {
                        //出起租凭证 , 改财务起租标记为Y ,
                        //改标记
                        HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
                        cusPrjQuotation.setQuotationId(cusConContract.getQuotationId());
                        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                        hlsCusPrjQuotation =  hlsCusPrjQuotationMapper.selectByPrimaryKey(cusPrjQuotation);
                        hlsCusPrjQuotation.setFinancialLeaseFlag("Y");
                        hlsCusPrjQuotationMapper.updateByPrimaryKey(hlsCusPrjQuotation);
                        //起租凭证
                     /*   Map contractInceptMap = new HashMap<>();
                        contractInceptMap.put("jeTrxId", cusConContract.getContractId());
                        contractInceptMap.put("companyId", cusConContract.getCompanyId());
                        contractInceptMap.put("contractId", cusConContract.getContractId());
                        contractInceptMap.put("sourceDoc", "CON_CONTRACT");
                        AbstractJeTrxService contractInceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
                        contractInceptJeTrxService.process(RequestHelper.getCurrentRequest(true), contractInceptMap);*/

                        //摊销的合同list
                        HlsCusConContract conContract = new HlsCusConContract();
                        conContract.setContractId(cusConContract.getContractId());
                        HlsCusConContract contract =  hlsCusConContractMapper.selectByPrimaryKey(conContract);
                        contractList.add(contract);
                    }
                }

            }

        }

        //摊销
        iContractFinanceIncomeService.financeIncomeSharing(RequestHelper.getCurrentRequest(true), contractList);

        if(CollectionUtils.isNotEmpty(hlsCusConContractList)){
            for(HlsCusConContract cusConContract : hlsCusConContractList) {
                Date leaseAccountDate = cusConContract.getLeaseAccountDate();
                if (leaseAccountDate != null) {
                    String leaseDate = dateFormat.format(leaseAccountDate);
                    //判断日期
                    int compareResult = currentDate.compareTo(leaseDate);
                    if (compareResult == 0 || compareResult > 0) {

                        //起租凭证
                        Map contractInceptMap = new HashMap<>();
                        contractInceptMap.put("jeTrxId", cusConContract.getContractId());
                        contractInceptMap.put("companyId", cusConContract.getCompanyId());
                        contractInceptMap.put("contractId", cusConContract.getContractId());
                        contractInceptMap.put("sourceDoc", "CON_CONTRACT");
                        AbstractJeTrxService contractInceptJeTrxService = JeTrxCommonService.map.get("CONTRACT_INCEPT");
                        contractInceptJeTrxService.process(RequestHelper.getCurrentRequest(true), contractInceptMap);

                    }
                }

            }

        }


    }


    @Override
    public boolean isRefireImmediatelyWhenException() {
        //任务发生异常时候进行的动作
        //false 挂起JOB等待处理
        //true 继续执行
        return false;
    }
}
