package com.hand.hls.vat.service;

import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.vat.dto.HlsCusAcrReceiptHd;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.util.List;
import java.util.Map;

public interface IAcrReceiptHdService extends IBaseService<HlsCusAcrReceiptHd>, ProxySelf<IAcrReceiptHdService> {

    List<Map> queryReceiptList(int pageNum, int pageSize, Map condition);

    List<Map> queryWaitingReceiptList(int pageNum, int pageSize, Map condition);

    void create(IRequest iRequest, List<HlsCusAcrReceiptHd> dto) throws IllegalArgumentException;

    FndAttachment receiptSaveDocAndDownload(List<HlsCusAcrReceiptHd> hlsCusAcrInvoiceHdList, IRequest requestCtx);

    FndAttachment docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws Exception;

}