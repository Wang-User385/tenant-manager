package com.hand.hls.partner.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import org.springframework.stereotype.Service;
import com.hand.hls.partner.dto.AlipayOrderDTO;
import com.hand.hls.partner.service.IAlipayOrderService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AlipayOrderServiceImpl extends BaseServiceImpl<AlipayOrderDTO> implements IAlipayOrderService{

    @Override
    public boolean batchAdd(IRequest requestCtx, List<HlsCusConContractCashflow> list) {
        for (HlsCusConContractCashflow cashflow : list) {

            AlipayOrderDTO alipayOrderDTO = new AlipayOrderDTO();
            alipayOrderDTO.setCashflowId(cashflow.getCashflowId());


        }


//        for (HlsCusConContractCashflow cashflowDTO : list) {
//
//            if(alipayOrderDTO.getStatus().equalsIgnoreCase("successs")&&alipayOrderDTO.getCashflowId()!=null){
//                throw new RuntimeException("CashflowId:"+alipayOrderDTO.getCashflowId()+"已经代扣,请勿重复代扣");
//            }
//            self().insertSelective(requestCtx,alipayOrderDTO);
//        }


        return false;
    }

    @Override
    public IAlipayOrderService self() {
        return IAlipayOrderService.super.self();
    }
}