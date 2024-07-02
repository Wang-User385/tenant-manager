//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CustomerLevelCaculator;
import com.hand.hls.bp.dto.FndScoreResultDtl;
import com.hand.hls.bp.dto.FndScoreTargetValues;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.FndScoreResultDtlMapper;
import com.hand.hls.bp.service.IFndScoreResultDtlService;
import com.hand.hls.bp.service.IFndScoreResultService;
import com.hand.hls.bp.service.IFndScoreTargetValuesService;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FndScoreResultDtlServiceImpl extends BaseServiceImpl<FndScoreResultDtl> implements IFndScoreResultDtlService {
    @Autowired
    FndScoreResultDtlMapper mapper;
    @Autowired
    IFndScoreResultDtlService fndScoreResultDtlService;
    @Autowired
    IFndScoreTargetValuesService fndScoreTargetValuesService;
    @Autowired
    IHlsScoreCalculationService hlsScoreCalculationService;
    @Autowired
    IFndScoreResultService fndScoreResultService;
    @Autowired
    CustomerLevelCaculator customerLevelCaculator;

    public FndScoreResultDtlServiceImpl() {
    }

    public void saveFnd(IRequest iRequest, FndScoreResultDtl fndt) {
        if (null != fndt.getRefndScoreResultDtls()) {
            Iterator var3 = fndt.getRefndScoreResultDtls().iterator();

            while(var3.hasNext()) {
                FndScoreResultDtl f = (FndScoreResultDtl)var3.next();
                f.setTargetScoreOriginal(f.getTargetScore());
                this.fndScoreResultDtlService.updateByPrimaryKeySelective(iRequest, f);
                FndScoreTargetValues fndScoreTargetValues = new FndScoreTargetValues();
                fndScoreTargetValues.setScoreTargetValueId(f.getScoreTargetValueId());
                this.fndScoreTargetValuesService.updateByPrimaryKeySelective(iRequest, fndScoreTargetValues);
            }
        }

        HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
//        hlsScoreCalculation.setScoreDate(new Date());
        hlsScoreCalculation.setScoreId(fndt.getScoreId());
        this.hlsScoreCalculationService.updateByPrimaryKeySelective(iRequest, hlsScoreCalculation);
        this.customerLevelCaculator.entrySec(iRequest, fndt.getScoreResultId());
        fndt.getFndScoreResult().set__status("update");
        this.fndScoreResultService.updateByPrimaryKeySelective(iRequest, fndt.getFndScoreResult());
    }

    public List<FndScoreResultDtl> selectscoreTemplateLnId(Long scoreTemplateHdId) {
        return this.mapper.selectscoreTemplateLnId(scoreTemplateHdId);
    }

    public Double selectTotalScore(FndScoreResultDtl fndScoreResultDtl) {
        return this.mapper.totalScore(fndScoreResultDtl);
    }
}
