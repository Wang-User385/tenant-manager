package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConContractCashflow;
import com.hand.hls.csh.dto.CshPaymentReqHd;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import di.dto.QueryOrder;
import di.dto.RepayPlanTermInfoDTO;
import org.apache.ibatis.annotations.Param;
import uncertain.composite.CompositeMap;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusPrjProjectMapper extends Mapper<HlsCusPrjProject> {
    /**
     * 查询项目的详细信息
     * @param hlsCusPrjProject 该对象中的projectId的值必须有，不能为空
     * @return 返回空或者只有一条记录的集合
     */
    List<HlsCusPrjProject> queryPrijectAndBpNameByProjectId(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProjectBp> queryPrijectBpCount(HlsCusPrjProjectBp hlsCusPrjProjectBp);
    //查询可以创建合同变更的合同
    List<HlsCusPrjProject> queryPrjContractChange();

    //项目立项首页环状图
    List<Map> prjHomePageGetAllStatusProjectCount(HlsCusPrjProject prjProject);

    //合同管理首页环状图
    List<Map> prjHomePageGetAllStatusConCount(HlsCusPrjProject prjProject);
    //项目立项首页grid
    List<HlsCusPrjProject> prjHomePageProjectInfoGrid(HlsCusPrjProject prjProject);
    List<HlsCusPrjProject> queryPrjHomePageProjectInfoGridNew(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryCreditAmt(@Param("tenantId") Long tenantId,@Param("leaseStartDate") Date leaseStartDate);

    Double queryUsedFinanceAmountRevolving(@Param("definitionId") Long definitionId);

    Double queryUsedFinanceAmountNonRevolving(@Param("definitionId") Long definitionId);


    List<HlsCusPrjProject> prjHomePageProjectInfoGridSecond(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> prjProjectSupplement(HlsCusPrjProject prjProject);

    // 财信客户动态
    List<HlsCusPrjProject> queryBpNotice(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> conHomePageContractInfoGrid(HlsCusPrjProject prjProject);

    List<Map> queryPrjDetail(HlsCusPrjProject prjProject);

    //项目专用，与合同分开
    List<Map> queryPrjDetailSecond(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryPrjDetailDelayRent(HlsCusPrjProject prjProject);

//根据chang_req_id查询历史历史project_id
    List<HlsCusPrjProject> historyPrjQuery(HlsCusPrjProject hlsCusPrjProject);

    int queryIsCreateContract(HlsCusPrjProject hlsCusPrjProject);

    List<Map> queryActivitiVariable(String queryActivitiVariable);

    List<HlsCusPrjProject> conSituationQuery(Map<String, Object> map);

    String getLeasingContractMaxNumber(HlsCusPrjProject hlsCusPrjProject);

    String getQuatationContractMaxNumber(HlsCusPrjProject hlsCusPrjProject);

    String getBpNameByProjectTenantId(HlsCusPrjProject hlsCusPrjProject);

    //todo 君成数据都是1,后面修改sql
    String selectLeasingNumber(Long companyId);

    /**
     * 根据项目Id查询 RefProject
     * @param projectId
     * @return
     */
    Long selectRefProjectIdByProjectId(Long projectId);

    List<Map> queryProjectAttachment();

    List<Map> selectChangeReqInfo();

    List<Map> queryProjectRiskReportAttachment(HlsCusPrjProject hlsCusPrjProject);

    List<CompositeMap> selectProjectTenantRecLoop(String tenant_sec_id);

    List<CompositeMap> selectProjectBpTabLoop(Long bpCount);

    List<HlsCusPrjProject> queryRentQuatation(HlsCusPrjProject prjProject);


    List<HlsCusPrjProject> queryLeaseDateInfo(HlsCusPrjProject prjProject);


    List<HlsCusPrjProject> queryRentQuatationDelay(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryMeetingApproval(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryApproveNotice(HlsCusPrjProject prjProject);
    List<HlsCusPrjProject> queryVetoNotice(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryApproveNoticeBp(HlsCusPrjProject prjProject);
    List<HlsCusPrjProject> queryVetoNoticeResult(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject>  selectProjectQuotationNoticeLoop(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryApprovePaymentCondition(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryApproveManageRequire(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryApproveRiskClass(HlsCusPrjProject prjProject);


    List<HlsCusPrjProject> queryApproveCreditPeriod(HlsCusPrjProject prjProject);
    List<HlsCusPrjProject> queryApproveCreditPeriodHistory(HlsCusPrjProject prjProject);

    List<Map> prjInfoSelectForConCreate(HlsCusPrjProject prjProject);


    List<Map> searchProjectQuery(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryVetoNoticeDefault(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> queryMeetingWflColomn(HlsCusPrjProject prjProject);

    String queryNoticeNumber(Long unitId);

    List<HlsCusPrjProject> queryContractStamp(HlsCusPrjProject hlsCusPrjProject);

    String queryEnableByAllocation(HlsCusPrjProject hlsCusPrjProject);

    List<HlsCusPrjProject> prjHistoryHomeGrid(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> rptProjectInfoQuery(HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> selectProjectChangeReqInfo(HlsCusPrjProject prjProject);

    List<Map> selectPrjBaseChangeInfo(HlsCusPrjProject prjProject);

    //项目尽调首页查询 prjHomePageProjectInfoGridHome
    List<HlsCusPrjProject> prjHomePageProjectInfoGridHome(HlsCusPrjProject prjProject);
    Long queryAssistUnitId(Long assistProjectManager);

    List<HlsCusPrjProject> queryAllApproveContract(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject> queryVirtualContract(HlsCusPrjProject var1);
    List<HlsCusPrjProject> prjManager(Long var1);
    List<HlsCusPrjProject> prjManagers(Long var1);

    /**
     * 查询合同的剩余可确认投放计划金额
     * @param projectId 合同ID
     * @return
     */
    Double queryLeaseItemAmount(@Param("projectId") Long projectId);

    List<HlsCusPrjProject> prjManager(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject> prjManagers(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject> conRentQueryAll(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject>conRentEmailQueryAll(Long tenantId);
    List<HlsCusPrjProject>conRentEmailQueryAllW(Long projectId);
    List<HlsCusPrjProject>conRentQueryAll1(Long days);
    List<HlsCusPrjProject>conRentQueryAll2();
    List<HlsCusPrjProject> prjCheckContract(HlsCusPrjProject hlsCusPrjProject);
    /**
     * 查询逾期的合同
     * @param
     * @return
     */
    List<HlsCusPrjProject> conRentQueryAll3();
    Double queryLeftAmount(@Param("projectId") Long projectId);

    /**
     * 查询已经结束的合同
     * @param hlsCusPrjProject
     * @return
     */
    List<HlsCusPrjProject> queryContractTerminateGrid(HlsCusPrjProject hlsCusPrjProject);
    String queryApprovalNumber(@Param("projectId") Long projectId);
    void updatePrjTenant(@Param("bpId") Long bpId,@Param("projectId") Long projectId);
    List<HlsCusPrjProject> queryContractEt(HlsCusPrjProject hlsCusPrjProject);

    /**
     * 查询项目信息
     * @param projectId
     * @return
     */
    HlsCusPrjProject queryPrjProjectBasicInfo(@Param("projectId") Long projectId);

    List<Map> queryPrjDetailNew(HlsCusPrjProject hlsCusPrjProject);
    List<Map> queryPrjDetailNew1(HlsCusPrjProject hlsCusPrjProject);

    void updateWflProject(HlsCusPrjProject hlsCusPrjProject);

    List<Map> selectChangeReqInfoWfl();
    /**
     * 进件资料补充
     */
    List<Map> prjSupplementQuery(Map<String, Object> prjProject);

    List<Map> prjSignEntranceQuery();
    String getManufacturerCode(@Param("contractNumber")String contractNumber);
    String selectManufacturerCodeByPrimaryKey(@Param("projectId")Long projectId);
    /**
     * 查询产品线
     * @param contractNumber 合同号或进件号
     * @return 产品线
     */
    String getDivision(@Param("contractNumber")String contractNumber);
    Double selectLeaseItemAmount(HlsCusPrjProject project);
    String selectTenantNameByProject(HlsCusPrjProject hlsCusPrjProject);
    public HlsCusPrjProject selectPrjById(Long projectId);

    Map queryBaseRateInfo(HlsCusPrjProject hlsCusPrjProject);

    Map prjRpLModifyDetailQuery(HlsCusPrjProject hlsCusPrjProject);

    Map prjProjectQuotationQuery(HlsCusPrjProject hlsCusPrjProject);

    Map prjRpLModifyQuery(HlsCusPrjProject hlsCusPrjProject);

    Map prjProjectBpTenantQuery1(HlsCusPrjProject hlsCusPrjProject);

    List<Map> prjRpModifyEntranceQuery(Map prjRpModifyMap);
    List<Map> prjRpModifyEntranceQuery1(Map prjRpModifyMap);

    void saveDescription(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject> manufacturerQueryProductInfo2(HlsCusPrjProject hlsCusPrjProject);
    void updateStatus(HlsCusPrjProject hlsCusPrjProject);

    String checkLeaseItemCheck(@Param("itemNumber")String itemNumber,@Param("columnName")String columnName
            ,@Param("contractNumber")String contractNumber,@Param("classifyId")String classifyId);
    /**
     * 当前登录用户信息获取
     */
    List<Map> queryUserInfo(String userId);

    List<Map> queryListForRPT(Map<String, Object> project);

    List<Map> prjProcessExportQuery(Map<String, Object> prjProject);

    Long queryCreditAssistProjectManager(Long tenantId);

    List<HlsCusPrjProject> selectProjectByIdCardNo(String idCardNo);

    HlsCusPrjProject selectProjectByOrderNo(String orderNo);

    CshPaymentReqHd selectPaymentByOrderNo(String orderNo);

    QueryOrder selectQueryOrderByOrderNo(String orderNo);

    List<RepayPlanTermInfoDTO> selectRepayPlanByOrderNo(String orderNo);
}
