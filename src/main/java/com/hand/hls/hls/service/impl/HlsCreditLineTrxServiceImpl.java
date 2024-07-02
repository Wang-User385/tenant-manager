package com.hand.hls.hls.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMapper;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.hls.dto.HlsCusHlsCreditLineTrx;
import com.hand.hls.hls.dto.HlsCusHlsCreditLineTrxMaster;
import com.hand.hls.hls.mapper.HlsCusHlsCreditLineTrxMasterMapper;
import com.hand.hls.hls.service.IHlsCreditLineTrxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ever
 * @DATE: 2020-02-14 14:21
 */
@Service
public class HlsCreditLineTrxServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLineTrx> implements IHlsCreditLineTrxService {

    @Autowired
    private HlsCusHlsCreditLineTrxMasterMapper hlsCreditLineTrxMasterMapper;
    @Autowired
    private HlsCusHlsCreditLineMapper hlsCreditLineMapper;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;

    public void calcCreditMethod(IRequest request, Long trxId) {
        boolean updateFlag = false;
        HlsCusHlsCreditLineTrx hlsCreditLineTrx = (HlsCusHlsCreditLineTrx)((Mapper)this.mappers.get(0)).selectByPrimaryKey(trxId);
        String trxCode = hlsCreditLineTrx.getTrxCode();
        Long creditLineId = hlsCreditLineTrx.getCreditLineId();
        HlsCusHlsCreditLineTrxMaster hlsCreditLineTrxMaster = hlsCreditLineTrxMasterMapper.selectByPrimaryKey(trxCode);
        String trxType = hlsCreditLineTrxMaster.getTrxType();
        HlsCusHlsCreditLine hlsCreditLine = hlsCreditLineMapper.selectByPrimaryKey(creditLineId);
        if (hlsCreditLine != null) {
            byte var11 = -1;
            switch(trxType.hashCode()) {
                case 84327:
                    if (trxType.equals("USE")) {
                        var11 = 0;
                    }
                    break;
                case 2573738:
                    if (trxType.equals("THAW")) {
                        var11 = 3;
                    }
                    break;
                case 1808577511:
                    if (trxType.equals("RELEASE")) {
                        var11 = 1;
                    }
                    break;
                case 2081894039:
                    if (trxType.equals("FREEZE")) {
                        var11 = 2;
                    }
            }

            switch(var11) {
                case 0:
                    hlsCreditLine.setCreditExposureAmt(CalculateUtil.add(hlsCreditLine.getCreditExposureAmt(), hlsCreditLineTrx.getTrxAmount()));
                    updateFlag = true;
                    break;
                case 1:
                    String documentType = hlsCreditLine.getDocumentType();
                    if (!"NON_REVOLVING".equalsIgnoreCase(documentType) && "REVOLVING".equalsIgnoreCase(documentType)) {
                        hlsCreditLine.setCreditExposureAmt(CalculateUtil.sub(hlsCreditLine.getCreditExposureAmt(), hlsCreditLineTrx.getTrxAmount()));
                        updateFlag = true;
                    }
                case 2:
                case 3:
            }

            if (updateFlag) {
                (hlsCreditLineService.self()).updateByPrimaryKeySelective(request, hlsCreditLine);
            }

        }
    }
}
