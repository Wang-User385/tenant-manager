package com.hand.hls.req.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;

import java.util.List;
import java.util.Map;

public interface HlsCusChangeReqInfoService extends IBaseService<HlsCusChangeReqInfo>, ProxySelf<HlsCusChangeReqInfoService> {
    /**
     * 查询租赁合同变更的信息
     * @param hlsCusChangeReqInfo 参数对象中的documentId必须有值
     * @return 返回只有一条记录的数据
     */
    List<HlsCusChangeReqInfo> queryChangeInfo(IRequest request, HlsCusChangeReqInfo hlsCusChangeReqInfo);

    List<HlsCusChangeReqInfo> queryContractChangeInfo(IRequest request, HlsCusChangeReqInfo hlsCusChangeReqInfo, int page, int pageSize);

    List<HlsCusChangeReqInfo> queryHistory(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    List<HlsCusChangeReqInfo> queryStatus(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    Map queryConChangeType(HlsCusChangeReqInfo hlsCusChangeReqInfo);

    Map queryPrcContractChangeType();

    List<HlsCusChangeReqInfo> selectConChangeType(IRequest requestContext, HlsCusChangeReqInfo hlsCusChangeReqInfo, int page, int pageSize);

    List<HlsCusChangeReqInfo> projectChangeReq(IRequest iRequest,HlsCusChangeReqInfo hlsCusChangeReqInfo) throws HlsCusException;

    void backupProject(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws ParameterNullException, HlsCusException;
}