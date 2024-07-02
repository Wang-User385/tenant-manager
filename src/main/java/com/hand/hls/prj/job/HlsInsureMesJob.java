package com.hand.hls.prj.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.pam.dto.JcInsurancePlan;
import com.hand.hls.pam.mapper.JcInsurancePlanMapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.prj.mapper.HlsCusPrjProjectInsureMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HlsInsureMesJob extends AbstractJob {
    @Autowired
    private HlsCusPrjProjectInsureMapper mapper;
    @Autowired
    private JcInsurancePlanMapper planMapper;
    @Autowired
    private SysEventService sysEventService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        /*job时间：每日凌晨
        对象：保险信息创建者
        判断信息到期日减一个月天是否等于当前日期，若是的话出发提醒*/
        List<HlsCusPrjProjectInsure> prjProjectInsures = mapper.queryMes();
        Date toDate = new Date();
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (prjProjectInsures.size() > 0) {
            for (HlsCusPrjProjectInsure insure : prjProjectInsures) {
                Map<String, Object> evenParams = new HashMap();
                String str = "您好：合同-【"+insure.getContractNumber()+"】保险【"+insure.getInsureNumber()+"】已到期，需进行保险续保及相关信息完善，请查阅租赁物保险信息完成相关操作，谢谢！";
                evenParams.put("message", str);
                evenParams.put("noticeTitle", "合同保险到期");
                evenParams.put("url", "");
                evenParams.put("level", 1L);
                evenParams.put("noticeType", "NOTICE");
                evenParams.put("allocation_id", insure.getAllocationId());
                evenParams.put("eventUserId", insure.getUserId());
                sysEventService.eventSave(iRequest, insure.getInsureId(), "PRJ_PROJECT_INSURE", "PRJ_PROJECT_INSURE", "PRJ", "PRJ_PROJECT_INSURE", "P2D", evenParams);

            }
        }
        /*job时间：每日凌晨
        对象：保险信息创建者
        检索租赁物信息中的保险计划，若存在保险计划未在保险信息中关联的数据，且计划购买时间减10等于系统时间，则发出提醒*/
        List<JcInsurancePlan> planList = planMapper.selectMes();
        if (planList.size() > 0) {
            for (JcInsurancePlan plan : planList) {
                Map<String, Object> evenParams = new HashMap();
                String str = "您好：租赁物【"+plan.getFullName()+"】需在【"+df.format(plan.getPlanPurchaseDate())+"】进行保险购买及相关信息完善，请查阅租赁物保险计划完成相关操作，谢谢！";
                evenParams.put("message", str);
                evenParams.put("noticeTitle", "合同保险到期");
                evenParams.put("url", "");
                evenParams.put("level", 1L);
                evenParams.put("noticeType", "NOTICE");
                evenParams.put("allocation_id", plan.getAllocationId());
                evenParams.put("eventUserId", plan.getUserId());
                sysEventService.eventSave(iRequest, plan.getPlanId(), "JC_INSURANCE_PLAN", "JC_INSURANCE_PLAN", "PRJ", "JC_INSURANCE_PLAN", "P2D", evenParams);

            }
        }
    }
}
