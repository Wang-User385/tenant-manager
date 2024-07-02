package com.hand.hls.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.sys.event.service.SysEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HlsCusPrjMeetingPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private SysEventService sysEventService;

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        List<Map> variableList = hlsCusProcess.getVariableMap();
        Map delegateExecution = new HashMap<String, String>();
        for (Map map : variableList) {
            delegateExecution.put(map.get("name_"), map.get("text_"));
        }

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(Long.valueOf(hlsCusProcess.getBussinessKey()));
        project = prjProjectService.selectByPrimaryKey(iRequest, project);


        String statusDesc = null;
        String status;

        if ("PASS".equals(hlsCusProcess.getType())) {
            status = "APPROVED";
            statusDesc = "通过";
            project.setApprovalStatus("MEETED");
            prjProjectService.updateByPrimaryKey(iRequest,project);
        } else {
            status = "REJECTED";
            statusDesc = "拒绝";
            project.setApprovalStatus("VOTED");
            prjProjectService.updateByPrimaryKey(iRequest,project);
        }

        // 更新事件方法
        Map<String, Object> evenParams = new HashMap<>();
        evenParams.put("message", "项目编号为：" + project.getProjectNumber() + "的上会工作流" + statusDesc);
        evenParams.put("noticeTitle", "项目上会制作审批通知书工作流");
        evenParams.put("url", "");
        evenParams.put("level", 1L);
        evenParams.put("noticeType", "NOTICE");
        sysEventService.eventSave(iRequest,  Long.parseLong(hlsCusProcess.getBussinessKey()), delegateExecution.get("documentCategory").toString(),project.getDocumentType(), "PRJ", "PRJ_PROJECT_MEETING.SUBMIT", "P2D", evenParams);
    }

}
