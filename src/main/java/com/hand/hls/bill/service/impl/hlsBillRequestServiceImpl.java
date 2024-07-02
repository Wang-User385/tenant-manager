package com.hand.hls.bill.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bill.service.IhlsPaymentDischargeService;
import com.hand.hls.bill.service.IhlsRefundDepositService;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bill.dto.hlsBillRequest;


import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;
import com.hand.hls.bill.mapper.hlsRefundDepositMapper;

import com.hand.hls.bill.dto.hlsPaymentDischarge;
import com.hand.hls.bill.dto.hlsRefundDeposit;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class hlsBillRequestServiceImpl extends BaseServiceImpl<hlsBillRequest> implements IhlsBillRequestService {
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    @Autowired
    hlsBillRequestMapper hlsBillRequestMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private IhlsBillRequestService hlsBillRequestService;

    @Autowired
    private IhlsPaymentDischargeService hlsPaymentDischargeService;
    @Autowired
    private IhlsRefundDepositService hlsRefundDepositService;

    @Autowired
    private hlsRefundDepositMapper hlsRefundDepositMapper;

    private void approveWfl(IRequest iRequest, hlsBillRequest hlsBillRequest) throws ResMessageException {
        hlsBillRequest = hlsBillRequestMapper.selectByPrimaryKey(hlsBillRequest);
        if (APPROVED.equals(hlsBillRequest.getBillStatus()) || APPROVING.equals(hlsBillRequest.getBillStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(hlsBillRequest);

        List<hlsBillRequest> cs = new ArrayList<>();
        cs.add(hlsBillRequest);
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "BILL_APPLICATION_WFL");
        params.put("billId", hlsBillRequest.getBillId());
        params.put("billAmount", hlsBillRequest.getBillAmount());
        params.put("documentName", hlsBillRequest.getBillApplyNumber());
        params.put("documentNumber", hlsBillRequest.getBillApplyNumber());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "BILL_APPLICATION_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "BILL_APPLICATION_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, hlsBillRequest.getBillId());
        params.put("documentCategory", "HLS_BILL_REQUEST");
        params.put("hlsBillRequest", JSON.toJSONString(hlsBillRequest));
        params.put("startUserName", iRequest.getUserName());
        activitiStartService.start(iRequest, cs, params);

        hlsBillRequest contractInsure = new hlsBillRequest();
        contractInsure.setBillId(hlsBillRequest.getBillId());
        contractInsure.setBillStatus(APPROVING);
        hlsBillRequestService.updateByPrimaryKeySelective(iRequest, contractInsure);

    }



    @Override
    public List<hlsBillRequest> insureSubmit(IRequest iRequest, hlsBillRequest hlsBillRequest) throws ResMessageException, ParameterNullException {


        //启动工作流
        approveWfl(iRequest, hlsBillRequest);

        List<hlsBillRequest> contractInsure = new ArrayList<>();
        contractInsure.add(hlsBillRequest);
        return contractInsure;
    }

    @Override
    public void bpAssetsInit(IRequest iRequest, hlsBillRequest hlsBillRequest) {
        Long billId = hlsBillRequest.getBillId();
        hlsBillRequest = hlsBillRequestMapper.selectByPrimaryKey(hlsBillRequest);

        hlsPaymentDischarge productCollection = new hlsPaymentDischarge();
        productCollection.setBillId(billId);
        productCollection.setStatus("CONFIRM");
        Double amount = 0D;
        Double amount2 = 0D;
        Double amount3 = 0D;
        List<hlsPaymentDischarge> productCollections = hlsPaymentDischargeService.select(iRequest, productCollection, 1, 9999);
        if (productCollections.size() != 0) {
            for (int i = 0; i < productCollections.size(); i++) {

                    amount =amount+ productCollections.get(i).getPaymentDischargeAmount();
                    amount3 =amount3+ productCollections.get(i).getReleaseMarginAmount();


            }

        }
        hlsRefundDeposit hlsCusFundingPlanLn = new hlsRefundDeposit();
        hlsCusFundingPlanLn.setBillId(billId);
        hlsCusFundingPlanLn.setStatus("CONFIRM");
        List<hlsRefundDeposit> hlsCusFundingPlanLns = hlsRefundDepositMapper.queryHlsRefundDeposit(hlsCusFundingPlanLn);
        if(hlsCusFundingPlanLns.size()!=0){
            for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
                amount2 =amount2+ hlsCusFundingPlanLns.get(i).getRefundDepositAmount();

            }
        }
        hlsBillRequest contractInsure = new hlsBillRequest();
        contractInsure.setBillId(hlsBillRequest.getBillId());
        contractInsure.setBillAmountBalance(Double.valueOf(hlsBillRequest.getBillAmount()-amount));
        contractInsure.setMarginBalance(Double.valueOf(hlsBillRequest.getMargeAmount()-amount3-amount2));
        Double billAmountBalance=OracleUtils.nvl(Double.valueOf(hlsBillRequest.getBillAmountBalance()), 0.0);
        Double marginBalance=OracleUtils.nvl(Double.valueOf(hlsBillRequest.getMarginBalance()), 0.0);

    if(billAmountBalance.compareTo(0.0)==0 && marginBalance.compareTo(0.0)==0){
          contractInsure.setBillStatus("PAYMENT_DISCHARGE");

     }
        hlsBillRequestService.updateByPrimaryKeySelective(iRequest, contractInsure);


    }
}