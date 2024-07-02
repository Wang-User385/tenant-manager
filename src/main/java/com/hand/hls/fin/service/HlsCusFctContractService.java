package com.hand.hls.fin.service;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctContract;
import com.hand.hls.fct.dto.HlsCusFctPkg;
import com.hand.hls.fct.dto.HlsCusFctProject;


import java.util.List;

public interface HlsCusFctContractService extends IBaseService<HlsCusFctContract>, ProxySelf<HlsCusFctContractService> {

    List<HlsCusFctProject> selectContract(IRequest requestContext, HlsCusFctProject hlsCusFctProject, Integer page, Integer pageSize);

    /**
     * 根据项目Id查询放款申请状态，然后根据状态返回  [todo][undo]或者[read]
     * @param iRequest
     * @param projectId
     * @return
     */
    String paymentStatus(IRequest iRequest, Long projectId);

    /**
     * 合同起贷的状态
     * @param iRequest
     * @param projectId
     * @return
     */
    String paymentReqConfirmStatus(IRequest iRequest, Long projectId);

    String interestDerateStatus(IRequest iRequest, Long projectId);

    String fctContractEtStatus(IRequest iRequest, Long projectId);

    String fctContractEndStatus(IRequest requestCtx, Long projectId);

    List<HlsCusFctContract> selectCshPaymentReq(IRequest iRequest, HlsCusFctContract dto, int page, int pageSize);

    /**
     * 保理合同放款申请汇总查询
     * @param iRequest
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusFctContract> selectCshPaymentReqForSummary(IRequest iRequest, HlsCusFctContract dto, int page, int pageSize);

    /**
     * 合同变更-复制数据
     * @param iRequest
     * @param hlsCusFctPkg
     * @return
     */
    HlsCusFctContract conPaymentChange(IRequest iRequest, HlsCusFctPkg hlsCusFctPkg) throws HlsCusException;

    /**
     * 合同-变更：追加担保提交
     * @param iRequest
     * @param hlsCusFctProject
     */
    void fctConChangeSubmitWfl(IRequest iRequest, HlsCusFctProject hlsCusFctProject);

    /**
     * 合同-变更，回款账户变更，提前结清
     * @param iRequest
     * @param hlsCusFctContract
     */
    void fctConChangeSubmitWfl(IRequest iRequest, HlsCusFctContract hlsCusFctContract) throws HlsCusException;

    List<HlsCusFctContract> selectFctContractDetail(HlsCusFctContract hlsCusFctContract);

    HlsCusFctContract fctContractSave(IRequest request, HlsCusFctPkg hlsCusFctPkg) throws HlsCusException;

    HlsCusFctContract fctPaymentSave(IRequest request, HlsCusFctPkg hlsCusFctPkg);

    void ctPrintSave(IRequest request, HlsCusFctPkg hlsCusFctPkg);

    void calcFctConFinIncome(IRequest request, HlsCusFctContract hlsCusFctContract);



    void fctMSendMail(String subject, StringBuffer content, String receivers, IRequest requestCtx, HlsCusFctProject hlsCusFctProject);

    HlsCusFctContract selectByProjectId(Long projectId);

    /**
     * 合同签约右侧栏状态控制
     * @param iRequest
     * @param projectId
     * @return
     */
    String contractSignStatus(IRequest iRequest, Long projectId);

    //公司投放统计
    List<HlsCusFctContract> queryFctCompanyInceptInfo(IRequest iRequest, HlsCusFctContract dto, int page, int pageSize);

    //项目回款情况
    List<HlsCusFctContract> queryContractReciptInfo(IRequest iRequest, HlsCusFctContract dto, Integer page, Integer pageSize);

    /**
     * 根据projectId查询放款申请状态
     * @param iRequest
     * @param hlsCusFctContract
     * @return
     */
    String selectPaymentReqStatus(IRequest iRequest, HlsCusFctContract hlsCusFctContract);

    /**
     * 根据 合同Id，放款信息行中的单据类别，以及放款信息头的单据类别来查询一个唯一的放款信息行ID
     * @param iRequest
     * @param contractId
     * @param sourceDocCategory
     * @param documentCategory
     * @return
     */
    Long selectPaymentReqLnIdByContractId(IRequest iRequest, Long contractId, String sourceDocCategory, String documentCategory);



    /**
     * 保理合同起贷工作流
     */
    HlsCusFctContract loanConfirmSubmitWfl(IRequest iRequest, HlsCusFctPkg hlsCusFctPkg);

    /**
     * 保理合同变更-提前结清计算剩余利息
     * @param iRequest
     * @param hlsCusFctPkg
     * @return
     */
    Double preCleanCalcResidualInterest(IRequest iRequest, HlsCusFctPkg hlsCusFctPkg);

    int insertByHand(HlsCusFctContract hlsCusFctContract);

    /**
     * 根据projectId更新合同的状态
     * @param hlsCusFctContract
     */
    void updateContractStatusByProjectId(HlsCusFctContract hlsCusFctContract);


    void createContractDocumentFile(IRequest iRequest, HlsCusFctContract hlsCusFctContract)  throws HlsCusException, FileReadIOException;

}