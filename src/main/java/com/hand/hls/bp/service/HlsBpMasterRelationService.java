//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;

import java.util.List;

public interface HlsBpMasterRelationService extends IBaseService<HlsCusBpMasterRelation>, ProxySelf<HlsBpMasterRelationService> {
    List<HlsCusBpMasterRelation> selectRelationType(Long var1);

    List<HlsCusBpMasterRelation> selectRelationCode(Long var1);

    Long queryBpId(String var1, String var2);

    List<HlsCusBpMasterRelation> queryAll(IRequest var1, HlsCusBpMasterRelation var2, int var3, int var4);

    List<HlsCusBpMasterRelation> queryAllType(IRequest var1, HlsCusBpMasterRelation var2, int var3, int var4);

    void batchRelationDelete(List<HlsCusBpMasterRelation> var1);

    List<HlsCusBpMasterRelation> batchRelationUpdate(IRequest var1, List<HlsCusBpMasterRelation> var2);

    List<HlsCusBpMasterRelation> selectBpRelationType(IRequest var1, HlsCusBpMasterRelation var2);
}
