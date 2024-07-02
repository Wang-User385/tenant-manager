package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ContractPaymentPlan;

import java.util.List;
import java.util.Map;

public interface ContractPaymentPlanMapper extends Mapper<ContractPaymentPlan>{

    List<Map> selectRaypmentPlanInfo(ContractPaymentPlan paymentPlan);

}