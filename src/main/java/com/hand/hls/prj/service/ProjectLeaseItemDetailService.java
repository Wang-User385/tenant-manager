package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.prj.dto.PrjLeaseItemInsurance;
import com.hand.hls.prj.dto.PrjProjectLeaseItemCondition;
import com.hand.hls.prj.dto.PrjProjectLeaseItemMortgage;
import com.hand.hls.prj.dto.PrjProjectLeaseItemSales;

import java.util.List;

/**
 * @author lipan
 * @date 2024/7/09 - 14:18
 */

public interface ProjectLeaseItemDetailService {

    List<PrjLeaseItemInsurance> queryPrjLeaseItemInsurance(Long projectLeaseItemId);

    List<PrjProjectLeaseItemSales> queryPrjLeaseItemSales(Long projectLeaseItemId);

    List<PrjProjectLeaseItemMortgage> queryPrjLeaseItemMortgages(Long projectLeaseItemId);

    List<PrjProjectLeaseItemCondition> queryPrjLeaseItemCondition(Long projectLeaseItemId);



}

