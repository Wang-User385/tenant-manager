package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BpShareholderInfoHt;

import java.util.List;

public interface IBpShareholderInfoHtService extends IBaseService<BpShareholderInfoHt>, ProxySelf<IBpShareholderInfoHtService>{

    List<BpShareholderInfoHt> selectAll(IRequest requestContext,BpShareholderInfoHt bpShareholderInfoHt, int page, int pagesize);

}