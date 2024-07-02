package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IHlsCusProjectCreditNoticeService;
import hls.core.utils.exception.HlsCusException;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectCheckExistServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private IHlsCusProjectCreditNoticeService hlsCusProjectCreditNoticeService;



    public HlsCusPrjProjectCheckExistServiceTask(){}
    @SneakyThrows
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

        if(hlsCusProjectCreditNoticeList.size() == 0){
            throw new HlsCusException("请生成审批通知书后再点击制作完成！");
        }

    }

}
