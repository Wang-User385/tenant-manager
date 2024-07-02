package com.hand.hls.fnd.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsFinStatementLn;

import java.util.List;
import java.util.Map;

/**
 * Created by wty on 2018/7/4.
 */
public interface HlsFinStatementLnService extends IBaseService<HlsFinStatementLn>, ProxySelf<HlsFinStatementLnService> {
    public List<HlsFinStatementLn> lnQuery(HlsFinStatementLn hlsFinStatementLn, int page, int pagesize);


}
