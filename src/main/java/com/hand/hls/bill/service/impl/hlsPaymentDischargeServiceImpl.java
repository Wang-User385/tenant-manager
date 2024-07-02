package com.hand.hls.bill.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bill.dto.hlsPaymentDischarge;
import com.hand.hls.bill.mapper.hlsPaymentDischargeMapper;

import com.hand.hls.bill.service.IhlsPaymentDischargeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.mail.dto.Message;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hap.mail.service.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LiJingJing
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class hlsPaymentDischargeServiceImpl extends BaseServiceImpl<hlsPaymentDischarge> implements IhlsPaymentDischargeService{
    @Autowired
    private hlsPaymentDischargeMapper mapper;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlscuscshpaymentreqhdmapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlscusprjprojectbpmapper;
    @Autowired
    private IMessageService messageService;
    private static final String EMAIL = "EMAIL";
    private static final String P = "P";
    private static final String BILL = "BILL";
    private static final String NORMAL = "NORMAL";
    private static final String SNZL = "SNZL";
    private static final Logger logger = LoggerFactory.getLogger(hlsBillRequestServiceImpl.class);
    @Autowired
    com.hand.hls.bill.mapper.hlsBillRequestMapper hlsBillRequestMapper;
    @Override
    public List<hlsPaymentDischarge>lonCreditBpLovQuery(hlsPaymentDischarge hlsPaymentDischarge, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<hlsPaymentDischarge> list = mapper.lonCreditBpLovQuery(hlsPaymentDischarge);
        return list;
    }

    @Override
    public void sendEmail(IRequest iRequest, hlsPaymentDischarge hlsPaymentDischarge) {
        Long paymentDischargeId = hlsPaymentDischarge.getPaymentDischargeId();
        hlsPaymentDischarge = mapper.selectByPrimaryKey(paymentDischargeId);
        Long billId = hlsPaymentDischarge.getBillId();
        hlsBillRequest hlsBillRequest = new hlsBillRequest();
        hlsBillRequest.setBillId(billId);
        hlsBillRequest = hlsBillRequestMapper.selectByPrimaryKey(hlsBillRequest);
        HlsCusCshPaymentReqHd hlscuscshpaymentreqhd = new HlsCusCshPaymentReqHd();
        hlscuscshpaymentreqhd.setPaymentReqId(hlsBillRequest.getPaymentReqId());
        hlscuscshpaymentreqhd = hlscuscshpaymentreqhdmapper.selectByPrimaryKey(hlscuscshpaymentreqhd);
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlscuscshpaymentreqhd.getSourceDocId());
        String bpName;
        if (hlsCusPrjProject.getBusinessType().equalsIgnoreCase("LEASE_WITHOUT_RECOURSE_IN") || hlsCusPrjProject.getBusinessType().equalsIgnoreCase("OPERATING_LEASE_WITHOUT_RECOURSE_IN")) {
            HlsCusPrjProjectBp  hlsCusPrjProjectBp=new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectBp.setBpType("ASSET_TRANSFEROR");
            List<HlsCusPrjProjectBp> hlsCusPrjProjectBps = hlscusprjprojectbpmapper.selectByForeignKey1(hlsCusPrjProjectBp);
            bpName=hlsCusPrjProjectBps.get(0).getBpName();

        } else  if (hlsCusPrjProject.getBusinessType().equalsIgnoreCase("T+0_LEASE_FACTORING") || hlsCusPrjProject.getBusinessType().equalsIgnoreCase("T+N_LEASE_FACTORING")) {
            HlsCusPrjProjectBp  hlsCusPrjProjectBp=new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectBp.setBpType("ACCOUNTS_RECEIVABLE");
            List<HlsCusPrjProjectBp> hlsCusPrjProjectBps = hlscusprjprojectbpmapper.selectByForeignKey1(hlsCusPrjProjectBp);
            bpName=hlsCusPrjProjectBps.get(0).getBpName();
        }else {
            HlsCusPrjProjectBp  hlsCusPrjProjectBp=new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectBp.setBpType("TENANT");
            List<HlsCusPrjProjectBp> hlsCusPrjProjectBps = hlscusprjprojectbpmapper.selectByForeignKey1(hlsCusPrjProjectBp);
            bpName=hlsCusPrjProjectBps.get(0).getBpName();
        }

            String content = new StringBuilder().append(bpName).append(",您好！").append("付款申请编号【").append(hlscuscshpaymentreqhd.getPaymentReqNumber()).append("】，票据申请编号【").append(hlsBillRequest.getBillApplyNumber()).append("】的国际信用证，本次解付比例【")
                .append(hlsPaymentDischarge.getPaymentDischargeRadio()).append("】%，解付当日汇率【")
                .append(hlsPaymentDischarge.getPaymentDischargeRate()).append("】，本次解付金额【")
                .append(hlsPaymentDischarge.getPaymentDischargeAmount()).append("】元，本次解付后票面金额【")
                .append(hlsPaymentDischarge.getCashDischargeAmount()).append("】元，请及时与资金部确认，并在系统发起相关支付表变更申请，感谢！ ").toString();
        List<HlsCusCshPaymentReqHd> hlsEmployeeList = hlscuscshpaymentreqhdmapper.queryEmployeeEmail(hlscuscshpaymentreqhd);

        Message message = new Message();
        message.setMessageType(EMAIL);
        message.setSendFlag(P);
        message.setSubject("解付确认通知");
        message.setContent(content);
        message.setMessageFrom("JC_EMAIL");
        message.setMessageSource(BILL);

        List<MessageReceiver> receiverList = new ArrayList<>();

        MessageReceiver receiver = new MessageReceiver();
        receiver.setMessageAddress(hlsEmployeeList.get(0).getEmail());
        receiver.setMessageType(NORMAL);
        receiverList.add(receiver);
        messageService.insertData(iRequest, message, receiverList, null);
        try {
            emailService.sendSingleEmailMessageWithFile(message, null);
        } catch (Exception e) {
            logger.error("email send error, {}", e);


        }
    }
}