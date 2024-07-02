package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusCtLonContractChangeReq;
import com.hand.hls.fin.dto.HlsCusLonContract;


import javax.servlet.http.HttpSession;
import java.util.List;

public interface HlsCusLonContractService extends IBaseService<HlsCusLonContract>, ProxySelf<HlsCusLonContractService> {

    Integer validata(IRequest request, HlsCusLonContract lonContract);

    List<HlsCusLonContract> queryLonContract(IRequest request, HlsCusLonContract lonContract, int page, int pageSize);

    void save(IRequest iRequest, HlsCusLonContract lonContract, HttpSession session) throws HlsCusException;

    HlsCusLonContract selectLonContractFormData(IRequest request, HlsCusLonContract lonContract);

    List<HlsCusLonContract> submitLonContractToWfl(IRequest iRequest, HlsCusLonContract lonContract, HttpSession session) throws HlsCusException;

    HlsCusLonContract lonContractChangeReqCreate(IRequest request, HlsCusLonContract lonContract, HttpSession session);

    void lonContractChangeReqConfirm(IRequest request, HlsCusLonContract lonContractChangeReq, HlsCusCtLonContractChangeReq ctLonContractChangeReq);

    void cancelLonContractChangeReq(IRequest iRequest, HlsCusLonContract lonContract, HttpSession session);

    List<HlsCusLonContract> lonContractQuery(IRequest request, HlsCusLonContract lonContract, int page, int pageSize);

    List<HlsCusLonContract> lonContractChangeReqQuery(IRequest request, HlsCusLonContract lonContract, int page, int pageSize);

    void updateAbsContractShowFlag(IRequest request, HlsCusLonContract lonContract, String absShowFlag);

    void updateLonContractApproval(IRequest request, HlsCusLonContract lonContract);



    /**
     * 更新合同结清状态
     * @return
     */
    int updateContractSettleStatus();

    /**
     * 提款审批个数
     * @param hlsCusLonContract
     * @return
     */
    int selectWithdrawValidStatus(IRequest requestContext, HlsCusLonContract hlsCusLonContract);


    /**
     * 更新sourceCreditLineId
     * @return
     */
    int updateLonContractSourceCreditLineId(Long creditContractId, Long creditLineId, Long sourceCreditLineId);

    List<HlsCusLonContract> contractNumberCheck(String contractNumber);
}
