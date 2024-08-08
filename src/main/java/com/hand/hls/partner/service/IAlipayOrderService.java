package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.partner.dto.AlipayOrderDTO;

import java.util.List;

/**
 * 代扣功能中间表
 */
public interface IAlipayOrderService extends IBaseService<AlipayOrderDTO>, ProxySelf<IAlipayOrderService>{


    boolean batchAdd(IRequest requestCtx, List<HlsCusConContractCashflow> list);
}