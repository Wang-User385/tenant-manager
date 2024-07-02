//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import java.util.List;

public interface HlsBpMasterAddressService extends IBaseService<HlsCusBpMasterAddress>, ProxySelf<HlsBpMasterAddressService> {
    List<HlsCusBpMasterAddress> selectAll(IRequest var1, HlsCusBpMasterAddress var2, int var3, int var4);
}
