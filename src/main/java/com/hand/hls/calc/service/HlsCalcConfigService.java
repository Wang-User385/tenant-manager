package com.hand.hls.calc.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface HlsCalcConfigService extends IBaseService<HlsCalcConfig>, ProxySelf<HlsCalcConfigService> {

    public List<HlsCalcConfig> queryUnSheets(IRequest requestContext, HlsCalcConfig hcc, int page, int pagesize);

    public List<HlsCalcConfig> queryAll(IRequest requestContext, HlsCalcConfig hcc, int page, int pagesize);

    void updateSheets(String priceList, String sheets,String compressSheets);

    public List<HlsPriceListConfigBT> btConfigQuery(String priceList);

    void doSubmit(IRequest requestCtx, List<HlsCalcConfig> lists);

    public List<Map> getSheetNames(HlsCalcConfig hlsCalcConfig) throws UnsupportedEncodingException;
}
