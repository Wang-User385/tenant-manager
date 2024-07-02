package com.hand.hls.bp.mapper;



import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.prj.dto.HlsBpMasterContactInfo;
import com.hand.hls.prj.mapper.HlsBpMasterContactInfoMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by lpc on 2017/9/25.
 */
public interface HlsCusBpMasterContactInfoMapper extends HlsBpMasterContactInfoMapper<HlsCusBpMasterContactInfo> {
    List<HlsCusBpMasterContactInfo> selectIsSignPersonByBpId(HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo);

    List<HlsCusBpMasterContactInfo> queryAll(HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo);

    List<HlsCusBpMasterContactInfo> queryAllAuthorize(HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo);

    List<HlsCusBpMasterContactInfo> queryAllBenifit(HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo);
    List<HlsBpMasterContactInfo> queryContractInfoDetails(Map map);
    List<HlsCusBpMasterContactInfo> query1();
}
