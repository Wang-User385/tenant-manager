package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContractPurpose;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusLonContractPurposeMapper<T extends HlsCusLonContractPurpose> extends Mapper<HlsCusLonContractPurpose> {


    /**
     * 查询
     * @param lonContractPurpose
     * @return
     */
    List<HlsCusLonContractPurpose> selectLonContractPur(HlsCusLonContractPurpose lonContractPurpose);

    /**
     * 金额汇总
     * @param withdrawId
     * @return
     */
   Double  selectPurposeSum(@Param("withdrawId") Long withdrawId);
}