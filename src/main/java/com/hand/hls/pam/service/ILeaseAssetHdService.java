package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface ILeaseAssetHdService extends IBaseService<LeaseAssetHd>, ProxySelf<ILeaseAssetHdService>{
    List<LeaseAssetHd> conInceptSubmit(IRequest iRequest, LeaseAssetHd leaseAssetHd) throws ResMessageException, ParameterNullException;

    List<LeaseAssetHd> confirm(IRequest iRequest, LeaseAssetHd leaseAssetHd);

    String generateAuthorityString(IRequest iRequest);
}