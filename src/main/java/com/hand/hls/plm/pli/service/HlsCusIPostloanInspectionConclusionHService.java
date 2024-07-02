package com.hand.hls.plm.pli.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspectionConclusionH;

import java.util.List;

/**
 * @Description:贷后检查历史结论service
 * @Author: Wty
 * @Date: Created om 16:28 2018/5/24
 */
public interface HlsCusIPostloanInspectionConclusionHService extends IBaseService<HlsCusPostloanInspectionConclusionH>, ProxySelf<HlsCusIPostloanInspectionConclusionHService> {
    List<HlsCusPostloanInspectionConclusionH> selectConclusionH(IRequest iRequest, HlsCusPostloanInspectionConclusionH postloanInspectionConclusionH, int page, int pageSize);
}