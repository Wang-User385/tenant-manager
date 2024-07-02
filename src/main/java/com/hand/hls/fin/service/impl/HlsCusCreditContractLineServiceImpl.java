package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCreditContractLine;
import com.hand.hls.fin.mapper.HlsCusCreditContractLineMapper;
import com.hand.hls.fin.service.HlsCusCreditContractLineService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCreditContractLineServiceImpl extends BaseServiceImpl<HlsCusCreditContractLine> implements HlsCusCreditContractLineService {

    @Autowired
    private HlsCusCreditContractLineMapper hlsCusCreditContractLineMapper;

    @Override
    public List<HlsCusCreditContractLine> selectCreditLineCarry(IRequest iRequest, HlsCusCreditContractLine hlsCusCreditContractLine, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusCreditContractLine> list = hlsCusCreditContractLineMapper.selectCreditLineCarry(hlsCusCreditContractLine);
        render(hlsCusCreditContractLine, list);
        return list;
    }

    @Override
    public List<HlsCusCreditContractLine> selectCreditContractLine(IRequest iRequest, HlsCusCreditContractLine hlsCusCreditContractLine, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusCreditContractLine> list = hlsCusCreditContractLineMapper.selectCreditContractLine(hlsCusCreditContractLine);
        render(hlsCusCreditContractLine, list);
        return list;
    }

    private void render(HlsCusCreditContractLine hlsCusCreditContractLine, List<HlsCusCreditContractLine> list) {
        for (HlsCusCreditContractLine t : list) {
            if ("Y".equals(t.getSourceCreditFlag())) {
                t.setQuoteFlag("是");
            } else if (t.getCreditLineId() == null) {
                t.setQuoteFlag("否");
            } else if ((!"Y".equals(t.getCancelFlag()) && t.getSourceCreditLineId() != null) || ("Y".equals(t.getCancelFlag()) && "Y".equals(t.getSourceCancelFlag()))) {
                t.setQuoteFlag("是");
            } else {
                t.setQuoteFlag("否");
            }

            Double creditAvailableAmt = t.getCreditAvailableAmt();
            Double amt2 = hlsCusCreditContractLine.getCreditAvailableAmt();
            if (amt2 != null && amt2 < creditAvailableAmt) {
                creditAvailableAmt = amt2;
            }
            t.setActualCreditAmt(creditAvailableAmt);
        }
    }
}
