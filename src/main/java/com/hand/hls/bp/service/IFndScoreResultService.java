package com.hand.hls.bp.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.FndScoreResult;

import java.util.List;

public interface IFndScoreResultService extends IBaseService<FndScoreResult>, ProxySelf<IFndScoreResultService>{
    List<FndScoreResult> queryFndScoreResult(Long var1);
}