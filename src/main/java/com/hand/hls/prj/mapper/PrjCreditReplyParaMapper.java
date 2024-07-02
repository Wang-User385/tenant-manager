package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.PrjCreditReplyPara;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrjCreditReplyParaMapper extends Mapper<PrjCreditReplyPara>{

    List<PrjCreditReplyPara> manufacturerBpParaQuery(PrjCreditReplyPara bpMasterReply);

    /**
     * 根据项目ID与参数类型获取授信默认值
     * @param bpMasterReply
     * @return
     */
    Double selectDefaultValueByReplyIdAndReplyPara(PrjCreditReplyPara bpMasterReply);

    /**
     * 批复比例
     */
    PrjCreditReplyPara queryPara(@Param("contractId") Long contractId, @Param("replyPara") String replyPara);

    /**
     * 根据厂商id查询最新的批复参数
     */
    List<PrjCreditReplyPara> queryLatestReplyParasByManufacturerId(@Param("manufacturerId") Long manufacturerId);

}
