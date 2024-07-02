package com.hand.hls.cont.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.csh.dto.CshDepositDeductReqLn;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.sys.dto.DocumentHistoryData;
import com.hand.hls.utils.ResMessageException;
import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Map;

import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;

public interface IConContractChangeReqService extends IBaseService<HlsCusConContractChangeReq>, ProxySelf<IConContractChangeReqService> {
    Boolean getIfQuotationPendding(IRequest var1, HlsCusConContract var2);

    ResponseData backUp(IRequest var1, DocumentHistoryData var2, LeafRequestData var3, String var4) throws ResMessageException;

    List<HlsCusConContractChangeReq> submit(IRequest var1, HlsCusConContractChangeReq var2) throws ParameterNullException;


    /**
     * 大单提前部分还本 - 保存/下一步 按钮逻辑
     * @param request
     * @param cusConContractChangeReq
     * @return
     * @throws ParameterNullException
     */
    List<HlsCusConContractChangeReq> submitPartialPrepayment(IRequest request, HlsCusConContractChangeReq cusConContractChangeReq) throws ParameterNullException;

    /**
     * 大单提前部分还本 - 计算 按钮逻辑
     * @param iRequest
     * @param documentId 变更申请ID
     * @param quotationId 报价ID
     * @param param
     * @return
     * @throws Exception
     */
    ResponseData recalculatePartialPrepayment(IRequest iRequest, Long documentId, Long quotationId, JSONObject param) throws Exception;

    /**
     * 大单提前部分还本 - 取消申请 按钮逻辑
     * @param iRequest
     * @param changeReqId
     * @throws NoSuchObjectException
     */
    void cancelChangeReqPartialPrepayment(IRequest iRequest, Long changeReqId) throws NoSuchObjectException;

    /**
     * 大单提前部分还本 - 提交审批 按钮逻辑
     * @param iRequest
     * @param changeReq
     * @throws Exception
     */
    void approvePartialPrepaymentWfl(IRequest iRequest, HlsCusConContractChangeReq changeReq) throws Exception;

    /**
     * 大单提前部分还本 - 填写提前还本日，带出变更起始期数
     * @param iRequest
     * @param changeReq
     * @return
     * @throws ResMessageException
     */
    Long checkTimesPartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq) throws ResMessageException;

    /**
     * 大单提前部分还本 - 修改变更起始期数，检验提前还本日是否在本期数与上一期日期之间
     * @param iRequest
     * @param changeReq
     * @return
     */
    boolean checkDatePartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq);

    /**
     * 大单提前部分还本 - 变更后的租金总和与该合同保证金比较大小：保证金 > 变更后的租金总和 返回 Y ；保证金 <= 变更后的租金总和 返回 N
     * @param iRequest
     * @param changeReq
     * @return
     */
    String checkDepositPartialPrepayment(IRequest iRequest, HlsCusConContractChangeReq changeReq);

    /**
     * 大单提前部分还本 - 审批结束 处理逻辑
     * @param iRequest
     * @param result
     * @param changeReq
     * @param processInstanceId
     */
    void changeReqPartialPrepaymentApproved(IRequest iRequest, String result, HlsCusConContractChangeReq changeReq, String processInstanceId) throws NoSuchObjectException;

    void approveWfl(IRequest var1, HlsCusConContractChangeReq var2) throws Exception;

    ResponseData recalculate(IRequest var1, Long var2, Long var3, JSONObject var4) throws Exception;

    void cancelChangeReq(IRequest var1, Long var2) throws NoSuchObjectException;

    List<Map<String, Object>> getDatas(Long var1, Long var2, List<HlsCusPrjQuotation> var3) throws ParameterNullException;

    void leaveHistory(IRequest var1, HlsCusConContractChangeReq var2, Long var3) throws Exception;

    String queryDocumentSheets(IRequest var1, Long var2, String var3, Long var4) throws NoSuchObjectException, ResMessageException;

    String querySheetByQuotation(IRequest var1, Long var2, String var3, Long var4);

    ResponseData queryContractBp(IRequest var1, LeafRequestData var2);

    List<HlsCusBpMaster> queryTenantBeforeChange(IRequest iRequest, Long changeReqId) throws NoSuchObjectException;

    /**
     * 变更结束
     */
    String contractChange(IRequest iRequest, String result, HlsCusConContractChangeReq changeReq, String processInstanceId);

    /**
     * 变更同步借据
     */
    void contractChangeApproved(IRequest iRequest, String changeType, String depositDeductFlag, Long contractId);

    /**
     * 合同变更审批结束接口逻辑封装
     */
    void dealInterfaceWithContractChangeApproved(IRequest iRequest, Long contractId, String changeType, String depositDeductFlag, Long changeReqId);
    /**
     * 版本迭代
     * 基于现有的版本加1
     */
    void plusVersionWithOne(IRequest iRequest, Long contractId, String changeType);

    /**
     * 变更前租赁物信息
     */
    List<JSONObject> prevContractLeaseItem(IRequest iRequest, Long changeReqId) throws NoSuchObjectException;

    /**
     * 变更抵扣现金流
     */
    void contractChangeDeposit(IRequest iRequest, Long contractId, String changeType, String depositDeductFlag);

    void autoWriteOffDeposit(IRequest iRequest,String flag,Long contractId,String changeType) throws HlsCusException;

    /**
     * 设置完全核销本息金额
     * @param cashflow 现金流
     * @param lnList 抵扣行
     * @return 现金流
     */
    HlsCusConContractCashflow calcFullCashflow(HlsCusConContractCashflow cashflow, List<CshDepositDeductReqLn> lnList);

    /**
     * 设置部分核销本息金额
     * @param cashflow 现金流
     * @param lnList 抵扣行
     * @param returnAmount 可退金额
     * @return 现金流
     */
    HlsCusConContractCashflow calcCashflow(HlsCusConContractCashflow cashflow, List<CshDepositDeductReqLn> lnList, Double returnAmount);

    boolean validata(IRequest requestCtx, Long contractId, Double maxAmount);
}
