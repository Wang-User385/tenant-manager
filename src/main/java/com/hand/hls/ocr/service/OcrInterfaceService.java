package com.hand.hls.ocr.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hls.exception.HlsCusException;

import javax.servlet.http.HttpServletRequest;

public interface OcrInterfaceService extends ProxySelf<OcrInterfaceService> {
    /**
     * ocr识别发票
     * @param request
     */
    void ocrImportInvoice(HttpServletRequest request, IRequest iRequest) throws HlsCusException;

    /**
     * ocr识别表格
     * @param request
     */
    String ocrImportTable(HttpServletRequest request, IRequest iRequest) throws HlsCusException;

    /**
     * ocr识别pdf
     * @param request
     */
    String ocrImportPdf(HttpServletRequest request, IRequest iRequest) throws HlsCusException;

    String getSavePathDir();
}
