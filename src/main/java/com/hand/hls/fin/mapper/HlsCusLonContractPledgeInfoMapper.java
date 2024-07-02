package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContractPledgeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusLonContractPledgeInfoMapper extends Mapper<HlsCusLonContractPledgeInfo> {


    /**
     * 质押明细查询
     * @param lonContractPledgeInfo
     * @return
     */
    List<HlsCusLonContractPledgeInfo> selectContractPledgeInfo(HlsCusLonContractPledgeInfo lonContractPledgeInfo);


    Map<String,Object> selectPledgeMaxAndMinTimes(@Param("pledgeId") Long pledgeId);

}