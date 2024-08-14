package com.hand.hap.activiti.components;

import cfca.paperless.base.util.StringUtil;
import com.alibaba.druid.util.StringUtils;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
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
 * @author JINGZHOU.LI@hand-china.com 2024/8/12 19:09
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class ContractFirstRiskSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long contractId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        String assignee = (String) delegateExecution.getVariables().get("assignee");

        if (!StringUtils.isEmpty(assignee)){
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(contractId);
            hlsCusConContract.setRiskAssistantFirst(Long.parseLong(assignee));
            hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);

        }


    }

}
