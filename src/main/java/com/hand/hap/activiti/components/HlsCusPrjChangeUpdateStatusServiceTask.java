package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjChangeUpdateStatusServiceTask  implements JavaDelegate, IActivitiBean {

    private static final String DIRECTOR = "DIRECTOR";
    private static final String PRESIDENT = "PRESIDENT";

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    public HlsCusPrjChangeUpdateStatusServiceTask(){

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        String currentActivityId = delegateExecution.getCurrentActivityId();
        String powerfulPerson = (String) delegateExecution.getVariable("powerfulPerson");
        //有权人节点id
        String directorActivityId = "sid-9863p43z-mIGe-4j39-8enT-vARoAoeVtxzD";
        String presidentActivityId = "sid-Qj5TwFUL-P2s5-4sbh-8xqu-WvF9NZlUZ7Us";

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProject);
        databaseLockProvider.lock(prjProject);

        if (powerfulPerson != null && powerfulPerson.trim().length() != 0) {
            if (DIRECTOR.equals(powerfulPerson) && directorActivityId.equals(currentActivityId)) {
                if(!prjProject.getProjectStatus().equals(result)) {
                    prjProject.setProjectStatus(result);
                    hlsCusPrjProjectService.updateByPrimaryKey(requestCtx, prjProject);
                    updateReqInfo(prjProject,requestCtx,result);
                }
            }
            if (!DIRECTOR.equals(powerfulPerson) && !"APPROVED".equals(result) && directorActivityId.equals(currentActivityId)){
                if(!prjProject.getProjectStatus().equals(result)) {
                    prjProject.setProjectStatus(result);
                    hlsCusPrjProjectService.updateByPrimaryKey(requestCtx, prjProject);
                    updateReqInfo(prjProject,requestCtx,result);
                }
            }
            if (PRESIDENT.equals(powerfulPerson) && presidentActivityId.equals(currentActivityId)) {
                if(!prjProject.getProjectStatus().equals(result)) {
                    prjProject.setProjectStatus(result);
                    hlsCusPrjProjectService.updateByPrimaryKey(requestCtx, prjProject);
                    updateReqInfo(prjProject,requestCtx,result);
                }
            }
        }
    }

    public void updateReqInfo(HlsCusPrjProject prjProject,IRequest requestCtx,String result){
        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        IRequest request = RequestHelper.getCurrentRequest(true);
        request.setAttribute("wflRuleControlFlag", "Y");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus(result);
        hlsCusChangeReqInfo.setWflNodeStatus(result);
        hlsCusChangeReqInfoService.updateByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

    }
}
