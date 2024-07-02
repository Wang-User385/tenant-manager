package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface IAssetsDisposalService extends IBaseService<AssetsDisposal>, ProxySelf<IAssetsDisposalService>{

    AssetsDisposal assetsDisposalCreate(IRequest requestCtx, AssetsDisposal dto) throws HlsCusException;

    List<AssetsDisposal> conInceptSubmit(IRequest iRequest, AssetsDisposal assetsDisposal) throws ResMessageException, ParameterNullException;
}