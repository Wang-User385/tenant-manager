package com.hand.hls.fnd.service;

import com.hand.hap.account.dto.User;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsCusImpBatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HlsCusImpBatchService extends IBaseService<HlsCusImpBatch>, ProxySelf<HlsCusImpBatchService> {
    /**
     * 查询单个批次
     * @param iRequest
     * @param batch
     * @return
     */
    HlsCusImpBatch getBatch(IRequest iRequest, HlsCusImpBatch batch);

    /**
     * 插入批次
     * @param request
     * @param batch
     * @return
     */
    HlsCusImpBatch insertBatch(IRequest request, HlsCusImpBatch batch);

    /**
     * 上传文件
     * @param request
     * @param batch
     */
    void uploadFile(HttpServletRequest request, HlsCusImpBatch batch);

    /**
     * 下载文件
     * @param response
     * @param batch
     */
    void downloadFile(HttpServletResponse response, HlsCusImpBatch batch);

    /**
     * 删除文件
     * @param request
     * @param batch
     */
    void deleteFile(IRequest request, HlsCusImpBatch batch);

    /**
     * 查询user
     * @return
     */
    List<User> selectUsers();

    /**
     * 查询多个批次
     * @param dto
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<HlsCusImpBatch> selectBatch(HlsCusImpBatch dto, int pageNum, int pageSize);

}