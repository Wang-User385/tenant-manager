package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.csh.mapper.HlsCusDepositDeductionHdMapper;
import com.hand.hls.csh.mapper.HlsCusDepositDeductionMapper;
import com.hand.hls.csh.mapper.HlsCusDepositRefundMapper;
import com.hand.hls.csh.service.HlsCusDepositDeductionHdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositDeductionHdServiceImpl extends BaseServiceImpl<HlsCusDepositDeductionHd> implements HlsCusDepositDeductionHdService {

    @Autowired
    private HlsCusDepositDeductionHdMapper depositDeductionHdMapper;

    @Autowired
    private HlsCusDepositDeductionMapper depositDeductionMapper;

    @Autowired
    private HlsCusDepositRefundMapper depositRefundMapper;

    @Override
    public List<HlsCusDepositDeductionHd> selectHlsCusDepositHeaderData(IRequest iRequest, HlsCusDepositDeductionHd depositDeductionHd, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);

        return depositDeductionHdMapper.selectHlsCusDepositHeaderData(depositDeductionHd);
    }


    @Override
    public HlsCusDepositDeductionHd selectHlsCusDepositDeductionData(IRequest iRequest, Long depositDeductionHdId) {
        return depositDeductionHdMapper.selectHlsCusDepositDeductionData(depositDeductionHdId);
    }

    @Override
    public void deleteDepositDeductionHd(IRequest iRequest, HlsCusDepositDeductionHd depositDeductionHd) {
        if ("DEPOSIT_DEDUCT_CREDIT".equals(depositDeductionHd.getDeductionType()) || "DEPOSIT_ADD_CREDIT".equals(depositDeductionHd.getDeductionType())) {
            depositDeductionHd = depositDeductionHdMapper.selectByPrimaryKey(depositDeductionHd);
            if ("APPROVED".equals(depositDeductionHd.getDepositStatus()) || "APPROVING".equals(depositDeductionHd.getDepositStatus())) {
                throw new RuntimeException("该状态不可删除，请刷新！");
            } else {
                //删除行数据
                HlsCusDepositDeduction deduction = new HlsCusDepositDeduction();
                deduction.setDepositDeductionHdId(depositDeductionHd.getDepositDeductionHdId());
                depositDeductionMapper.delete(deduction);

                //删除头数据
                depositDeductionHdMapper.deleteByPrimaryKey(depositDeductionHd);

            }
        } else {
            HlsCusDepositRefund hlsCusDepositRefund = new HlsCusDepositRefund();
            hlsCusDepositRefund.setDepositRefundId(depositDeductionHd.getDepositDeductionHdId());
            hlsCusDepositRefund = depositRefundMapper.selectByPrimaryKey(hlsCusDepositRefund);
            if ("APPROVED".equals(hlsCusDepositRefund.getRefundStatus()) || "APPROVING".equals(hlsCusDepositRefund.getRefundStatus())) {
                throw new RuntimeException("该状态不可删除，请刷新！");
            } else {
                depositRefundMapper.deleteByPrimaryKey(hlsCusDepositRefund);
            }
        }
    }

    @Override
    public Map<String, Long> selectProjectUnitAndCompany(String deductionDocCategory, Long contractId) {
        return depositDeductionHdMapper.selectProjectUnitAndCompany(deductionDocCategory, contractId);
    }
}