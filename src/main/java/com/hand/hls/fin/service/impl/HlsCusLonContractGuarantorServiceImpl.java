package com.hand.hls.fin.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractGuarantor;
import com.hand.hls.fin.mapper.HlsCusLonContractGuarantorMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.service.IHlsCusLonContractGuarantorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractGuarantorServiceImpl extends BaseServiceImpl<HlsCusLonContractGuarantor> implements IHlsCusLonContractGuarantorService {

    @Autowired
    private HlsCusLonContractGuarantorMapper mapper;

    @Autowired
    private HlsCusLonContractMapper hlsCusLonContractMapper;

    @Override
    public List<HlsCusLonContract> selectCompanyByBpId(IRequest requestContext, HlsCusLonContractGuarantor lonContractGuarantor) {
        System.out.println(lonContractGuarantor.toString());
        //System.out.println(requestContext.toString());
        List<HlsCusLonContractGuarantor> lonContractGuarantors = mapper.selectCompanyByBpId(lonContractGuarantor);
        System.out.println("=============================>bp表查询结果"+lonContractGuarantors.size());
        if (lonContractGuarantors.size() >= 1) {
            for (int i = 0; i < lonContractGuarantors.size(); i++) {
                Long bpId = lonContractGuarantors.get(i).getBpId();
                return hlsCusLonContractMapper.selectLonContractByBpId(bpId);
                //System.out.println("=============================>合同表查询结果"+lonContracts.size());
                /*for (int j = 0; j < lonContracts.size(); j++) {
                    if (!lonContractGuarantor.getContractId().equals(lonContracts.get(j).getContractId())){
                        return Arrays.asList("担保人（"+lonContractGuarantors.get(i).getDescription()+"）在其他融资合同（"+lonContracts.get(j).getContractNumber()+"）内存在担保。");
                    }
                }*/
            }
        }
        return null;
    }
}