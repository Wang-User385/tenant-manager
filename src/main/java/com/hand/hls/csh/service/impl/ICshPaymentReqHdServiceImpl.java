package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ICshPaymentReqHdServiceImpl extends BaseServiceImpl<HlsCusCshPaymentReqHd> implements ICshPaymentReqHdService {
    @Autowired
    private HlsCusCshPaymentReqHdMapper mapper;

    public ICshPaymentReqHdServiceImpl() {
    }

    @Override
    public List<HlsCusCshPaymentReqHd> queryAll(HlsCusCshPaymentReqHd paymentReqHd, int page, int pageSzie) {
        Map<String, Object> params = new HashMap();
        List<String> list_req = new ArrayList();
        List<String> list_flag = new ArrayList();
        String[] payment_req_status = null;
        String[] payment_flag = null;
        int j;
        if (paymentReqHd.getPaymentReqStatus() != null) {
            payment_req_status = paymentReqHd.getPaymentReqStatus().split(",");

            for (j = 0; j < payment_req_status.length; ++j) {
                list_req.add(payment_req_status[j]);
            }

            params.put("list_req", list_req);
        }

        if (paymentReqHd.getPaymentFlag() != null) {
            payment_flag = paymentReqHd.getPaymentFlag().split(",");

            for (j = 0; j < payment_flag.length; ++j) {
                list_flag.add(payment_flag[j]);
            }

            params.put("list_flag", list_flag);
        }

        params.put("payment_req_id", paymentReqHd.getPaymentReqId());
        params.put("payment_req_number", paymentReqHd.getPaymentReqNumber());
        params.put("payment_amount_1", paymentReqHd.getPaymentAmount1());
        params.put("payment_amount_2", paymentReqHd.getPaymentAmount2());
        params.put("payment_amount_3", paymentReqHd.getPaymentAmount3());
        params.put("amount_from", paymentReqHd.getAmountFrom());
        params.put("amount_to", paymentReqHd.getAmountTo());
        PageHelper.startPage(page, pageSzie);
        return this.mapper.queryAll(params);
    }

    @Override
    public HlsCusCshPaymentReqHd queryCshPaymentHdById(Long payment_req_id) {
        return this.mapper.queryCshPaymentHdById(payment_req_id);
    }

    @Override
    public List<HlsCusCshPaymentReqHd> queryApply(Long contractId) {
        return this.mapper.queryApply(contractId);
    }
}
