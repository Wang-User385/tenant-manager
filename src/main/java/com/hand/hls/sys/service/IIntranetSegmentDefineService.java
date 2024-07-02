package com.hand.hls.sys.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.sys.dto.IntranetSegmentDefine;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

/**
 * @Description：内网网段定义
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/1 14:44
 * @Version：1.0
 */

public interface IIntranetSegmentDefineService extends IBaseService<IntranetSegmentDefine>, ProxySelf<IIntranetSegmentDefineService>{

    /**
     * 二期功能：内网网段定义批量插入或更新
     * @param iRequest
     * @param list
     * @return
     * @throws Exception
     */
    List<IntranetSegmentDefine> batchInsertUpdate(IRequest iRequest, List<IntranetSegmentDefine> list) throws Exception;

    /**
     * 根据条件获取内网网段定义信息
     * @param define 查询条件
     * @return 所有符合条件的记录
     */
    List<IntranetSegmentDefine> getIntranetSegmentDefineInfo(IntranetSegmentDefine define);
}