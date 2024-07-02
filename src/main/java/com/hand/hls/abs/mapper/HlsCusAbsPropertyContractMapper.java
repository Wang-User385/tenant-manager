package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsPropertyContract;

import java.util.List;
import java.util.Map;

public interface HlsCusAbsPropertyContractMapper extends Mapper<HlsCusAbsPropertyContract> {
    /**
     * 资产合同表格查询
     */
    List<HlsCusAbsPropertyContract> query(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

    /**
     * 资产管理首页查询
     */
    List<HlsCusAbsPropertyContract> queryHome(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

    /**
     * 产品明细-资产明细-选择租金支付表
     */
    List<HlsCusAbsPropertyContract> queryContract(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

    /**
     * 资产变更-资产包替换查询合同
     */
    List<HlsCusAbsPropertyContract> queryChangeContract(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

    /**
     * 产品明细-资产明细-选择租金支付表
     */
    List<Map<String, Object>> queryContractForProductContract(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

    /**
     * 资产变更-资产包替换
     */
    List<HlsCusAbsPropertyContract> queryAssetChangeContract(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

    /**
     * 资产变更-提前结清-选择租金支付表
     */
    List<HlsCusAbsPropertyContract> queryFinishContract(HlsCusAbsPropertyContract hlsCusAbsPropertyContract);

}

