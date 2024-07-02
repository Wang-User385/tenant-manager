package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpSpouse;

import java.util.List;

public interface HlsBpSpouseService extends IBaseService<HlsBpSpouse>, ProxySelf<HlsBpSpouseService>{

    List<HlsBpSpouse> selectAll(IRequest iRequest, HlsBpSpouse hlsBpSpouse, int page, int pageSzie);
}