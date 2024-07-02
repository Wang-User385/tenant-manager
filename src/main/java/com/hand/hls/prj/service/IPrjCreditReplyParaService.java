package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.PrjCreditReplyPara;

import java.util.List;

public interface IPrjCreditReplyParaService extends IBaseService<PrjCreditReplyPara>, ProxySelf<IPrjCreditReplyParaService>{

    List<PrjCreditReplyPara> manufacturerBpParaQuery(IRequest iRequest, PrjCreditReplyPara bpReplyPara, int pageNum, int pageSize);

}