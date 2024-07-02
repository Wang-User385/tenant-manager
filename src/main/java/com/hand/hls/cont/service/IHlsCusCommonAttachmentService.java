package com.hand.hls.cont.service;

import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusCommonAttachment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IHlsCusCommonAttachmentService extends IBaseService<HlsCusCommonAttachment>, ProxySelf<IHlsCusCommonAttachmentService>{

    /**
     * 关税资料清单文本批量下载
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param requestCtx
     * @param attachmentPara
     * @return
     */
    String contractContextPackage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, IRequest requestCtx, HlsCusCommonAttachment attachmentPara);
    String cshContractContextPackage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, IRequest requestCtx, HlsCusCommonAttachment attachmentPara);

    /**
     * 关税资料清单初始化
     * @param iRequest
     * @param attachmentPara
     */
    void tariffContractContextInit(IRequest iRequest,HlsCusCommonAttachment attachmentPara);

}