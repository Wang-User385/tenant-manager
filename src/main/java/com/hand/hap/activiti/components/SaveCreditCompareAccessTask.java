package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsChanceBusinessAccessCompareMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fnd.dto.HlsBusinessAccessCompare;
import com.hand.hls.fnd.mapper.HlsBusinessAccessCompareMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
public class SaveCreditCompareAccessTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsBusinessAccessCompareMapper businessAccessCompareMapper;
    @Autowired
    private HlsChanceBusinessAccessCompareMapper chanceCompareMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String result = (String) delegateExecution.getVariable("approveResult");
        if ("APPROVED".equalsIgnoreCase(result)) {
            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsBusinessAccessCompare accessCompare = new HlsBusinessAccessCompare();
            accessCompare.setBusinessAccess("CHANCE");
            List<HlsBusinessAccessCompare> select = businessAccessCompareMapper.select(accessCompare);
            if(CollectionUtils.isNotEmpty(select)){
                for (HlsBusinessAccessCompare businessAccessCompare : select) {
                    if(businessAccessCompare != null){
                        HlsChanceBusinessAccessCompare chanceCompare = new HlsChanceBusinessAccessCompare();
                        BeanUtils.copyProperties(businessAccessCompare,chanceCompare);
                        chanceCompare.setDocumentId(chanceId);
                        chanceCompareMapper.insert(chanceCompare);
                    }
                }
            }
        }
    }
}
