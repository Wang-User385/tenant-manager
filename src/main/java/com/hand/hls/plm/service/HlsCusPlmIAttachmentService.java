package com.hand.hls.plm.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface HlsCusPlmIAttachmentService extends IBaseService<HlsCusPlmAttachment>, ProxySelf<HlsCusPlmIAttachmentService> {
    List<HlsCusPlmAttachment> plmAttachmentDetailQuery(IRequest iRequest, HlsCusPlmAttachment hlsCusFinanceAttachment, int page, int pageSize);

    List<HlsCusPlmAttachment> plmAttachmentDetailQuery2(IRequest iRequest, HlsCusPlmAttachment hlsCusFinanceAttachment, int page, int pageSize);

    List<HlsCusPlmAttachment> queryAllFile(IRequest requestContext, HlsCusPlmAttachment hlsCusBpMasterAttachment, int page, int pagesize);

    void plmAttachmentChangeOldRemove(IRequest iRequest, HlsCusPlmAttachment hlsCusFinanceAttachment);

   // List<HlsCusSysFile> createPrintText(IRequest iRequest, HlsCusPlmAttachment hlsCusPlmAttachment);

    //  void downPLmZip(List<HlsCusSysFile> fileLists, IRequest iRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;
    void downPLmZip(String postloanInspectionIds, String printType, IRequest iRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;

}