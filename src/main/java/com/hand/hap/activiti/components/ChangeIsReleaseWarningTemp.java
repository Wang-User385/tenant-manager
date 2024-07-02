package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Vincent(wenzheng.shao @ hand - china.com)
 * @version 1.0
 * @date 2019/9/23 0023 17:25
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class ChangeIsReleaseWarningTemp implements JavaDelegate, IActivitiBean {
    private static final String BEAN_NAME = "changeIsReleaseWarningTemp";
    @Autowired
    private HlsCusIRiskWarningService service;

    @Override
    public void execute(DelegateExecution execution) {

        String approveResult = execution.getVariable("approveResult", String.class);
        if ("APPROVED".equalsIgnoreCase(approveResult)) {
            String key = execution.getProcessInstanceBusinessKey();
            HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
            riskWarning.setRiskWarningId(Long.parseLong(key));
            List<HlsCusRiskWarning> list = service.queryAll(null, riskWarning);
            if (!list.isEmpty()) {
                riskWarning = list.get(0);
            }
            if (null != riskWarning) {
                if ("N".equalsIgnoreCase(riskWarning.getIsPresenceWarning())) {
                    riskWarning.setIsReleaseWarning("Y");
                    service.updateByPrimaryKey(null, riskWarning);
                }
            }
        }

    }

    @Override
    public String getBeanName() {
        return BEAN_NAME;
    }
}
