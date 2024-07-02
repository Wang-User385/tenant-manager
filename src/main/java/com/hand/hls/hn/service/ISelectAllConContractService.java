package com.hand.hls.hn.service;

import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.hn.dto.CheckPlanConContract;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;

import java.util.List;

public interface ISelectAllConContractService extends IBaseService<CheckPlanConContract>{
    /**
     * 查询逾期的合同
     * @return
     */
    List<CheckPlanConContract> queryAllApproveContract(CheckPlanConContract checkPlanConContract);
}