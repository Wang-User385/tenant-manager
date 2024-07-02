package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
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
public class HlsCusPrjProjectUpdateAccountFlagServiceTask implements JavaDelegate, IActivitiBean {


    public HlsCusPrjProjectUpdateAccountFlagServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {

        delegateExecution.setVariable("accountFlag", "Y");

    }

}
