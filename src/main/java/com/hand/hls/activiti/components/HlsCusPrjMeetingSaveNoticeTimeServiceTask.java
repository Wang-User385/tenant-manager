package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IHlsCusProjectCreditNoticeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjMeetingSaveNoticeTimeServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED_TASK_ID = "sid-HiX4oIjM-bwx6-4HtJ-8ke5-z0trcNpLhN32";
    private static final String CHANGE_APPROVED_TASK_ID = "sid-7VJTxQPB-avmX-4T0e-8Jtn-pqIn6RTCpwgY";
    private static final String SUSPEND_TASK_ID = "sid-yuWGD0AG-KBEx-43Uh-8o6L-8NhVku45SEOr";
    private static final String CHANGE_SUSPEND_TASK_ID = "sid-9p84di7m-54rM-4Enf-8fT6-ZFgl9w96vx8K";
    private static final String REJECT_TASK_ID = "sid-SRRbenNU-OmW5-4yc6-8rAZ-WEyF7jIgMfiG";
    private static final String CHANGE_REJECT_TASK_ID = "sid-AUKjnf8Y-nMjU-45Jd-8CNK-Fu6gf1PBfAuA";

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private IHlsCusProjectCreditNoticeService hlsCusProjectCreditNoticeService;



    public HlsCusPrjMeetingSaveNoticeTimeServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());

        String currentActivityId = delegateExecution.getCurrentActivityId();
        String result = (String) delegateExecution.getVariable("approveResult");
        Long instanceId = Long.parseLong(delegateExecution.getProcessInstanceId());

        HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
        hlsCusProjectCreditNotice.setInstanceId(instanceId);
        hlsCusProjectCreditNotice.setProjectId(projectId);
        List<HlsCusProjectCreditNotice> hlsCusProjectCreditNoticeList = hlsCusProjectCreditNoticeService.QueryAllByInstanceId(hlsCusProjectCreditNotice);

        String updateFlag = "N";

        if(currentActivityId.equalsIgnoreCase(APPROVED_TASK_ID) || currentActivityId.equalsIgnoreCase(CHANGE_APPROVED_TASK_ID)){
            //同意（终批）
            if(result.equalsIgnoreCase("APPROVED")){
                updateFlag = "Y";
            }
        }else if(currentActivityId.equalsIgnoreCase(SUSPEND_TASK_ID) || currentActivityId.equalsIgnoreCase(CHANGE_SUSPEND_TASK_ID)){
            //暂缓
            if(result.equalsIgnoreCase("SUSPEND")){
                updateFlag = "Y";
            }
        }else if(currentActivityId.equalsIgnoreCase(REJECT_TASK_ID) || currentActivityId.equalsIgnoreCase(CHANGE_REJECT_TASK_ID)){
            //否决
            if(result.equalsIgnoreCase("REJECTED")){
                updateFlag = "Y";
            }
        }

        if(updateFlag == "Y"){
            Date sysDate = new Date();
            for(HlsCusProjectCreditNotice dt:hlsCusProjectCreditNoticeList){
                dt.setNoticeCreateDate(sysDate);
                hlsCusProjectCreditNoticeService.updateByPrimaryKeySelective(iRequest,dt);
            }
        }

    }

}
