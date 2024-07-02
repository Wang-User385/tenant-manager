package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsPropertyContract;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HlsCusAbsPropertyContractService extends IBaseService<HlsCusAbsPropertyContract>, ProxySelf<HlsCusAbsPropertyContractService> {

    /**
     * 资产合同表格查询
     */
    List<HlsCusAbsPropertyContract> query(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize);

    /**
     * 资产管理首页查询
     */
    List<HlsCusAbsPropertyContract> queryHome(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize);

    /**
     * 产品明细-资产明细-选择租金支付表
     */
    List<Map<String, Object>> queryContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize);

    /**
     * 资产变更-资产包替换
     */
    List<Map<String, Object>> queryChangeContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize);

    /**
     * 资产变更-资产包替换
     */
    List<HlsCusAbsPropertyContract> queryAssetChangeContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize);

    /**
     * 资产变更-提前结清-选择租金支付表
     */
    List<Map<String, Object>> queryFinishContract(IRequest request, HlsCusAbsPropertyContract hlsCusAbsPropertyContract, int page, int pageSize);

    /**
     * 产品下选择租赁保理合同金额计算
     * @param maps 待处理的合同集合
     * @param baseDate 封包日
     */
    List<Map<String, Object>> calcContractAmount(List<Map<String, Object>> maps, LocalDate baseDate, IRequest request);

    /**
     * 删除资产合同
     */
    void delete(IRequest request, List<HlsCusAbsPropertyContract> list);

}

