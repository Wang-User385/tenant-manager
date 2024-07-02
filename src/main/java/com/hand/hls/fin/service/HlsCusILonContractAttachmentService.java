package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractAttachment;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HlsCusILonContractAttachmentService extends IBaseService<HlsCusLonContractAttachment>, ProxySelf<HlsCusILonContractAttachmentService> {

    List<HlsCusLonContractAttachment> selectLonConAttachment(IRequest request, HlsCusLonContractAttachment lonContractAttachment, int page, int pageSize);

    List<HlsCusLonContractAttachment> lonContractAttachmentDetailQuery(IRequest request, HlsCusLonContractAttachment lonContractAttachment, int page, int pageSize);

    void updateLonContractAttachmentStatus(IRequest iRequest, HlsCusLonContract hlsCusLonContract);

    HlsCusLonContractAttachment saveLonContractAttachment(IRequest request, HlsCusLonContractAttachment lonContractAttachment);

    void lonContractAttachmentDownload(IRequest request, HlsCusLonContractAttachment dto);

    void selectOrderNumberMax(IRequest request, List<HlsCusLonContractAttachment> dto);


    void downloadContractFile(Long contractId, IRequest iRequest, HttpServletRequest request, HttpServletResponse response);

    int selectAttachmentCodeNullCount(Long contractId);
}