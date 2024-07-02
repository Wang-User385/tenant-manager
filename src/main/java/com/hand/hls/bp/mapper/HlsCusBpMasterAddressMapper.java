//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.prj.dto.HlsBpMasterAddress;
import com.hand.hls.prj.mapper.HlsBpMasterAddressMapper;

import java.util.List;
import java.util.Map;

public interface HlsCusBpMasterAddressMapper extends HlsBpMasterAddressMapper<HlsCusBpMasterAddress> {
    List<HlsCusBpMasterAddress> queryAll(HlsCusBpMasterAddress var1);

    List<HlsBpMasterAddress> queryDetails(Map var1);
}
