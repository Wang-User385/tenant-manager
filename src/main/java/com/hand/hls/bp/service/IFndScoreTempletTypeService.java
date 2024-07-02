//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.FndScoreTempletType;
import java.util.List;

public interface IFndScoreTempletTypeService extends IBaseService<FndScoreTempletType>, ProxySelf<IFndScoreTempletTypeService> {
    List<FndScoreTempletType> query(IRequest var1, FndScoreTempletType var2, int var3, int var4);

    int batchDelete(IRequest var1, List<FndScoreTempletType> var2);
}
