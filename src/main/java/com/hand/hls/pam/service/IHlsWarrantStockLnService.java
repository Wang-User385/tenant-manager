package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.pam.dto.HlsWarrantStockLn;

import java.util.List;

public interface IHlsWarrantStockLnService extends IBaseService<HlsWarrantStockLn>, ProxySelf<IHlsWarrantStockLnService>{

    List<HlsWarrantStockLn> warrantStockCheck(IRequest iRequest , List<HlsWarrantStockLn> hlsWarrantStockLnList) throws HlsCusException;
}