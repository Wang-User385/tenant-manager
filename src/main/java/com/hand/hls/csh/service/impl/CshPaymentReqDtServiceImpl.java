//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.service.CshPaymentReqDtService;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class CshPaymentReqDtServiceImpl extends BaseServiceImpl<HlsCusCshPaymentReqDt> implements CshPaymentReqDtService {
    @Autowired
    HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;
    @Autowired
    HlsCusConContractCashflowService hlsCusConContractCashflowService;

    public CshPaymentReqDtServiceImpl() {
    }
    @Override
    public List<HlsCusCshPaymentReqDt> queryCshPaymentReqDt(IRequest iRequest, HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.hlsCusCshPaymentReqDtMapper.queryCshPaymentReqDt(hlsCusCshPaymentReqDt);
    }

    /**
     * 删除抵扣数据
     *
     * @param hlsCusCshPaymentReqDt
     */
    @Override
    public void deleteDeductInfo(HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt) {
        hlsCusCshPaymentReqDtMapper.delete(hlsCusCshPaymentReqDt);
    }

    @Override
    public List<HlsCusCshPaymentReqDt> cshPaymentReqDtCreate(IRequest iRequest, List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList) throws ResMessageException {
        Double totalDeductionAmount = 0.0D;

        HlsCusCshPaymentReqDt item;
        for(Iterator var4 = hlsCusCshPaymentReqDtList.iterator(); var4.hasNext(); totalDeductionAmount = MathUtil.add(totalDeductionAmount, item.getDeductAmount())) {
            item = (HlsCusCshPaymentReqDt)var4.next();
            if (item.getPaymentReqDtId() == null) {
                item = (HlsCusCshPaymentReqDt)((CshPaymentReqDtService)this.self()).insertSelective(iRequest, item);
            } else {
                item = (HlsCusCshPaymentReqDt)((CshPaymentReqDtService)this.self()).updateByPrimaryKeySelective(iRequest, item);
            }
        }

        Double canDeductionAmount = ((HlsCusCshPaymentReqDt)hlsCusCshPaymentReqDtList.get(0)).getCanDeductionAmount();
        Double subAmount = MathUtil.sub(canDeductionAmount, totalDeductionAmount);
        if (subAmount.compareTo(0.0D) < 0) {
            throw new ResMessageException("抵扣金额超出限制!");
        } else {
            return hlsCusCshPaymentReqDtList;
        }
    }
    @Override
    public List<HlsCusCshPaymentReqDt> cshPaymentReqDtSave(IRequest iRequest, List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList) throws ResMessageException {
        for (int i = 0; i < hlsCusCshPaymentReqDtList.size(); i++) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(hlsCusCshPaymentReqDtList.get(i).getSourceDocLineId());
            hlsCusConContractCashflow.setFundingPlanStatus("REPORTING");
            hlsCusConContractCashflow.setWhetherDeduct("Y");
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,hlsCusConContractCashflow);
        }
        return hlsCusCshPaymentReqDtList;
    }
    @Override
    public List<HlsCusCshPaymentReqDt> cshPaymentReqDtDelete(IRequest iRequest, List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList) throws ResMessageException {
        for (int i = 0; i < hlsCusCshPaymentReqDtList.size(); i++) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(hlsCusCshPaymentReqDtList.get(i).getSourceDocLineId());
            hlsCusConContractCashflow.setFundingPlanStatus("");
            hlsCusConContractCashflow.setWhetherDeduct("N");
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,hlsCusConContractCashflow);
        }
        return hlsCusCshPaymentReqDtList;
    }
}
