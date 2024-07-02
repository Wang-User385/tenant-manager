package com.hand.hls.eft.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.exception.HlsCusException;

import java.util.List;


public interface HlsCusFundTransferService extends IBaseService<HlsCusFundTransfer>, ProxySelf<HlsCusFundTransferService> {


    /**
     * 已办查询
     * @param iRequest
     * @param fundTransfer
     * @param page
     * @param pageSie
     * @return
     */
    List<HlsCusFundTransfer> selectFundTransferData(IRequest iRequest, HlsCusFundTransfer fundTransfer, int page, int pageSie);


    /**
     * 变更
     * @param fundTransfer
     * @return
     */
    List<HlsCusFundTransfer> selectFundTransferChangeData(IRequest iRequest, HlsCusFundTransfer fundTransfer, int page, int pageSie);


    /**
     * 更新调拨
     * @param iRequest
     * @param fundTransfer
     * @return
     */
   HlsCusFundTransfer updateFundTransferData(IRequest iRequest, HlsCusFundTransfer fundTransfer) throws HlsCusException;


    /**
     * 新增缺口调拨
     * @param iRequest
     * @param fundTransfer
     * @return
     */
    HlsCusFundTransfer createFundTransferGap(IRequest iRequest, HlsCusFundTransfer fundTransfer);


    /**
     * 提交审批流
     * @param iRequest
     * @param fundTransfer
     * @return
     */
    List<HlsCusFundTransfer> submitApprovalFundTransfer(IRequest iRequest, HlsCusFundTransfer fundTransfer)  throws HlsCusException;



    /**
     * 查询新建或审批中的CHANGE
     * @param refTransferId
     * @return
     */
    HlsCusFundTransfer selectFundTransferNewChange(Long refTransferId);
}