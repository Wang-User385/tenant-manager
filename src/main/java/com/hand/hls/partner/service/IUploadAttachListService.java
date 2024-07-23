package com.hand.hls.partner.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.partner.dto.UploadAttachList;
import org.springframework.web.multipart.MultipartFile;


public interface IUploadAttachListService extends IBaseService<UploadAttachList>, ProxySelf<IUploadAttachListService>{
    String getUploadUrl(String decryptedStr);

    String upload(String fileId, MultipartFile file);
}