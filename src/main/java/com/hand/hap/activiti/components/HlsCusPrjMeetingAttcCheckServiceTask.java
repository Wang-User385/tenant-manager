package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import hls.core.utils.exception.HlsCusException;
import lombok.SneakyThrows;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjMeetingAttcCheckServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private TaskService taskService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IProjectMeetingApproverService projectMeetingApproverService;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    public HlsCusPrjMeetingAttcCheckServiceTask(){}

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        Long approvalId = prjProject.getApprovalId();

        ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
        projectMeetingApprover.setProjectId(projectId);
        projectMeetingApprover.setApprovalId(approvalId);
        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        List<ProjectMeetingApprover> approverList = projectMeetingApproverService.selectSelective(requestCtx, projectMeetingApprover);
        for(ProjectMeetingApprover dt : approverList){
            if("Y".equals(dt.getEmptyFlag())){
                Map<String,String> map = new HashMap<>();
                map.put("table_name","PRJ_PROJECT_MEETING_APPROVER");
                map.put("header_id",dt.getApproverRecordId().toString());
                List<Map> list = fndAttachmentMapper.queryAttachment(map,"");
                if(list.isEmpty()){
                    throw new HlsCusException("请上传评委投票意见附件！");
                }
            }
        }
    }
}
