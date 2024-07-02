package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductCashDetail;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import com.hand.hls.exception.HlsCusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public interface HlsCusAbsProductCashDetailService extends IBaseService<HlsCusAbsProductCashDetail>, ProxySelf<HlsCusAbsProductCashDetailService> {


   List<HlsCusAbsProductCashDetail> selectProductCashDetailData(IRequest iRequest, HlsCusAbsProductCashDetail productCashDetail, int page, int pageSize);



   int deleteCashDetailByProduct(Long productId, String dataClass);



   BigDecimal selectCashPrincipalSum(Long productId, Long times);

   void exportCashDeatil(HttpServletRequest request, HttpServletResponse response, HlsCusAbsProductCashDetail hlsCusFctQuotationCashflow) throws IOException, InvocationTargetException, IllegalAccessException;

   void deleteCashDetailByCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection);

   /**
    * 兑付计划导入
    * @param iRequest
    * @param hdId
    * @param productId 产品ID
    * @throws HlsCusException
    * @throws ParseException
    */
   void ctAbsProductCashImport(IRequest iRequest, Long hdId , Long productId) throws HlsCusException, ParseException;

   /**
    * 兑付计划确认
    * @param iRequest
    * @param cash_detail_id 兑付计划ID
    */
   void ctAbsProductCashConfirm(IRequest iRequest, Long cash_detail_id);
}