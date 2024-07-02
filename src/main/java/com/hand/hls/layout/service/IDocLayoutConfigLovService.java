package com.hand.hls.layout.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayoutConfigLov;

import java.util.List;

public interface IDocLayoutConfigLovService extends IBaseService<DocLayoutConfigLov>, ProxySelf<IDocLayoutConfigLovService> {

    List<DocLayoutConfigLov> selectByConfigId(IRequest requestContext, DocLayoutConfigLov layoutConfigLov, int page, int pageSize);
}