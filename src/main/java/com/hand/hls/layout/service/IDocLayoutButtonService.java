package com.hand.hls.layout.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayoutButton;
import com.hand.hls.layout.dto.LovDocLayoutButtonDto;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface IDocLayoutButtonService extends IBaseService<DocLayoutButton>, ProxySelf<IDocLayoutButtonService> {

    List<DocLayoutButton> load(String functionCode);

    List<LovDocLayoutButtonDto> queryForLov(Map param, int pageNum, int pageSize);

    ResponseData queryLovByfunctionCode(CompositeMap map, String whereStr);

    void reload(IRequest requestContext, List<DocLayoutButton> listToModify);
}
