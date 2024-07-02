package com.hand.hls.ast.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ast.dto.AssetClassLine;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface IAssetClassLineService extends IBaseService<AssetClassLine>, ProxySelf<IAssetClassLineService>{

    ResponseData updateInitLevel(IRequest iRequest, List<AssetClassLine> lineList);

    ResponseData updateApproveReviewLevel(IRequest iRequest, List<AssetClassLine> lineList);

    /**
     * 系统自动对所选合同进行分类
     */
    void transforByContracts(IRequest iRequest, List<HlsCusConContract> contracts,Long classHeadId) throws Exception;

    /**
     * 保存资产分类行数据
     * @param classHeadId
     * @param assetClassLineList
     */
    void insertClassLineInfo(IRequest iRequest,Long classHeadId,List<AssetClassLine> assetClassLineList);


//    void excelBatchImport(IRequest iRequest, Long headerId, Long classHeadId, String layoutCode) throws HlsCusException;

}