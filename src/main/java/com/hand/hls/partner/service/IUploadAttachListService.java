package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.partner.dto.UploadAttachList;

import javax.servlet.http.HttpServletRequest;

public interface IUploadAttachListService extends IBaseService<UploadAttachList>, ProxySelf<IUploadAttachListService>{
    JSONObject getUploadUrl(JSONObject jsonObject, HttpServletRequest request, IRequest iRequest) throws Exception;

}