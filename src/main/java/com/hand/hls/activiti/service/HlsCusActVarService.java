package com.hand.hls.activiti.service;


import com.hand.hap.core.ProxySelf;

import java.util.List;
import java.util.Map;

/**
 * @Author: qixiang.shao
 * @Description: 工作流参数Service
 * @Date: Created in 19:19 2018/4/25
 * @Modified By:
 */
public interface HlsCusActVarService extends ProxySelf<HlsCusActVarService> {
    /**
     * 查询工作流流程id 结束的
     * @param workFlowKey
     * @param businessKey
     * @return
     */
    String selectWorkFlowProcessInstanceId(String workFlowKey, String businessKey);

    /**
     * 询proceInId和taskId
     * @param workFlowKey
     * @param businessKey
     * @return
     */
    Map<String, Object> queryTaskId(String workFlowKey, String businessKey);
}
