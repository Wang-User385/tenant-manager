package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.dto.HlsWarrantStockTran;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IHlsWarrantStockHdService extends IBaseService<HlsWarrantStockHd>, ProxySelf<IHlsWarrantStockHdService>{

    HlsWarrantStockTran warrantStockSubmitWfl(IRequest requestCtx, HlsWarrantStockTran hlsWarrantStockTran);


    List<FndAttachmentMulti> warrantNoticeSave(IRequest iRequest, Long projectId, String templetCode,Long warrantStockId ) throws HlsCusException;

    List<HlsCusFctProjectAttachment> warrantTextSave(IRequest iRequest, Long projectId, Long warrantStockId ,  String templateCode) throws HlsCusException;


}