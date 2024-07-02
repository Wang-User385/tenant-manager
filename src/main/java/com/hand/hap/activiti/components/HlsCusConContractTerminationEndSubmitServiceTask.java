package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusContractTermination;
import com.hand.hls.cont.service.HlsCusContractAttachmentService;
import com.hand.hls.cont.service.HlsCusContractTerminationService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractTerminationEndSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusContractTerminationService hlsCusContractTerminationService;
    @Autowired
    private HlsCusContractAttachmentService hlsCusContractAttachmentService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Override
    public void execute(DelegateExecution delegateExecution) {

        String contractId = delegateExecution.getProcessInstanceBusinessKey();
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable(HlsCusConstant.WORKFLOW_PARAMS.IREQUEST);
        //获取前台的判断信息，通过或是不通过等
        String result = (String) delegateExecution.getVariable(HlsCusConstant.WORKFLOW_PARAMS.APPROVE_RESULT);
        HlsCusContractTermination hlsCusContractTermination = new HlsCusContractTermination();
        hlsCusContractTermination.setContractId(Long.parseLong(contractId));
        hlsCusContractTermination = hlsCusContractTerminationService.queryTerminationByContractId(Long.parseLong(contractId));
        HlsCusConContract resultHlsCusConContract = new HlsCusConContract();
        resultHlsCusConContract.setContractId(Long.parseLong(contractId));
        HlsCusContractTermination resultHlsCusContractTermination = new HlsCusContractTermination();
        resultHlsCusContractTermination.setContractTerminationId(hlsCusContractTermination.getContractTerminationId());

        resultHlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestCtx, resultHlsCusConContract);
//项目数据
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(resultHlsCusConContract.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProject);
        resultHlsCusContractTermination = hlsCusContractTerminationService.selectByPrimaryKey(requestCtx, resultHlsCusContractTermination);
        //为表上锁，防止操作中对数据进行修改
        databaseLockProvider.lock(resultHlsCusContractTermination);

        //当是通过时复制表信息
        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equalsIgnoreCase(result)) {
            //修改流程的状态
            databaseLockProvider.lock(resultHlsCusConContract);
            resultHlsCusConContract.setContractStatus(HlsCusConstant.CONTRACT_STATUS.TERMINATION);
            resultHlsCusContractTermination.setStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, resultHlsCusConContract);
            hlsCusContractTerminationService.updateByPrimaryKeySelective(requestCtx, resultHlsCusContractTermination);
            hlsCusPrjProject.setContractStatus(HlsCusConstant.CONTRACT_STATUS.TERMINATION);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
            //生成【所有权有权转移证书】
//            HlsCusConContract hlsCusConContractDocCreate = new HlsCusConContract();
//            hlsCusConContractDocCreate = hlsCusConContractService.selectByPrimaryKey(requestCtx, resultHlsCusConContract);
//            hlsCusConContractDocCreate.setDocumentType("PRJ_TRANSFER_GD");
//            hlsCusConContractDocCreate.setDocDocumentName("所有权转让证明");
//            hlsCusConContractDocCreate.setTempletCode("EBFIL_PRJ_TRANSFER_GD");
//            try {
//                hlsCusContractAttachmentService.createContractDocumentFile(requestCtx, hlsCusConContractDocCreate);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if (HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN.equalsIgnoreCase(result)) {
            resultHlsCusContractTermination.setStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN);
            hlsCusContractTerminationService.updateByPrimaryKeySelective(requestCtx, resultHlsCusContractTermination);
            hlsCusPrjProject.setContractStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        } else if (HlsCusConstant.WORKFLOW_STATUS.REJECTED.equalsIgnoreCase(result)) {
            resultHlsCusContractTermination.setStatus(HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            hlsCusContractTerminationService.updateByPrimaryKeySelective(requestCtx, resultHlsCusContractTermination);
            hlsCusPrjProject.setContractStatus(HlsCusConstant.WORKFLOW_STATUS.REJECTED);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
        }
    }
}
