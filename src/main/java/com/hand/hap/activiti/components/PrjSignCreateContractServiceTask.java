package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.IPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description：二期功能：进件投放审查工作流结束后自动创建合同数据监听器
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/11/23 14:44
 * @Version：1.0
 */
@Component
public class PrjSignCreateContractServiceTask implements JavaDelegate, IActivitiBean {

    private static final Logger logger = LoggerFactory.getLogger(PrjSignCreateContractServiceTask.class);


    private static final String PROJECT = "project";

    private static final String IREQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";
    private static final String APPROVED = "APPROVED";

    private final static String CONTRACT_TEXT_STATUS_AUDITED = "AUDITED";
    public static final String APPROVING = "APPROVING";

    @Autowired
    private IConContractService conContractService;
    @Autowired
    private IPrjProjectService prjProjectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution delegateExecution) {
        logger.info("--------进件投放审查工作流 结束监听器-自动生成合同数据:PrjSignCreateContractServiceTask--------");
        String prj = (String) delegateExecution.getVariable(PROJECT);
        HlsCusPrjProject prjProject = JSON.parseObject(prj, HlsCusPrjProject.class);

        IRequest requestCtx = (IRequest) delegateExecution.getVariable(IREQUEST);
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(delegateExecution.getVariable(START_USER_ID));
        requestCtx.setUserId(Long.valueOf(userId));

        HlsCusPrjProject project = prjProjectService.selectByPrimaryKey(requestCtx, prjProject);

        if(APPROVING.equalsIgnoreCase(project.getSignStatus())) {
            //流程审批通过后生成合同
            if (APPROVED.equalsIgnoreCase(result)) {
                conContractService.saveConContractFromPrjProjectSign(requestCtx, prjProject);
                project.setContractTextStatus(CONTRACT_TEXT_STATUS_AUDITED);
                prjProjectService.updateByPrimaryKeySelective(requestCtx, project);
            }
        }

    }

}
