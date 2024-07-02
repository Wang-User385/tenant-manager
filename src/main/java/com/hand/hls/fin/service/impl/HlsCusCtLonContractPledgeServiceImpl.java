package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractPledge;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.mapper.HlsCusCtLonContractPledgeMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.service.HlsCusCtLonContractPledgeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class HlsCusCtLonContractPledgeServiceImpl extends BaseServiceImpl<HlsCusCtLonContractPledge> implements HlsCusCtLonContractPledgeService {


    @Autowired
    HlsCusCtLonContractPledgeMapper hlsCusCtLonContractPledgeMapper;

    @Autowired
    HlsCusLonContractMapper hlsCusLonContractMapper;

    private final static String FCT_CONTRACT="FCT_CONTRACT";

    private final static String CON_CONTRACT="CON_CONTRACT";


    @Override
    public List<HlsCusCtLonContractPledge> selectLonContractPledge(IRequest iRequest, HlsCusCtLonContractPledge lonContractPledge, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return hlsCusCtLonContractPledgeMapper.selectLonContractPledge(lonContractPledge);
    }

    @Override
    public List<HlsCusCtLonContractPledge> selectContractSurpusAmount(HlsCusCtLonContractPledge lonContractPledge) {
        return null;
    }

    @Override
    public void deleteContractPledge(IRequest iRequest, List<HlsCusCtLonContractPledge> lonContractPledges) {

    }

    @Override
    public void updatePledgeFlag(IRequest iRequest, HlsCusCtLonContractPledge hlsCusCtLonContractPledge, String PledgeFlag) {

    }

    @Override
    public List<HlsCusCtLonContractPledge> selectContractCashFlow(IRequest iRequest, HlsCusCtLonContractPledge lonContractPledge, int page, int pageSize) {
        return null;
    }

    @Override
    public int updateFctContractPledgeFlag() {
        return 0;
    }

    @Override
    public int updateConContractPledgeFlag() {
        return 0;
    }

    @Override
    public int updatePledgeReleaseFlag() {
        return 0;
    }

    @Override
    public void contractPledgeRelease(IRequest iRequest, List<HlsCusCtLonContractPledge> lonContractPledges) {

    }
    @Override
    public List<Double> queryForAmount(HlsCusCtLonContractPledge hlsCusCtLonContractPledge){
        List<Double> admount = new ArrayList<>();
        if(hlsCusCtLonContractPledge.getContractId() != null && hlsCusCtLonContractPledge.getPledgeTimesFrom() !=null && hlsCusCtLonContractPledge.getPledgeTimesTo() !=null){
            admount.add(hlsCusCtLonContractPledgeMapper.queryForAmount(hlsCusCtLonContractPledge.getContractId(),hlsCusCtLonContractPledge.getPledgeTimesFrom(),hlsCusCtLonContractPledge.getPledgeTimesTo()));
        }
        return admount;
    }

    @Override
    public List<HlsCusCtLonContractPledge> queryContractNumber(IRequest requestContext, List<HlsCusCtLonContractPledge> pledges) {
        HlsCusCtLonContractPledge ret = new HlsCusCtLonContractPledge();
        List<HlsCusCtLonContractPledge> retList = new ArrayList<HlsCusCtLonContractPledge>();
        if (pledges != null) {
            for (int i = 0; i < pledges.size(); i++) {
                ret = pledges.get(i);
                Example example = new Example(HlsCusCtLonContractPledge.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("pledgeDocId", ret.getPledgeDocId()) ;
                criteria.andEqualTo("pledgeDocCategory", ret.getPledgeDocCategory()) ;
                criteria.andNotEqualTo("contractId",ret.getContractId());
                List< HlsCusCtLonContractPledge > dtos =   hlsCusCtLonContractPledgeMapper.selectByExample(example);
                if(dtos.size()>0){
                    retList.add(ret);
                    return retList;
                }
            }
        }
        return retList;
    }
}
