//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import com.hand.hls.prj.dto.HlsBpMasterRelation;
import com.hand.hls.prj.mapper.HlsBpMasterRelationMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusBpMasterRelationMapper extends HlsBpMasterRelationMapper<HlsCusBpMasterRelation> {
    /**
     * 根据 bpId 查询客户集团信息明细
     * @param map
     * @return java.util.List<com.hand.hls.bp.dto.HlsCusBpMasterRelation>
     */
    List<HlsCusBpMasterRelation> queryCusBpMasterRelationByBpId(Map map);

    List<HlsCusBpMasterRelation> query(@Param("bpId")Long bpId);

    List<HlsCusBpMasterRelation> queryRelationByBpRelationId(@Param("bpRelationId")Long bpRelationId);
    List<HlsCusBpMasterRelation> queryRelationDetails(Map map);
}
