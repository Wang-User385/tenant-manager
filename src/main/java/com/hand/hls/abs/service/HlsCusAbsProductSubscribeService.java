package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductSubscribe;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface HlsCusAbsProductSubscribeService extends IBaseService<HlsCusAbsProductSubscribe>, ProxySelf<HlsCusAbsProductSubscribeService> {
    /**
     * 查询自持认购详细信息
     * 自持认购表格展示
     */
    List<HlsCusAbsProductSubscribe> queryProductSubscribe(IRequest request, HlsCusAbsProductSubscribe hlsCusAbsProductSubscribe, int page, int pageSize);

    /**
     * 自持认购保存
     */
    List<HlsCusAbsProductSubscribe> saveProductSubscribe(IRequest request, List<HlsCusAbsProductSubscribe> list) throws HlsCusException;

    /**
     * 自持认购确认
     */
    List<HlsCusAbsProductSubscribe> confirmProductSubscribe(IRequest request, List<HlsCusAbsProductSubscribe> list) throws HlsCusException;


    /**
     * 删除
     * @param request
     * @param list
     * @throws HlsCusException
     */
    void batchDeleteProductSubscribe(IRequest request, List<HlsCusAbsProductSubscribe> list) throws HlsCusException;


    /**
     * 未释放完全的数据
     * @param productId
     * @return
     */
    int selectSubscribeNotReleaseCount(Long productId);
}
