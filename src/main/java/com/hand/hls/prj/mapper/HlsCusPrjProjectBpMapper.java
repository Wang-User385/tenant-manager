package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjProjectBpMapper extends Mapper<HlsCusPrjProjectBp> {
    List<HlsCusPrjProjectBp> queryPledge(HlsCusPrjProjectBp hlsCusPrjProjectBp);
    List<HlsCusPrjProjectBp> prjProjectBpInfoQuery(HlsCusPrjProjectBp hlsCusPrjProjectBp);

    List<HlsCusPrjProjectBp> prjProjectBpQuery(HlsCusPrjProjectBp var1);

    List<Map> selectProjectBpInfo(Map map);

    List<Map> selectPledgeAndMortgagor(Map map);

    //int deleteByPrjBpIds(List<Long> prjBpIds, Long projectId);
    int deleteByPrjBpIds(Map<String, Object> map2);

    HlsCusPrjProjectBp selectByBpId(HlsCusPrjProjectBp hlsCusPrjProjectBp);

    List<Map> selectProjectBpCountMax(HlsCusPrjProjectBp hlsCusPrjProjectBp);

    List<HlsCusPrjProjectBp> prjProjectBpQueryWfl(HlsCusPrjProjectBp hlsCusPrjProjectBp);

    void deleteBpByProjectId(HlsCusPrjProject prjProject);

    List<HlsCusHlsCreditLineChanceBp> selectByForeignKey(HlsCusPrjProjectBp hlsCusPrjProjectBp);
    List<HlsCusPrjProjectBp> selecttBpByProjectIdOrderByBpCategory(HlsCusPrjProjectBp var1);
    List<HlsCusPrjProjectBp> selectByForeignKey1(HlsCusPrjProjectBp hlsCusPrjProjectBp);
    List<HlsCusPrjProjectBp> selectByForeignKey2(HlsCusPrjProjectBp hlsCusPrjProjectBp);

    /**
     * 承租人增量授信总额
     * @param bpId
     * @return
     */
    Double selectProjectBpCreditAmountTotalByBpId(@Param("bpId") Long bpId);

    /**
     * 查询项目尽调提交校验不满足承租人/应收账款债权人/资产转让方必须存在评分数据的商业伙伴
     * @param
     * @return
     */
    List<HlsCusPrjProjectBp> selectCheckProjectBp(@Param("projectId") Long projectId);
    List<HlsCusPrjProjectBp> selectByProjectId(@Param("projectId") Long projectId);

    Long getBpIdByProjectId(@Param("projectId") Long projectId);


    HlsCusPrjProjectBp selectProjectBpByBpId(@Param("bpId") Long bpId);
}