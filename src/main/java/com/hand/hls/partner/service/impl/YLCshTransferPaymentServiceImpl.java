package com.hand.hls.partner.service.impl;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.HlsCusCshTransactionService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.mapper.YLCshTransferPaymentDtoMapper;
import com.hand.hls.partner.service.IYLCshTransferPaymentService;
import com.hand.hls.utils.HlsCusMathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class YLCshTransferPaymentServiceImpl extends BaseServiceImpl<YLCshTransferPaymentDto> implements IYLCshTransferPaymentService {


    @Autowired
    private YLCshTransferPaymentDtoMapper ylCshTransferPaymentDtoMapper;

    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;


    public static final String DOCUMENT_CATEGORY = "CSH_TRANSACTION";

    public static final String DOCUMENT_TYPE = "RECEIPT";

    public static final String BUSINESS_TYPE = "RECEIPT";

    @Override
    public  List<YLCshTransferPaymentDto> updateTransferStatus(IRequest requestCtx , List<YLCshTransferPaymentDto> list) {
        List<YLCshTransferPaymentDto> res = new ArrayList<>();
        for (YLCshTransferPaymentDto ylCshTransferPaymentDto : list) {
            YLCshTransferPaymentDto ylCshTransferPaymentDto1 = new YLCshTransferPaymentDto();
            ylCshTransferPaymentDto1.setPaymentId(ylCshTransferPaymentDto.getPaymentId());
            ylCshTransferPaymentDto1 =
                    ylCshTransferPaymentDtoMapper.selectByPrimaryKey(ylCshTransferPaymentDto1);
            ylCshTransferPaymentDto1.setTransferPaymentStatus("CANCEL");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto1);
            res.add(ylCshTransferPaymentDto1);
        }
        return res;
    }

    @Override
    public List<YLCshTransferPaymentDto> updateAndVerification( IRequest requestCtx ,List<YLCshTransferPaymentDto> ylCshTransferPaymentDtoList) {
        ylCshTransferPaymentDtoList.forEach(ylCshTransferPaymentDto -> {
            //生产现金事务数据
            HlsCusCshTransaction hlsCusCshTransaction = getHlsCusCshTransaction(requestCtx, ylCshTransferPaymentDto);
            //查出合同现金流数据
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(ylCshTransferPaymentDto.getCashflowId());
            hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflow);
            //应收金额
            Double dueAmount = hlsCusConContractCashflow.getDueAmount();
            //已收金额
            Double receivedAmount = hlsCusConContractCashflow.getReceivedAmount();
            //不存在应收小于已收 逾期和罚息是新的现金流
            //已收加还款总金额
            Double amount = HlsCusMathUtil.add(receivedAmount,ylCshTransferPaymentDto.getRepayAmount());
            //应收  == 已收加还款
            if (HlsCusMathUtil.compare(amount,dueAmount) == -1){
                throw new RuntimeException("多还金额");
            }
            //设置核销字段
            if (HlsCusMathUtil.compare(amount,dueAmount) == 0){
                //部分核销
                hlsCusCshTransaction.setWriteOffFlag("PARTIAL");
                hlsCusConContractCashflow.setWriteOffFlag("PARTIAL");
            }else {
                //完全核销
                hlsCusCshTransaction.setWriteOffFlag("FULL");
                hlsCusConContractCashflow.setWriteOffFlag("FULL");
            }
            //设置核销金额
            hlsCusCshTransaction.setWriteOffAmount(HlsCusMathUtil.add(hlsCusCshTransaction.getUnWriteOffAmount(),ylCshTransferPaymentDto.getRepayAmount()));
            //设置还款后的已收金额
            hlsCusConContractCashflow.setReceivedAmount(amount);
            hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);



            //更新转付确认表
            ylCshTransferPaymentDto.setTransferPaymentStatus("CONFIRMED");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto);
        });
        return ylCshTransferPaymentDtoList;
    }

    private HlsCusCshTransaction getHlsCusCshTransaction(IRequest requestCtx, YLCshTransferPaymentDto ylCshTransferPaymentDto) {
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        Map<String, String> params = new HashMap<>();
        String transactionNum = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
        hlsCusCshTransaction.setTransactionCategory(DOCUMENT_CATEGORY);
        hlsCusCshTransaction.setTransactionType(DOCUMENT_TYPE);
        hlsCusCshTransaction.setBusinessType(BUSINESS_TYPE);
        hlsCusCshTransaction.setTransactionNum(transactionNum);
        hlsCusCshTransaction.setTransactionDate(new Date());
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
        hlsCusCshTransaction.setTransactionAmount(ylCshTransferPaymentDto.getRepayAmount());
        hlsCusCshTransaction.setContractId(ylCshTransferPaymentDto.getContractId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        //系统字段
        hlsCusCshTransaction.setCreationDate(new Date());
        hlsCusCshTransaction.setCreatedBy(requestCtx.getUserId());
        hlsCusCshTransaction.setLastUpdateDate(new Date());
        hlsCusCshTransaction.setLastUpdatedBy(requestCtx.getUserId());
        return hlsCusCshTransaction;
    }

    public static void main(String[] args) {
        Double a = 2D;
        Double b = 1D;
        System.out.println(HlsCusMathUtil.compare("1", "1"));
        System.out.println(HlsCusMathUtil.compare(a, b));
    }

}