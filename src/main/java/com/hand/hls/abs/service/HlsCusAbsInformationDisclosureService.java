package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsInformationDisclosure;
import com.hand.hls.exception.HlsCusException;

public interface HlsCusAbsInformationDisclosureService extends IBaseService<HlsCusAbsInformationDisclosure>, ProxySelf<HlsCusAbsInformationDisclosureService>{

    /**
     * 创建单据
     * @param requestContext
     * @param dto
     * @return
     */
    HlsCusAbsInformationDisclosure ctAbsInformationDisclosureCreate(IRequest requestContext,HlsCusAbsInformationDisclosure dto);

    /**
     * 信息披露提交审批
     * @param iRequest
     * @param dto
     */
    void submitInformationDisclosureWfl(IRequest iRequest,HlsCusAbsInformationDisclosure dto) throws HlsCusException;
}