package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface HlsCusBpMasterService extends IBaseService<HlsCusBpMaster>, ProxySelf<HlsCusBpMasterService> {

    List<HlsCusBpMaster> queryHlsBpMasterCreditInfoAll(HlsCusBpMaster dto, int page, int pagesize);

    List<HlsCusBpMaster> queryCusBpMasterDetailsForPrj(IRequest iRequest, HlsCusBpMaster dto, int page, int pagesize);

    List<HlsCusBpMaster> queryBpLov(HlsCusBpMaster hlsCusBpMaster, int page, int pagesize);

    ResponseData queryPlatformRisk(IRequest iRequest, HlsCusBpMaster dto);

    List<CompositeMap> queryCreatedName(CompositeMap var1, String var2);

    ResponseData queryCreatedId(CompositeMap var1, String var2);

    Map queryBySky(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster, int page, int pageSize);

    String queryBpTypeByRole(Long bpId);

//    void bpWflSubmit(IRequest iRequest, HlsCusBpMaster bpMaster, Long userId, Long bpRelationId);
    void bpWflSubmit(IRequest iRequest, HlsCusBpMaster bpMaster);
    String getAuthorityString(IRequest iRequest);

    List<Long> getCityIdAndProvinceIdByDistrictId(Long districtId);

    void saveOrder(Long conditionId, Long orderId);

    void saveBusinessCondition(Long conditionId, Long bpId);

    List<Long> getConditionId(Long bpId);
}
