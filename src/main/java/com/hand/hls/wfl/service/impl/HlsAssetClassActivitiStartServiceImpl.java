package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.ast.dto.AssetClassHead;
import com.hand.hls.ast.service.IAssetClassHeadService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description: 资产分类审批流提交
 * @date:2019/8/7
 */
@Service
@Transactional
public class HlsAssetClassActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String ASSET_CLASSIFICATION_WORK_FLOW = "ASSET_CLASSIFICATION_WORK_FLOW";
    private static final String DEMO = "DEMO";
    private static final String BUSINESS_KEY = "BUSINESS_KEY";
    private static final String ASSET = "ASSET";

    @Autowired
    private IActivitiService iActivitiService;
    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private IAssetClassHeadService assetClassHeadService;


    @Override
    public String getWorkFlowType() {
        return ASSET_CLASSIFICATION_WORK_FLOW;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        //设置主键
        map.put(WORK_FLOW_NAME, ASSET_CLASSIFICATION_WORK_FLOW);
        map.put(DEMO, ASSET);
        map.put(BUSINESS_KEY, ((AssetClassHead) list.get(0)).getClassHeadId());

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, map);
        this.iActivitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        AssetClassHead assetClassHead = new AssetClassHead();
        assetClassHead.setClassHeadId(id);
        assetClassHead.setApproveStatus(HlsConstantUtil.WorkFlowStatus.CANCEL);
        this.assetClassHeadService.updateByPrimaryKeySelective(iRequest, assetClassHead);
    }
}
