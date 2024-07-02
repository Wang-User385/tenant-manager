package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.ReplyProductPara;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReplyProductParaMapper extends Mapper<ReplyProductPara> {
    /**
     * 查询批复产品参数列表
     *
     * @param replyProductId
     * @return list
     */
    List<ReplyProductPara> selectReplyProductParaList(@Param("replyProductId") Long replyProductId);

    List<ReplyProductPara> manufacturerQueryProductParaInfo(ReplyProductPara replyProductPara);

    /**
     * 查询批复产品参数
     */
    List<ReplyProductPara> query(ReplyProductPara replyProductPara);
}
