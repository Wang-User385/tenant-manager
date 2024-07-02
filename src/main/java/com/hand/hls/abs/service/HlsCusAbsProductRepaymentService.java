package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductRepayment;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.HlsCusImportInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface HlsCusAbsProductRepaymentService extends IBaseService<HlsCusAbsProductRepayment>, ProxySelf<HlsCusAbsProductRepaymentService>, HlsCusImportInterface {


    /**
     * 查询
     * @param iRequest
     * @param productRepayment
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProductRepayment> selectRepaymentPlanData(IRequest iRequest, HlsCusAbsProductRepayment productRepayment, int page, int pageSize);


    /**
     * 删除
     * @param request
     * @param productRepayments
     * @throws HlsCusException
     */
    void batchDeleteRepayment(IRequest request, List<HlsCusAbsProductRepayment> productRepayments) throws HlsCusException;


    /**
     * 导出
     * @param request
     * @param response
     * @param hlsCusLonContractRepayment
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportRepaymentReport(HttpServletRequest request, HttpServletResponse response, HlsCusAbsProductRepayment hlsCusLonContractRepayment) throws IOException, InvocationTargetException, IllegalAccessException;


    /**
     * 还款确认
     * @param iRequest
     * @param fundTransferList
     */
    void confirmProductRepayment(IRequest iRequest, HlsCusFundTransferList fundTransferList) throws HlsCusException;



    HlsCusAbsProductRepayment saveRepaymentRelease(IRequest request, HlsCusAbsProductRepayment hlsCusAbsProductRepayment) throws HlsCusException ;


    /**
     * 未核销完的数据
     * @param productId
     * @return
     */
    int selectNotFullRepaymentCount(Long productId);
}