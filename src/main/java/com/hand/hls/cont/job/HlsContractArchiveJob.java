package com.hand.hls.cont.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.ConContractArchive;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractArchive;
import com.hand.hls.cont.mapper.ConContractArchiveMapper;
import com.hand.hls.cont.mapper.HlsCusConContractArchiveMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractArchiveService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HlsContractArchiveJob extends AbstractJobWithIRequest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Autowired
    private IConContractArchiveService contractArchiveService;

    @Autowired
    private HlsCusConContractArchiveMapper contractArchiveMapper;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) throws Exception {
       try {
           List<HlsCusConContract> list = new ArrayList();
           HlsCusConContract conContract = new HlsCusConContract();
           list = conContractMapper.selectAll();
           int num = 0;

           for (HlsCusConContract hlsCusConContract : list) {
               HlsCusConContractArchive contractArchive = new HlsCusConContractArchive();
               contractArchive.setContractId(hlsCusConContract.getContractId());
               contractArchive = contractArchiveMapper.selectOne(contractArchive);
               if (contractArchive == null) {
                   contractArchiveService.saveContractArchive(iRequest, hlsCusConContract);
                   num++;
               }
           }
           logger.info("===================================================================================");
           logger.info("系统已执行合同档案生成，当前执行时间为：" + new Date().toString());
           logger.info("" + num);
           logger.info("===================================================================================");
           setExecutionSummary("success");
       }catch (Exception e){
           exception = e;
           logger.error("contractArchvie execute error 合同档案任务执行时出现问题 : ", e.getMessage());
           this.setExecutionSummary(this.exception.getClass().getName() + ":" + this.exception.getMessage());
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
