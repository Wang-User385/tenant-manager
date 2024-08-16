package com.hand.hls.partner.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.partner.dto.AlipayOrderDTO;
import org.apache.ibatis.annotations.Param;

public interface AlipayOrderMapper extends Mapper<AlipayOrderDTO>{

    AlipayOrderDTO selectOrderForWithhold(@Param("orderId") Long orderId);

    String getMeaningSysCode(@Param("code") String code,@Param("codeValue") String codeValue);
}