package com.hand.hls.eas.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * description
 *  银行账号同步接口
 * @author wangchao 2020/05/14 5:56 PM
 */
public class HlsCusBasicDataSynJob extends AbstractJob {



    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;




    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");

                if(jobExecutionContext.getMergedJobDataMap().get("contractNumber")!=null){
                    String contractNumber = jobExecutionContext.getMergedJobDataMap().get("contractNumber").toString();

                    HlsCusConContract hlsCusConContract= new HlsCusConContract();

                    hlsCusConContract.setContractNumber(contractNumber);
                    hlsCusConContract.setDataClass("NORMAL");
                    List<HlsCusConContract> hlsCusConContractList=  hlsCusConContractMapper.select(hlsCusConContract);

                    if(hlsCusConContractList.size()>0){
                        hlsCusEasLoginService.easDataSyn(iRequest,hlsCusConContractList.get(0).getContractId());
                    }

                }

        if(jobExecutionContext.getMergedJobDataMap().get("paymentReqNumber")!=null){
            String paymentReqNumber = jobExecutionContext.getMergedJobDataMap().get("paymentReqNumber").toString();

            HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd= new HlsCusCshPaymentReqHd();
            hlsCusCshPaymentReqHd.setPaymentReqNumber(paymentReqNumber);

            List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHdList=  hlsCusCshPaymentReqHdMapper.select(hlsCusCshPaymentReqHd);
            if(hlsCusCshPaymentReqHdList.size()>0){
                hlsCusEasLoginService.easVenderDataSyn(iRequest,hlsCusCshPaymentReqHdList.get(0).getPaymentReqId());
            }

        }


        }






}
