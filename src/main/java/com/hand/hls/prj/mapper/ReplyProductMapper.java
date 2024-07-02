package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.ReplyProduct;

import java.util.List;

public interface ReplyProductMapper extends Mapper<ReplyProduct>{
    List<ReplyProduct> manufacturerQueryProductInfo(ReplyProduct replyProduct);
    List<ReplyProduct> manufacturerQueryProductInfo1(ReplyProduct replyProduct);


}