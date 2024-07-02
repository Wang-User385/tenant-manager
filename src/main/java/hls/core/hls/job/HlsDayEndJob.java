package hls.core.hls.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.plm.rc.service.HlsCusRentCollectionRuleService;
import hls.core.hls.service.HlsDayEndService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HlsDayEndJob extends AbstractJob {
    private Logger logger = LoggerFactory.getLogger(HlsDayEndJob.class);
    @Autowired
    private HlsDayEndService hlsDayEndService;

    @Autowired
    private HlsCusRentCollectionRuleService hlsCusRentCollectionRuleService;

    private Exception exception = null;
    private Object result;

    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        String dayEndDate = context.getMergedJobDataMap().getString("dayEndDate");
        String dayEndType = context.getMergedJobDataMap().getString("dayEndType");

        Long companyId = null;
        Long contractId = null;
        Long withdrawId = null;

        if (context.getMergedJobDataMap().getString("companyId") != null) {
            companyId = Long.parseLong(context.getMergedJobDataMap().getString("companyId"));
        }

        if (context.getMergedJobDataMap().getString("contractId") != null) {
            contractId = Long.parseLong(context.getMergedJobDataMap().getString("contractId"));
        }

        if (context.getMergedJobDataMap().getString("withdrawId") != null) {
            withdrawId = Long.parseLong(context.getMergedJobDataMap().getString("withdrawId"));
        }

        Map<String, Object> param = new HashMap<>();
        if (dayEndDate == null || dayEndDate.equals("")) {
            param.put("dayEndDate", new Date());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dayEndDate);
            param.put("dayEndDate", date);
        }
        if (dayEndType == null || dayEndType.equals("")) {
            param.put("dayEndType", "ALL");
        } else {
            param.put("dayEndType", dayEndType);
        }
        //先固定公司
        if (companyId == null || companyId.equals("")) {
            if ("FCT_PENALTY".equalsIgnoreCase(dayEndType)) {
                param.put("companyId", 264);
            } else {
                param.put("companyId", 248);
            }
        } else {
            param.put("companyId", companyId);
        }

        if (contractId == null || contractId.equals("")) {
            param.put("contractId", "");
        } else {
            param.put("contractId", contractId);
        }

        if (withdrawId == null || withdrawId.equals("")) {
            param.put("withdrawId", "");
        } else {
            param.put("withdrawId", withdrawId);
        }


        param.put("jobExecute", 1);
        try {
            hlsDayEndService.excEndDay(param);

            //自动插入租金催收规则数据
            List<HlsCusRentCollectionRule> collectionRules = hlsCusRentCollectionRuleService.selectOverTimesContract();
            for(HlsCusRentCollectionRule rule:collectionRules){
                rule.setCollectionDays(7L);
                rule.setEnabledFlag("Y");
                IRequest iRequest = RequestHelper.newEmptyRequest();
                iRequest.setUserId(10001L);
                hlsCusRentCollectionRuleService.insertSelective(iRequest,rule);
            }

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            exception = e;
            throw e;
        }
        if (exception != null) {
            setExecutionSummary(exception.getClass().getName() + ":" + exception.getMessage());
        } else {
            setExecutionSummary("执行完成！");
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
