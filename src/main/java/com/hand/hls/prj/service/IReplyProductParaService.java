package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.ReplyProductPara;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IReplyProductParaService extends IBaseService<ReplyProductPara>, ProxySelf<IReplyProductParaService>{
    /**
     * 查询批复产品参数列表
     *
     * @param request
     * @param replyProductId
     * @param page
     * @param pagesize
     * @return 批复产品参数列表
     */
    List<ReplyProductPara> selectReplyProductParaList(IRequest request, @Param("replyProductId") Long replyProductId, int page, int pagesize);

    List<ReplyProductPara> manufacturerQueryProductParaInfo(IRequest iRequest, ReplyProductPara replyProductPara, int pageNum, int pageSize);

    /**
     * 查询批复产品参数
     */
    List<ReplyProductPara> query(IRequest iRequest, ReplyProductPara replyProductPara, int page, int pageSize);
}
