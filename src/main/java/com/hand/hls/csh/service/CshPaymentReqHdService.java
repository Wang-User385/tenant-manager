//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

public interface CshPaymentReqHdService extends IBaseService<HlsCusCshPaymentReqHd>, ProxySelf<CshPaymentReqHdService> {
    List<HlsCusCshPaymentReqHd> queryCshPaymentReqHd(IRequest var1, HlsCusCshPaymentReqHd var2, int var3, int var4);
    HlsCusCshPaymentReqHd save(IRequest iRequest,HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    HlsCusCshPaymentReqHd create(IRequest iRequest,HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    HlsCusCshPaymentReqHd cshHdCreate(IRequest iRequest,HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd> transferSubmit(IRequest iRequest,HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd)throws ResMessageException, ParameterNullException;
    void checkSupplement(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws ResMessageException;
    List<HlsCusCshPaymentReqHd> abandonTransfer(IRequest iRequest,List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds)throws ResMessageException, ParameterNullException;
    void saveEftTransfer(IRequest iRequest,HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd)throws ResMessageException;
    void createEftTransferList(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

    /**
     * 获取付款支付头编码
     * @param requestContext
     * @return
     */
    String getCodeValue(IRequest requestContext);
    /**
     *
     * 资金调拨和付款单申请
     *
     * @param request
     * @param code
     * @param response
     * @param paymentId
     * @throws Exception
     */
    List<FndAttachmentMulti> contextCreateMultiple(IRequest request, String code , String paymentId, HttpServletResponse response) throws Exception;

    void contextCreateMultipleSave(IRequest request, String code , String paymentId, FndAttachment sysFile,String copyPath,int fileLength, HttpServletResponse response) throws Exception;

    void cshInImport(IRequest iRequest, Long hdId , Long paymentReqId) throws ExcelException, Exception, ParseException;
    List<HlsCusCshPaymentReqHd> dailyrate(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, int page, int pagesize);

    /**
     * 关税付款申请单
     * @param iRequest
     * @return 关税现金流列表
     */
    HlsCusCshPaymentReqHd tariffPaymentReqCreate(IRequest iRequest, List<HlsCusConContractCashflow> tariffCashflows) throws Exception;

    /**
     * 关税付款申请审批提交审批
     * @param iRequest
     * @param hlsCusCshPaymentReqHd
     * @return
     * @throws HlsCusException
     */
    List<HlsCusCshPaymentReqHd> submitTariffPaymentReqWfl(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws HlsCusException;

    /**
     * 二期功能：零售业务付款支付首页查询
     * @param iRequest
     * @param hlsCusCshPaymentReqHd
     * @param page
     * @param pagesize
     * @return
     */
    List<HlsCusCshPaymentReqHd> retailPaymentHomeQuery(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, int page, int pagesize);
    /**
     * 关税付款申请审批通过
     * @param iRequest
     * @param hlsCusCshPaymentReqHd
     * @return
     */
    void tariffPaymentReqApproved(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<Double> queryActualPaymentAmount(HlsCusCshPaymentReqHd hlscuscshpaymentreqhd);
    List<Double> queryPaymentAmount(HlsCusCshPaymentReqHd hlscuscshpaymentreqhd);


}
