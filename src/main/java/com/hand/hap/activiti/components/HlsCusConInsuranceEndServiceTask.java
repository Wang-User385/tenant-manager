package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.prj.service.HlsCusPrjProjectInsureService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/15 15:29
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConInsuranceEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String REJECTED = "REJECTED";

    private static final String APPROVED = "APPROVED";

    @Autowired
    private HlsCusPrjProjectInsureService hlsCusPrjProjectInsureService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    public HlsCusConInsuranceEndServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long contractId = (Long) delegateExecution.getVariable("contractId");
        HlsCusPrjProjectInsure prjProjectInsure = (HlsCusPrjProjectInsure) delegateExecution.getVariable("hlsCusPrjProjectInsure");
        HlsCusPrjProjectInsure hlsCusPrjProjectInsure = new HlsCusPrjProjectInsure();

        hlsCusPrjProjectInsure.setInsureId(prjProjectInsure.getInsureId());
        hlsCusPrjProjectInsure = hlsCusPrjProjectInsureService.selectByPrimaryKey(requestCtx, hlsCusPrjProjectInsure);
        databaseLockProvider.lock(hlsCusPrjProjectInsure);
        if (APPROVED.equalsIgnoreCase(result)) {
            flag = "APPROVED";
            hlsCusPrjProjectInsure.setApprovalStatus(flag);
            hlsCusPrjProjectInsureService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProjectInsure);
        } else if (REJECTED.equalsIgnoreCase(result)) {
            flag = "REJECTED";
            hlsCusPrjProjectInsure.setApprovalStatus(flag);
            hlsCusPrjProjectInsureService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProjectInsure);
        }
    }
}
