package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;

import java.io.UnsupportedEncodingException;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Map;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/16 - 14:18
 */

public interface ConFloatingRateReqLnService extends IBaseService<HlsCusConFloatingRateReqLn>, ProxySelf<ConFloatingRateReqLnService> {
    List<HlsCusConFloatingRateReqLn> queryConFloatingRateReqLn(IRequest var1, HlsCusConFloatingRateReqLn var2, int var3, int var4);

    List<HlsCusConContractCashflow> queryCompare(IRequest var1, HlsCusConFloatingRateReqLn var2, int var3, int var4) throws NoSuchObjectException;

    void cancelFloatingRateReq(IRequest var1, HlsCusConFloatingRateReq var2);

    void confirmFloatingRate(IRequest var1, HlsCusConFloatingRateReq var2) throws Exception;

    List<HlsCusConFloatingRateReq> queryHistory(IRequest var1, HlsCusConFloatingRateReq var2, int var3, int var4);

    HlsCusConFloatingRateReq createFloatingRateChangeList(IRequest var1);

    List<HlsCusConFloatingRateReqLn> createFloatingRateQuotation(IRequest iRequest ,List<HlsCusPrjQuotation> prjQuotationList)throws Exception;

    Map<String, Object> calRateChange(IRequest var1, Long var2, List<Long> var3) throws Exception  ;

    List<HlsCusConFloatingRateReqLn> queryFloatlnCalcDetail(IRequest iRequest, HlsCusConFloatingRateReqLn var2, int pagenum, int pagesize);

}

