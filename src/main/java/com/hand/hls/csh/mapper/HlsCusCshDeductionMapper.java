package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusCshDeduction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;

import java.util.List;
import java.util.Map;

/**
 * Created by FJM on 2016/12/5.
 */
public interface HlsCusCshDeductionMapper extends Mapper<HlsCusCshDeduction> {
    void addCshWriteOff(HlsCusCshWriteOff cshwriteoff);

    void updateWriteOff(HlsCusCshWriteOff cshwriteoff);

    void updateCshTransaction(HlsCusCshWriteOff cshwriteoff);

    void updateCshFlow(HlsCusCshWriteOff cshwriteoff);

    List<HlsCusCshDeduction> queryLovContract(HlsCusCshDeduction cshDeduction);

    List<HlsCusCshDeduction> queryLovPool(HlsCusCshDeduction cshDeduction);

    List<Map> homePageDepositMgr(Map map);

    List<Map> homePagePoolDepositMgr(Map map);

    List<Map> depositByMonth(Map map);

    List<Map> depositBySeason(Map map);

    List<Map> depositByHalfYear(Map map);

    List<Map> depositByYear(Map map);

    List<Map> depositPoolByMonth(Map map);

    List<Map> depositPoolBySeason(Map map);

    List<Map> depositPoolByHalfYear(Map map);

    List<Map> depositPoolByYear(Map map);

    List<Map> queryDepositPoolByBp(Map map);

    List<Map> DepositPoolWriteOffByBp(Map map);

    List<Map> DepositPoolSourceByBp(Map map);

    List<Map> queryDepositByCon(Map map);

    List<Map> queryDepositSourceByCon(Map map);

    List<Map> queryDepositUseByCon(Map map);

    List<Map> selectDepositByRate(Map map);
    List<Map> selectDepositPollByRate(Map map);

    Map homePageMgrWriteOff(Map map);
    Map homePagePoolMgrWriteOff(Map map);


    /**
     * 查询可以进行抵扣的合同
     * @return
     */
    List<Map<String,Object>> selectCanDeductionDepositCon();


}
