package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractArchive;
import com.hand.hls.utils.ResMessageException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IConContractArchiveService extends IBaseService<HlsCusConContractArchive>, ProxySelf<IConContractArchiveService> {

    void saveContractArchive(IRequest iRequest, HlsCusConContract contract);

    ResponseData uploadFile(HttpServletRequest request, IRequest iRequest, Long contractId, String fileCategory);

    Long uploadToNodes(IRequest iRequest, Long conractId, String fileCategory);

    /**
     * 归档确认
     */
    void archiveConfirm(IRequest iRequest, List<HlsCusConContractArchive> dto);

}
