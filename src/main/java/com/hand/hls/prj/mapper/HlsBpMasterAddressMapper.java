//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.prj.dto.HlsBpMasterAddress;

import java.util.List;
import java.util.Map;

public interface HlsBpMasterAddressMapper<T extends HlsCusBpMasterAddress> extends Mapper<HlsCusBpMasterAddress> {

    List<HlsBpMasterAddress> query();

    List<HlsBpMasterAddress> queryDetails();

    List<Map> getAddressKindCombo();
}
