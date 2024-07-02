//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.PrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.service.IPrjQuotationCashflowService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrjQuotationCashflowServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationCashflow> implements IPrjQuotationCashflowService {
    @Autowired
    private HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    public PrjQuotationCashflowServiceImpl() {
    }

    private HlsCusPrjQuotationCashflow updateOtherCfItemTimes(HlsCusPrjQuotationCashflow cashflow) {
        if (cashflow.getCfItem() != 0L && cashflow.getCfItem() != 2L && cashflow.getCfItem() != 3L && cashflow.getCfItem() != 5L && cashflow.getCfItem() != 6L) {
            if (cashflow.getCfItem() == 8L) {
                cashflow.setTimes(cashflow.getTimes());
            }
        } else {
            cashflow.setTimes(0L);
        }

        return cashflow;
    }

    @Override
    public List<HlsCusPrjQuotationCashflow> saveCalc2PrjQuotationCashflow(IRequest requestContext, PrjQuotation prjQuotation) throws Exception {
        List<Object> ObjectList = this.hlsCalcExcelImportUtilService.getExcelToCalcLnTable(requestContext, prjQuotation.getSheets(), prjQuotation.getPriceList(), "prj", Integer.parseInt(prjQuotation.getLeaseTimes().toString()), prjQuotation.getSourceDocumentCategory());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = new ArrayList();

        for(int i = 0; i < ObjectList.size(); ++i) {
            String s = JSON.toJSONString(ObjectList.get(i));
            HlsCusPrjQuotationCashflow HlsCusPrjQuotationCashflow = (HlsCusPrjQuotationCashflow)JSON.parseObject(s, HlsCusPrjQuotationCashflow.class);
            HlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
            HlsCusPrjQuotationCashflow.setCfStatus("RELEASE");
            HlsCusPrjQuotationCashflow.set__status("insert");
            prjQuotationCashflowList.add(HlsCusPrjQuotationCashflow);
        }

        prjQuotationCashflowMapper.deleteByQuotationId((HlsCusPrjQuotation) prjQuotation);
        ((IPrjQuotationCashflowService)this.self()).batchUpdate(requestContext, prjQuotationCashflowList);
        return prjQuotationCashflowList;
    }
}