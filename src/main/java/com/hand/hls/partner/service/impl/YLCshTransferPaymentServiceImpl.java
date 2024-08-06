package com.hand.hls.partner.service.impl;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.HlsCusCshTransactionService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.mapper.YLCshTransferPaymentDtoMapper;
import com.hand.hls.partner.service.IYLCshTransferPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            //设置核销
//            hlsCusCshTransaction.setWriteOffFlag();
//            hlsCusCshTransaction.setWriteOffAmount();
            hlsCusCshTransaction.setReversedFlag("N");
            hlsCusCshTransaction.setPostedFlag("N");
            //系统字段
            hlsCusCshTransaction.setCreationDate(new Date());
            hlsCusCshTransaction.setCreatedBy(requestCtx.getUserId());
            hlsCusCshTransaction.setLastUpdateDate(new Date());
            hlsCusCshTransaction.setLastUpdatedBy(requestCtx.getUserId());
            hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);



            //更新转付确认表
            ylCshTransferPaymentDto.setTransferPaymentStatus("CONFIRMED");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto);
        });
        return ylCshTransferPaymentDtoList;
    }
}