package com.hand.hls.hls.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsDurationDepositAccount;
import com.hand.hls.hls.mapper.HlsDurationDepositAccountMapper;
import com.hand.hls.hls.service.HlsDurationDepositAccountService;
import com.hand.hls.utils.ResMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationDepositAccountServiceImpl extends BaseServiceImpl<HlsDurationDepositAccount> implements HlsDurationDepositAccountService {
    @Autowired
    private HlsDurationDepositAccountMapper hlsDurationDepositAccountMapper;

    @Override
    public List<HlsDurationDepositAccount> hlsDurationDepositAccountDetailQuery(HlsDurationDepositAccount hlsDurationDepositAccount, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsDurationDepositAccountMapper.hlsDurationDepositAccountDetailQuery(hlsDurationDepositAccount);

    }

    @Override
    public List<HlsDurationDepositAccount> hlsDurationDepositAccountSave(IRequest iRequest, List<HlsDurationDepositAccount> list) throws ResMessageException {
        if (list.size() == 0) {
            throw new ResMessageException("退款金额与本方账户出资金额不等！");
        }
        Long depositId = list.get(0).getDepositId();
        Double refundAmount = hlsDurationDepositAccountMapper.getRefundAmount(depositId);
        Double refundAmountTotal = list.stream().collect(Collectors.summingDouble(HlsDurationDepositAccount::getRefundAmount)).doubleValue();

        if (refundAmount.compareTo(refundAmountTotal) != 0) {
            throw new ResMessageException("退款金额与本方账户出资金额不等！");
        }

        for (HlsDurationDepositAccount account : list) {
            if (account.getAccountId() == null) {
                self().insertSelective(iRequest, account);
            } else {
                self().updateByPrimaryKeySelective(iRequest, account);
            }
        }
        return list;
    }
}