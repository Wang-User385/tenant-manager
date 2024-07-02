package com.hand.hls.csh.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshConContract;

import java.util.List;
import java.util.Map;


public interface ICshConContractService extends IBaseService<HlsCusCshConContract>, ProxySelf<ICshConContractService> {

    List<HlsCusCshConContract> contractHomeSecondQuery(Map<String, Object> map, int page, int pagesize);

    List<HlsCusCshConContract> contractHomeThirdQuery(Map<String, Object> map);

    List<HlsCusCshConContract> csh001contractHomeSecondQuery(Map<String, Object> map , int page, int pagesize ,String sortName,String sortOrder);

}
