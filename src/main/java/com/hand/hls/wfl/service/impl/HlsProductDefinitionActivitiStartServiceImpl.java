package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.service.IHlsProductDefinitionService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HlsProductDefinitionActivitiStartServiceImpl implements IActivitiCommonService {
    //获取实现类
    private static final String WORK_FLOW_TYPE = "PRODUCT_DEFINITION";
    //对应工作流页面配置的唯一标志
    private static final String WORK_FLOW_KEY = "PRODUCT_DEFINITION";
    //对应工作流页面配置的分类
    private static final String DEMO = "FND";
    private static final String BUSINESS_KEY = "BUSINESS_KEY";

    @Autowired
    private IActivitiService iActivitiService;
    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private IHlsProductDefinitionService iHlsProductDefinitionService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        //设置主键
        map.put(WORK_FLOW_NAME, WORK_FLOW_KEY);
        map.put(DEMO_NAME, DEMO);
        map.put(BUSINESS_KEY, ((HlsProductDefinition) list.get(0)).getDefinitionId());

        ProcessInstanceCreateRequest processInstanceCreateRequest = this.wflGetProcessInstanceComponents.getProcessInstance(iRequest, map);

//        ProcessInstanceResponse processInstanceResponse = iActivitiService.startProcess(iRequest, processInstanceCreateRequest);
        this.iActivitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        Long definitionId = Long.parseLong(businessKey);

        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
        hlsProductDefinition.setDefinitionId(definitionId);
        hlsProductDefinition.setProductStatus(HlsConstantUtil.WorkFlowStatus.CANCEL);

        iHlsProductDefinitionService.updateByPrimaryKeySelective(iRequest, hlsProductDefinition);
    }
}
