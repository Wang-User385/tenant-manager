package com.hand.hls.common.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.service.HlsBpMasterBankAccountService;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.common.dto.HlsCusItfcBankFlow;
import com.hand.hls.common.service.HlsCusItfcBankFlowService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusItfcBankFlowServiceImpl extends BaseServiceImpl<HlsCusItfcBankFlow> implements HlsCusItfcBankFlowService {

    @Autowired
    private HlsCusBpMasterService bpMasterService;
    @Autowired
    private HlsCusBpMasterBankAccountMapper bankAccountMapper;
    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Override
    public void dealRivalAccount(IRequest iRequest, List<HlsCusItfcBankFlow> bankFlowList){
        for(HlsCusItfcBankFlow item : bankFlowList){
            HlsCusItfcBankFlow bankFlow = self().selectByPrimaryKey(iRequest,item);
            if("AL002".equals(bankFlow.getIoCode()) && bankFlow.getRivalAccountId() == null && item.getBpId() != null){
                HlsCusBpMaster bpMaster = new HlsCusBpMaster();
                bpMaster.setBpId(item.getBpId());
                bpMaster = bpMasterService.selectByPrimaryKey(iRequest,bpMaster);
                HlsCusBpMasterBankAccount bpMasterBankAccount = new HlsCusBpMasterBankAccount();
                bpMasterBankAccount.setBpId(bpMaster.getBpId());
                bpMasterBankAccount.setBankAccountNum(item.getRivalAccountNo());
                List<HlsCusBpMasterBankAccount> list = bankAccountMapper.select(bpMasterBankAccount);
                if(list.size() == 0){
                    bpMasterBankAccount.setBankAccountType("GENERAL_ACCOUNT");
                    bpMasterBankAccount.setBankAccountName(item.getRivalAccountName());
                    bpMasterBankAccount.setBankFullName("财务流水同步");
                    bankAccountMapper.insertSelective(bpMasterBankAccount);
                }

                try {
                    financeBaseUtils.dealFlowItfc(iRequest,bankFlow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}