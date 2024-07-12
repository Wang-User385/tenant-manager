package com.hand.hls.prj.service.impl;

import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProjectLeaseItemDetailServiceImpl implements ProjectLeaseItemDetailService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectLeaseItemDetailServiceImpl.class);

    @Autowired
    private PrjLeaseItemInsuranceMapper prjLeaseItemInsuranceMapper;
    @Autowired
    private ProjectLeaseItemMortgageMapper projectLeaseItemMortgageMapper;
    @Autowired
    private ProjectLeaseItemSalesMapper projectLeaseItemSalesMapper;
    @Autowired
    private ProjectLeaseItemConditionMapper projectLeaseItemConditionMapper;



    @Override
    public List<PrjLeaseItemInsurance> queryPrjLeaseItemInsurance(Long projectLeaseItemId) {
        //复制保险信息
        PrjLeaseItemInsurance itemInsurance = new PrjLeaseItemInsurance();
        itemInsurance.setProjectLeaseItemId(projectLeaseItemId);
        List<PrjLeaseItemInsurance> itemInsurances = prjLeaseItemInsuranceMapper.select(itemInsurance);
        return itemInsurances;
    }

    @Override
    public List<PrjProjectLeaseItemSales> queryPrjLeaseItemSales(Long projectLeaseItemId) {
        return projectLeaseItemSalesMapper.prjProjectLeaseItemSalesQuery(projectLeaseItemId);
    }

    @Override
    public List<PrjProjectLeaseItemMortgage> queryPrjLeaseItemMortgages(Long projectLeaseItemId) {
        return projectLeaseItemMortgageMapper.prjProjectLeaseItemMortgageQuery(projectLeaseItemId);
    }

    @Override
    public List<PrjProjectLeaseItemCondition> queryPrjLeaseItemCondition(Long projectLeaseItemId) {
        return projectLeaseItemConditionMapper.prjProjectLeaseItemConditionQuery(projectLeaseItemId);
    }
}

