package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.cont.dto.BusinessConfirm;
import com.hand.hls.cont.dto.ConfirmBatch;
import com.hand.hls.cont.dto.HlsCusConContract;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IBusinessConfirmService extends IBaseService<BusinessConfirm>, ProxySelf<IBusinessConfirmService>{
    /**
     *  生成业务确认函中间表
     */
    Long createBusinessConfirm(IRequest iRequest, List<HlsCusConContract> list);

    /**
     * 退出业务确认函创建
     * @param iRequest
     * @param batchId
     */
    void cancelBusinessConfirm(IRequest iRequest,Long batchId);
    /**
     * 确认业务确认函创建
     * @param iRequest
     * @param batchId
     */
    void confirmBusinessConfirm(IRequest iRequest,Long batchId,List<Long> list);
//
//    /**
//     * 批量打印业务确认函，生成zip
//     * @param iRequest
//     * @param request
//     * @param response
//     * @param list
//     * @throws Exception
//     */
//    void downloadBusinessConfirm(IRequest iRequest, HttpServletResponse response,HttpServletRequest request, List<ConfirmBatch> list) throws Exception;
//
//    void downloadDealerBusinessConfirm(IRequest iRequest, HttpServletResponse response, HttpServletRequest request, List<ConfirmBatch> list) throws Exception;

    /**
     *
     * @param iRequest IRequest
     * @param batchIds 确认函集合
     */
    void delBusinessConfirm(IRequest iRequest,String batchIds);
    List<FndAttachmentMulti> contextCreateMultiple(IRequest request, List<ConfirmBatch> list, HttpServletResponse response) throws Exception;
    List<FndAttachmentMulti> contextCreateMultiple1(IRequest request, List<ConfirmBatch> list, HttpServletResponse response) throws Exception;
}