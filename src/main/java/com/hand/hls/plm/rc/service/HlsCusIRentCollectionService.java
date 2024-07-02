package com.hand.hls.plm.rc.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.rc.dto.HlsCusRentCollection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


/**
 * @Description:租金催收service
 * @Author: Wty
 * @Date: Created om 21:37 2018/6/6
 */

public interface HlsCusIRentCollectionService extends IBaseService<HlsCusRentCollection>, ProxySelf<HlsCusIRentCollectionService> {
    List<HlsCusRentCollection> queryAll(IRequest iRequest, HlsCusRentCollection rentCollection, int page, int pageSize);

    /**
     * 更新催收状态
     * @param procInstId
     * @param collectionStatus
     * @return
     */
    int updateCollectionStatus(String procInstId, String collectionStatus);


    /**
     * 导出合同现金流核销记录
     * @param request
     * @param response
     * @param rentCollection
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void exportRentCollectionReport(HttpServletRequest request, HttpServletResponse response, HlsCusRentCollection rentCollection)  throws IOException,InvocationTargetException, IllegalAccessException;

}