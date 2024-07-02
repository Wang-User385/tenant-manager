package com.hand.hap.activiti.components;


import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.utils.HlsCusConstant;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjChangeMeetingEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";
    private static final String APPROVED_RETURN = "APPROVED_RETURN";
    private static final String REJECTED = "REJECTED";
    private static final String SUSPEND = "SUSPEND";
    //风险变更
    private static final String PROJECT_RISK_CHANGE = "PROJECT_RISK_CHANGE";
    //风险+价格变更
    private static final String PROJECT_PRICE_RISK_CHANGE = "PROJECT_PRICE_RISK_CHANGE";

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private TaskService taskService;

    public HlsCusPrjChangeMeetingEndServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProject);
        databaseLockProvider.lock(prjProject);

        String taskDefinitionKey = "";

        if ("CHANGE_REQ".equals(prjProject.getDataType())) {
            HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
            Long changeReqId = prjProject.getChangeReqId();

            hlsCusChangeReqInfo.setChangeReqId(changeReqId);
            hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCtx, hlsCusChangeReqInfo);
            if (PROJECT_RISK_CHANGE.equals(hlsCusChangeReqInfo.getChangeType())) {
                taskDefinitionKey = "sid-6CUSZngz-JaAR-4wki-8Rt4-CfaxkL4Y0irS";
            } else if (PROJECT_PRICE_RISK_CHANGE.equals(hlsCusChangeReqInfo.getChangeType())) {
                taskDefinitionKey = "sid-XRBJIeKk-58xW-4Ih9-8wT8-zG1yrc8TCHQM";
            }
        }

        List<Task> taskList = taskService.createTaskQuery().taskDefinitionKey(taskDefinitionKey).list();
        if (taskList.size() > 0) {
            for (Task item : taskList) {
                Long processInstanceId = prjProject.getProcessInstanceId();
                if (processInstanceId != null) {
                    if (String.valueOf(processInstanceId).equals(item.getProcessInstanceId())) {
//                        if(!prjProject.getProjectStatus().equals(result)){
//                            prjProject.setProjectStatus(result);
//                            hlsCusPrjProjectService.updateByPrimaryKey(requestCtx, prjProject);

                        HlsCusPrjProject hlsCusPrjProjectOld = new HlsCusPrjProject();
                        hlsCusPrjProjectOld.setProjectId(prjProject.getRefProjectId());
                        hlsCusPrjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProjectOld);

                        HlsCusPrjProject hlsCusPrjProjectNew = new HlsCusPrjProject();
                        hlsCusPrjProjectNew.setProjectId(prjProject.getProjectId());
                        hlsCusPrjProjectNew = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProjectNew);

                        //结束节点
                        Map<String, Object> vMap = new HashMap<String, Object>(2);
                        vMap.put("approveResult", "MEETING_APPROVED");
                        vMap.put("approveResultResult", "同意");
                        vMap.put("hlsCusPrjProjectOld", JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectOld)).toString());
                        vMap.put("hlsCusPrjProjectNew", JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectNew)).toString());
                        vMap.put("comment", "编号为" + prjProject.getProjectNumber() + "的项目变更上会已完成。");
                        // 添加审批备注
                        taskService.addComment(item.getId(), item.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "自动审批");
                        taskService.addComment(item.getId(), item.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, "编号为" + prjProject.getProjectNumber() + "的项目变更上会已完成。");
                        // 自动审批
                        taskService.complete(item.getId(), vMap);
//                        }else{
//                            throw new ActivitiException("测试");
//                        }
                    }
                }
            }
        }

        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

        if (hlsCusChangeReqInfo.getWflNodeStatus() == null) {

            hlsCusChangeReqInfo.setStatus(result);

        } else if ("APPROVED".equals(hlsCusChangeReqInfo.getWflNodeStatus())) {

            //同意通知书制作完成
            prjProject.setProjectStatus("NOTICE_APPROVED");
            prjProject.setApprovalStatus("MEETED");
            hlsCusChangeReqInfo.setStatus("NOTICE_APPROVED");

        } else if ("REJECTED".equals(hlsCusChangeReqInfo.getWflNodeStatus())) {

            //否决通知书制作完成
            prjProject.setProjectStatus("NOTICE_VETO");
            prjProject.setApprovalStatus("VOTED");
            hlsCusChangeReqInfo.setStatus("NOTICE_VETO");

        } else if ("SUSPEND".equals(hlsCusChangeReqInfo.getWflNodeStatus())) {

            //暂缓通知书制作完成
            prjProject.setProjectStatus("NOTICE_HOLD");
            hlsCusChangeReqInfo.setStatus("NOTICE_HOLD");
        }


        hlsCusChangeReqInfoService.updateByPrimaryKey(requestCtx, hlsCusChangeReqInfo);
    }
}
