//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.dto.AstFcEstimateResult;
import leaf.bean.LeafRequestData;
import uncertain.composite.CompositeMap;

import java.util.List;

public interface IAstFcEstimateResultService extends IBaseService<AstFcEstimateResult>, ProxySelf<IAstFcEstimateResultService> {
    ResponseData queryByRequestData(IRequest var1, LeafRequestData var2, int var3, int var4);

    ResponseData batchUpdate(IRequest var1, LeafRequestData var2);

    ResponseData execute(IRequest var1, LeafRequestData var2);

    ResponseData deleteByRequestData(IRequest var1, CompositeMap var2);

    ResponseData updateByRequestData(IRequest var1, CompositeMap var2);

    ResponseData astFcEstimateResultInit(Long var1, IRequest var2);

    ResponseData astFcEstimateResultInit(Long var1, Long var2, Long var3, String var4);

    AstFcEstimate assessment(IRequest requestContext, AstFcEstimate astFcEstimate, AstFcEstimate resources);
}
