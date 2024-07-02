package com.hand.hls.plm.fc.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.pli.dto.PlmPliContract;

import java.util.List;
import java.util.Map;

public interface HlsCusIFiveClassificationContractService extends IBaseService<HlsCusFiveClassificationContract>, ProxySelf<HlsCusIFiveClassificationContractService> {
    List<HlsCusFiveClassificationContract> selectAllContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<HlsCusFiveClassificationContract> selectFcContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<HlsCusFiveClassificationContract> homeChartQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract);

    List<PlmPliContract> selectRwContractsByBpId(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<Map> rcHomeRollTableQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<Map> rcSelectOverdueContractsInfo(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<Map> rcSelectBaseInfo(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<Map> rcHomeFirstChartQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<Map> rcHomeSecondChartQuery(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    void setAmount(String type, HlsCusFiveClassificationContract contract, Long companyId);

    List<HlsCusFiveClassificationContract> selectFcActivitiContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract, int page, int pageSize);

    List<HlsCusFiveClassificationContract> getCopyContracts(IRequest iRequest, HlsCusFiveClassificationContract fiveClassificationContract);

    List<HlsCusFiveClassificationContract> saveOpinions(IRequest iRequest, List<HlsCusFiveClassificationContract> list, String approvalNode);

    /**
     * 查询当前用户的unitCode 如果='003'则false
     * @param requestContext
     * @return
     */
    Boolean shouldButtonShow(IRequest requestContext);
}
