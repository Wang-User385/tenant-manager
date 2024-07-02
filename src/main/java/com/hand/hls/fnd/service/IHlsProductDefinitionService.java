package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsProductDefinition;

import java.util.List;

public interface IHlsProductDefinitionService extends IBaseService<HlsProductDefinition>, ProxySelf<IHlsProductDefinitionService> {

    /**
     * 查询产品列表
     *
     * @param request
     * @param hlsProductDefinition
     * @param page
     * @param pagesize
     * @return 产品列表
     */
    List<HlsProductDefinition> selectHlsProductDefinitionList(IRequest request, HlsProductDefinition hlsProductDefinition, int page, int pagesize);

    /**
     * 批量保存头行
     *
     * @param request
     * @param hlsProductDefinitionList
     * @return
     */
    List<HlsProductDefinition> batchUpdateProductDefinition(IRequest request, @StdWho List<HlsProductDefinition> hlsProductDefinitionList) throws HlsCusException;

    /**
     * 新增产品头行
     *
     * @param hlsProductDefinition
     * @return
     */
    HlsProductDefinition insertHlsProductDefinition(IRequest request, HlsProductDefinition hlsProductDefinition) throws HlsCusException;

    /**
     * 更新产品头行
     *
     * @param hlsProductDefinition
     * @return
     */
    HlsProductDefinition updateHlsProductDefinition(IRequest request, HlsProductDefinition hlsProductDefinition) throws HlsCusException;

    /**
     * 提交产品申请
     *
     * @param request
     * @param hlsProductDefinition
     * @return
     */
    HlsProductDefinition submitHlsProductDefinition(IRequest request, @StdWho HlsProductDefinition hlsProductDefinition) throws HlsCusException;

    /**
     * 变更产品申请
     *
     * @param request
     * @param hlsProductDefinition
     * @return
     */
    HlsProductDefinition changeHlsProductDefinition(IRequest request, @StdWho HlsProductDefinition hlsProductDefinition) throws HlsCusException;
}