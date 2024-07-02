package com.hand.hls.ty.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.ty.dto.JcTianyanchaInterfaceInfo;
import com.hand.hls.ty.service.IJcTianyanchaInterfaceInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcTianyanchaInterfaceInfoServiceImpl extends BaseServiceImpl<JcTianyanchaInterfaceInfo> implements IJcTianyanchaInterfaceInfoService{

}