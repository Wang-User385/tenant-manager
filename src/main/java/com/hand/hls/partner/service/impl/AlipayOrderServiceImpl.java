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
    private static  Double  rate=100.00;
    @Autowired
    private IAlipayService  alipayService ;

    /**
     * 添加
     * @param requestCtx
     * @param cashflow
     * @return
     */

    @Override
    public AlipayOrderDTO batchAdd(IRequest requestCtx, HlsCusConContractCashflow cashflow) throws HlsCusException {

            AlipayOrderDTO alipayOrderDTO = new AlipayOrderDTO();
            alipayOrderDTO.setCashflowId(cashflow.getCashflowId());

            //给属性赋值
            //应收金额
        long totalDueAmount = (long)(cashflow.getTotalDueAmount() * rate);

            //已核销金额
        long totalWriteOffAmount = (long)(cashflow.getTotalWriteOffAmount() * rate);


            //确保 totalDueAmount 大于totalWriteOffAmount
            Long amount = null;
            if (totalDueAmount > totalWriteOffAmount) {
                amount = (totalDueAmount - totalWriteOffAmount);
            } else {
               throw  new HlsCusException("现金流为："+cashflow.getCashflowId()+"的核销金额为0分");
            }
            //代扣金额
            alipayOrderDTO.setAmount(amount);
            //状态
            alipayOrderDTO.setStatus("NEW");
            //描述
            alipayOrderDTO.setSubject("承租人"+cashflow.getTenantIdN()
                    +"("+cashflow.getContractId()+")第"
                    +cashflow.getTimes()+"期"+"核销金额为"
                    +amount.toString()+"分");
            //业务号
            String formattedCashflowId = String.format("%010d", cashflow.getCashflowId());
            alipayOrderDTO.setOutSeqNo(System.currentTimeMillis() + formattedCashflowId);

            AlipayOrderDTO orderDTO = self().insertSelective(requestCtx, alipayOrderDTO);

            return orderDTO;
    }

    /**
     * 查询
     * @param requestContext
     * @param cashflow
     * @return
     * @throws ResMessageException
     */

    @Override
    public List<AlipayOrderDTO> selectState(IRequest requestContext, HlsCusConContractCashflow cashflow) throws ResMessageException {

            AlipayOrderDTO alipayOrderDTO = new AlipayOrderDTO();

            alipayOrderDTO.setCashflowId(cashflow.getCashflowId());

            //根据现金流Id查找代扣中间表AlipayOrderDTO
            List<AlipayOrderDTO> orderDTOS = self().selectSelective(requestContext, alipayOrderDTO);

            if(orderDTOS.isEmpty()){
            throw new ResMessageException("查询结果为空");
           }


        return orderDTOS;
    }

    @Override
    public IAlipayOrderService self() {
        return IAlipayOrderService.super.self();
    }
}