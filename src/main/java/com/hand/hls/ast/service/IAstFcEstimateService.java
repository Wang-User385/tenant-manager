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
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;
import uncertain.composite.CompositeMap;

import java.util.List;

public interface IAstFcEstimateService extends IBaseService<AstFcEstimate>, ProxySelf<IAstFcEstimateService> {
    ResponseData queryByRequestData(IRequest var1, LeafRequestData var2, int var3, int var4);

    ResponseData modelQuery(CompositeMap var1, String var2);

    void fcEstimateApprove(IRequest var1, Long var2);

    AstFcEstimate astFcEstimateCreate(IRequest requestCtx, AstFcEstimate dto) throws HlsCusException;

    List<AstFcEstimate> queryAstFcEstimate(IRequest var1, AstFcEstimate var2, int var3, int var4);

    List<AstFcEstimate> conInceptSubmit(IRequest iRequest, AstFcEstimate astFcEstimate) throws ResMessageException, ParameterNullException;

    List<AstFcEstimate> conInceptSubmit2(IRequest iRequest, AstFcEstimate astFcEstimate) throws ResMessageException, ParameterNullException;
}
