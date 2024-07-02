package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.mapper.HlsCusFctContractMapper;
import com.hand.hls.fin.dto.HlsCusLonContractPurpose;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractPurposeMapper;
import com.hand.hls.fin.service.HlsCusLonContractPurposeService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractPurposeServiceImpl extends BaseServiceImpl<HlsCusLonContractPurpose> implements HlsCusLonContractPurposeService {

    public static final String CON_CONTRACT = "CON_CONTRACT";
    public static final String FCT_CONTRACT = "FCT_CONTRACT";
    public static final String LON_CONTRACT = "LON_CONTRACT";
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusFctContractMapper hlsCusFctContractMapper;
    @Autowired
    private HlsCusLonContractMapper hlsCusLonContractMapper;

    @Autowired
    private HlsCusLonContractPurposeMapper purposeMapper;

    @Autowired
    private HlsCusLonContractWithdrawService lonContractWithdrawService;

    @Override
    public List<HlsCusLonContractPurpose> selectLonContractPur(IRequest request, HlsCusLonContractPurpose lonContractPurpose, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return purposeMapper.selectLonContractPur(lonContractPurpose);
    }

    @Override
    public List<HlsCusLonContractPurpose> batchUpdatePurpose(IRequest request, List<HlsCusLonContractPurpose> purposeList) throws HlsCusException {
        List<HlsCusLonContractPurpose> purposes = self().batchUpdate(request, purposeList);
        if(purposeList.size()>0){
            HlsCusLonContractWithdraw withdraw=new HlsCusLonContractWithdraw();
            withdraw.setWithdrawId(purposeList.get(0).getWithdrawId());
            withdraw=lonContractWithdrawService.selectByPrimaryKey(request,withdraw);
            Double purposeSum = selectPurposeSum(purposeList.get(0).getWithdrawId());
            if(purposeSum.compareTo(withdraw.getDueAmount())==1){
                throw new HlsCusException("货款用款金额总和超出提款金额");
            }
        }
        return  purposes;
    }

    @Override
    public Double selectPurposeSum(Long withdrawId) {
        return purposeMapper.selectPurposeSum(withdrawId);
    }
}