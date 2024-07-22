package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ast.dto.VirtualConContractLov;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractRentPaymentConfirm;
import com.hand.hls.cont.dto.HlsCusContractPkg;
import com.hand.hls.cont.dto.HlsCusContractTermination;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.utils.ResMessageException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 19:27
 * @description 因依赖里面与原本的光大环境有出入 这里直接创建一个
 */
public interface HlsCusConContractService extends IBaseService<HlsCusConContract>, ProxySelf<HlsCusConContractService> {
    /**
     * queryConContractByKey
     *
     * @param iRequest IRequest
     * @param hlsCusConContract HlsCusConContract
     * @return Result<select value by key>
     */
    HlsCusConContract queryConContractByKey(IRequest iRequest, HlsCusConContract hlsCusConContract);



    /**
     * 合同变更租金支付表lov
     */
    List<Map<String, Object>> queryPaymentChangeInfoLov(IRequest request, HlsCusConContract hlsCusConContract, int page, int pageSize);
    /**
     * 提交提前还款工作流
     */
    boolean submitConContractPreRepaymentChange(IRequest request, HlsCusConContract hlsCusConContract);

    /**
     * 提交租金计划变更工作流
     */
    boolean submitConContractRentplanChange(IRequest request, HlsCusConContract hlsCusConContract);

    /**
     * 提交变更工作流
     */
    boolean submitConContractChange(IRequest request, HlsCusConContract hlsCusConContract);

    /**
     * 保存合同变更
     */
    boolean saveConContractChange(IRequest request, HlsCusConContract hlsCusConContract);

    /**
     * 取消回款账户，提前还款，租金计划变更
     */
    boolean backConContractChange(IRequest request, HlsCusConContract hlsCusConContract);

    /**
     * @param hlsCusConContract 根据该对象的refContractId查询变更信息
     */
    HlsCusChangeReqInfo getChangeReqInfo(IRequest request, HlsCusConContract hlsCusConContract);

    //合同管理首页环状图
    List<Map> conHomePageGetAllStatusContractCount(IRequest iRequest, HlsCusConContract conContract);
    //合同管理首页grid
    List<HlsCusConContract> conHomePageContractInfoGrid(IRequest iRequest, HlsCusConContract conContract, int page, int pageSize);

    //合同信息保存和更新
    List<HlsCusConContract> conContractSave(IRequest iRequest, List<HlsCusPrjQuotation> hlsCusPrjQuotationList);

