package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.dto.CshDepositDeductReqLn;
import com.hand.hls.csh.mapper.CshDepositDeductReqLnMapper;
import com.hand.hls.csh.service.ICshDepositDeductReqLnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshDepositDeductReqLnServiceImpl extends BaseServiceImpl<CshDepositDeductReqLn> implements ICshDepositDeductReqLnService {
    @Autowired
    private CshDepositDeductReqLnMapper cshDepositDeductReqLnMapper;
    /*@Autowired
    private CshDepositWriteOffHisMapper cshDepositWriteOffHisMapper;*/

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<CshDepositDeductReqLn> selectCshDepositDeductReqLnList(IRequest request, CshDepositDeductReqLn cshDepositDeductReqLn, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<CshDepositDeductReqLn> cshDepositDeductReqLnList = cshDepositDeductReqLnMapper.selectCshDepositDeductReqLnList(cshDepositDeductReqLn);
        return cshDepositDeductReqLnList;
    }

    @Override
    public Long selectUnDeductCashflowMaxTimes(CshDepositDeductReqHd cshDepositDeductReqHd) {
        Long maxTimes = cshDepositDeductReqLnMapper.selectUnDeductCashflowMaxTimes(cshDepositDeductReqHd);
        return maxTimes;
    }

    @Override
    public Long queryNextPkValue() {
        return cshDepositDeductReqLnMapper.queryNextPkValue();
    }


    @Override
    public void insertDepositWriteOffHistoryByDeduct(Long reqLnId,Double writeOffSum){
        /*try {
            cshDepositWriteOffHisMapper.insertDepositWriteOffHistoryByDeduct(reqLnId,writeOffSum);
        } catch (Exception e) {
            logger.error("insertDepositWriteOffHistoryByDeduct error reqLnId:{}",reqLnId,e);
        }*/
    }
}