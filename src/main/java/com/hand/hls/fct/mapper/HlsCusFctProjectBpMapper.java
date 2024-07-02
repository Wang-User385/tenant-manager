package com.hand.hls.fct.mapper;

import com.hand.hls.fct.dto.HlsCusFctProjectBp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: cyy
 * @Description:
 * @Date: Created in 2018/4/18 1:28
 * @Modified By:
 */
public interface HlsCusFctProjectBpMapper extends FctProjectBpMapper<HlsCusFctProjectBp> {

    List<Map> queryMortgagePersion(Long projectId);


    List<HlsCusFctProjectBp> selectSellerInfoByProjectId(@Param("projectId") Long projectId);


    List<HlsCusFctProjectBp> fctProjectBpQuery1(HlsCusFctProjectBp var1);

    List<HlsCusFctProjectBp> fctProjectBpQuery2(HlsCusFctProjectBp var1);


    List<HlsCusFctProjectBp> selectBpMasterNotSave(@Param("projectId") Long projectId);

    List<HlsCusFctProjectBp> selectSentBpName(HlsCusFctProjectBp var1);

    List<HlsCusFctProjectBp> fctProjectBpQueryAccount(HlsCusFctProjectBp var1);
}
