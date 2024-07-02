package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.BpSignService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.SignService;
import com.hand.hls.sign.dto.SignRecord;
import com.hand.hls.sign.mapper.SignRecordMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description：进件投放审查工作流结束监听器
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/11/17 16:37
 * @Version：1.0
 */
@Component
public class PrjSignServiceTask implements JavaDelegate, IActivitiBean {

    Logger logger = LoggerFactory.getLogger(PrjSignServiceTask.class);

    private static final String PROJECT = "project";

    private static final String IREQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final String NEW = "NEW";

    private static final String N = "N";


    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    @Qualifier("orgTenantSignServiceImpl")
    private BpSignService bpSignService;


    public PrjSignServiceTask(){
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution delegateExecution) {

        logger.info("--------进件投放审查工作流 结束监听:PrjSignServiceTask--------");
        String prj = (String) delegateExecution.getVariable(PROJECT);
        HlsCusPrjProject prjProject = JSON.parseObject(prj, HlsCusPrjProject.class);

        IRequest requestCtx = (IRequest) delegateExecution.getVariable(IREQUEST);
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(delegateExecution.getVariable(START_USER_ID));

        HlsCusPrjProject project = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        // lock prj_project
        //databaseLockProvider.lock(project);
        requestCtx.setUserId(Long.valueOf(userId));

        if (APPROVED.equalsIgnoreCase(result)){

            project.setSignStatus(APPROVED);
            project.setSignDate(new Date());
            try {
                elecSign(project.getProjectId());
            }catch (Exception e){
                logger.error("投放审查调用电子签章失败!");
            }
        } else if (REJECTED.equalsIgnoreCase(result)){
            project.setSignStatus(REJECTED);
        }

        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, project);

    }

    public void elecSign(Long projectId){
        SignRecord signRecord = new SignRecord();
        signRecord.setSourceDocCategory("PRJ_PROJECT");
        signRecord.setSourceDocId(projectId);
        signRecord = signRecordMapper.selectOne(signRecord);
        if(signRecord != null){
            // async
//            bpSignService.longTermSign(signRecord.getSignId(), RequestHelper.getCurrentRequest(),projectId,"甲方（盖章）","company");
        }
    }
}
