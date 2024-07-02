package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCreditContractLine;

import java.util.List;

public interface HlsCusCreditContractLineService extends IBaseService<HlsCusCreditContractLine>, ProxySelf<HlsCusCreditContractLineService> {

    /**
     * 选择相同的融资机构带出
     * @param hlsCusCreditContractLine
     * @return
     */
    List<HlsCusCreditContractLine> selectCreditLineCarry(IRequest iRequest, HlsCusCreditContractLine hlsCusCreditContractLine, int page, int pageSize);

    /**
     * 查询
     * @param iRequest
     * @param hlsCusCreditContractLine
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCreditContractLine> selectCreditContractLine(IRequest iRequest,HlsCusCreditContractLine hlsCusCreditContractLine,int page,int pageSize);
}
