package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusPrjNoticeVeto;
import com.hand.hls.prj.service.IHlsCusPrjNoticeVetoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjNoticeVetoServiceImpl extends BaseServiceImpl<HlsCusPrjNoticeVeto> implements IHlsCusPrjNoticeVetoService{

}