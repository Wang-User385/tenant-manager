package com.hand.hls.atm.service;

import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface HlsCusFndAttachmentService extends IBaseService<FndAttachment>, ProxySelf<HlsCusFndAttachmentService> {

    /**
     * 压缩打包文件
     * @param httpServletRequest
     * @param httpServletResponse
     * @param request
     * @param attachmentIds 附件ID数组（fnd_atm_attachment表的主键）
     * @param name 生成的压缩文件名
     */
    void attachmentPackageDownload(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, IRequest request, Long[] attachmentIds, String name);
    void batchDownloadAttachment(IRequest iRequest,String attachmentIds,HttpServletResponse response) throws IOException, AttachmentException;
}
