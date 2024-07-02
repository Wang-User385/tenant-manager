package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.HlsCusLonContractRepaymentMerge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusLonContractRepaymentMergeMapper extends Mapper<HlsCusLonContractRepaymentMerge>{
    /**
     * 查询合并后的计提现金流
     * 如果手续费和担保费的分摊方式均为一次性分摊则收益为利息的收益，如果手续费和担保费的分摊方式中包含实际利率法则用代码计算收益
     * @param withdrawId
     * @return
     */
    List<HlsCusLonContractRepaymentMerge> queryMergeRepayment(@Param("withdrawId") Long withdrawId);

    /**
     * 根据现金流类型查询分摊方式
     * @param withdrawId
     * @param cfItem
     * @return
     */
    String queryAmortizationMethod(@Param("withdrawId") Long withdrawId,@Param("cfItem") Long cfItem);

    /**
     * 从数据查询表
     * @return
     */
    List<HlsCusLonContractRepaymentMerge> queryMergeRepaymentLast(HlsCusLonContractRepaymentMerge lsCusLonContractRepaymentMerge);

    /**
     * 查询未确认收入的起始现金流期数
     * @param withdrawId
     * @return
     */
    Long queryChangeTermRepayment(@Param("withdrawId") Long withdrawId);
}