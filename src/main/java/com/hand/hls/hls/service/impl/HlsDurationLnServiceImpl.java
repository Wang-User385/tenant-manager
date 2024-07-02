package com.hand.hls.hls.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.utils.ResMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.service.HlsDurationLnService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationLnServiceImpl extends BaseServiceImpl<HlsDurationLn> implements HlsDurationLnService{

    @Autowired
    private HlsDurationLnMapper mapper;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private HlsDurationHdServiceImpl hlsDurationHdService;
    @Override
    public List<HlsDurationLn> hlsDurationLnItemDetailQuery(HlsDurationLn ln) {
        return mapper.hlsDurationLnItemDetailQuery(ln);
    }

    @Override
    public List<HlsDurationLn> hlsDurationLnWarrantDetailQuery(HlsDurationLn ln) {
        return mapper.hlsDurationLnWarrantDetailQuery(ln);
    }

    /**
     * @Title: executeCheck
     * @Discription: 执行审批通过前 校验
     * @Param: [hdId]
     * @Return: java.lang.String
     */
    @Override
    public void executeCheck(Long contractId,Date ChangeDate) throws ResMessageException {
        //计算日之后的现金流 不能发生过核销
        if (contractId != null && ChangeDate != null) {
            Example conContractCashflowExample = new Example(HlsCusConContractCashflow.class);
            //计算日之后的现金流 不能发生过核销
            conContractCashflowExample.createCriteria().andEqualTo("contractId", contractId).
                    andGreaterThan("dueDate", ChangeDate).
                    andNotEqualTo("writeOffFlag", "NOT").
                    andEqualTo("cfItem", 1L).
                    andEqualTo("cfStatus", "RELEASE");
            List<HlsCusConContractCashflow> cashflowList = conContractCashflowMapper.selectByExample(conContractCashflowExample);
            if (cashflowList.size() > 0) {
                throw new ResMessageException("执行日之后的现金流发生过核销，请先核销反冲！");
            }
        }
    }

    @Override
    public List<HlsDurationLn> hlsLnChangeDateUpdate(IRequest iRequest, HlsDurationLn ln) throws ResMessageException {
        Date ChangeDate = ln.getChangeDate();
        Long contractId = ln.getContractId();
        HlsDurationLn lnQuery = new HlsDurationLn();
        lnQuery.setChangeDate(ChangeDate);
        lnQuery.setContractId(contractId);
        //选择变更日需要校验是否存在核销
        executeCheck(contractId,ChangeDate);
        List<HlsDurationLn> lnList = mapper.hlsDurationLnChangeDateQuery(lnQuery);
        if(lnList.size()>0){
            return lnList;
        }else{
            throw new ResMessageException("请选择合适的变更起始日！");
        }
    }
}