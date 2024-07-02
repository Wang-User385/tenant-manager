package com.hand.hls.vat.job;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.vat.dto.*;
import com.hand.hls.vat.service.IAcrInvoiceHdService;
import com.hand.hls.vat.service.IAcrInvoiceHdTempService;
import com.hand.hls.vat.service.impl.AcrInvoiceLnServiceImpl;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description:自动开票
 * @author: zhangdan
 * @date: 2022-10-14
 */
@Component
public class HlsCusAcrInvoiceAutoCreateJob extends AbstractJobWithIRequest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    @Autowired
    private IAcrInvoiceHdService invoiceHdService;
    @Autowired
    private AcrInvoiceLnServiceImpl acrInvoiceLnService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private IAcrInvoiceHdTempService acrInvoiceHdTempService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext context,IRequest requestCtx) throws Exception {
        try {
            Object contractNumber= context.getMergedJobDataMap().get("contract_number");

            HlsCusConContractCashflow cashflow =new HlsCusConContractCashflow();
            cashflow.setWriteOffFlag("FULL");
            if (null != contractNumber){
                cashflow.setContractNumber(contractNumber.toString());
            }
            List<HlsCusConContractCashflow> cashflowInvoiceList = cashflowMapper.queryFactoringInvoice(cashflow);
            for(HlsCusConContractCashflow cashflowItem : cashflowInvoiceList){
                AcrInvoiceHdTemp temp = new AcrInvoiceHdTemp();
                String billingType="";
                if("本金".equals(cashflowItem.getCfItemDesc())){
                    billingType = "PRINCIPAL";
                }else if("'利息'".equals(cashflowItem.getCfItemDesc())){
                    billingType = "INTEREST";
                }

                String UUID = java.util.UUID.randomUUID().toString().replaceAll("-", "");
                temp.setInvoiceHdTempId(UUID);
                temp.setCashflowId(cashflowItem.getCashflowId());
                temp.setBillingType(billingType);
                acrInvoiceHdTempService.insertSelective(requestCtx, temp);

                Long cashflowId = Long.valueOf(cashflowItem.getCashflowId().toString());
                List<Long> cashflows = new ArrayList<>();
                cashflows.add(cashflowId);
                List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHds = invoiceHdService.selectForCreate(UUID, cashflows, "N");
                List<AcrInvoiceLn> hlsCusAcrInvoiceLns = acrInvoiceLnService.selectForCreate(cashflows, "N", billingType);
                List<HlsCusAcrInvoiceLn> acrInvoiceLns = new ArrayList<>();
                hlsCusAcrInvoiceLns.forEach((ln) -> {
                    HlsCusAcrInvoiceLn acrInvoiceLn = new HlsCusAcrInvoiceLn();
                    BeanRefUtils.beanToBean(ln, acrInvoiceLn, hlsBeanRefUtilService);
                    acrInvoiceLn.setTotalAmount(MathUtil.sub(acrInvoiceLn.getDueAmount(),acrInvoiceLn.getBillingAmount()));
                    acrInvoiceLns.add(acrInvoiceLn);
                });

                hlsCusAcrInvoiceHds.forEach((item) -> {
                    item.setBillingWay("1");
                    item.setInvoiceDate(new Date());
                    item.setAcrInvoiceLns(acrInvoiceLns);
                });
                invoiceHdService.create(requestCtx,hlsCusAcrInvoiceHds);

                acrInvoiceHdTempService.deleteByPrimaryKey(temp);

                logger.info("===================================================================================");
                logger.info("生成销项发票成功:" + cashflows);
                logger.info("===================================================================================");
                setExecutionSummary("success");
            }
        } catch (Exception e) {
            exception = e;
            logger.info("===================================================================================");
            logger.info("生成销项发票成功:" + e);
            logger.info("===================================================================================");
            this.setExecutionSummary(this.exception.getClass().getName() + ":" + this.exception.getMessage());
        }

    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        //任务发生异常时候进行的动作
        //false 挂起JOB等待处理
        //true 继续执行
        return false;
    }

}
