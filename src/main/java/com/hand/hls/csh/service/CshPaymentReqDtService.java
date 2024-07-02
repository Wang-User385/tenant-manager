//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.utils.ResMessageException;
import java.util.List;

public interface CshPaymentReqDtService extends IBaseService<HlsCusCshPaymentReqDt>, ProxySelf<CshPaymentReqDtService> {
    List<HlsCusCshPaymentReqDt> queryCshPaymentReqDt(IRequest var1, HlsCusCshPaymentReqDt var2, int var3, int var4);
    /**
     * 删除抵扣数据
     * @param hlsCusCshPaymentReqDt
     */
    void deleteDeductInfo(HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt);

    List<HlsCusCshPaymentReqDt> cshPaymentReqDtCreate(IRequest var1, List<HlsCusCshPaymentReqDt> var2) throws ResMessageException;
    List<HlsCusCshPaymentReqDt> cshPaymentReqDtSave(IRequest iRequest, List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList) throws ResMessageException;
    List<HlsCusCshPaymentReqDt> cshPaymentReqDtDelete(IRequest iRequest, List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList) throws ResMessageException;}