    HlsCusPrjProject conContractSubmit(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws HlsCusException;

    String paymentTableMakeStatus(IRequest iRequest, Long projectId);

    String paymentReqStatus(IRequest iRequest, Long projectId);

    String paymentTableConfirmStatus(IRequest iRequest, Long projectId);

    String conContractChangeStatus(IRequest iRequest, Long projectId);

    String conContractEtStatus(IRequest iRequest, Long projectId);

    String conContractCancelStatus(IRequest iRequest, Long projectId);

    List<HlsCusConContract> queryPaymentInfoList(IRequest iRequest, HlsCusConContract conContract, int page, int pageSize);

    void calcConFinIncome(IRequest iRequest, HlsCusConContract conContract);

    List<Map<String, Object>> selectContractForABS(IRequest request, HlsCusConContract hlsCusConContract, int page, int pagesize);

    List<Map<String, Object>> calcContractAmount(List<Map<String, Object>> maps, LocalDate baseDate, LocalDate dueDateBegin, IRequest request);

    List<HlsCusConContract> selectConCshPaymentReqForSummary(IRequest iRequest, HlsCusConContract dto, int page, int pageSize);


    List<HlsCusConContract> conContractCshReqDetail(HlsCusConContract dto);

    void conContractValidate(IRequest request, HlsCusContractPkg hlsCusContractPkg) throws HlsCusException;
    HlsCusConContract conContractSave(IRequest request, HlsCusContractPkg hlsCusContractPkg);

    void conLoanSubmit(IRequest iRequest, Long paymentReqId) throws ResMessageException;
    void conPaymentSubmit(IRequest iRequest, Long paymentReqId) throws ResMessageException;

    void contractUpdateCashflowDueDate(IRequest iRequest, HlsCusConContract hlsCusConContract) throws IllegalArgumentException;

    void conInceptSubmit(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException;

    void conInceptSave(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException;

    HlsCusConContract selectByProjectId(Long projectId);
    Integer updateByProjectId(HlsCusConContract hlsCusConContract);

    HlsCusConContract conContractEndSubmitWfl(IRequest requestCtx, HlsCusContractTermination hlsCusContractTermination) throws HlsCusException;

    HlsCusConContract conContractChangeSave(IRequest request, HlsCusContractPkg hlsCusContractPkg) throws HlsCusException;

    void contractGenerate(IRequest request, HlsCusPrjProject dto);

    //void calcOperatingLeaseFinIncome(IRequest iRequest, HlsCusConContract conContract);
    HlsCusConContract conInceptSave(IRequest request, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws HlsCusException;

    HlsCusPrjProject conContractChangeSubmit(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws HlsCusException;

    Long getRentingFrequency(String rentingFrequency);

    void calcConFinIncomeSpecialGd(IRequest iRequest, HlsCusConContract conContract);

    List<HlsCusConContract> queryInsureContractList(IRequest iRequest, HlsCusConContract dto, int page, int pageSize);

    List<HlsCusConContract> createContractBatchByContract(IRequest iRequest,List<HlsCusConContract> list) throws Exception;

    void deleteContract(IRequest iRequest,List<HlsCusConContract> list);

    String exitCheckBp(IRequest iRequest, List<HlsCusConContract> list) throws Exception ;


    List<VirtualConContractLov> queryVirtualContractLov(IRequest iRequest, VirtualConContractLov virtualConContractLov, int page, int pageSize);

    /**
     * 创建投放计划报价
     * @param iRequest
     * @param list
     * @return
     * @throws Exception
     */
    List<HlsCusPrjQuotation> createContractPlanByContract(IRequest iRequest,List<HlsCusPrjQuotation> list) throws Exception;

    /**
     * 删除投放计划报价
     * @param iRequest
     * @param list
     */
    void deleteContractPlan(IRequest iRequest,List<HlsCusPrjQuotation> list);

    /**
     * 确认投放计划报价
     * @param iRequest
     * @param list
     */
    void confirmContractPlan(IRequest iRequest,List<HlsCusPrjQuotation> list);

    /**
     * 支付表确认新建
     * @param iRequest
     * @param conContractRentPaymentConfirm
     */
    List<HlsCusConContractRentPaymentConfirm> createContractConfirm(IRequest iRequest, HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm) throws HlsCusException;

    /**
     * 支付表确认提交审批
     * @param iRequest
     * @param conContractRentPaymentConfirm
     */
    List<HlsCusConContractRentPaymentConfirm> submitContractConfirmWfl(IRequest iRequest, HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm) throws HlsCusException;

    /**
     * 支付表确认审批通过
     * @param iRequest
     * @param conContractRentPaymentConfirm
     */
    List<HlsCusConContractRentPaymentConfirm> submitContractConfirmApproved(IRequest iRequest, HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm) throws Exception;

    /**
     * 校验已经核销的现金流是否更改
     * @param iRequest
     * @param paymentConfirmId
     * @throws HlsCusException
     */
    void checkCashflow (IRequest iRequest,Long paymentConfirmId) throws HlsCusException;

    /**
     * 校验采购合同是否在审批中
     * @param iRequest
     * @param paymentReqId
     * @throws HlsCusException
     */
    void validatePurchaseContractStatus(IRequest iRequest,Long paymentReqId) throws HlsCusException;

    List<HlsCusConContract> selectStampDutyLease(HlsCusConContract dto) throws HlsCusException;

    List<HlsCusConContract> selectRiskFundAccrual(HlsCusConContract dto);

    void conInceptconfirm(IRequest iRequest, Map paraMap) throws HlsCusException;

    void updateCshDate(IRequest iRequest, Map paraMap) throws HlsCusException;

    void updateQuotationInfo(IRequest iRequest, Map paraMap) throws HlsCusException;

    void conTerminateConfirm(IRequest iRequest, Map paraMap) throws HlsCusException;

    void conContractChangeSubmitWfl(IRequest requestCtx, HlsCusPrjProject dto);

    boolean contractChangeRepaymentCalculate(IRequest req, HlsCusConContract hlsCusConContract);

    boolean contractChangeEtCalculate(IRequest req, HlsCusConContract hlsCusConContract);

    void conContractChangeCancel(IRequest requestCtx, HlsCusPrjProject dto);

    boolean compareinfoGenerate(IRequest req, HlsCusConContract hlsCusConContract);

    /**
     * 从老报价复制到新报价
     *
     * @param iRequest
     * @param quotationId               老报价的quotationID
     * @param newSourceDocumentId       新报价的SourceDocumentId
     * @param newSourceDocumentCategory 新报价的SourceDocumentCategory
     * @param newDataClass              新报价的DataClass
     * @return
     */
    HlsCusPrjQuotation copyQuotationRelated(IRequest iRequest, Long quotationId, Long newSourceDocumentId, String newSourceDocumentCategory, String newDataClass);
    /**
     * 根据合同编号查找合同
     */
    List<HlsCusConContract> queryContractIdByContractNumber(String contractNumber);
    /**
     * 进件合同起租列表页面查询
     */
    List<HlsCusConContract> queryContractInceptInfoMain(IRequest iRequest, HlsCusPrjProject dto, int page, int pageSize);

}
