package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPackage;
import hls.core.utils.exception.HlsCusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface HlsCusAbsAssetsPackageService extends IBaseService<HlsCusAbsAssetsPackage>, ProxySelf<HlsCusAbsAssetsPackageService> {


    /**
     * 资产包查询
     * @param iRequest
     * @param absAssetsPackage
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectAssetsPackage(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize);


    /**
     * 资产包新增查询
     * @param iRequest
     * @param absAssetsPackage
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackageContract(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize);


    /**
     *  资产包现金流查询
     * @param iRequest
     * @param absAssetsPackage
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackageConCashFlow(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize);


    /**
     * 资产包选择期数后 现金流数据
     * @param iRequest
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage>selectBetweenTimesCashflow(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage);



    /**
     * 占用单据
     * @param packId
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackageOccupyData(IRequest iRequest, Long packId);



    /**
     * 日期区间内的现金流
     * @param iRequest
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackContarctCashFlow(IRequest iRequest, HlsCusAbsAssetsPackage absAssetsPackage, int page, int pageSize);


    /**
     * 导出现金流
     * @param request
     * @param response
     * @param absAssetsPackage
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportCashflow(HttpServletRequest request, HttpServletResponse response, HlsCusAbsAssetsPackage absAssetsPackage) throws IOException, InvocationTargetException, IllegalAccessException;


    /**
     * 导出归集现金流
     * @param request
     * @param response
     * @param absAssetsPackage
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportCashFlowBetweenDate(HttpServletRequest request, HttpServletResponse response, HlsCusAbsAssetsPackage absAssetsPackage) throws IOException, InvocationTargetException, IllegalAccessException;

    /**
     * 资产包.资产明细 导出
     * @param request
     * @param response
     * @param absAssetsPackage
     */
    void exportAssetDetails(HttpServletRequest request, HttpServletResponse response, HlsCusAbsAssetsPackage absAssetsPackage) throws InvocationTargetException, IllegalAccessException, IOException;

    /**
     *
     * @param requestContext
     * @param dto 检验的数据
     * @return <p>ResponseData</p>
     */
    ResponseData checkAssetsPackage(IRequest requestContext, List<HlsCusAbsAssetsPackage> dto);

    /**
     *
     * @param requestContext
     * @param dto 检验已经被占用的数据
     * @return <p>ResponseData</p>
     */
    ResponseData checkAssetsPackagePackDate(IRequest requestContext, List<HlsCusAbsAssetsPackage> dto);

    void checkAssetsPackagePackDateNew(IRequest requestContext, HlsCusAbsAssetsPackage hlsCusAbsAssetsPackage) throws HlsCusException;
}
