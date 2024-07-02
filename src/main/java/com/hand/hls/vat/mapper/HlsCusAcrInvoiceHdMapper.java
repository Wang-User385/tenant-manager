package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HlsCusAcrInvoiceHdMapper extends Mapper<HlsCusAcrInvoiceHd> {

    List<HlsCusAcrInvoiceHd> selectForCreateAcrInvoice(@Param("list") List<Long> cashflowIds, @Param("tempId") String tempId);

    List<HlsCusAcrInvoiceHd> queryReceiptPrepInfo(HlsCusAcrInvoiceHd acrInvoiceLn);

    List<Map<String, Object>> selectReceiptInfo(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);


    List<HlsCusAcrInvoiceHd> queryInvoiceDetail(HlsCusAcrInvoiceHd acrInvoiceHd);

    List<Map> queryVatAcrInvoiceDetail(Map param);

    List<HlsCusAcrInvoiceHd> queryAcrInvoiceHdDetail(HlsCusAcrInvoiceHd condition);
    List<HlsCusAcrInvoiceHd> queryAcrInvoiceHdDetailNew(HlsCusAcrInvoiceHd condition);

    HlsCusAcrInvoiceHd queryAcrInvoiceHdDetailById(Long invoiceHdId);

    HlsCusAcrInvoiceHd queryAcrInvoiceHdDetailByIdNew(Long invoiceHdId);

    List<HlsCusAcrInvoiceHd> queryHandOverInvoice(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);

    int updateHandoverStatus(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);

    List<HlsCusAcrInvoiceHd> queryHandOverInvoiceConfirm(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);

    int updateHandoverStatusReview(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);


    List<HlsCusAcrInvoiceHd> searchInvoicHdHomeQuery(HlsCusAcrInvoiceHd condition);
    List<HlsCusAcrInvoiceHd> queryHandOverInvoiceNew(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);

    List<HlsCusAcrInvoiceHd> querydzfpUrl(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);
}
