package com.hand.hls.cont.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/5/4 - 18:06
 */
public class HlsContractLeaseJob extends AbstractJob  {
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
        //查出每个合同的起租日  -- 查的是prj_quotation 表里的lease_start_date

        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        List<HlsCusConContract> hlsCusConContractList = hlsCusConContractMapper.queryToInceptList(hlsCusConContract);

        List<HlsCusConContract> list = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(hlsCusConContractList)){
            for(HlsCusConContract cusConContract : hlsCusConContractList) {
                Date leaseStartDate = cusConContract.getLeaseStartDate();
                if (leaseStartDate != null) {
                    String leaseDate = dateFormat.format(leaseStartDate);
                    //判断日期
                    int compareResult = currentDate.compareTo(leaseDate);
                    if (compareResult == 0 || compareResult > 0) {

                        //需要判断是否放款才能改状态

                        int account = hlsCusConContractMapper.queryCountPaymentById(cusConContract);
                        if (account > 0) {
                            //更新合同状态为起租状态
                            cusConContract.setContractStatus("INCEPT");
                            cusConContract.setInceptFlag("Y");
                            hlsCusConContractMapper.updateByPrimaryKeySelective(cusConContract);
                            list.add(cusConContract);
                        }
                    }
                }

            }

        }
        //起租时收益分摊
        try {
            iContractFinanceIncomeService.financeIncomeSharing(RequestHelper.getCurrentRequest(true), list);
        } catch (Exception e) {
            logger.error(e.getMessage());
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
