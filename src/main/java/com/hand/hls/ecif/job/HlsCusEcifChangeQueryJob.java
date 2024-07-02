package com.hand.hls.ecif.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.mapper.HlsCusBpMasterRequestRecordsMapper;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * description
 *  客户变更查询接口
 * @author wangchao 2020/07/23 5:56 PM
 */
public class HlsCusEcifChangeQueryJob extends AbstractJob {



    @Autowired
    private HlsCusBpMasterRequestRecordsService hlsCusBpMasterRequestRecordsService;

    @Autowired
    private HlsCusBpMasterRequestRecordsMapper hlsCusBpMasterRequestRecordsMapper;



    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");

         String changeDate = null;

         if(jobExecutionContext.getMergedJobDataMap().get("changeDate")==null){

             //获取曾经成功的下一日期作为起始日期
             HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords=hlsCusBpMasterRequestRecordsMapper.selectChangeDate();

             if(hlsCusBpMasterRequestRecords!=null){
                 changeDate=hlsCusBpMasterRequestRecords.getChangeDate();
             }
             else{
                 Date date = new Date();
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 changeDate = dateFormat.format(date);
             }

         }
         else{
             changeDate= jobExecutionContext.getMergedJobDataMap().get("changeDate").toString();
         }

        hlsCusBpMasterRequestRecordsService.wsEcifChangeQuery(iRequest,changeDate);
        }






}
