package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.HlsCusImportInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface HlsCusAbsProductCollectionService extends IBaseService<HlsCusAbsProductCollection>, ProxySelf<HlsCusAbsProductCollectionService>, HlsCusImportInterface {

    /**
     * 查询归集情况
     * @param request
     * @param hlsCusAbsProductCollection
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProductCollection> selectProductCollectionData(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection, int page, int pageSize);

    /**
     * 根据 productId 删除归集信息
     */
    void deleteNotConfirmByProductId(Long productId, String dataClass);

    /**
     * 保存产品某一期归集信息
     */
    HlsCusAbsProductCollection save(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 保存产品某一期转付信息
     */
    HlsCusAbsProductCollection remittanceSave(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 提交产品某一期转付信息
     */
    HlsCusAbsProductCollection remittanceSubmit(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 确认产品某一期归集信息
     */
    HlsCusAbsProductCollection colConfirm(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 归集申请页面归集信息表单查询
     */
    HlsCusAbsProductCollection queryCollection(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 兑付申请转付表单查询
     */
    HlsCusAbsProductCollection queryCashDeatil(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);

    /**
     * 转付申请页面转付信息表单查询
     */
    HlsCusAbsProductCollection queryRemittance(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);


    /**
     * 删除未归集
     * @param productId
     * @return
     */
    int deleteProductNotCollection(Long productId);


    /**
     * 最大的租金回收计算日
     * @param productId
     * @return
     */
    Date selectMaxRentalBackDate(Long productId);

    Long selectMaxCollectionTimes(Long productId);


    BigDecimal selectCashFeeSum(Long collectionId);


    HlsCusAbsProductCollection saveCashDeatil(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);


    HlsCusAbsProductCollection confirmCashDeatil(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);


    HlsCusAbsProductCollection calculateCashDetail(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection);


    /**
     * 归集确认
     * @param request
     * @param fundTransferList
     * @throws HlsCusException
     */
    void confirmProductCollection(IRequest request, HlsCusFundTransferList fundTransferList) throws HlsCusException;



    /**
     * 转付确认
     * @param request
     * @param fundTransferList
     * @throws HlsCusException
     */
    void confirmProductRemittance(IRequest request, HlsCusFundTransferList fundTransferList) throws HlsCusException;


    /**
     * 已确认的最大的一起的归集数据
     * @param productId
     * @return
     */
    HlsCusAbsProductCollection selectLastConfirmCollection(Long productId);


    /**
     * 未确认的归集兑付个数
     * @param productId
     * @return
     */
    int selectNotConfirmCollectionCount(Long productId);

    /**
     * 自定义导出
     * @param exportConfig
     * @param request
     * @param httpServletResponse
     * @param requestContext
     */
    void exportAndDownloadExcel(ExportConfig<HlsCusAbsProductCollection, ColumnInfo> exportConfig, HttpServletRequest request, HttpServletResponse httpServletResponse, IRequest requestContext) throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException;

    void deleteByCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection);


}

