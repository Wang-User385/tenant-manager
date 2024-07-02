package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.BpMasterReply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BpMasterReplyMapper extends Mapper<BpMasterReply> {

    List<BpMasterReply> selectReplyInfo(BpMasterReply bpMasterReply);

    List<BpMasterReply> manufacturerQuery(BpMasterReply bpMasterReply);

    List<BpMasterReply> selectReplyParaInfo(BpMasterReply bpMasterReply);

    BpMasterReply queryMaxCreationDateReplyInfo(Long creditLineId);

    BpMasterReply queryLatestReplyInfo(Long creditLineId);

    List<BpMasterReply> queryNotLatestReplyInfo(BpMasterReply bpMasterReply);

    /**
     *  通过报价查询批复信息
     */
    BpMasterReply queryReplyInfoByQuotationId(Long quotationId);

    /**
     * 根据厂商ID 批复参数代码 查找批复相关参数
     */
    List<BpMasterReply> queryReplyParaByBpIdAndReplyPara(@Param("bpId")Long bpId);

}