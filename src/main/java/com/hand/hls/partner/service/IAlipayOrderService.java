package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.partner.dto.AlipayOrderDTO;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;

import java.util.ArrayList;
import java.util.List;

/**
 * 代扣功能中间表
 */
public interface IAlipayOrderService extends IBaseService<AlipayOrderDTO>, ProxySelf<IAlipayOrderService>{


    AlipayOrderDTO batchAdd(IRequest requestCtx, HlsCusConContractCashflow cashflow) throws HlsCusException;

    List<AlipayOrderDTO> selectState(IRequest requestContext, HlsCusConContractCashflow cashflowList) throws ResMessageException;
}