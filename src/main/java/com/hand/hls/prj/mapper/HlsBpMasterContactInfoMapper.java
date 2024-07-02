//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.prj.dto.HlsBpMasterContactInfo;

import java.util.List;
import java.util.Map;

public interface HlsBpMasterContactInfoMapper<T extends HlsCusBpMasterContactInfo> extends Mapper<HlsCusBpMasterContactInfo> {
    List<HlsBpMasterContactInfo> query();

    List<Map> getPositionCombo();

    List<Map> getIdTypeCombo();
}
