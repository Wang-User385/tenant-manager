//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import com.hand.hls.prj.dto.HlsBpMasterRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsBpMasterRelationMapper<T extends HlsCusBpMasterRelation> extends Mapper<HlsCusBpMasterRelation> {
    List<HlsCusBpMasterRelation> selectRelationType(Long var1);

    List<HlsCusBpMasterRelation> selectRelationCode(Long var1);

    Long queryBpId(@Param("bpCode") String var1, @Param("bpName") String var2);

    List<HlsCusBpMasterRelation> queryAll(HlsCusBpMasterRelation var1);

    List<HlsCusBpMasterRelation> queryAllType(HlsCusBpMasterRelation var1);

    List<HlsCusBpMasterRelation> selectBpRelationType(HlsCusBpMasterRelation var1);
}
