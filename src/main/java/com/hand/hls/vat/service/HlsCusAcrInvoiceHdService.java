package com.hand.hls.vat.service;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HlsCusAcrInvoiceHdService extends IBaseService<HlsCusAcrInvoiceHd>, ProxySelf<HlsCusAcrInvoiceHdService> {
//    List<HlsCusAcrInvoiceHd> queryInvoiceHdInfo(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize);
//
//    List<Map<String,Object>> queryWriteOffDetail(IRequest request, HlsCusCshWriteOff hlsCusCshWriteOff);
//
//    Map<String,Object> queryAcpInvoicePercent();
//
//    List<HlsCusAcrInvoiceHd> queryAcpInvoice(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize);
//
//    List<HlsCusAcrInvoiceHd> queryReceiptPrepGuaranteeInfo(IRequest request, HlsCusAcrInvoiceHd acrInvoiceLn, int page, int pageSize);

    List<HlsCusAcrInvoiceHd> queryReceiptPrepInfo(IRequest request, HlsCusAcrInvoiceHd acrInvoiceLn, int page, int pageSize);
//
//    List<Map<String, Object>> selectReceiptInfo(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize);
//
//    List<HlsCusAcrInvoiceHd> queryInvoiceByConditions(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize);
//
//    boolean createFctInvoice(IRequest request, List<HlsCusAcrInvoiceHd> list);
//
    boolean createInvoice(IRequest request, List<HlsCusAcrInvoiceHd> list);
//
//    List<HlsCusAcrInvoiceHd> queryByCashflowIds(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd);
//
//    List<HlsCusAcrInvoiceHd> queryInvoiceInfo(HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow, IRequest request, int page, int pageSize);
//
//    List<Map<String, Object>> queryFctInvoiceTitle(Long contractId, IRequest request);
//
//    List<HlsCusAcrInvoiceHd> selectByKindTime(IRequest iRequest, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize);
//
//    List<HashMap> acrInvoiceDetail(HlsCusAcrInvoiceHd acrInvoiceHd, IRequest iRequest);
//
//
//    void updateInvoiceStatusAfterExport(HlsCusAcrInvoiceHd acrInvoiceHd, IRequest iRequest);
//
//
    HlsCusSysFile docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws TokenException, FileReadIOException, Docx4JException;
//
    void batchDownloadWithZip(List<HlsCusAcrInvoiceHd> acrInvoiceHdList, HttpServletRequest request, HttpServletResponse response) throws Docx4JException, FileReadIOException, TokenException;


    // 销项发票关闭
    void closeVatInvoice(List<HlsCusAcrInvoiceHd> acrInvoiceHdList);

    // 销项发票导出
    void exportVatAcrInvoiceExcel(HttpServletRequest request, HttpServletResponse response, String invoiceHdIdStr) throws IOException, InvocationTargetException, IllegalAccessException, HlsCusException;

    // 销项发票红冲
    void reverseVatAcrInvoice(List<HlsCusAcrInvoiceHd> acrInvoiceHdList);

    // 销项发票导入
    void acrInvoiceExcelImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException;

    // 进项发票导入
    void importVatAcpInvoice(IRequest iRequest, Long hdId);


    // 收据生成和下载
    void saveDocAndDownload(List<HlsCusAcrInvoiceHd> dto, HttpServletRequest request, HttpServletResponse response) throws TokenException, FileReadIOException, Docx4JException;

    List<HlsCusAcrInvoiceHd> updateHandoverStatus(IRequest iRequest , List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList  );
    List<HlsCusAcrInvoiceHd> updateHandoverConfirmStatus(IRequest iRequest , List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList  );

}
