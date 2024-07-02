package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.ReplyProduct;

import java.util.List;

public interface IReplyProductService extends IBaseService<ReplyProduct>, ProxySelf<IReplyProductService>{
    List<ReplyProduct> manufacturerQueryProductInfo(IRequest iRequest, ReplyProduct replyProduct, int pageNum, int pageSize);
    List<ReplyProduct> manufacturerQueryProductInfo1(IRequest iRequest, ReplyProduct replyProduct, int pageNum, int pageSize);
}