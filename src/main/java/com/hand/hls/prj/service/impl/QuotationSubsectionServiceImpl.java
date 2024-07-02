package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.QuotationSubsection;
import com.hand.hls.prj.mapper.QuotationSubsectionMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.IQuotationSubsectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class QuotationSubsectionServiceImpl extends BaseServiceImpl<QuotationSubsection> implements IQuotationSubsectionService {
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private QuotationSubsectionMapper quotationSubsectionMapper;

    /**
     * 更新quotation数据
     * @param iRequest
     * @param hlsCusPrjQuotation
     * @throws IllegalArgumentException
     */
    @Override
    public void updateQuotationBySubsectionData(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation) throws IllegalArgumentException {
        double leaseTerm = 0D;
        QuotationSubsection quotationSubsection = new QuotationSubsection();
        quotationSubsection.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        List<QuotationSubsection> quotationSubsectionList = self().select(iRequest, quotationSubsection, 1, 9999999);
        if (quotationSubsectionList.size() > 0) {
            for (QuotationSubsection subsection : quotationSubsectionList) {
                leaseTerm = (subsection.getEndTime() - subsection.getStartTime() + 1) * subsection.getAnnualPayTimes() + leaseTerm;
            }
            Long leaseTimes = quotationSubsectionMapper.quotationSubsectionMaxTimes(quotationSubsectionList.get(0).getQuotationId()).longValue();
            hlsCusPrjQuotation.setLeaseTerm(leaseTerm);
            hlsCusPrjQuotation.setLeaseTimes(leaseTimes);
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, hlsCusPrjQuotation);
        }

    }
}