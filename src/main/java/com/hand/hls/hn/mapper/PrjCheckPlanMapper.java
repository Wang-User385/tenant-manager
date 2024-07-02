package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.hn.dto.PrjCheckPlan;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PrjCheckPlanMapper extends Mapper<PrjCheckPlan>{
    void insertPrjCheckPlan(PrjCheckPlan prjCheckPlan);
    List<PrjCheckPlan> selectPrjCheckPlan(PrjCheckPlan prjCheckPlan);
    List<PrjCheckPlan> selectPrjCheckPlan();
    Date selectMaxCheckDate(Long bpId);
    void updatePrjCheckPlan(PrjCheckPlan prjCheckPlan);

    List<PrjCheckPlan> selectPrjCheckPlanByContractId(PrjCheckPlan prjCheckPlan);

    /**
     * 查询已经审批通过的租后检查计划
     * @return
     */
    List<PrjCheckPlan> selectAchievedPrjCheckPlan();

    /**
     * 更新项目ID为projectId下的所有的合同检查计划为已完成
     * @param projectId 项目Id
     * @param prjRegularAssign 类型
     */
    void updateAchievedPrjCheckPlanByProject(@Param("projectId") Long projectId,@Param("prjRegularAssign") String prjRegularAssign,@Param("actualFinishDate") Date actualFinishDate);

    /**
     * 更新项目ID为bpId下的所有的合同检查计划为已完成
     * @param bpId 客户Id
、     */
    void updateAchievedPrjCheckPlanByBpId(@Param("bpId")Long bpId, @Param("actualFinishDate")Date actualFinishDate);
}