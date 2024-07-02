package com.hand.hls.calc.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;

import java.util.List;


public interface HlsPriceListConfigLnService extends IBaseService<HlsPriceListConfigLn>,ProxySelf<HlsPriceListConfigLnService> {

	public List<HlsPriceListConfigLn> selectAllHlsPriceListConfigLn(HlsPriceListConfigLn configLn);

	public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByPriceList(HlsPriceListConfigLn configLn, int page, int pagesize);

	public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByHdId(HlsPriceListConfigLn configLn, int page, int pagesize);

	public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineHide(HlsPriceListConfigLn configLn);

	List<HlsPriceListConfigLn> selectTargetColumnCode(HlsPriceListConfigLn var1);
}
