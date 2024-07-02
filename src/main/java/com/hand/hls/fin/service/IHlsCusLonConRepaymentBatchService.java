package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatch;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public interface IHlsCusLonConRepaymentBatchService extends IBaseService<HlsCusLonConRepaymentBatch>, ProxySelf<IHlsCusLonConRepaymentBatchService>{

    HlsCusLonConRepaymentBatch lonConRepaymentBatchSubmitWfl(IRequest iRequest, HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch);

    List<FndAttachmentMulti> contextCreateMultiple(IRequest request, String code, String batchId, HttpServletResponse response) throws Exception;

    void contextCreateMultipleSave(IRequest request, String code, String paymentId, FndAttachment sysFile, String copyPath, int fileLength, HttpServletResponse response) throws Exception;

    void confirmBatchStatus(IRequest request, List<HlsCusLonConRepaymentBatch> hlsCusLonConRepaymentBatchList, final HttpSession session) ;
    void saveConfirmBatchStatus(IRequest request, List<HlsCusLonConRepaymentBatch> hlsCusLonConRepaymentBatchList, final HttpSession session) ;
}