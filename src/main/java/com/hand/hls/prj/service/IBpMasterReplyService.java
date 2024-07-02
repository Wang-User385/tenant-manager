package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.prj.dto.BpMasterReply;

import java.util.List;

public interface IBpMasterReplyService extends IBaseService<BpMasterReply>, ProxySelf<IBpMasterReplyService>{

    List<BpMasterReply> selectReplyInfo(IRequest iRequest, String manufacturerId,String dealerId);

    List<BpMasterReply>  manufacturerQuery(IRequest iRequest, BpMasterReply bpMasterReply, int pageNum, int pageSize);

    /**
     * 根据厂商ID 以及 批复参数名 获取对应的参数最大值
     * @param bpId
     * @param replyPara
     * @return
     */
    String queryReplyParaValueToByBpIdAndReplyPara(Long bpId, String replyPara);

}