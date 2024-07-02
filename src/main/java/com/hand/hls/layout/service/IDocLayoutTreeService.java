package com.hand.hls.layout.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayoutTree;
import uncertain.composite.CompositeMap;

import java.util.Map;

public interface IDocLayoutTreeService extends IBaseService<DocLayoutTree>, ProxySelf<IDocLayoutTreeService> {

    Map queryFlag(DocLayoutTree docLayoutTree);

    ResponseData selectDocLayoutTree(CompositeMap map, String whereStr);

}