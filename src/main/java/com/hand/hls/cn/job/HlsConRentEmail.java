package com.hand.hls.cn.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.mail.ReceiverTypeEnum;
import com.hand.hap.mail.controllers.MessageController;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.mapper.MessageReceiverMapper;
import com.hand.hap.mail.mapper.MessageTransactionMapper;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HlsConRentEmail extends AbstractJob {
    private static final String TEMPLATE_CODE = "JC_CSH";
    public static final String CAP_CAPITAL_INVESTMENT_PLAN_SUBMIT = "CAP_CAPITAL_INVESTMENT_PLAN.SUBMIT";
    @Autowired
    private IMessageService messageService;
    @Autowired
    private MessageController messageController;
    @Autowired
    private MessageTransactionMapper messageTransactionMapper;
    @Autowired
    private MessageReceiverMapper messageReceiverMapper;
    @Autowired
    private HlsCusPrjProjectMapper projectMapper;
    @Autowired
    private SysEventService sysEventService;

    /*
     * 租金催收邮件发送
     * 1.逾期：回款前1个月、15天、7天、3天、1天系统自动发送催收提醒短信/邮件给到财务负责人及财务经办人员
     * 2.未逾期：系统每日自动发送催收提醒短信/邮件给到客户法定代表人/实际控制人以及财务负责人直到相应款项核销
     * 3.以上两点不发送邮件 改为给项目经理发送提示信息
     * */
    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        //1。未逾期
        Long[] dayList = new Long[]{30L, 15L, 5L};
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        for (int i = 0; i < dayList.length; i++) {
            Long days = dayList[i];
            String str1 = "";
            if (days == 30) {
                str1 = "催收管理";
            } else if (days == 15) {
                str1 = "催收函";
            } else if (days == 5) {
                str1 = "电话催收";
            }
            List<HlsCusPrjProject> prjProjects = projectMapper.conRentQueryAll1(days);
            if (prjProjects.size() > 0) {
                for (HlsCusPrjProject prjProject : prjProjects) {
//                    if (prjProject.getProjectId() == 7687) {
                        Map<String, Object> evenParams = new HashMap();
                        String str = "合同号为" + prjProject.getContractNumber() + "的本期租金即将到期，请尽快进行【" + str1 + "】催收事宜！";
                        evenParams.put("message", str);
                        evenParams.put("noticeTitle", "租金催收");
                        evenParams.put("url", "");
                        evenParams.put("level", 3L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", prjProject.getAllocationId());
                        evenParams.put("eventUserId",prjProject.getHostProjectManager());
                        sysEventService.eventSave(iRequest, prjProject.getProjectId(), "PRJ_PROJECT", "PRJ_PROJECT", "CON", "CON_CONTRACT", "P2D", evenParams);

//                    }
                }
            }
            //已逾期
            List<HlsCusPrjProject> prjProject = projectMapper.conRentQueryAll2();
            if (prjProject.size() > 0) {
                for (HlsCusPrjProject Project : prjProject) {
//                    if (Project.getProjectId() == 7687){
                        Map<String, Object> evenParams = new HashMap();
                        String str = "合同号为" + Project.getContractNumber() + "的本期租金即将到期，请尽快在【催收管理】模块进行催收事宜;";
                        evenParams.put("message", str);
                        evenParams.put("noticeTitle", "租金催收");
                        evenParams.put("url", "");
                        evenParams.put("level", 3L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", Project.getAllocationId());
                        evenParams.put("eventUserId",Project.getHostProjectManager());
                        sysEventService.eventSave(iRequest, Project.getProjectId(), "PRJ_PROJECT", "PRJ_PROJECT", "CON", "CON_CONTRACT", "P2D", evenParams);
//                    }
                }
            }
        }
    }
}
