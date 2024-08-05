package com.hand.hls.partner.service.impl;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.mapper.YLCshTransferPaymentDtoMapper;
import com.hand.hls.partner.service.IYLCshTransferPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class YLCshTransferPaymentServiceImpl extends BaseServiceImpl<YLCshTransferPaymentDto> implements IYLCshTransferPaymentService {


    @Autowired
    private YLCshTransferPaymentDtoMapper ylCshTransferPaymentDtoMapper;

    @Override
    public  List<YLCshTransferPaymentDto> updateTransferStatus(IRequest requestCtx,List<YLCshTransferPaymentDto> list) {
        list.forEach(ylCshTransferPaymentDto -> {
           ylCshTransferPaymentDto =
                   ylCshTransferPaymentDtoMapper.selectByPrimaryKey(ylCshTransferPaymentDto);
           ylCshTransferPaymentDto.setTransferPaymentStatus("CANCEL");
        });
        return list;
    }
}