package com.hand.hls.layout.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.layout.dto.DocLayout;
import com.hand.hls.layout.dto.LovDocLayoutDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import uncertain.composite.CompositeMap;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface IDocLayoutService extends IBaseService<DocLayout>, ProxySelf<IDocLayoutService> {

    List<DocLayout> selectDocLayout(Map<String, Object> paramMap, int page, int pageSize);

//    void layoutCopy(LeafRequestData data, IRequest requestContext);

    ResponseData layoutCopy(IRequest requestContext, String fromLayoutCode, String toLayoutCode, String toDesc);

    ResponseData selectDocLayoutSequence(CompositeMap map, String whereStr);

    List<LovDocLayoutDto> selectForLov(Map param, int pageNum, int pageSize);

    ResponseEntity<Resource> exportLayout(DocLayout layout);

    void importLayout(String content);

    ResponseEntity<Resource> exportLayoutButton(@NotNull final String functionCode);

    void importLayoutButton(@NotNull final String content);
}