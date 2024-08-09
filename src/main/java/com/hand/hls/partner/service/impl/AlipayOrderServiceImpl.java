package com.hand.hls.partner.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.AuditEntry;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.partner.dto.AlipayOrderDTO;
import com.hand.hls.partner.service.IAlipayOrderService;
import org.springframework.transaction.annotation.Transactional;
import org.terracotta.modules.ehcache.store.nonstop.LocalReadsOnTimeoutStore;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AlipayOrderServiceImpl extends BaseServiceImpl<AlipayOrderDTO> implements IAlipayOrderService{
    private static  Long  rate=100L;
    @Autowired
    private IAlipayService  alipayService ;

    @Override
    public AlipayOrderDTO batchAdd(IRequest requestCtx, HlsCusConContractCashflow cashflow) {
        ArrayList<AlipayOrderDTO> orderDTOS = new ArrayList<>();

            AlipayOrderDTO alipayOrderDTO = new AlipayOrderDTO();
            alipayOrderDTO.setCashflowId(cashflow.getCashflowId());

            //给属性赋值
            //应收金额
            Long totalDueAmount = cashflow.getTotalDueAmount().longValue();
            //已核销金额
            Long totalWriteOffAmount = cashflow.getTotalWriteOffAmount().longValue();

            //确保 totalDueAmount 大于totalWriteOffAmount
            Long amount;
            if (totalDueAmount > totalWriteOffAmount) {
                amount = totalDueAmount - totalWriteOffAmount;
            } else {
               throw  new RuntimeException("现金流为："+alipayOrderDTO.getCashflowId()+"的核销金额为0分");
            }
            //代扣金额
            alipayOrderDTO.setAmount(amount*rate);
            //状态
            alipayOrderDTO.setStatus("NEW");
            //描述
            alipayOrderDTO.setSubject("承租人"+cashflow.getTenantIdN()
                    +"("+cashflow.getContractId()+")第"
                    +cashflow.getTimes()+"期"+"核销金额为"
                    +((amount*rate))+"分");
            //业务号
            String formattedCashflowId = String.format("%010d", cashflow.getCashflowId());
            alipayOrderDTO.setOutSeqNo(System.currentTimeMillis() + formattedCashflowId);

            AlipayOrderDTO orderDTO = self().insertSelective(requestCtx, alipayOrderDTO);
//            try {
//                //代扣签约
//                alipayService.withhold(alipayOrderDTO.getOrderId());
//            } catch (HlsCusException e) {
//                throw new RuntimeException("订单号为"+alipayOrderDTO.getOrderId()+":"+e);
//            }
//            orderDTOS.add(orderDTO);


        return orderDTO;
    }

    @Override
    public List<AlipayOrderDTO> selectState(IRequest requestContext, HlsCusConContractCashflow cashflow) throws ResMessageException {
        ArrayList<AlipayOrderDTO> alipayOrderDTOS = new ArrayList<>();

            AlipayOrderDTO alipayOrderDTO = new AlipayOrderDTO();

            alipayOrderDTO.setCashflowId(cashflow.getCashflowId());

            //根据现金流Id查找代扣中间表AlipayOrderDTO
            List<AlipayOrderDTO> orderDTOS = self().selectSelective(requestContext, alipayOrderDTO);

            if(orderDTOS.isEmpty()){
            throw new ResMessageException("查询结果为空");
           }
        // 确保 orderDTOS 至少有一个元素
        if (!orderDTOS.isEmpty()) {
            alipayOrderDTOS.add(orderDTOS.get(orderDTOS.size()-1));
        }






        return orderDTOS;
    }

    @Override
    public IAlipayOrderService self() {
        return IAlipayOrderService.super.self();
    }
}