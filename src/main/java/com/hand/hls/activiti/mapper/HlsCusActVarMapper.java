package com.hand.hls.activiti.mapper;


import org.apache.ibatis.annotations.Param;

/**
 * @Author: qixiang.shao
 * @Description: 工作流参数Mapper
 * @Date: Created in 19:22 2018/4/25
 * @Modified By:
 */
public interface HlsCusActVarMapper {
    /**
     * 查询工作流流程id 结束的
     *
     * @param workFlowKey
     * @param businessKey
     * @return
     */
    String selectWorkFlowProcessInstanceId(@Param("workFlowKey") String workFlowKey, @Param("businessKey") String businessKey);

    /**
     * 获取taskId
     * @param processInstanceId
     * @return
     */
    String selectWorkFlowTaskId(@Param("processInstanceId") String processInstanceId);

}
