package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductBuyback;
import com.hand.hls.abs.mapper.HlsCusAbsProductBuybackMapper;
import com.hand.hls.abs.service.HlsCusAbsProductBuybackService;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductBuybackServiceImpl extends BaseServiceImpl<HlsCusAbsProductBuyback> implements HlsCusAbsProductBuybackService {


    @Autowired
    private HlsCusAbsProductBuybackMapper productBuybackMapper;


    @Override
    public List<HlsCusAbsProductBuyback> selectProductBuybackData(IRequest iRequest, HlsCusAbsProductBuyback productBuyback, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return productBuybackMapper.selectProductBuybackData(productBuyback);
    }

    @Override
    public void confirmReceiverProductBuyback(IRequest request, HlsCusFundTransferList fundTransferList) throws HlsCusException {
        HlsCusAbsProductBuyback productBuyback = productBuybackMapper.selectByPrimaryKey(fundTransferList.getSourceDocLineId());
        if(productBuyback.getReceiverAmount()==null){
            productBuyback.setReceiverAmount(BigDecimal.ZERO);
        }
        productBuyback.setReceiverAmount(productBuyback.getReceiverAmount().add(fundTransferList.getActualPayAmount()));
        if (productBuyback.getReceiverAmount().compareTo( productBuyback.getBuybackAmount())==1) {
            throw new HlsCusException("实际总支付金额不能大于当期回购金额");
        }
        productBuybackMapper.updateByPrimaryKeySelective(productBuyback);
    }
}