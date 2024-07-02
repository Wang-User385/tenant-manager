package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContractQuotation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusLonContractQuotationMapper<T extends HlsCusLonContractQuotation> extends Mapper<HlsCusLonContractQuotation> {
    HlsCusLonContractQuotation selectByLonContract(HlsCusLonContractQuotation hlsCusLonContractQuotation);

    List<HlsCusLonContractQuotation>lonContractQuotationById(HlsCusLonContractQuotation hlscusloncontractquotation);

    int updateXirrNull(@Param("quotationId") Long quotationId);


}