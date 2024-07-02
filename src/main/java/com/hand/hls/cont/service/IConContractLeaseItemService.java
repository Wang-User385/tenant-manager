package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;

import java.util.List;
import java.util.Map;

public interface IConContractLeaseItemService extends IBaseService<HlsCusConContractLeaseItem>, ProxySelf<IConContractLeaseItemService> {

    /**
     * 二期功能：租赁物与保险信息复制
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */
    List<HlsCusConContractLeaseItem> saveLeaseItemFromPrj(IRequest iRequest, Long contractId, Long projectId);

    /**
     * 查询租赁物
     */
   // HlsCusConContractLeaseItem queryLeaseItem(IRequest iRequest, Long contractId, String partnersLeaseItemId);

    /**
     * 查询租赁物（带权限）
     * @param iRequest 请求信息
     * @param conContract 查询参数
     * @param pagenum 页数
     * @param pagesize 查询行数
     * @return 租赁物信息
     */
    List<Map<String,Object>> queryContractLeaseItem(IRequest iRequest, Map<String, Object> conContract, int pagenum, int pagesize);


    /**
     * 查询租赁物信息
     * @param requestContext 请求用户
     * @param conContract 查询参数
     */
    void leaseItemQuery(IRequest requestContext, Map<String, Object> conContract);

}
