package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.ProjectCreditCondition;

import java.util.List;

public interface ProjectCreditConditionMapper extends Mapper<ProjectCreditCondition>{

    List<ProjectCreditCondition> queryAll(List<ProjectCreditCondition> list);
    List<ProjectCreditCondition> queryByPrj(ProjectCreditCondition projectCreditCondition);

    void deleteCreditConditionByProjectId(ProjectCreditCondition creditCondition);

    List<ProjectCreditCondition> selectChangeAddInfo(ProjectCreditCondition creditCondition);

    List<ProjectCreditCondition> selectChangeRemoveInfo(ProjectCreditCondition creditCondition);

    List<ProjectCreditCondition> selectChangeDiffInfo(ProjectCreditCondition creditCondition);

    List<ProjectCreditCondition> selectNotChangeInfo(ProjectCreditCondition creditCondition);

    List<ProjectCreditCondition> queryByPrjNew(ProjectCreditCondition projectCreditCondition);

    List<ProjectCreditCondition> queryOtherByPrj(ProjectCreditCondition projectCreditCondition);

    List<ProjectCreditCondition> queryConFactoringLoanCreditCondi(ProjectCreditCondition projectCreditCondition);

}