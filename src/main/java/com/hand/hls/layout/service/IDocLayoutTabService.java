package com.hand.hls.layout.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayoutTab;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface IDocLayoutTabService extends IBaseService<DocLayoutTab>, ProxySelf<IDocLayoutTabService> {

    List<DocLayoutTab> selectDocLayoutTab(Map<String, Object> paramMap, int page, int pageSize);

    /**
     * 批量删除布局组件
     *
     * @param dto
     */
    void deleteDocLayoutTab(List<DocLayoutTab> dto);

    void loadDocLayoutTab(IRequest requestContext, DocLayoutTab layoutTab);

    ResponseData selectLayoutTabForTabConfig(CompositeMap map, String whereStr);

    ResponseData layoutTabCopy(IRequest requestContext, String fromLayoutCode, String toLayoutCode, String fromTabCode, String toTableCode, String toTabDesc, String TabOnly, String parentTabCode);
}