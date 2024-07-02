package com.hand.hls.sys.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.sys.dto.HlsCusSysUserAuthorityTrx;

import java.util.List;

public interface HlsCusSysUserAuthorityTrxService extends IBaseService<HlsCusSysUserAuthorityTrx>, ProxySelf<HlsCusSysUserAuthorityTrxService> {

    List<HlsCusSysUserAuthorityTrx> queryAuthorUser(IRequest iRequest, HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx);

    void insertUserAuthor(IRequest iRequest, String documentCategory, Long documentId, String unitType);

    void insertChiefUserAuthor(IRequest iRequest, String documentCategory, Long documentId);

    List<HlsCusSysUserAuthorityTrx> queryAuthorityList(IRequest iRequest, HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx, int page, int pageSize);

    List<HlsCusSysUserAuthorityTrx> queryAuthorityDocument(IRequest iRequest, HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx, int page, int pageSize);

    void updatePrjProjectCreatedBy(HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx);

    void updateFctProjectCreatedBy(HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx);
    void updateLonContractCreatedBy(HlsCusSysUserAuthorityTrx hlsCusSysUserAuthorityTrx);

}