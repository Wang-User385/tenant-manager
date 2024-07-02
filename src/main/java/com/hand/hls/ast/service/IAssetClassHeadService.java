package com.hand.hls.ast.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ast.dto.AssetClassHead;

public interface IAssetClassHeadService extends IBaseService<AssetClassHead>, ProxySelf<IAssetClassHeadService>{

    ResponseData createAssetClassInfo(IRequest iRequest);

    ResponseData assetClassWflStart(IRequest iRequest, AssetClassHead assetClassHead);
    Long queryId(IRequest iRequest, AssetClassHead assetClassHead) throws NullPointerException;

    /**
     * 创建资产分类头表
     */
    ResponseData createAssetClassHead (IRequest iRequest,AssetClassHead assetClassHead) throws Exception;

    /**
     * 每季度自动创建资产分类信息
     * @param iRequest
     */
    void periodCreateAssetClassInfo(IRequest iRequest);

    /**
     * 工作流启动过程
     * @param iRequest
     * @param assetClassHead
     */
    void assetClassWflProcedure(IRequest iRequest, AssetClassHead assetClassHead);

}