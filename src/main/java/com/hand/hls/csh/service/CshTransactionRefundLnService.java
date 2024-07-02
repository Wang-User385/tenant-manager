package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshTransactionRefundLn;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface CshTransactionRefundLnService extends IBaseService<CshTransactionRefundLn>, ProxySelf<CshTransactionRefundLnService> {
    List<CshTransactionRefundLn> detailQuery(CshTransactionRefundLn cshTransactionRefundLn, int page, int pagesize);
    List<CshTransactionRefundLn> saveLn(IRequest iRequest, List<CshTransactionRefundLn> list) throws ResMessageException;
}