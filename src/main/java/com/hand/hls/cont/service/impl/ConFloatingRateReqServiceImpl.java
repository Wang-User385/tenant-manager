package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqLnMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.ConFloatingRateReqService;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/16 - 14:16
 */

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class ConFloatingRateReqServiceImpl extends BaseServiceImpl<HlsCusConFloatingRateReq> implements ConFloatingRateReqService {
    private static final String APPROVING_STATUS = "APPROVING";
    @Autowired
    private HlsCusConFloatingRateReqMapper mapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusConFloatingRateReqLnMapper hlsCusConFloatingRateReqLnMapper ;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Autowired
    private HlsCusConContractService contractService;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowService conContractCashflowService;

    public ConFloatingRateReqServiceImpl() {
    }

    public List<HlsCusConFloatingRateReq> queryConFloatingRateReq(IRequest iRequest, HlsCusConFloatingRateReq conFloatingRateReq, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.mapper.queryConFloatingRateReq(conFloatingRateReq);
    }

    public ResponseData submitFloatingRateWfl(IRequest iRequest, HlsCusConFloatingRateReq rateReq) {
        if (rateReq == null) {
            return new ResponseData(false, "请求参数有误,请联系管理员");
        } else {
            HlsCusConFloatingRateReq hlsCusConFloatingRateReq = (HlsCusConFloatingRateReq)this.mapper.selectByPrimaryKey(rateReq);


            Map<String, Object> param = new HashMap();
            param.put("workFlowType", "CON_FLOAT_RATE");
            param.put("documentCategory", "CON_FLOATING_RATE");
            param.put("documentId", rateReq.getFltReqId());
            param.put("documentName", rateReq.getDescription());
            param.put("documentNumber", rateReq.getFltReqNumber());
            param.put("BUSINESS_KEY", rateReq.getFltReqId());
            this.activitiStartService.start(iRequest, Collections.singletonList(hlsCusConFloatingRateReq), param);
            //暂挂调息支付表
            HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
            hlsCusConFloatingRateReqLn.setFltReqId(hlsCusConFloatingRateReq.getFltReqId());
            List<HlsCusConFloatingRateReqLn> hlsCusConFloatingRateReqLnList =  hlsCusConFloatingRateReqLnMapper.queryFloatByReqId(hlsCusConFloatingRateReqLn);
            for(HlsCusConFloatingRateReqLn cusConFloatingRateReqLn  : hlsCusConFloatingRateReqLnList){
                HlsCusConContract conContract = new HlsCusConContract();
                conContract.setContractId(cusConFloatingRateReqLn.getContractId());
                conContract.setContractStatus("PENDING");
                contractService.updateByPrimaryKeySelective(iRequest,conContract);
            }
            //更新调息状态
            hlsCusConFloatingRateReq.setStatus("APPROVING");
            this.mapper.updateByPrimaryKeySelective(hlsCusConFloatingRateReq);
            return new ResponseData(true);
        }
    }

    @Override
    public String confirmOldCashflow(IRequest requestContext, HlsCusConFloatingRateReq conFloatingRateReq) {

        HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        hlsCusConFloatingRateReqLn.setFltReqId(conFloatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> hlsCusConFloatingRateReqLnList =  hlsCusConFloatingRateReqLnMapper.queryFloatByReqId(hlsCusConFloatingRateReqLn);

        //遍历调息行表 ，更新原来的con_contract_cashflow
        for(HlsCusConFloatingRateReqLn cusConFloatingRateReqLn  : hlsCusConFloatingRateReqLnList){

            Long quotationIdOld = cusConFloatingRateReqLn.getQuotationId();
            Long quotationIdNew = cusConFloatingRateReqLn.getQuotationIdNew();
            Long contractId = cusConFloatingRateReqLn.getContractId();

            //更新原来的con_contract_cashflow  - 现有的prj_quotation_cashflow 找到原来的con_contract_cashflow  , update
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotation.setQuotationId(cusConFloatingRateReqLn.getQuotationIdNew());

            hlsCusPrjQuotationCashflow.setQuotationId(quotationIdNew);
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList =  hlsCusPrjQuotationCashflowMapper.queryCashByQuotation(hlsCusPrjQuotationCashflow);
            for(HlsCusPrjQuotationCashflow cusPrjQuotationCashflow : hlsCusPrjQuotationCashflowList){

                HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                contractCashflow.setContractId(contractId);
//                contractCashflow.setQuotationId(quotationIdOld);
                contractCashflow.setDueDate(cusPrjQuotationCashflow.getDueDate());
                contractCashflow.setTimes(cusPrjQuotationCashflow.getTimes());
                contractCashflow.setCfType(cusPrjQuotationCashflow.getCfType());
                contractCashflow.setCfItem(cusPrjQuotationCashflow.getCfItem());

                //根据  due_date ,contract_id , quotation_id_old , cf_item ,cf_type ,times ,找到原来的进行update
                List<HlsCusConContractCashflow> contractCashflows =  conContractCashflowMapper.queryOldCashflowByQuotation(contractCashflow);
                if(contractCashflows.size() == 0){
                    return "false";
                }
            }

        }

        return "true";
    }



}
