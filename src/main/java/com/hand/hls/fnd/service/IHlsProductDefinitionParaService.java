package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;

import java.util.List;

public interface IHlsProductDefinitionParaService extends IBaseService<HlsProductDefinitionPara>, ProxySelf<IHlsProductDefinitionParaService> {
    /**
     * 查询产品参数列表
     *
     * @param request
     * @param hlsProductDefinition
     * @param page
     * @param pagesize
     * @return 产品参数列表
     */
    List<HlsProductDefinitionPara> selectHlsProductDefinitionParaList(IRequest request, HlsProductDefinition hlsProductDefinition, int page, int pagesize);
}