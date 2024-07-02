package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface HlsCusAbsProductCollectionMapper extends Mapper<HlsCusAbsProductCollection> {


    /**
     * 查询归集情况
     * @param hlsCusAbsProductCollection
     * @return
     */
    List<HlsCusAbsProductCollection> selectProductCollectionData(HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 根据 productId 删除归集信息
     */
    @Delete("delete from ct_abs_product_collection where confirm_flag='N' and product_id = #{productId} and data_class=#{dataClass}")
    void deleteNotConfirmByProductId(@Param("productId") Long productId, @Param("dataClass") String dataClass);

    /**
     * 根据 projectId 查询项目下的所有归集编号信息
     */
    List<HlsCusAbsProductCollection> queryAllNumberFromProject(@Param("numberType") String numberType, @Param("projectId") Long projectId);

    /**
     * 归集申请归集表单查询
     */
    HlsCusAbsProductCollection queryCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 兑付申请转付表单查询
     */
    HlsCusAbsProductCollection queryCashDeatil(HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 转付申请转付表单查询
     */
    HlsCusAbsProductCollection queryRemittance(HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 删除指定归集期数的归集编号
     */
    @Update("update ct_abs_product_collection set collection_number = '',collection_apply_date = null,collection_organization_id = null where collection_id = ${collectionId}")
    void resetCollection(@Param("collectionId") Long collectionId);

    /**
     * 删除指定转付期数的转付编号
     */
    @Update("update ct_abs_product_collection set remittance_number = '',remittance_apply_date = null,remittance_organization_id = null where collection_id = ${collectionId}")
    void resetRemittance(@Param("collectionId") Long collectionId);


    /**
     * 删除未归集
     * @param productId
     * @return
     */
    int deleteProductNotCollection(@Param("productId") Long productId);


    /**
     * 最大的租金回收计算日
     * @param productId
     * @return
     */
    Date selectMaxRentalBackDate(@Param("productId") Long productId);


    /**
     * 最大的期数
     * @param productId
     * @return
     */
    Long selectMaxCollectionTimes(@Param("productId") Long productId);



   BigDecimal selectCashFeeSum(@Param("collectionId") Long collectionId);


    /**
     * 查询分摊记录
     *
     * @return
     */
    List<HlsCusAbsProductCollection> selectProductCollectionInfoByCfItem(@Param("productId") Long productId, @Param("cfItem") Long cfItem);


    /**
     * 冻结归集兑付
     * @param productId
     * @return
     */
   int updateCollectionStatusBlock(@Param("productId") Long productId);


    /**
     * 获取最后确认一期的数据
     * @param productId
     * @return
     */
    HlsCusAbsProductCollection selectLastConfirmCollection(@Param("productId") Long productId);


    /**
     * 更新复制临时的兑付反馈计划出来confirmFlag
     */
   int  updateConfirmTempCollection(@Param("productId") Long productId, @Param("times") Long times);


    /**
     * 未确认的归集兑付个数
     * @param productId
     * @return
     */
    int selectNotConfirmCollectionCount(@Param("productId") Long productId);

    void deleteByCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection);
}

