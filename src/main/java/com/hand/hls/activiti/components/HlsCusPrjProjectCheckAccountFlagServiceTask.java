package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectCheckAccountFlagServiceTask implements JavaDelegate, IActivitiBean {


    public HlsCusPrjProjectCheckAccountFlagServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String quotaFlag = (String) delegateExecution.getVariable("quotaFlag");
        String accountFlag = (String) delegateExecution.getVariable("accountFlag");

        if(quotaFlag.equals("N") && (!accountFlag.equals("Y"))){
            throw new ActivitiException("价格未经计财审定，初审无法提交至复审!");
        }

    }

}
