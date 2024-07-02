package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductRelease;
import com.hand.hls.exception.HlsCusException;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusAbsProductReleaseService extends IBaseService<HlsCusAbsProductRelease>, ProxySelf<HlsCusAbsProductReleaseService> {





    /**
     * 查询数据
     * @param productRelease
     * @return
     */
    List<HlsCusAbsProductRelease> selectProductReleaseData(IRequest iRequest, HlsCusAbsProductRelease productRelease, int page, int pageSize);




    List<HlsCusAbsProductRelease> confirmtProductReleaseData(IRequest iRequest, List<HlsCusAbsProductRelease> productReleases);


    /**
     * 删除
     * @param iRequest
     * @param productReleases
     * @throws HlsCusException
     */
    void deleteProductReleaseData(IRequest iRequest, List<HlsCusAbsProductRelease> productReleases) throws HlsCusException;



    BigDecimal selectRealseAmounSum(HlsCusAbsProductRelease productRelease);
}