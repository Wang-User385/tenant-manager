package com.hand.hls.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectChangeStatusServiceTask implements JavaDelegate, IActivitiBean{

    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        //SysUser sysUser = userService.queryUserByCode(employeeCode);
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProjectOld");
        HlsCusPrjProject hlsCusPrjProjectOld = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectOld = new HlsCusPrjProject();

        String hlsCusPrjProjectPramsNew = (String) delegateExecution.getVariable("hlsCusPrjProjectNew");
        HlsCusPrjProject hlsCusPrjProjectNew = JSON.parseObject(hlsCusPrjProjectPramsNew, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectNew = new HlsCusPrjProject();

        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(hlsCusPrjProjectNew.getChangeReqId());
        IRequest request = RequestHelper.getCurrentRequest(true);
        request.setAttribute("wflRuleControlFlag", "Y");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        hlsCusChangeReqInfo.setStatus(result);
        hlsCusChangeReqInfo.setWflNodeStatus(result);
        hlsCusChangeReqInfoService.updateByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

    }

}
