package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProFeeInfo;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.HlsCusImportInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface HlsCusAbsProFeeInfoService extends IBaseService<HlsCusAbsProFeeInfo>, ProxySelf<HlsCusAbsProFeeInfoService>, HlsCusImportInterface {


    /**
     * 查询
     * @param iRequest
     * @param projectFeeInfo
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProFeeInfo> selectProjectFeeInfo(IRequest iRequest, HlsCusAbsProFeeInfo projectFeeInfo, int page, int pageSize);


    /**
     * 更新organizationId
     * @param productId
     * @param sourceOrganizationId
     * @param targetOrganizationId
     * @return
     */
    int updateFeeOrganizationId(Long productId, Long sourceOrganizationId, Long targetOrganizationId);


    /**
     * 更新费用金额
     * @param productId
     * @return
     */
    int updateProductFeeAmount(Long productId);


    /**
     * 还款确认
     * @param iRequest
     * @param fundTransferList
     * @throws HlsCusException
     */
    void confirmAbsProFee(IRequest iRequest, HlsCusFundTransferList fundTransferList) throws HlsCusException;




    /**
     * 导出
     * @param request
     * @param response
     * @param absProFeeInfo
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportAbsProFeeInfo(HttpServletRequest request, HttpServletResponse response, HlsCusAbsProFeeInfo absProFeeInfo) throws IOException, InvocationTargetException, IllegalAccessException;


}