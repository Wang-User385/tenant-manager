package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.sys.dto.HlsSystemNotice;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusAbsProductMapper extends Mapper<HlsCusAbsProduct> {
    /**
     * 变更首页查询
     */
    List<HlsCusAbsProduct> queryChangeHome(HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 产品变更首页扇形图
     */
    Map<String, Object> queryProductChangeChart();

    /**
     * 产品变更明细
     */
    HlsCusAbsProduct queryChangeDetail(HlsCusAbsProduct hlsCusAbsProduct);


    /**
     * 主界面查询
     * @param hlsCusAbsProduct
     * @return
     */
    List<HlsCusAbsProduct> queryProductHome(HlsCusAbsProduct hlsCusAbsProduct);


    List<HlsCusAbsProduct> queryProductData(HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 产品首页分析图查询
     */
    List<Map<String, Object>> queryProductChart(HlsCusAbsProduct hlsCusAbsProduct);

    /**
     * 产品首页动态查询
     */
    List<HlsSystemNotice> queryProductNotice();

    /**
     * 根据日期查询每期应归集金额的本金部分
     */
    BigDecimal queryPrincipal(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("productId") Long productId);


    /**
     * 根据日期查询第一期应归集金额的利息部分
     */
    List<HlsCusConContractCashflow> queryInterest(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("productId") Long productId);

    /**
     * 根据日期查询每期(除第一期)应归集金额的利息部分
     */
    BigDecimal queryInterest2(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("productId") Long productId);

    /**
     * 查询第一期归集申请金额待减的冲销金额
     */
    BigDecimal queryFirstCollectionAmount(@Param("productId") Long productId);

    /**
     * 项目首页综合查询
     */
    List<HlsCusAbsProduct> queryForProject(HlsCusAbsProduct hlsCusAbsProduct);


    /**
     * 更新报表名称
     */
    int updateProductReportName(@Param("productId") Long productId);

    /**
     * 获取提款总金额
     *
     * @param productId
     * @return
     */
    Double queryProductIncomeAmount(@Param("productId") Long productId);

    /**
     * 获取相应费用总金额
     *
     * @return
     */
    BigDecimal queryProductFeeAmountbyCfItem(HlsCusAbsProduct hlsCusAbsProduct);




    /**
     * 编号最大后两位
     * @param projectId
     * @return
     */
    String selectProductNumberMax(@Param("projectId") Long projectId);

    /**
     * 融资计划关联产品明细查询
     */
    List<HlsCusAbsProduct> queryAbsProductByCapLineId(HlsCusAbsProduct hlsCusAbsProduct);


    /**
     * 取资产包最大日期
     * @param productId
     * @return
     */
    Date selectConPackageMaxDueDate(@Param("productId") Long productId);

    /**
     * 提前结清金额
     * @param fromDate
     * @param toDate
     * @param productId
     * @return
     */
    BigDecimal selectOtherFinishAmount(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("productId") Long productId);
}

