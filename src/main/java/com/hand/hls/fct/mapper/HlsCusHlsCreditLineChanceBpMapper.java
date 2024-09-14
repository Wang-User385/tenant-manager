package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditChancePledge;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;

import java.util.List;

public interface HlsCusHlsCreditLineChanceBpMapper extends Mapper<HlsCusHlsCreditLineChanceBp> {

    List<HlsCusHlsCreditLineChanceBp> selectByForeignKey(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);
    List<HlsCusHlsCreditLineChanceBp> selectChanceBpByChanceId(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);
    List<HlsCusHlsCreditLineChanceBp>  selectProjectBpByProjectId(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceBp> selectBpByMarket(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceBp> selectDistinctByBp(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceBp> selectTenantBpByChance(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceBp> selectBpInfoByChance(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);


    List<HlsCusHlsCreditLineChanceBp> selectTenantSecBpByChance(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceBp> selectGuarantorIdBpByChance(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    List<HlsCusHlsCreditLineChanceBp> selectGuarantorNpIdBpByChance(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    //用来更新授信用户的已用金额
    void updateUsedAmountByBpId(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);
    /**
     * 保理客户信息
     */
    List<HlsCusHlsCreditLineChanceBp> findCustomer(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);
    List<HlsCusHlsCreditLineChanceBp> findCreditCustomer(HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    HlsCusHlsCreditLineChanceBp selectByBpId(HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp);
}