package hls.core.hls.job;

import cn.hutool.core.date.DateUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.bp.job.HlsDayEndJob;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.plm.rc.service.HlsCusRentCollectionRuleService;
import com.hand.hls.service.HlsDayEndJobSerivce;
import hls.core.hls.service.HlsDayEndLsJobService;
import hls.core.hls.service.HlsDayEndService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PC
 */
@Component
public class HlsDayEndLsJob extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(HlsDayEndJob.class);
    public static final String PARAM_DAY_END_DATE = "dayEndDate";
    public static final String PARAM_DAY_END_TYPE = "dayEndType";
    public static final String PARAM_COMPANY_ID = "companyId";
    public static final String PARAM_CONTRACT_ID = "contractId";
    public static final String PARAM_WITHDRAW_ID = "withdrawId";
    @Autowired
    private HlsDayEndLsJobService hlsDayEndJobSerivce;
    private Exception exception = null;

    public HlsDayEndLsJob() {
    }

    @Override
    public void safeExecute(JobExecutionContext context) {
        String dayEndDate = context.getMergedJobDataMap().getString("dayEndDate");
        String dayEndType = context.getMergedJobDataMap().getString("dayEndType");
        String companyId = context.getMergedJobDataMap().getString("companyId");
        String contractId = context.getMergedJobDataMap().getString("contractId");
        String withdrawId = context.getMergedJobDataMap().getString("withdrawId");
        Map<String, Object> param = new HashMap();
        if (StringUtils.isNotEmpty(dayEndDate)) {
            Date date = DateUtil.parse(dayEndDate);
            param.put("dayEndDate", date);
        } else {
            param.put("dayEndDate", new Date());
        }

        if (StringUtils.isNotEmpty(dayEndType)) {
            param.put("dayEndType", dayEndType);
        } else {
            param.put("dayEndType", "ALL");
        }

        if (StringUtils.isNotEmpty(companyId)) {
            param.put("companyId", companyId);
        }

        if (StringUtils.isNotEmpty(withdrawId)) {
            param.put("withdrawId", withdrawId);
        }

        if (StringUtils.isNotEmpty(contractId)) {
            param.put("contractId", contractId);
        }

        try {
            this.hlsDayEndJobSerivce.execute(param);
            this.setExecutionSummary("执行完成！");
        } catch (Exception var9) {
            logger.error("DayEndJob execute error 日结任务执行时出现问题 : ", var9);
            this.setExecutionSummary(this.exception.getClass().getName() + ":" + this.exception.getMessage());
        }

    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }


}
