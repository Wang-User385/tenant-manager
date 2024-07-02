package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.mapper.ContentNumberHeadMapper;
import com.hand.hls.cont.service.IContentNumberHeadService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
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
public class ConContentNumActivitiStartServiceImpl implements IActivitiCommonService {

    //获取实现类
    private static final String WORK_FLOW_TYPE = "CON_CONTENT_NUM_WFL";

    //对应工作流页面配置的唯一标志
    private static final String WORK_FLOW_KEY = "CON_CONTENT_NUM_WFL";
    //对应工作流页面配置的分类
    private static final String DEMO = "CON_CONTENT_NUM_WFL";

    private static final String CREDIT_FILE_FLAG = "creditFileFlag";


    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    private IContentNumberHeadService iContentNumberHeadService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        //设置主键
        params.put(BUSINESS_KEY, ((ContentNumberHead) list.get(0)).getHeadId());
        params.put(WORK_FLOW_NAME, WORK_FLOW_KEY);
        params.put(DEMO_NAME, DEMO);

        //设置doubleFlag
        ContentNumberHead head = new ContentNumberHead();
        head.setHeadId(((ContentNumberHead) list.get(0)).getHeadId());

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);

        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        ContentNumberHead head = new ContentNumberHead();
        head.setHeadId(id);
        head.setStatus(CANCEL_STATUS);
        iContentNumberHeadService.updateByPrimaryKeySelective(iRequest, head);
    }
}
