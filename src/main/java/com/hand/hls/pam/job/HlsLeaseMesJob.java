package com.hand.hls.pam.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.JcInsurancePlan;
import com.hand.hls.pam.mapper.HlsCusLeaseItemMapper;
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

public class HlsLeaseMesJob extends AbstractJob {
    @Autowired
    private HlsCusLeaseItemMapper mapper;
    @Autowired
    private SysEventService sysEventService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        /*job时间：每日凌晨对象：合同项目经理A角判断租赁物在系统时间当年是否有资产价值重估，若没有，
         *则找到对应合同下支付表投放日期最小的月和日（例如7月15日），然后判断当前年+月日减一个月（2021年6月15日）等于系统时间，
         *则触发提醒（无对应合同或者合同状态不正常，不提醒）
         */
        List<HlsCusLeaseItem> leaseItemList = mapper.selectLeaseMes();
        Date toDate = new Date();
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = df.format(toDate);
        if (leaseItemList.size() > 0) {
            for (HlsCusLeaseItem item : leaseItemList) {
                if(item.getLeaseStartDate() != null){
                    String minLeaseStartDate = df.format(item.getLeaseStartDate());
                    if(nowDate.equalsIgnoreCase(minLeaseStartDate)){
                        Map<String, Object> evenParams = new HashMap();
                        String str = "您好：租赁物【"+item.getFullName()+"】需进行资产价值重估，请进行对应操作及信息维护。";
                        evenParams.put("message", str);
                        evenParams.put("noticeTitle", "资产价值重估");
                        evenParams.put("url", "");
                        evenParams.put("level", 1L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", item.getAllocationId());
                        evenParams.put("eventUserId", item.getHostProjectManager());
                        iRequest.setUserId(item.getHostProjectManager());
                        sysEventService.eventSave(iRequest, item.getLeaseItemId(), "PRJ_PROJECT_LEASE_ITEM", "PRJ_PROJECT_LEASE_ITEM", "PRJ", "PRJ_PROJECT_LEASE_ITEM", "P2D", evenParams);
                    }
                }
            }
        }
        /*job时间：每日凌晨对象：合同项目经理A角判断抵质押物在系统时间当年是否有资产价值重估，
         *若没有，则找到对应合同下支付表投放日期最小的月和日（例如7月15日），然后判断当前年+月日减一个月（2021年6月15日）等于系统时间，
         *则触发提醒（无对应合同或者合同状态不正常，不提醒）
        */
        List<HlsCusLeaseItem> plgItemList = mapper.selectPlgMes();
        if (plgItemList.size() > 0) {
            for (HlsCusLeaseItem item : plgItemList) {
                if(item.getLeaseStartDate() != null){
                    String minLeaseStartDate = df.format(item.getLeaseStartDate());
                    if(nowDate.equalsIgnoreCase(minLeaseStartDate)){
                        Map<String, Object> evenParams = new HashMap();
                        String str = "您好：抵质押物【"+item.getFullName()+"】需进行资产价值重估，请进行对应操作及信息维护。";
                        evenParams.put("message", str);
                        evenParams.put("noticeTitle", "资产价值重估");
                        evenParams.put("url", "");
                        evenParams.put("level", 1L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", item.getAllocationId());
                        evenParams.put("eventUserId", item.getHostProjectManager());
                        iRequest.setUserId(item.getHostProjectManager());
                        sysEventService.eventSave(iRequest, item.getLeaseItemId(), "PRJ_PROJECT_CHANCE_MP", "PRJ_PROJECT_CHANCE_MP", "PRJ", "PRJ_PROJECT_CHANCE_MP", "P2D", evenParams);
                    }
                }
            }
        }
    }
}
