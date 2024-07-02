package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductRepayment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusAbsProductRepaymentMapper extends Mapper<HlsCusAbsProductRepayment> {


    /**
     * 界面查询
     * @param hlsCusAbsProductRepayment
     * @return
     */
    List<HlsCusAbsProductRepayment>  selectRepaymentPlanData(HlsCusAbsProductRepayment hlsCusAbsProductRepayment);


    List<HlsCusAbsProductRepayment> selectRepaymentPlanOrderByRepaymentDate(HlsCusAbsProductRepayment hlsCusAbsProductRepayment);



    Double selectInterestSum(HlsCusAbsProductRepayment hlsCusAbsProductRepayment);


    Double selectRepaymentPlanAmountSum(@Param("productId") Long productId, @Param("cfItem") String cfItem);


    /**
     * 未核销完的数据
     * @param productId
     * @return
     */
    int selectNotFullRepaymentCount(@Param("productId") Long productId);
}