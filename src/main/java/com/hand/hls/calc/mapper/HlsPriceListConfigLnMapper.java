package com.hand.hls.calc.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface HlsPriceListConfigLnMapper extends Mapper<HlsPriceListConfigLn> {

	public List<HlsPriceListConfigLn> selectAllHlsPriceListConfigLn(HlsPriceListConfigLn configLn);

	public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByPriceList(HlsPriceListConfigLn configLn);

	public List<HlsPriceListConfigLn> targetColumnCode(HlsPriceListConfigLn var1);

	public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByHdId(HlsPriceListConfigLn configLn);

	public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByPriceListReq(HlsPriceListConfigLn configLn);

	HlsPriceListConfigLn queryConfigLnByPriceListAndColumnCode(@Param("priceList")String priceList, @Param("columnCode")String columnCode);

}
