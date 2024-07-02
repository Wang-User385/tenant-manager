//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.FctProjectBpInfo;
import com.hand.hls.fct.dto.HlsCusFctProjectBp;

import java.util.List;

public interface FctProjectBpMapper<T extends HlsCusFctProjectBp> extends Mapper<HlsCusFctProjectBp> {
    List<FctProjectBpInfo> queryBpInfo(Long var1);

    List<HlsCusFctProjectBp> fctProjectBpQuery(HlsCusFctProjectBp var1);

    List<HlsCusFctProjectBp> fctProjectBpInfoQuery(HlsCusFctProjectBp var1);

    void updateProjectBp(HlsCusFctProjectBp var1);
    /*根据projectId查询授信方信息*/
    HlsCusFctProjectBp queryCreditGrantorByProjectId(Long projectId);
}
