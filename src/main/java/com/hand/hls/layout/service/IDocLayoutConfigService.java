package com.hand.hls.layout.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayoutConfig;
import leaf.bean.LeafRequestData;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface IDocLayoutConfigService extends IBaseService<DocLayoutConfig>, ProxySelf<IDocLayoutConfigService> {

    List<DocLayoutConfig> selectDocLayoutConfig(Map<String, Object> map, int page, int pageSize);

    ResponseData selectDocLayoutConfigInit(CompositeMap map, String whereStr);

    List<DocLayoutConfig> selectDocLayoutConfigWithoutPrecision(DocLayoutConfig config);

    boolean configReload(DocLayoutConfig config);

    ResponseData resolveConfigField(LeafRequestData data);

    ResponseData updateConfig(LeafRequestData data);
}