package com.hand.hls.ins.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ins.dto.PrjInsureBeneficiary;

import java.util.List;

public interface PrjInsureBeneficiaryMapper extends Mapper<PrjInsureBeneficiary>{
    List<PrjInsureBeneficiary> queryAll(PrjInsureBeneficiary prjInsureBeneficiary);
}