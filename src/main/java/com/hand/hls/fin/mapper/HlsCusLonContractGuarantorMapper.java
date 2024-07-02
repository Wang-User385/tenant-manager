package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContractGuarantor;

import java.util.List;

public interface HlsCusLonContractGuarantorMapper extends Mapper<HlsCusLonContractGuarantor>{

    List<HlsCusLonContractGuarantor> selectCompanyByBpId(HlsCusLonContractGuarantor lonContractGuarantor);
}