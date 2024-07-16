package com.hand.hls.web.logs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.app.utils.generalUtils.AppResponseData;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liao
 */
public interface IHlsWsRequestsService extends IBaseService<HlsWsRequests>, ProxySelf<IHlsWsRequestsService>{

    /**
     * 接口请求保存 根据主键  更新/保存
     *  请求所有信息
     * @param dto  对象
     * @param request  请求头（）
     * @param iRequest 解析后请求头
     * @return  返回结果集
     * @throws Exception  异常
     */
    HlsWsRequests interfaceSaveAll(HlsWsRequests dto, HttpServletRequest request, IRequest iRequest) throws Exception;

    /**
     * 接口请求保存 根据主键  更新/保存
     *  请求体信息
     * @param dto  对象
     * @param iRequest 解析后请求头
     * @return  返回结果集
     * @throws Exception  异常
     */
    HlsWsRequests interfaceSave(HlsWsRequests dto, IRequest iRequest);


    /**
     *
     * 接口完成后 对日志表中接口请求状态 做修改
     * @param result 接口请求结果
     * @param hlsWsRequests 日志对象
     * @param iRequest      请求
     * @param dateType      数据类型 （json/ xml）
     * @throws Exception 异常
     */
    void updateResult(ResponseData result, HlsWsRequests hlsWsRequests, IRequest iRequest, String dateType) throws Exception;
    /**
     *
     * 接口完成后 对日志表中接口请求状态 做修改
     * @param result 接口请求结果
     * @param hlsWsRequests 日志对象
     * @param iRequest      请求
     * @param dateType      数据类型 （json/ xml）
     * @throws Exception 异常
     */
    void updateResult(AppResponseData result, HlsWsRequests hlsWsRequests, IRequest iRequest, String dateType) throws Exception;
    /**
     * 文件 类型专用
     * 接口完成后 对日志表中接口请求状态 做修改
     * @param result 接口请求结果
     * @param hlsWsRequests 日志对象
     * @param iRequest      请求
     * @throws Exception 异常
     */
    void updateResultFile(ResponseEntity<byte[]> result, HlsWsRequests hlsWsRequests, IRequest iRequest) throws Exception;

    /**
     * 获取头中的一些信息
     * @param request  请求头
     * @return 返回string
     * @throws Exception
     */
    String getFileInfo(HttpServletRequest request) throws Exception;
}