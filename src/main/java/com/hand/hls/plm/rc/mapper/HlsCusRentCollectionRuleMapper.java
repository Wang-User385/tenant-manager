package com.hand.hls.plm.rc.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;

import java.util.List;

public interface HlsCusRentCollectionRuleMapper extends Mapper<HlsCusRentCollectionRule> {


    /**
     * 租金催收规则查询
     * @param rentCollectionRule
     * @return
     */
    List<HlsCusRentCollectionRule> selectRentCollectionRuleData(HlsCusRentCollectionRule rentCollectionRule);


    /**
     * 租金催收规则选择合同
     * @param rentCollectionRule
     * @return
     */
    List<HlsCusRentCollectionRule> selectRentCollectionContract(HlsCusRentCollectionRule rentCollectionRule);


    /**
     * 当前合同催收记录个数
     * @param rentCollectionRule
     * @return
     */
    int selectContractRentCount(HlsCusRentCollectionRule rentCollectionRule);


    /**
     * 查询逾期合同且维护了催收规则的
     * @return
     */
    List<HlsCusRentCollectionRule> selectOverDateContract();


    /**
     * 更新最后日期
     * @param rentCollectionRule
     * @return
     */
    int updateLastCollectionDate(HlsCusRentCollectionRule rentCollectionRule);


    /**
     * 查询逾期超过30天的合同
     * @return
     */
    List<HlsCusRentCollectionRule> selectOverMonthContract();


    /**
     * 查询逾期的合同
     * @return
     */
    List<HlsCusRentCollectionRule> selectOverTimesContract();

}