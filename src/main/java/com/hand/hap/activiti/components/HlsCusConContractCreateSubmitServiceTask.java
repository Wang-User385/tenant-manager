package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.fnd.dto.HlsCusDocumentList;
import com.hand.hls.fnd.service.HlsCusDocumentListService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.service.SysUserService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractCreateSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SysUserService userService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusDocumentListService hlsCusDocumentListService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    public HlsCusConContractCreateSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String projectId = delegateExecution.getProcessInstanceBusinessKey();

        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        //根据状态修改合同信息
        resultHlsCusPrjProject.setProjectId(Long.parseLong(projectId));
//        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProject);
//        resultHlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(resultHlsCusPrjProject);
//        databaseLockProvider.lock(resultHlsCusPrjProject);
        if ("APPROVED".equalsIgnoreCase(result)) {
            resultHlsCusPrjProject.setCreateContractStatus("APPROVED");
            resultHlsCusPrjProject.setContractStatus("APPROVED");
            resultHlsCusPrjProject.setContractEndDate(new Date());
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            resultHlsCusPrjProject.setCreateContractStatus("REJECT");
            resultHlsCusPrjProject.setContractStatus("REJECTED");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        }else{
            resultHlsCusPrjProject.setCreateContractStatus("APPROVED_RETURN");
            resultHlsCusPrjProject.setContractStatus("APPROVED_RETURN");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        }
    }

}
