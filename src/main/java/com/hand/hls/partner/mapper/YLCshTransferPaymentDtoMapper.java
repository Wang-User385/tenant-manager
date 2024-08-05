package com.hand.hls.partner.mapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface YLCshTransferPaymentDtoMapper extends Mapper<YLCshTransferPaymentDto>{
    List<YLCshTransferPaymentDto> findAll(YLCshTransferPaymentDto ylCshTransferPaymentDto);

    YLCshTransferPaymentDto findByPaymentId(YLCshTransferPaymentDto ylCshTransferPaymentDto);

}