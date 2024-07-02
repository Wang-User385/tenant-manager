package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.prj.service.IBpMasterChangeReqService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 商业伙伴变更工作流结束任务监听器
 * Created by ximufeng on 2019/07/10
 */
@Component
public class BpMasterChangeServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsBpMasterService bpMasterService;
    @Autowired
    private IBpMasterChangeReqService bpMasterChangeReqService;
    @Autowired
    private IActivitiService activitiService;


    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NO_FLAG = "N";
    private static final String DOCUMENT_CATEGORY_BP = "HLS_BP_MASTER_CHANGE";

    public BpMasterChangeServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String bpChangeReq = (String) delegateExecution.getVariable("bpChangeReq");
        BpMasterChangeReq changeReq = JSON.parseObject(bpChangeReq, BpMasterChangeReq.class);


        HlsBpMaster hlsBpMaster = new HlsBpMaster();
        hlsBpMaster.setBpId(changeReq.getBpId());
        hlsBpMaster = bpMasterService.selectByPrimaryKey(requestCtx, hlsBpMaster);

        // lock con_contract
//        databaseLockProvider.lock(hlsBpMaster);
        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            try {
                bpMasterChangeReqService.leaveHistory(requestCtx, changeReq, Long.parseLong(delegateExecution.getProcessInstanceId()));
                //更新SAP接口传输标志
                hlsBpMaster = bpMasterService.selectByPrimaryKey(requestCtx, hlsBpMaster);
//                hlsBpMaster.setSapSendVenderFlag("");
//                hlsBpMaster.setSapSendBpFlag("");
                bpMasterService.updateByPrimaryKey(requestCtx, hlsBpMaster);
            } catch (Exception e) {
                logger.error("leave history error:", e);
            }
        } else if ("CANCEL".equalsIgnoreCase(result)) {
            flag = "CANCEL";
        } else {
            flag = "REJECTED";
        }

        BpMasterChangeReq bpMasterChangeReq = new BpMasterChangeReq();
        bpMasterChangeReq.setChangeReqId(changeReq.getChangeReqId());
        bpMasterChangeReq.setProcessInstanceId(Long.parseLong(delegateExecution.getProcessInstanceId()));
        bpMasterChangeReq.setChangeReqDate(new Date());
        bpMasterChangeReq.setStatus(flag);
        bpMasterChangeReqService.updateByPrimaryKeySelective(requestCtx, bpMasterChangeReq);
        try {
            activitiService.contractChangeEndDataTransferTask(result,changeReq.getChangeReqId(),DOCUMENT_CATEGORY_BP);
        }catch (Exception e) {
            logger.info("迁移变更数据异常，{}",e.getMessage());
        }
    }


}
