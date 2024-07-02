package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractBp;
import com.hand.hls.fnd.service.HlsCusImportInterface;

import java.util.List;
import java.util.Map;

public interface HlsCusConContractBpService extends IBaseService<HlsCusConContractBp>, ProxySelf<HlsCusConContractBpService>, HlsCusImportInterface {

    List<Map> queryByContractId(HlsCusConContractBp bp, int pageNum, int pageSize);

    /**
     * 二期功能：进件投放审查通过后，复制项目bp
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */

    List<HlsCusConContractBp> saveConContractBpFromPrj(IRequest iRequest, Long contractId, Long projectId);

    /**
     * 二期功能：付款申请创建-付款对象银行信息Lov
     * @param iRequest
     * @param bp
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<HlsCusConContractBp> queryPaymentBpBankInfoLov(IRequest iRequest, HlsCusConContractBp bp, int pageNum, int pageSize);


}