package com.hand.hls.plm.pli.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;

import java.util.List;

/**
 * @Description:贷后检查service
 * @Author: Wty
 * @Date: Created om 16:26 2018/5/24
 */
public interface HlsCusIPostloanInspectionService extends IBaseService<HlsCusPostloanInspection>, ProxySelf<HlsCusIPostloanInspectionService> {
    HlsCusPostloanInspection savePostLoan(IRequest irequest, HlsCusPostloanInspection postloanInspection);

    HlsCusPostloanInspection selectPostloadInspection(IRequest iRequest, HlsCusPostloanInspection postloanInspection);

    List<HlsCusPostloanInspection> homeRollTableQuery(IRequest iRequest, HlsCusPostloanInspection postloanInspection, int page, int pageSize);

    List<HlsCusPostloanInspection> selectContractFrequency(IRequest iRequest, HlsCusPostloanInspection HlsCusPostloanInspection);

    List<HlsCusPostloanInspection> selectCheckList(Long companyId, String MODEL);

    HlsCusPostloanInspection submitWfl(IRequest iRequest, HlsCusPostloanInspection hlsCusPostloanInspection);

    List<HlsCusPostloanInspection> createPostloanInspection(IRequest iRequest, HlsCusPostloanInspection hlsCusPostloanInspection);
    }
