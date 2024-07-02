//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsScoreTarget;
import java.util.List;

public interface IHlsScoreTargetService extends IBaseService<HlsScoreTarget>, ProxySelf<IHlsScoreTargetService> {
    List<HlsScoreTarget> query(IRequest var1, HlsScoreTarget var2, int var3, int var4);

    void deleteHl(List<HlsScoreTarget> var1);
}
