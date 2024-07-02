package com.hand.hls.bill.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bill.dto.hlsBillRequest;

import java.util.List;
import java.util.Map;

public interface hlsBillRequestMapper extends Mapper<hlsBillRequest>{
    List<hlsBillRequest> queryEmployee(hlsBillRequest hlsBillRequest);
    List<hlsBillRequest> queryHlsBillRequestReport(hlsBillRequest hlsBillRequest);
    List<hlsBillRequest> queryHlsBillRequestJob(hlsBillRequest hlsBillRequest);
    List<hlsBillRequest> queryHlsBillRequestJob30(hlsBillRequest hlsBillRequest);
    List<hlsBillRequest> queryHlsBillRequest(Map map);
    List<hlsBillRequest> queryAllocationIdByUserId(Long userId);
}