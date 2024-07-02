package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductRelease;
import com.hand.hls.abs.mapper.HlsCusAbsProductReleaseMapper;
import com.hand.hls.abs.service.HlsCusAbsProductReleaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.service.HlsCreditLineService;
//import com.hand.hls.lon.service.HlsCusICreditContractService;
import com.hand.hls.utils.HlsCusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductReleaseServiceImpl extends BaseServiceImpl<HlsCusAbsProductRelease> implements HlsCusAbsProductReleaseService {

    @Autowired
    private HlsCusAbsProductReleaseMapper productReleaseMapper;

    @Autowired
    private HlsCreditLineService hlsCreditLineService;

//    @Autowired
//    private HlsCusICreditContractService creditContractService;

    @Override
    public List<HlsCusAbsProductRelease> selectProductReleaseData(IRequest iRequest, HlsCusAbsProductRelease productRelease, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return productReleaseMapper.selectProductReleaseData(productRelease);
    }


    @Override
    public List<HlsCusAbsProductRelease> confirmtProductReleaseData(IRequest iRequest, List<HlsCusAbsProductRelease> productReleases) {
         for(HlsCusAbsProductRelease productRelease:productReleases){
             if(productRelease.getReleaseId()!=null){
                 productRelease.set__status(DTOStatus.UPDATE);
             }else{
                 productRelease.set__status(DTOStatus.ADD);
             }
             productRelease.setReleaseStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
         }
         self().batchUpdate(iRequest,productReleases);

        return productReleases;
    }

    @Override
    public void deleteProductReleaseData(IRequest iRequest, List<HlsCusAbsProductRelease> productReleases) throws HlsCusException {
        for(HlsCusAbsProductRelease productRelease:productReleases){
            if(HlsCusConstant.WORKFLOW_STATUS.CONFIRM.equals(productRelease.getReleaseStatus())){
                throw new HlsCusException("请勿删除已经确认的数据");
            }
        }
        self().batchDelete(productReleases);
    }

    @Override
    public BigDecimal selectRealseAmounSum(HlsCusAbsProductRelease productRelease) {
        return productReleaseMapper.selectRealseAmounSum(productRelease);
    }
}