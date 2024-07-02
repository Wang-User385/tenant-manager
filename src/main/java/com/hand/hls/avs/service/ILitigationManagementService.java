package com.hand.hls.avs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface ILitigationManagementService extends IBaseService<LitigationManagement>, ProxySelf<ILitigationManagementService>{
    List<LitigationManagement> conInceptSubmit(IRequest iRequest, LitigationManagement litigationManagement) throws ResMessageException, ParameterNullException;
}