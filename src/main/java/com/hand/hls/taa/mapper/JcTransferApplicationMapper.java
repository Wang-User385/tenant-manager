package com.hand.hls.taa.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.taa.dto.JcTransferApplication;
import org.opensaml.xmlsec.signature.J;

import java.util.List;

public interface JcTransferApplicationMapper extends Mapper<JcTransferApplication>{
    List<JcTransferApplication> queryJcTransferApplication(JcTransferApplication jcTransferApplication);
}