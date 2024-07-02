package com.hand.hls.ast.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.ast.service.INoticeManageService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.mapper.HlsCusFctQuotationCashflowMapper;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.sys.service.IFndCompanyService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 通知书job
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/4/16 11:12
 */
public class AstNoticeManageJob extends AbstractJob {

    @Autowired
    private HlsCusConContractCashflowMapper mapper;
    @Autowired
    private INoticeManageService noticeService;
    @Autowired
    private IFndCompanyService fndCompanyService;


    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        //实际支付表
//        List<HlsCusConContractCashflow> actualPayList = mapper.selectActualPayList();
//        if(CollectionUtils.isNotEmpty(actualPayList)){
//            for(HlsCusConContractCashflow cashflow : actualPayList){
//                NoticeManage noticeManage = new NoticeManage();
//                noticeManage.setContractId(cashflow.getContractId());
//                noticeManage.setBpName(cashflow.getBpName());
//                noticeManage.setSendingObject(cashflow.getBpName());
//                noticeManage.setNoticeType(cashflow.getNoticeType());
//                noticeManage.setTempletId(174L);
//                noticeManage.setDueAmount(cashflow.getDueAmount());
//                noticeManage.set__status("insert");
//                IRequest iRequest = RequestHelper.newEmptyRequest();
//                iRequest.setUserId(10001L);
//                noticeService.insertSelective(iRequest,noticeManage);
//            }
//        }
        //支付通知书
        List<HlsCusConContractCashflow> payList = mapper.selectPayList();
        if(CollectionUtils.isNotEmpty(payList)){
            for(HlsCusConContractCashflow cashflow : payList){
                NoticeManage noticeManage = new NoticeManage();
                noticeManage.setContractId(cashflow.getContractId());
                noticeManage.setCashflowId(cashflow.getCashflowId());
                noticeManage.setBpName(cashflow.getBpName());
                noticeManage.setSendingObject(cashflow.getBpName());
                noticeManage.setNoticeType(cashflow.getNoticeType());
                noticeManage.setTempletId(170L);
                noticeManage.setDueAmount(cashflow.getDueAmount());
                noticeManage.set__status("insert");
                IRequest iRequest = RequestHelper.newEmptyRequest();
                iRequest.setUserId(10001L);
                noticeService.insertSelective(iRequest,noticeManage);
            }
        }
    }
}
