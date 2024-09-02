package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineLease;
import com.hand.hls.prj.dto.HlsCusPrjProjectLease;

import java.util.List;

public interface HlsCusPrjProjectLeaseMapper extends Mapper<HlsCusPrjProjectLease>{
    /***
     * 保理项目审批抵质押明细查询
     * @param hlsCusPrjProjectLease
     * @return
     */
    List<HlsCusPrjProjectLease> findFactoringApprovalInfo(HlsCusPrjProjectLease hlsCusPrjProjectLease);
}