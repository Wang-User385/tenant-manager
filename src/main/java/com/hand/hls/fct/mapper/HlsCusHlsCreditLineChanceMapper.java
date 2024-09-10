package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.sys.dto.SysUser;
import org.apache.ibatis.annotations.Param;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface HlsCusHlsCreditLineChanceMapper extends Mapper<HlsCusHlsCreditLineChance> {

    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByStatus(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByStatusAndPass(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByConditions(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    List<HlsCusHlsCreditLineChance> selectGroupCreditLineChance(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    List<HlsCusHlsCreditLineChance> queryCreditLineChanceByQuery(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    //授信立项扇形图查询
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceStatusInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    //授信立项首页条件查询
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByCondition(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    /**
     * 按照条件查询授信立项明细
     * @param hlsCusHlsCreditLineChance
     * @return 返回一个对象的集合
     */
    List<HlsCusHlsCreditLineChance> selectModelByCondition(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);


    List<HlsCusHlsCreditLineChance> queryCreditChanceAttachmentInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);


    /**
     * 更新授信立项状态
     */
    int updateCreditChanceStatus(@Param("chanceId") Long chanceId);

    HlsCusHlsCreditLineChance selectCreditLineChanceById(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    HlsCusHlsCreditLineChance selectCreditLineChanceWay(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByMarketintReportId(Long MarketintReportId);

    List<CompositeMap> queryChanceId(CompositeMap var1, String var2);
    //
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceByCreditLineStatus();
    List<HlsCusHlsCreditLineChance> selectCreditLineChanceById1(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    List<HlsCusHlsCreditLineChance> queryChanceIdNew();

    /**
     * BaseInfo基本信息比对
     */
    List<Map> selectChangeCompareBaseInfo();

    /**
     * BusinessAdmittance业务准入对照比对
     */
    List<Map> selectChangeCompareBusinessAdmittance();

    List<HlsCusHlsCreditLineChance> queryChanceIdByCooperativeOrganization(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    public List<SysUser> selectByUserId(@Param("userId")Long userId);

    /***
     * 保理主页面查询
     * @param hlsCusHlsCreditLineChance
     * @return
     */
    List<HlsCusHlsCreditLineChance> queryFactoringAll(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    List<HlsCusHlsCreditLineChance> queryFactoringAllTwo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    List<HlsCusHlsCreditLineChance> createInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
    /***
     * 保理基本信息查询
     */
    List<HlsCusHlsCreditLineChance> basicInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    /***
     *评委及表决结果
     * @param hlsCusHlsCreditLineChance
     * @return
     */
    List<HlsCusHlsCreditLineChance> findFactoringMeetingInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    HlsCusHlsCreditLineChance selectCreditLineChanceVoteCountById(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);

    List<HlsCusHlsCreditLineChance> findAll(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
}