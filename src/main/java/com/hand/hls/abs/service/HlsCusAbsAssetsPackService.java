package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPack;
import hls.core.utils.exception.HlsCusException;

import java.util.List;

/**
 * <p>
 *  资产包
 * </p>
 *
 * @author yuanyuan 2019/04/02 6:21 PM
 */
public interface HlsCusAbsAssetsPackService extends IBaseService<HlsCusAbsAssetsPack>, ProxySelf<HlsCusAbsAssetsPackService> {


    /**
     * 资产包查询
     * @param iRequest
     * @param assetsPack
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsAssetsPack> selectAssetsPack(IRequest iRequest, HlsCusAbsAssetsPack assetsPack, int page, int pageSize);


    /**
     * 资产打包新增修改
     * @param iRequest
     * @param assetsPack
     * @return
     */
    HlsCusAbsAssetsPack submitAssetsPack(IRequest iRequest, HlsCusAbsAssetsPack assetsPack) throws HlsCusException;


    /**
     * 作废
     * @param iRequest
     * @param assetsPack
     */
    void cancelAssetsPack(IRequest iRequest, HlsCusAbsAssetsPack assetsPack);

    /**
     * 查询封包日Lov
     * @param assetsPack
     * @return
     */
    List<HlsCusAbsAssetsPack>  getLovData(HlsCusAbsAssetsPack assetsPack);
}
