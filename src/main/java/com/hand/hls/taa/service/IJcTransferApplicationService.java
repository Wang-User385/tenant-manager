package com.hand.hls.taa.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.taa.dto.JcTransferApplication;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface IJcTransferApplicationService extends IBaseService<JcTransferApplication>, ProxySelf<IJcTransferApplicationService>{
    List<JcTransferApplication> conInceptSubmit(IRequest iRequest, JcTransferApplication jcTransferApplication) throws ResMessageException, ParameterNullException;
}