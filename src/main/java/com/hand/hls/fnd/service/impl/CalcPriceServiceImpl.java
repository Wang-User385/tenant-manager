package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.CalcPrice;
import com.hand.hls.fnd.service.ICalcPriceService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CalcPriceServiceImpl extends BaseServiceImpl<CalcPrice> implements ICalcPriceService{

    @Autowired
    private HlsCusPrjQuotationService quotationService;
    @Autowired
    private HlsCusPrjQuotationMapper quotationMapper;

    private final static String DOCUMENT_CATEGORY = "FINANCE_CALC";
    private final static String PRJ_DOCUMENT_CATEGORY = "PRJ_PROJECT";
    private final static String STATUS = "NEW";

    @Override
    public List<CalcPrice> svaeCalcPrice(IRequest iRequest, List<CalcPrice> list) {

        for(CalcPrice calcPrice:list){
            HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
            if(calcPrice.getFinanceId() == null){
                self().insert(iRequest,calcPrice);

                //新建的时候插入一条报价记录
                quotation.setSourceDocumentCategory(DOCUMENT_CATEGORY);
                quotation.setSourceDocumentId(calcPrice.getFinanceId());
                quotation.setStatus(STATUS);
                quotation.setPriceList(calcPrice.getPriceList());
                quotation.setDescription(calcPrice.getCalcName());
                quotationService.insert(iRequest,quotation);
            }else{
                self().updateByPrimaryKey(iRequest,calcPrice);

                //更新报价表对应的报价类型
                quotation.setSourceDocumentCategory(DOCUMENT_CATEGORY);
                quotation.setSourceDocumentId(calcPrice.getFinanceId());

                quotation = quotationMapper.selectOne(quotation);
                quotation.setPriceList(calcPrice.getPriceList());
                quotation.setDescription(calcPrice.getCalcName());
                quotationService.updateByPrimaryKeySelective(iRequest,quotation);
            }
        }
        return list;
    }

    @Override
    public List<HlsCusPrjQuotation> savePrjCalcPrice(IRequest iRequest, List<HlsCusPrjQuotation> hlsCusPrjQuotationList) {
        for(HlsCusPrjQuotation hlsCusPrjQuotation:hlsCusPrjQuotationList){
            if(hlsCusPrjQuotation.getQuotationId() == null){
                HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
                quotation.setSourceDocumentCategory(PRJ_DOCUMENT_CATEGORY);
                quotation.setSourceDocumentId(hlsCusPrjQuotation.getSourceDocumentId());
                quotation.setCalcName(hlsCusPrjQuotation.getCalcName());
                quotation.setCreatedBy(hlsCusPrjQuotation.getCreatedBy());
                quotation.setLastUpdatedBy(hlsCusPrjQuotation.getLastUpdatedBy());
                quotation.setPriceList(hlsCusPrjQuotation.getPriceList());
                quotation.setDataClass("PRJ_PROJECT_INVEST");
                quotation.setStatus(STATUS);
                quotationService.insert(iRequest,quotation);
            }else{
                HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
                quotation = quotationService.selectByPrimaryKey(iRequest,hlsCusPrjQuotation);
                quotation.setCalcName(hlsCusPrjQuotation.getCalcName());
                quotation.setLastUpdatedBy(hlsCusPrjQuotation.getLastUpdatedBy());
                quotation.setLastUpdateDate(new Date());
                quotation.setPriceList(hlsCusPrjQuotation.getPriceList());
                quotationService.updateByPrimaryKeySelective(iRequest,quotation);
            }
        }




        return hlsCusPrjQuotationList;
    }
}