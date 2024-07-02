package com.hand.hls.eft.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusFundTransferMapper extends Mapper<HlsCusFundTransfer> {


    /**
     * 已办事项
     * @param fundTransfer
     * @return
     */
    List<HlsCusFundTransfer> selectFundTransferData(HlsCusFundTransfer fundTransfer);


    /**
     * 变更
     * @param fundTransfer
     * @return
     */
    List<HlsCusFundTransfer> selectFundTransferChangeData(HlsCusFundTransfer fundTransfer);


    /**
     * 查询新建或审批中的CHANGE
     * @param refTransferId
     * @return
     */
    HlsCusFundTransfer selectFundTransferNewChange(@Param("refTransferId") Long refTransferId);

}