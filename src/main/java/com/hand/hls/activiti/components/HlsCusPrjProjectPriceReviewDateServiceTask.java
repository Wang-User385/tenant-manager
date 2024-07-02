package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author Eugene Song
 * @Date: 2020/5/21
 * @Description: 价格审核岗审批通过日期 && 价格审核人 更新
 * @Purpose:
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectPriceReviewDateServiceTask implements TaskListener, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;


    @Override
    public void notify(DelegateTask delegateTask) {

        IRequest iRequest = RequestHelper.getCurrentRequest();
        String projectId = delegateTask.getExecution().getVariable("projectId").toString();

        //当前审批人
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        Long allocationId = Long.valueOf(delegateTask.getAssignee());
        SysUserAllocation sysUserAllocation = sysUserAllocationMapper.selectByPrimaryKey(allocationId);
        prjProject.setPriceReviewPerson(sysUserAllocation.getUserId());
        prjProject.setProjectId(Long.parseLong(projectId));
        prjProject.setPriceReviewDate(new Date());
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
    }
}
