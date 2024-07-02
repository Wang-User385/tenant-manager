package com.hand.hls.fct.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;

import java.util.List;

public interface HlsCusFctProjectAttachmentService extends IBaseService<HlsCusFctProjectAttachment>, ProxySelf<HlsCusFctProjectAttachmentService> {

    List<HlsCusFctProjectAttachment> fctProjectNoticeAttachmentDetailQuery(IRequest iRequest, HlsCusFctProjectAttachment hlsCusFctProjectAttachment, int page, int pageSize);

    /**
     * mode：1 查询标准模板
     * mode: 2 查询所有模板，包括自定义模板
     */
    List<HlsCusFctProjectAttachment> queryContentFileInfo(IRequest requestCtx,HlsCusFctProjectAttachment hlsCusFctProjectAttachment, int page, int pageSize, int mode );

    List<HlsCusFctProjectAttachment> prjConContentSave(IRequest iRequest, Long projectId) throws HlsCusException;

    List<HlsCusFctProjectAttachment> prjApprovalNoticeTextSave(IRequest iRequest, Long projectId, Long prjNoticeId,Long quotationId) throws HlsCusException;

    List<FndAttachmentMulti> prjContentSave(IRequest iRequest, Long projectId, String templetCode) throws HlsCusException;

    List<FndAttachmentMulti> prjApprovalNoticeSave(IRequest iRequest, Long projectId, String templetCode,Long prjNoticeId,Long quotationId,Long oldProjectId) throws HlsCusException;

    void contentFinalized(IRequest iRequest,HlsCusFctProjectAttachment hlsCusFctProjectAttachment);

    List<HlsCusFctProjectAttachment> selectAttachmentByProjectId(HlsCusFctProjectAttachment hlscusfctprojectattachment);

    List<HlsCusFctProjectAttachment> selecttAttachmentByTemplateId(HlsCusFctProjectAttachment hlscusfctprojectattachment);

}
