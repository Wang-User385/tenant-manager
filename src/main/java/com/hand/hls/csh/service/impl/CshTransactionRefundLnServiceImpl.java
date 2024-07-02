package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.CshTransactionRefundLn;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.mapper.CshTransactionRefundLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.CshTransactionRefundLnService;
import com.hand.hls.utils.ResMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshTransactionRefundLnServiceImpl extends BaseServiceImpl<CshTransactionRefundLn> implements CshTransactionRefundLnService {
    @Autowired
    CshTransactionRefundLnMapper cshTransactionRefundLnMapper;

    @Autowired
    private HlsCusCshTransactionRefundMapper hdMapper;

    @Override
    public List<CshTransactionRefundLn> detailQuery(CshTransactionRefundLn cshTransactionRefundLn, int page, int pageszie) {
        PageHelper.startPage(page, pageszie);
        return cshTransactionRefundLnMapper.detailQuery(cshTransactionRefundLn);
    }

    @Override
    public List<CshTransactionRefundLn> saveLn(IRequest iRequest, List<CshTransactionRefundLn> list) throws ResMessageException {

        List<CshTransactionRefundLn> refundLnList = self().batchUpdate(iRequest, list);

        //金额校验 行金额 要等于 头金额
        if (!list.isEmpty()) {

            Long refundId = list.get(0).getRefundId();
            CshTransactionRefundLn ln = new CshTransactionRefundLn();
            ln.setRefundId(refundId);
            List<CshTransactionRefundLn> lnList = cshTransactionRefundLnMapper.select(ln);

            Double refundAmount = lnList.stream().collect(Collectors.summingDouble(CshTransactionRefundLn::getRefundAmount));

            HlsCusCshTransactionRefund hd = hdMapper.selectByPrimaryKey(refundId);

            if (refundAmount.compareTo(hd.getRefundAmount()) != 0) {
                throw new ResMessageException("银行账户付款金额总和不等于退款金额！");
            }
        }
        return refundLnList;
    }
}