package com.hand.hls.fct.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.hn.dto.CheckPlanConContract;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.hn.service.ISelectAllConContractService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * description
 *  租后检查计划初始化数据
 * @author wangchao 2020/05/14 5:56 PM
 */
public class HlsCusHlsCreditLineChanceModifyDataJob extends AbstractJob {

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;

    private final static String CREDITLINESTATUS = "CLOSED";
    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");
        //获取立项状态认为“新建”或者“审批退回 的单据
        List<HlsCusHlsCreditLineChance> hlsCusHlsCreditLineChanceList = hlsCusHlsCreditLineChanceMapper.selectCreditLineChanceByCreditLineStatus();
        if(CollectionUtils.isNotEmpty(hlsCusHlsCreditLineChanceList)){
            for(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance:hlsCusHlsCreditLineChanceList){
                //当立项到期日小于等于系统当前日期 系统自动将单据状态更改为“失效”，该条单据不允许再次编辑和提交
                if(hlsCusHlsCreditLineChance.getValidTo()!=null&&(new Date()).after(hlsCusHlsCreditLineChance.getValidTo())){
                    hlsCusHlsCreditLineChance.setCreditLineStatus(CREDITLINESTATUS);
                    hlsCusHlsCreditLineChanceService.updateByPrimaryKey(iRequest,hlsCusHlsCreditLineChance);
                }
            }
        }

    }


}
