package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflowH;

import java.util.List;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 17:09
 * @Description:
 * @Purpose:
 **/
public interface HlsCusPrjQuotationCashflowHMapper extends Mapper<HlsCusPrjQuotationCashflowH> {
    /*清除报价数据*/
    void deleteQuotationCalcData(HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH);

    /*查找对应分段报价的数据*/
    HlsCusPrjQuotationCashflowH selectPrjQuotationSubDetail(HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH);

    /*查找满足条件对应分段报价的数据*/
    List<HlsCusPrjQuotationCashflowH> selectPrjQuotationSubListDetail(HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH);
}
