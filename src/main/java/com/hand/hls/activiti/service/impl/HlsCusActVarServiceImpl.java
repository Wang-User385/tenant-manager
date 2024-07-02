package com.hand.hls.activiti.service.impl;


import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.activiti.service.HlsCusActVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: qixiang.shao
 * @Description: 工作流参数Service实现类
 * @Date: Created in 19:21 2018/4/25
 * @Modified By:
 */
@Service
@Transactional
public class HlsCusActVarServiceImpl implements HlsCusActVarService {

    @Autowired
    private HlsCusActVarMapper hlsCusActVarMapper;

    @Override
    public String selectWorkFlowProcessInstanceId(String workFlowKey, String businessKey) {
        return hlsCusActVarMapper.selectWorkFlowProcessInstanceId(workFlowKey, businessKey);
    }

    @Override
    public Map<String, Object> queryTaskId(String workFlowKey, String businessKey) {
        String processInstanceId = selectWorkFlowProcessInstanceId(workFlowKey, businessKey);
        String taskId = hlsCusActVarMapper.selectWorkFlowTaskId(processInstanceId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("taskId", taskId);
        map.put("processInstanceId", processInstanceId);
        return map;
    }


}
