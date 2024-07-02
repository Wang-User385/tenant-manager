package com.hand.hls.app.file.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liao
 */
public interface IAppAttachmentService  extends IBaseService<FndAttachment>, ProxySelf<IAppAttachmentService> {
    /**
     * 附件查询
     * @param iRequest 请求
     * @param jsonObject 请求参数
     * @return 返回结果
     */
    ResponseData queryAttachment(IRequest iRequest, JSONObject jsonObject);
    /**
     * 附件下载
     * @param iRequest 请求
     * @param jsonObject 请求参数
     * @return 返回结果
     */
    ResponseEntity<byte[]> downloadAtt(IRequest iRequest, JSONObject jsonObject);
    /**
     * 附件  删除
     * @param iRequest 请求
     * @param jsonObject 请求参数
     * @return 返回结果
     */
    ResponseData deleteAtt(IRequest iRequest, JSONObject jsonObject);
    /**
     * 附件  删除
     *
     * @param request  请求
     * @param iRequest 请求头
     * @return 返回结果
     */
    ResponseData uploadFile(HttpServletRequest request, IRequest iRequest);
}
