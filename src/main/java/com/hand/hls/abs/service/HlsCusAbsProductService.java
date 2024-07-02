package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsPkg;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.dto.HlsCusAbsProductQuotation;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.sys.dto.HlsSystemNotice;

import java.util.List;
import java.util.Map;

public interface HlsCusAbsProductService extends IBaseService<HlsCusAbsProduct>, ProxySelf<HlsCusAbsProductService> {

    List<HlsCusAbsProduct> createProduct(Long projectId, String productShortName, String productName, String productNumber, IRequest request);

    /**
     * 产品变更首页
     */
    List<HlsCusAbsProduct> queryChangeHome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize);

    /**
     * 产品变更首页扇形图
     */
    Map<String, Object> queryProductChangeChart();

    /**
     * 产品变更创建
     */
    //HlsCusAbsProduct createChangeProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 产品变更明细
     */
    HlsCusAbsProduct queryChangeDetail(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 产品变更保存
     */
    HlsCusAbsProduct saveProductChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 资产变更保存
     */
    HlsCusAbsPkg saveAssetChange(IRequest request, HlsCusAbsPkg hlsCusAbsPkg);


    /**
     * 资产变更计算
     */
    //HlsCusAbsPkg calcAssetChange(IRequest request, HlsCusAbsPkg hlsCusAbsPkg)  throws HlsCusException;


    /**
     * 资产变更确认queryProductNotice
     */
    //HlsCusAbsProduct confirmAssetChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);


    /**
     * 资产变更提交
     * @param request
     * @param hlsCusAbsPkg
     * @return
     */
    HlsCusAbsProduct submitAssetChange(IRequest request, HlsCusAbsPkg hlsCusAbsPkg)  throws HlsCusException;

    /**
     * 产品变更确认
     */
    HlsCusAbsProduct confirmProductChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 主界面查询
     * @param hlsCusAbsProduct
     * @return
     */
    List<HlsCusAbsProduct> queryProductHome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize);

    /**
     * 查询ABS数据
     */
    List<HlsCusAbsProduct> queryProductData(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize);

    /**
     * 产品首页分析图查询
     */
    List<Map<String, Object>> queryProductChart(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 产品首页动态查询
     */
    //List<HlsSystemNotice> queryProductNotice(IRequest request, int page, int pageSize);

    /**
     * 更新ABS产品
     */
    HlsCusAbsProduct updateProduct(IRequest request, HlsCusAbsPkg pkg);

    /**
     * ABS产品归集计算
     */
    HlsCusAbsProduct calcProduct(IRequest request, HlsCusAbsPkg pkg) throws HlsCusException ;

    void calcProduct(IRequest request, Long productId) throws HlsCusException;

    /**
     * ABS产品归集行计算
     */
    //HlsCusAbsProduct calcCashProduct(IRequest request, HlsCusAbsPkg pkg) throws HlsCusException ;


    /**
     * ABS产品归集行重算
     */
    HlsCusAbsProduct reCalcCashProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, String dataClass);

    /**
     * ABS产品确认
     */
    HlsCusAbsProduct submitProduct(IRequest request, HlsCusAbsPkg pkg) throws HlsCusException;

    /**
     * excel 导入
     */
    void excelImport(IRequest request, Long headerId, Long key);

    /**
     * 项目首页综合查询
     */
    List<HlsCusAbsProduct> queryForProject(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize);

    /**
     * 产品取消变更
     */
    void cancelProductChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);


    /**
     * 发债产品报价计算
     * @param request
     * @param pkg
     * @return
     */
    HlsCusAbsProduct calculateIssueProduct(IRequest request, HlsCusAbsPkg pkg) throws Exception;



    void calXirr(IRequest iRequest, HlsCusAbsProductQuotation productQuotation);


    /**
     * 回购保存
     * @param request
     * @param pkg
     * @return
     */
    HlsCusAbsProduct saveBuyBackData(IRequest request, HlsCusAbsPkg pkg);


    /**
     * 回购 提交
     * @param request
     * @param pkg
     * @return
     */
    HlsCusAbsProduct submitBuyBackData(IRequest request, HlsCusAbsPkg pkg)  throws HlsCusException;

    /**
     * abs 分摊
     *
     * @param request
     * @param hlsCusAbsProduct
     * @return
     */
    void absProductIncome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException;




    /**
     * 临时兑付计划变更
     * @param request
     * @param hlsCusAbsProduct
     */
    void confirmCollectionTemp(IRequest request, HlsCusAbsProduct hlsCusAbsProduct);


    /**
     * 产品完结
     * @param request
     * @param hlsCusAbsProduct
     */
    void endAbsProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct)  throws HlsCusException;
}

