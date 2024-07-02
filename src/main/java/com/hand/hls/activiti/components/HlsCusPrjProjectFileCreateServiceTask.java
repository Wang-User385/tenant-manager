package com.hand.hls.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMapper;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectFileCreateServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusHlsCreditLineMapper hlsCusHlsCreditLineMapper;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusActVarMapper hlsCusActVarMapper;

    @Autowired
    private HlsCusBpMasterRequestRecordsService hlsCusBpMasterRequestRecordsService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;



    public HlsCusPrjProjectFileCreateServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProject");
        HlsCusPrjProject hlsCusPrjProject = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        String unitId = String.valueOf(delegateExecution.getVariable("unitId"));
        String companyId = String.valueOf(delegateExecution.getVariable("companyId"));
        //项目审查岗生成附件
        hlsCusPrjProject.setProjectId(hlsCusPrjProject.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx,hlsCusPrjProject);
        if(hlsCusPrjProject.getProjectId() != null){
            int attachmentExistCount = hlsCusPrjProjectAttachmentService.selectAttachmentExistCount(hlsCusPrjProject.getProjectId(),"PRJ_REVIEW_REPORT");
            if(attachmentExistCount == 0){
                HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
                hlsCusPrjProjectAttachment.setProjectId(hlsCusPrjProject.getProjectId());
                //审查报告
                hlsCusPrjProjectAttachment.setDocumentName("审查报告");
                hlsCusPrjProjectAttachment.setProjectAttachmentCategory("PRJ_REVIEW_REPORT");
                hlsCusPrjProjectAttachmentService.insertSelective(requestCtx,hlsCusPrjProjectAttachment);
            }

        }

    }

}
