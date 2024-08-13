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

       // 计算应收金额和已核销金额
        long totalDueAmount = (long) (cashflow.getTotalDueAmount() * rate);
        long totalWriteOffAmount = (long) (cashflow.getTotalWriteOffAmount() * rate);

        // 确保 totalDueAmount 大于 totalWriteOffAmount
        if (totalDueAmount <= totalWriteOffAmount) {
            throw new HlsCusException("现金流为：" + cashflow.getCashflowId() + "的核销金额为0分");
        }

        // 代扣金额
        long amount = totalDueAmount - totalWriteOffAmount;
        alipayOrderDTO.setAmount(amount);

        // 设置其他属性
        alipayOrderDTO.setStatus("NEW");
        alipayOrderDTO.setSubject("承租人" + cashflow.getTenantIdN()
                + "(" + cashflow.getContractId() + ")第"
                + cashflow.getTimes() + "期" + "核销金额为"
                + amount + "分");
        // 业务号
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

       // 根据现金流ID查找代扣中间表 AlipayOrderDTO
        List<AlipayOrderDTO> orderDTOS = self().selectSelective(requestContext, alipayOrderDTO);

        // 检查查询结果是否为空
        if (orderDTOS.isEmpty()) {
            throw new ResMessageException("查询结果为空");
        }

        // 返回查询结果
        return orderDTOS;
    }

    @Override
    public IAlipayOrderService self() {
        return IAlipayOrderService.super.self();
    }
}