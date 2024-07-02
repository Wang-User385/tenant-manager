package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProFeeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusAbsProFeeInfoMapper extends Mapper<HlsCusAbsProFeeInfo> {


    /**
     * 项目费用信息
     * @param projectFeeInfo
     * @return
     */
   List<HlsCusAbsProFeeInfo> selectProjectFeeInfo(HlsCusAbsProFeeInfo projectFeeInfo);

    /**
     * 更新OrganizationId
     * @param productId
     * @param sourceOrganizationId
     * @param targetOrganizationId
     * @return
     */
   int updateFeeOrganizationId(@Param("productId") Long productId, @Param("sourceOrganizationId") Long sourceOrganizationId, @Param("targetOrganizationId") Long targetOrganizationId);


    /**
     * 更新费用金额
     * @param productId
     * @return
     */
    int updateProductFeeAmount(@Param("productId") Long productId);
}