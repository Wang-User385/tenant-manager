package com.hand.hls.layout.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.function.dto.Function;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface IDocLayoutButtonFunctionService extends IBaseService<Function>, ProxySelf<IDocLayoutButtonFunctionService> {
    List<Function> selectForFunctionInfo(IRequest requestContext, Function param, int page, int pageSize);
}
