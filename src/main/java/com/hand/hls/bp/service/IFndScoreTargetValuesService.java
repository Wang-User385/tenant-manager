//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.FndScoreTargetValues;

import java.util.List;

public interface IFndScoreTargetValuesService extends IBaseService<FndScoreTargetValues>, ProxySelf<IFndScoreTargetValuesService> {
    List<FndScoreTargetValues> findScoreTargetValueList(IRequest requestContext, FndScoreTargetValues fndScoreTargetValues, int pagenum, int pageSize);
}
