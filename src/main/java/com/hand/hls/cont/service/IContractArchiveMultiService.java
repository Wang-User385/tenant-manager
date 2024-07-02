package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.ContractArchiveMulti;
import java.util.List;

public interface IContractArchiveMultiService extends IBaseService<ContractArchiveMulti>, ProxySelf<IContractArchiveMultiService>{

    List<ContractArchiveMulti> selectArchiveAttachment(Long contractId, String archiveType, IRequest iRequest, int pagenum, int pagesize);

    void saveArchiveAttachment(List<ContractArchiveMulti> list, IRequest iRequest);

    List<ContractArchiveMulti> selectAllArchiveAttachment(Long contractId, String status, IRequest iRequest, int pagenum, int pagesize);

    List<Long> selectAttachmentId(List<ContractArchiveMulti> list);

}