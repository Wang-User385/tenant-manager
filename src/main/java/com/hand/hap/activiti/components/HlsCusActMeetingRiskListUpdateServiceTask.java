package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.activiti.dto.HlsCusActAssigneeNode;
import com.hand.hls.activiti.dto.HlsCusActMeetingRiskList;
import com.hand.hls.activiti.service.HlsCusActAssigneeNodeService;
import com.hand.hls.activiti.service.HlsCusActMeetingRiskListService;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangyan on 2017/11/13.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusActMeetingRiskListUpdateServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusActAssigneeNodeService hlsCusActAssigneeNodeService;
    @Autowired
    private HlsCusActMeetingRiskListService hlsCusActMeetingRiskListService;

    public static final String PROPERTY_FCT_PROJECT_APPROVE = "FCT_PROJECT.APPROVE";


    public HlsCusActMeetingRiskListUpdateServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectIdOld = (Long) delegateExecution.getVariable("projectIdOld");
        String documentCategory = (String) delegateExecution.getVariable("documentCategory");
        Long newProcessInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        if ("APPROVED".equalsIgnoreCase(result)) {
            HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
            hlsCusActAssigneeNode.setProjectId(projectIdOld);
            hlsCusActAssigneeNode.setProjectDocumentCategory(documentCategory);
            List<HlsCusActAssigneeNode> list = hlsCusActAssigneeNodeService.select(iRequest, hlsCusActAssigneeNode, 1, 1000);
            if (CollectionUtils.isNotEmpty(list)) {
                //获取到原流程ID，利用流程ID获取到原来的投放条件及贷后管理要求
                Long processInstanceId = list.get(0).getProcessInstanceId();
                HlsCusActMeetingRiskList hlsCusActMeetingRiskList = new HlsCusActMeetingRiskList();
                hlsCusActMeetingRiskList.setProcessInstanceId(processInstanceId);
                List<HlsCusActMeetingRiskList> oldRiskList = hlsCusActMeetingRiskListService.select(iRequest, hlsCusActMeetingRiskList, 1, 10000);
                //删除原来的投放条件及管理要求
                hlsCusActMeetingRiskListService.batchDelete(oldRiskList);
                //查询出最新的投放条件并更新
                hlsCusActMeetingRiskList.setProcessInstanceId(newProcessInstanceId);
                List<HlsCusActMeetingRiskList> newRiskList = hlsCusActMeetingRiskListService.select(iRequest, hlsCusActMeetingRiskList, 1, 10000);
                for (HlsCusActMeetingRiskList dt : newRiskList) {
                    dt.setProcessInstanceId(processInstanceId);
                    hlsCusActMeetingRiskListService.updateByPrimaryKeySelective(iRequest, dt);
                }
            }
        } else {
            HlsCusActMeetingRiskList hlsCusActMeetingRiskList = new HlsCusActMeetingRiskList();
            hlsCusActMeetingRiskList.setProcessInstanceId(newProcessInstanceId);
            List<HlsCusActMeetingRiskList> newRiskList = hlsCusActMeetingRiskListService.select(iRequest, hlsCusActMeetingRiskList, 1, 10000);
            //删除插入的投放条件及管理要求
            for (HlsCusActMeetingRiskList dt : newRiskList) {
                hlsCusActMeetingRiskListService.deleteByPrimaryKey(dt);
            }

        }


    }


}
