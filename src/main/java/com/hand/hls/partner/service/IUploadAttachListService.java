package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.partner.dto.UploadAttachList;

import javax.servlet.http.HttpServletRequest;

public interface IUploadAttachListService extends IBaseService<UploadAttachList>, ProxySelf<IUploadAttachListService>{
    ResponseData getUploadUrl(UploadAttachList uploadAttachList,HttpServletRequest request,IRequest iRequest);

}