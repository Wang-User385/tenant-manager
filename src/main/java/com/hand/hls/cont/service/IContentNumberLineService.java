package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.ContentNumberLine;
import com.hand.hls.fct.dto.FctProjectAttachment;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface IContentNumberLineService extends IBaseService<ContentNumberLine>, ProxySelf<IContentNumberLineService>{

    List<ContentNumberLine> createDocumentNumber(IRequest iRequest,List<ContentNumberLine> lines);

    List<HlsCusFctProjectAttachment> createDocumentNumberForCon(IRequest iRequest,List<HlsCusFctProjectAttachment> lines) throws ResMessageException;

}