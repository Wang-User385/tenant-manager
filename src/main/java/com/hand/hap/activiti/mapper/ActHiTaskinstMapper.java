package com.hand.hap.activiti.mapper;

import com.hand.hap.activiti.dto.ActHiTaskinst;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ActHiTaskinstMapper extends Mapper<ActHiTaskinst> {
    String queryFormKey(@Param("taskId") String taskId);

    void updateCopyMessage(Map message);

    String selectCopyMessage(Map message);

    List<String> selectHistoryProcessInstanceIdList(@Param("workFlowType") String workFlowType, @Param("businessKey") String businessKey);

    String selectLastApproveAction(Map message);

    String selectDumpApprove(Map message);

    void updateUserId(Map message);


}