package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.fct.service.HlsCusFctProjectService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.service.SysUserService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyan on 2017/11/13.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectChangeSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public HlsCusPrjProjectChangeSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String processDefinitionId =  delegateExecution.getProcessDefinitionId().split(":")[0];
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProjectOld");
        HlsCusPrjProject hlsCusPrjProjectOld = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectOld = new HlsCusPrjProject();

        String hlsCusPrjProjectPramsNew = (String) delegateExecution.getVariable("hlsCusPrjProjectNew");
        HlsCusPrjProject hlsCusPrjProjectNew = JSON.parseObject(hlsCusPrjProjectPramsNew, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectNew = new HlsCusPrjProject();

        //根据状态修改项目信息
        resultHlsCusPrjProjectOld.setProjectId(hlsCusPrjProjectOld.getProjectId());
        resultHlsCusPrjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProjectOld);

        resultHlsCusPrjProjectNew.setProjectId(hlsCusPrjProjectNew.getProjectId());
        resultHlsCusPrjProjectNew = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProjectNew);

        Long projectIdOld = hlsCusPrjProjectOld.getProjectId();
        Long projectIdNew = hlsCusPrjProjectNew.getProjectId();

        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(resultHlsCusPrjProjectNew.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

        Boolean swapParojectFlag = false;

        if(hlsCusChangeReqInfo.getWflNodeStatus() == null || "APPROVED".equals(hlsCusChangeReqInfo.getWflNodeStatus())){
            hlsCusChangeReqInfo.setStatus(result);
            swapParojectFlag = true;
        }else if("REJECTED".equals(hlsCusChangeReqInfo.getWflNodeStatus())){
            //更新项目状态
            hlsCusPrjProjectOld.setProjectStatus("APPROVED");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx,hlsCusPrjProjectOld);
            resultHlsCusPrjProjectNew.setDataType("HISTORY");
            resultHlsCusPrjProjectNew.setDataClass("NORMAL");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx,resultHlsCusPrjProjectNew);
            hlsCusChangeReqInfo.setStatus("REJECT");
        }

        hlsCusChangeReqInfo.setInstanceEndFlag("Y");

        if(swapParojectFlag){
            /*删除旧数据，将新数据回写*/
            requestCtx.setAttribute("processDefinitionId", processDefinitionId);

            hlsCusPrjProjectService.deleteOld(projectIdOld, requestCtx);
            try {
                hlsCusPrjProjectService.updateOld(requestCtx,projectIdOld, projectIdNew, requestCtx);
            } catch (HlsCusException e) {
                logger.info("项目变更更新数据失败!");
                logger.info(e.getMessage());
            }

            /*更新normal*/
            HlsCusPrjProject prjProjectnNew = new HlsCusPrjProject();
            Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(resultHlsCusPrjProjectNew);
            hlsBeanRefUtilService.setFieldValue(prjProjectnNew, map1);
            prjProjectnNew.setProjectStatus("APPROVED");
            prjProjectnNew.setDataClass("NORMAL");
            prjProjectnNew.setDataType("NORMAL");
            prjProjectnNew.setRefProjectId(null);
            prjProjectnNew.setChangeReqId(null);

            //更新原始项目
            prjProjectnNew.setProjectId(projectIdOld);
            prjProjectnNew.setMeetingStatus("APPROVED");

            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProjectnNew);

            //变更历史单据状态从CHANGE_REQ_HISTORY改为HISTORY
            HlsCusPrjProject prjChanceH = new HlsCusPrjProject();
            prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
            prjChanceH.setDataType("CHANGE_REQ_HISTORY");
            //prjChanceH = hlsCusPrjProjectService.select(requestCtx, prjChanceH, 1, 999).get(0);
            List<HlsCusPrjProject> select = hlsCusPrjProjectMapper.select(prjChanceH);
            if(select.size() > 0){
                prjChanceH = select.get(0);
                prjChanceH.setDataType("HISTORY");
                //hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjChanceH);
                hlsCusPrjProjectMapper.updateByPrimaryKeySelective(prjChanceH);
            }
        }
        hlsCusChangeReqInfoService.updateByPrimaryKey(requestCtx, hlsCusChangeReqInfo);
    }
}
