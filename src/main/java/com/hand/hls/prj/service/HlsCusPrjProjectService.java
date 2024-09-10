package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.prj.dto.*;
import com.hand.hls.utils.ResMessageException;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface HlsCusPrjProjectService extends IBaseService<HlsCusPrjProject>, ProxySelf<HlsCusPrjProjectService> {
    //合同文本
    public static final String REPORT_DOCX1 = "HLS_PRJ_PROJECT_DOCX1";
    public static final String REPORT_DOCX2 = "HLS_PRJ_PROJECT_DOCX2";
    public static final String REPORT_DOCX3 = "HLS_PRJ_PROJECT_DOCX3";
    public static final String REPORT_DOCX3_CHANGE = "REPORT_DOCX3_CHANGE";
    public static final String REPORT_DOCX4 = "HLS_PRJ_PROJECT_DOCX4";
    //附件表名
    public static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    public static final String NOT_FOUND_CONTRACT_TEMPLATE  = "找不到对应的项目尽调报告模板";
    public static final String TABLE_NAME = "table_name";
    public static final String CONTRACT_DOCX_DESCRIPTION = "项目尽调报告";
    void clonePrjProjectLnList(IRequest request, Long projectIdOld, Long projectIdNew);

    List<HlsCusPrjProject> queryPrijectAndBpNameByProjectId(IRequest request, HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProjectBp> queryPrijectBpCount(IRequest request, HlsCusPrjProjectBp hlsCusPrjProjectBp);

    void submitPrjContractChange(IRequest request, HlsCusPrjProject hlsCusPrjProject);

    /*List<HlsCusPrjProject> savePrjContractChange(IRequest request, HlsCusPrjProject hlsCusPrjProject);*/

    boolean backPrjContractChange(IRequest request, HlsCusPrjProject hlsCusPrjProject);

    List<HlsCusPrjProject> queryPrjContractChange(int page, int pageSize);


    HlsCusPrjProject prjProjectSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo);

    HlsCusPrjProject prjProjectSaveSupple(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo);

    HlsCusPrjProject prjProjectSaveWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject);

    HlsCusPrjProjectAttachment prjProjectSaveWflAttach(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment);

    //项目变更保存修改信息
    HlsCusPrjProject prjProjectChangeSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo);

    List<Map> prjHomePageGetAllStatusProjectCount(IRequest iRequest, HlsCusPrjProject prjProject);

    List<Map> prjHomePageGetAllStatusConCount(IRequest iRequest, HlsCusPrjProject prjProject);

    List<HlsCusPrjProject> prjHomePageProjectInfoGrid(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pagesize);
    List<HlsCusPrjProject> queryPrjHomePageProjectInfoGridNew(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pagesize);

    List<HlsCusPrjProject> prjHomePageProjectInfoGridSecond(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pagesize);

    List<HlsCusPrjProject> queryBpNotice(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pagesize);

    List<HlsCusPrjProject> conHomePageContractInfoGrid(IRequest iRequest, HlsCusPrjProject prjProject, int page, int pagesize);

    HlsCusPrjProject prjSubmitWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;


    HlsCusPrjProject prjSubmitSuppleWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject ,Long prjSuppleId) throws HlsCusException;



    //项目变更工作流
    HlsCusPrjProject prjChangeSubmitWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject);

    List<Map> queryPrjDetail(IRequest iRequest, HlsCusPrjProject prjProject);

    //项目专用，与合同分开
    List<Map> queryPrjDetailSecond(IRequest iRequest, HlsCusPrjProject prjProject);

    HlsCusPrjProject prjCreateVirtualCon(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject);
    HlsCusPrjProject prjCreateVirtualConFrame(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject);

    //保存chang_req
    HlsCusPrjProject changeCreate(IRequest request, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;

    HlsCusPrjProject cancelSubmit(IRequest request, HlsCusPrjProject hlsCusPrjProject);

    //根据changereq查询历史
    List<HlsCusPrjProject> historyPrjQuery(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject);

    //备份从表数据
    public void projectInfoCopy(IRequest iRequest, HlsCusPrjProject prjProject);

    //项目变更，备份从表数据
    public void projectBackUp(IRequest iRequest, HlsCusPrjProject prjProject, Boolean noticeFlag) throws HlsCusException;


    void deleteOld(Long projectIdOld, IRequest request);

    void updateOld(IRequest requestCtx, Long projectIdOld, Long projectIdNew, IRequest request) throws HlsCusException;

    void updateAtt(List<HlsCusPrjProjectAttachment> attachmentList, Long projectIdOld, IRequest requestCt);

    void deleteAtt(List<HlsCusPrjProjectAttachment> attachmentList, IRequest requestCtx);

    boolean isCreateContract(HlsCusPrjProject hlsCusPrjProject);

    boolean changeCancel(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject);

    List<HlsCusPrjProject> conSituationQuery(IRequest requestCt, HlsCusPrjProject hlsCusPrjProject, int page, int pageSize);

    String contractSignStatus(IRequest iRequest, Long projectId);

    String paymentStatus(IRequest iRequest, Long projectId);

    String paymentReqConfirmStatus(IRequest iRequest, Long projectId);

    String interestDerateStatus(IRequest iRequest, Long projectId);

    String fctContractEtStatus(IRequest iRequest, Long projectId);

    String fctContractEndStatus(IRequest requestCtx, Long projectId);

    String selectPaymentReqStatus(IRequest iRequest, HlsCusConContract hlsCusConContract);

    HlsCusPrjProject cancelProjectInfo(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject);

    void prjContractSignSubmitWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject);

    /*Double getRate(IRequest request, String invoiceProfile, String businessType);*/

    Long selectRefProjectIdByProjectId(IRequest requestContext, Long projectId);

    List<Map> queryProjectRiskReportAttachment(IRequest requestCt,HlsCusPrjProject hlsCusPrjProject,int page, int pageSize);
    List<HlsCusPrjProject> queryCreditProject(IRequest requestCt,HlsCusPrjProject hlsCusPrjProject,int page, int pageSize);

    List<CompositeMap> selectProjectTenantRecLoop(CompositeMap map, String whereStr);

    List<CompositeMap> selectProjectTenantSecLoop(CompositeMap map, String whereStr);

    List<CompositeMap> selectPrjQuotationLoop(CompositeMap map,String whereStr);

    List<CompositeMap> selectProjectGuarantorLoop(CompositeMap map, String whereStr);

    List<HlsCusPrjProject> contractInceptFinance(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception ;

    String contractInceptCheck(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception ;

    List<HlsCusPrjProject> contractSaveIncept(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception ;

    List<CompositeMap> selectProjectGuarantorNpLoop(CompositeMap map, String whereStr);
    List<HlsCusPrjProject> contractInceptDelayFinance(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception ;

    String contractInceptCheckFinance(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception ;

    List<HlsCusPrjProject> updateApprovalStatus(IRequest requestCtx, List<HlsCusPrjProject> hlsCusPrjProject) throws ResMessageException;

    /**
     * 上会时项目撤回
     * @param requestCtx
     * @param hlsCusPrjProject
     */
    void projectMeetingWithdraw(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject);

    List<CompositeMap> selectProjectQuotationNoticeLoop(CompositeMap map, String whereStr);

    List<HlsCusPrjProject> createApproveNotice(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception ;

    /**
     * 租金支付表按钮控制
     * @param iRequest
     * @param projectId
     * @return
     */
    String rentpaymentStatus(IRequest iRequest, Long projectId);

    /**
     * 付款前提条件豁免 按钮控制
     * @param iRequest
     * @param projectId
     * @return
     */
    String conditionStatus(IRequest iRequest, Long projectId);

    /**
     * 合同起租日调整 按钮控制
     * @param iRequest
     * @param projectId
     * @return
     */
    String leaseStatus(IRequest iRequest, Long projectId);


    /**
     * 头寸报备 按钮控制
     * @param iRequest
     * @param projectId
     * @return
     */
    String reportFinanceStatus(IRequest iRequest, Long projectId);

    /**
     * 合同关闭 按钮控制
     * @param iRequest
     * @param projectId
     * @return
     */
    String cancelStatus(IRequest iRequest, Long projectId);



    void checkItems(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception;


    List<Map> selectPrjBaseChangeInfo(IRequest iRequest, HlsCusPrjProject prjProject,int pagenum, int pagesize);
    //项目尽调查询 客户类型及数量以及主键
    List<CompositeMap> selectProjectBp(CompositeMap map, String whereStr);


    List<FndAttachment> reportCreateDocx(IRequest iRequest, Long projectId, String templateType, Long cashflowId) throws Exception;
    List<FndAttachment> reportCreateDocx(IRequest iRequest, HlsCusPrjProject prjProject, Long cashflowId) throws Exception;
    List<FndAttachment> reportVirCreateDocx(IRequest iRequest, Long projectId, String templateType) throws Exception;
    List<FndAttachment> reportVirCreateDocx(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception;
    List<FndAttachment> reportVirCreateDocxContent(IRequest iRequest, HlsCusPrjProject prjProject) throws Exception;
    List<HlsCusPrjProject> queryVirtualContract(HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject> prjManager(IRequest iRequest,HlsCusPrjProject hlsCusPrjProject);
    List<HlsCusPrjProject> prjManagers(IRequest iRequest,HlsCusPrjProject hlsCusPrjProject);
    List<Double> queryLeftAmount(HlsCusPrjProject hlsCusPrjProject);


    /**
     * 二期功能：投放审查管理-投放审查申请按钮：批量提交审批
     * @param iRequest 请求
     * @param list 待提交审批记录
     * @return
     */
    String signBatchWflSubmit(IRequest iRequest,List<HlsCusPrjProject> list) throws Exception;

    /**
     * 二期功能：进件投放审查退回
     * @param iRequest
     * @param list
     * @throws Exception
     */
    void prjSignReturn(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception;

    /**
     * 合同结束提交审批
     * @param iRequest
     * @param hlsCusPrjProject
     * @return
     * @throws HlsCusException
     */
    List<HlsCusPrjProject> submitContractTerminateWfl(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;

    /**
     * 项目预设附件生成
     */
    List<HlsCusPrjProjectAttachment> generatePrjBaseAttachment(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception;

    /**
     * 项目预设附件生成  根据选择的type
     */
    List<HlsCusPrjProjectAttachment> generatePrjAttachment(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject,List<String> type) throws Exception;

    List<FndAttachmentMulti> downloadAuditContent(List<PrjProjectApproval> hlsCusLonContractRepayment, IRequest requestContext, HttpServletRequest request, HttpServletResponse response)throws Exception;

    List<FndAttachment> reportVirCreateCheckDocx(IRequest iRequest, PrjCheck prjcheck) throws Exception;

    HlsCusPrjProject terminateProject(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;

    HlsCusPrjProject creditProjectGenerate(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject);

    HlsCusPrjProject prjCreateCreditCon(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;

    List<FndAttachmentMulti> downloadEtBooksContent(List<HlsCusPrjProject> project, IRequest requestContext, HttpServletRequest request, HttpServletResponse response);

    void projectConditionImport(IRequest iRequest, Long headerId,Long approvId, Long projectId, String approvalType);
    /**
     * 进件资料补充
     */
    List<Map> prjSupplementQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize);

    //额度增量申请
    HlsCusPrjProject projectAmountAllocateCreate(IRequest request, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;

    HlsCusPrjProject projectAmountAllocateSubmit(IRequest iRequest, HlsCusPrjProject prjProject);
    //excel批量导入
    void  excelBatchImport(IRequest iRequest,Long headerId, String division) throws HlsCusException;
    /**
     * 进件生成签约附件清单
     */
    void generateProjectSignAttach(IRequest iRequest, Long projectId) throws HlsCusException;
    /**
     * 厂商起租规则查询
     */
    List<HlsBpMasterInceptRule> queryManufacturerInceptType(HlsCusBpMaster hlsBpMaster);
    List<FndAttachmentMulti> contextCreateMultiple(IRequest request, List<Long> list, HttpServletResponse response) throws Exception;
    List<HlsCusPrjProject> manufacturerQueryProductInfo2(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject, int pageNum, int pageSize);
    List<HlsCusPrjProject> manufacturerQueryProductInfo3(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject, int pageNum, int pageSize);
    List<HlsEmployee> selectSalesByEmployeeName(IRequest iRequest, String name, int pageNum, int pageSize);

    /**
     * 检查租赁物编号是否重复
     * @param itemNumber 车架号或者整机编号
     * @param division 业务线
     * @param contractNumber 合同编号，不与自身校验
     * @param classifyId 规格型号
     * @return 重复校验结果，空为通过
     */
    String allLeaseItemCheck(String itemNumber,String division, String contractNumber,String classifyId);
    /**
     *当前登录用户信息获取
     * @return
     */
    List<Map> queryUserInfo(IRequest iRequest, Map params);
    List<Map> queryListForRpt(IRequest requestContext, Map<String, Object> project, int pagenum, int pagesize);

    /**
     * 进件审批历史流程查询
     */
    List<Map> prjProcessInfoQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize);

    /***
     * 保理立项提交审批
     * @param hlsCusPrjProject
     * @param requestCtx
     * @return
     */
    List<HlsCusPrjProject> submit(HlsCusPrjProject hlsCusPrjProject, IRequest requestCtx);

    /**
     * 保理合同审批提交
     */
    List<HlsCusPrjProject> submitVirtualFactoringWfl(HlsCusPrjProject hlsCusPrjProject, IRequest requestCtx);

    List<FndAttachmentMulti> contextFactoringCreateMultiple(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject, String templateCode, HttpServletResponse response) throws Exception;
}
