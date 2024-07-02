package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface HlsCusContractAttachmentService extends IBaseService<HlsCusContractAttachment>, ProxySelf<HlsCusContractAttachmentService> {
    List<HlsCusContractAttachment> queryContractAttachment(IRequest request, HlsCusContractAttachment hlsCusContractAttachment);

    List<HlsCusContractAttachment> selectCshDocByContractIdAndCategory(IRequest request, HlsCusContractAttachment hlsCusContractAttachment);

    List<HlsCusContractAttachment> contractAttachmentLendDocumentListQuery(IRequest request, HlsCusContractAttachment hlsCusContractAttachment);

    public void createContractDocumentFile(IRequest iRequest, HlsCusConContract hlsCusConContract) throws Exception;



    List<HlsCusContractAttachment>  selectContractFineAttachment(IRequest request, HlsCusContractAttachment hlsCusContractAttachment, int page, int pageSize);

    List<HlsCusContractAttachment> selectContractEndFileByContractId(IRequest request, HlsCusContractAttachment hlsCusContractAttachment);

}