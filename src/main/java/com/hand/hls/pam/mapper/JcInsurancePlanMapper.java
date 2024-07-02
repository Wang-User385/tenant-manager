package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.JcInsurancePlan;

import java.util.List;

public interface JcInsurancePlanMapper extends Mapper<JcInsurancePlan>{
    List<JcInsurancePlan> selectJcInsurancePlan();
    List<JcInsurancePlan> selectMes();
}