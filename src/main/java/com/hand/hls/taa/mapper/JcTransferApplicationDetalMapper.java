package com.hand.hls.taa.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.taa.dto.JcTransferApplicationDetal;

import java.util.List;

public interface JcTransferApplicationDetalMapper extends Mapper<JcTransferApplicationDetal>{
    List<JcTransferApplicationDetal> queryJcTransferApplicationDetal(JcTransferApplicationDetal jcTransferApplicationDetal);
}