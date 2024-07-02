package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCtLonContractChangeReq;
import org.activiti.engine.task.Task;

import java.util.List;

public interface HlsCusCtLonContractChangeReqMapper<T extends HlsCusCtLonContractChangeReq> extends Mapper<HlsCusCtLonContractChangeReq> {
    List<HlsCusCtLonContractChangeReq> selectAllChangeReq(HlsCusCtLonContractChangeReq lonContractChangeReq);

    void updateChangeReq(HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq);

    void updateActivitiProcessEndTime(Long processInstanceId);

    HlsCusCtLonContractChangeReq selectLonConChangeReqChart(Long companyId);

    List<Task> selectProjectInvestmentWflTask();
}