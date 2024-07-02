package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;
import com.hand.hls.archive.service.HlsCusArchiveBorrowService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:档案借阅
 * @author: congweijing
 * @date: 2021/8/5 14:08
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveActivitiBorrowStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "JC_BORROWING_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusArchiveBorrowService hlsCusArchiveBorrowService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;



    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(list,iRequest);

        ProcessInstanceResponse processInstanceResponse  = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        for (Object o : list) {
            //根据勾选的内容插入审批表
            HlsCusArchiveBorrow newItem = (HlsCusArchiveBorrow)o;
            newItem.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
            newItem.setBorrowReturnStatus("APPROVING");
            hlsCusArchiveBorrowService.updateByPrimaryKey(iRequest,newItem);
        }
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(List list, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("JC_BORROWING_WFL","JC_BORROWING_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);

        HlsCusArchiveBorrow hlsCusArchiveBorrow = (HlsCusArchiveBorrow)list.get(0);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusArchiveBorrow.getProjectId());
        iRequest.setAttribute("wflRuleControlFlag","Y");
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);
        //设置参数
        createRequest.setBusinessKey(hlsCusArchiveBorrow.getBorrowId().toString());

        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();
        RestVariable restVariable1 = new RestVariable();
        restVariable1.setName("processDefinitionId");
        restVariable1.setValue(id);
        variables.add(restVariable1);
        RestVariable restVariable2 = new RestVariable();
        restVariable2.setName("startUserDescription");
        restVariable2.setValue(sysUser.getDescription());
        variables.add(restVariable2);
        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("iRequest");
        restVariable3.setValue(iRequest);
        variables.add(restVariable3);
        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("startUserName");
        restVariable4.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable4);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue("");
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue("");
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(hlsCusPrjProject.getProjectName());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentNumber");
        restVariable13.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable13);

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusArchiveBorrow.getBorrowId());
        variables.add(restVariable0);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsCusArchiveBorrow newItem = new HlsCusArchiveBorrow();
        newItem.setBorrowId(id);
        newItem.setBorrowReturnStatus(CANCEL_STATUS);
        hlsCusArchiveBorrowService.updateByPrimaryKeySelective(iRequest,newItem);
    }

}
