package com.hand.hls.lease.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.lease.dto.YxLeaseItemClassify;

import java.util.List;
import java.util.Map;

public interface YxLeaseItemService {
    public List<YxLeaseItemClassify> yxLeaseItemClassifyTreeQuery(YxLeaseItemClassify yxLeaseItemClassify);
    public List<YxLeaseItemClassify> yxLeaseItemClassifyForLov(YxLeaseItemClassify yxLeaseItemClassify);
    /**
     * 查询租赁物产品可比价格
     */
    public List<Map> queryComparePrice(IRequest iRequest, Map params);

    void leaseItemExcelBatchImport(IRequest iRequest,Long headerId) throws HlsCusException;

    void manufacturerExcelBatchImport(IRequest iRequest,Long headerId) throws HlsCusException;

    void leaseItemLogCreate(IRequest iRequest, Map params);

    public String leaseItemCheckBeforeSubmit(IRequest iRequest, Map params);

    void updateAdvancedEquipmentFlag(Long classifyId, String advancedEquipmentFlag);

    boolean updateLeaseItemCheck(IRequest iRequest) throws HlsCusException;
}