package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 工作流结束保存事件
 * @Author: wty
 * @Date: Created in 16:51 2018/5/21
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmFiveClassificationSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusIFiveClassificationService fcService;

    @Autowired
    private HlsCusFiveClassificationContractMapper fcContractMapper;

    @Autowired
    private HlsCusIFiveClassificationContractService fcContractService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long fiveClassificationId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());

        HlsCusFiveClassification newDto = new HlsCusFiveClassification();
        newDto.setFiveClassificationId(fiveClassificationId);
        if ("APPROVED".equalsIgnoreCase(result)||"APPROVED_COMPLETE".equals(result)) {
            newDto.setStatus("APPROVED");
            HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
            fiveClassificationContract.setFiveClassificationId(fiveClassificationId);
            List<HlsCusFiveClassificationContract> contracts = fcContractMapper.selectFiveClassificationContracts(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(contracts)) {
                    for (HlsCusFiveClassificationContract c : contracts) {
                        Map map = new HashMap();
                        map.put("projectId", c.getProjectId());
                        map.put("fiveClassificationResult", c.getFinalClassificationResult());
                        if ("CON".equals(c.getContractType())) {
                            fcContractMapper.updateConFiveClassificationResult(map);
                        } else if ("FCT".equals(c.getContractType())) {
                            fcContractMapper.updateFctFiveClassificationResult(map);
                        }
                    }
                }

        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            newDto.setStatus("APPROVED_RETURN");
        }
        fcService.updateByPrimaryKeySelective(iRequest, newDto);
    }
}
