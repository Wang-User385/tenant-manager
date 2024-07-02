package com.hand.hls.archive.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.archive.dto.HlsCusArchive;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;
import hls.core.utils.exception.HlsCusException;
public interface HlsCusArchiveBorrowService extends IBaseService<HlsCusArchiveBorrow>, ProxySelf<HlsCusArchiveBorrowService>{

    /**
     * 借阅申请
     * @param iRequest
     * @param hlsCusArchiveBorrow
     */
    void approvalSubmit(IRequest iRequest,HlsCusArchiveBorrow hlsCusArchiveBorrow)throws HlsCusException;

    /**
     * 档案归还
     * @param iRequest
     * @param hlsCusArchiveBorrow
     * @throws HlsCusException
     */
    void archiveBorrowReturn(IRequest iRequest,HlsCusArchiveBorrow hlsCusArchiveBorrow);

    /**
     * 档案借阅类型校验
     * @param iRequest
     * @param hlsCusArchive
     * @return
     */
    Boolean archiveBorrowCheck(IRequest iRequest,HlsCusArchive hlsCusArchive) throws HlsCusException;
}