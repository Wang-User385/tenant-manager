package com.hand.hls.ty.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ty.dto.JcTianyanchaInterfaceInfo;
import org.opensaml.xmlsec.signature.J;

import java.util.List;

public interface JcTianyanchaInterfaceInfoMapper extends Mapper<JcTianyanchaInterfaceInfo>{

    List<JcTianyanchaInterfaceInfo> queryJcTianyanchaInterfaceInfoList(JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo);


}