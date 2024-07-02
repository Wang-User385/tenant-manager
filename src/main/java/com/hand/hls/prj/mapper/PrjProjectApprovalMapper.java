//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectParam;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PrjProjectApprovalMapper extends Mapper<PrjProjectApproval> {
    List<Map> query();

    List<PrjProjectApproval> queryAll(PrjProjectApproval prjProjectApproval);

    List<PrjProjectApproval> queryAllChange(PrjProjectApproval prjProjectApproval);

    List<PrjProjectApproval> queryReplyAll(PrjProjectApproval prjProjectApproval);

    List<PrjProjectApproval> conQueryAll(PrjProjectApproval prjProjectApproval);

    void updateApprovalStatus(@Param("projectId") Long projectId, @Param("dataClass") String dataClass);

    List<PrjProjectApproval> queryAuditInfo(HlsCusPrjProjectParam hlsCusPrjProject);

    List<PrjProjectApproval> queryAuditAdjust(ProjectApprovalCondition projectApprovalCondition);

    List<PrjProjectApproval> queryAuditDiscuss(ProjectApprovalCondition projectApprovalCondition);

    void updateApprovalResult(PrjProjectApproval projectApproval);
}
