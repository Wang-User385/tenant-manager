package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.FndBaseRate;
import com.hand.hls.fnd.dto.FndBaseRateSet;

import java.util.List;


public interface FndBaseRateSetService extends IBaseService<FndBaseRateSet>, ProxySelf<FndBaseRateSetService> {
    public void batchChildUpdate(IRequest iRequest, FndBaseRateSet fndBaseRateSet, List<FndBaseRate> fndBaseRate, List<FndBaseRateSet> datas);

    List<FndBaseRateSet> queryAll();

    List<FndBaseRateSet> getCount();

    public List<FndBaseRateSet> selectByQuery(FndBaseRateSet fndBaseRateSet, int page, int pagesize);

    //融资合同-根据基础利率类型和融资期限查询利率值
    List<FndBaseRateSet> queryForFinanceContract(FndBaseRateSet fndBaseRateSet);

    //级联删除对应的行表
    public void batchDeleteLine(List<FndBaseRateSet> fndBaseRateSetList);
}
