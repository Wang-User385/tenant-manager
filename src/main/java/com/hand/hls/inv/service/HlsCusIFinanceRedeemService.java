package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;

import java.util.List;

/**
 * @Description:赎回service
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
public interface HlsCusIFinanceRedeemService extends IBaseService<HlsCusFinanceRedeem>, ProxySelf<HlsCusIFinanceRedeemService> {

    List<HlsCusFinanceRedeem> redeemSave(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem);

    List<HlsCusFinanceRedeem> queryAll(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem, int page, int pageSize);

    List<HlsCusFinanceRedeem> checkRedeemNewOrReturn(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem, int page, int pageSize);

    List<HlsCusFinanceRedeem> redeemSubmitWfl(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem);

    HlsCusFinanceRedeem redeemInvalidSubmit(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem);

    HlsCusFinanceRedeem redeemChangesSubmit(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem);

    //查询赎回明细信息
    List<HlsCusFinanceRedeem> queryRedeemDetail(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem);

}