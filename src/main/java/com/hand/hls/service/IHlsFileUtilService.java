package com.hand.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;

/**
 * @author zhaokai
 * @date 2021-7-13 11:38:09
 */
public interface IHlsFileUtilService extends ProxySelf<IHlsFileUtilService> {

    /**
     * 企业微信下载附件
     *
     * @param mediaId
     */
    String weChatDownloadsFile(String mediaId) throws Exception;

    /**
     * 通用插入附件信息
     * @param filePath
     * @param sourceTable
     * @param fileName
     * @param id
     * @param iRequest
     * @return
     */
    Long attchmentFileInsert(String filePath, String sourceTable, String fileName, Long id, IRequest iRequest);

    void attchmentFileDelete(Long attchmentId , IRequest iRequest);
}
