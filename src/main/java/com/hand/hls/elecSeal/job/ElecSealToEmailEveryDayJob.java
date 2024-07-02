package com.hand.hls.elecSeal.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.mail.dto.Message;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.service.impl.MessageServiceImpl;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.bp.mapper.HlsCusBpMasterContactInfoMapper;
import com.hand.hls.cn.dto.RentInfo;
import com.hand.hls.cn.mapper.RentInfoMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ElecSealToEmailEveryDayJob extends AbstractJobWithIRequest {

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjProjectService service;
    @Autowired
    private HlsCusBpMasterContactInfoMapper hlsCusBpMasterContactInfoMapper;
    @Autowired
    private RentInfoMapper rentInfoMapper;
    @Autowired
    private MessageServiceImpl messageService;
    private static final String Y = "Y";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext context, IRequest iRequest) throws Exception {
        //第一步：找出所有的符合条件合同
        List<HlsCusPrjProject> hlsCusPrjProjectList = hlsCusPrjProjectMapper.conRentQueryAll3();
        for (HlsCusPrjProject cusPrjProject : hlsCusPrjProjectList) {
            RentInfo rentInfo=new RentInfo();
            rentInfo.setProjectId(cusPrjProject.getProjectId());
            List<RentInfo> rentInfoList = rentInfoMapper.conRentCashQueryAll1(rentInfo);
            Long cashflowId = null;
            Boolean flag=false;
            for (RentInfo info : rentInfoList) {
                if(info.getDueDate().getTime()>new Date().getTime()){
                    continue;
                }
                int days = (int) ((info.getDueDate().getTime()-new Date().getTime()) / (1000*3600*24));
                if(days<=15&&info.getWriteOffFlag()!="FULL"){
                    flag=true;
                    cashflowId= Long.valueOf(info.getCashflowId());
                    break;
                }
            }
            if(!flag){
               continue;
            }
            //第二步：将这些符合条件的合同进行world转pdf盖章，得到一个list

            cusPrjProject.setDocxFlag(Y);
            cusPrjProject.setCreateContractStatus("CREATED");
            cusPrjProject.setTemplateCode("COLLECTION_BOOKS");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(cusPrjProject);
            //自动生成合同文本
            List<FndAttachment> list = new ArrayList<>();
            try {
                list = service.reportCreateDocx(iRequest, cusPrjProject, cashflowId);
            }catch (Exception e){
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            //第三步：找到客户的email
            HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo=new HlsCusBpMasterContactInfo();
            hlsCusBpMasterContactInfo.setBpId(cusPrjProject.getTenantId());
            List<HlsCusBpMasterContactInfo> hlsCusBpMasterContactInfos = hlsCusBpMasterContactInfoMapper.queryAll(hlsCusBpMasterContactInfo);
            List<MessageReceiver> receivers=new ArrayList<>();
            for (HlsCusBpMasterContactInfo cusBpMasterContactInfo : hlsCusBpMasterContactInfos) {
                MessageReceiver messageReceiver=new MessageReceiver();
                messageReceiver.setMessageType("NORMAL");
                messageReceiver.setMessageAddress(cusBpMasterContactInfo.getEmail());
                receivers.add(messageReceiver);
            }
            //第四步：发送邮件
            String templateCode="Pdf_To_Email_Test";
            Map<String, Object> templateData=new HashMap<>();
            List<Long> attachmentIds=new ArrayList<>();
            attachmentIds.add(list.get(0).getAttachmentId());
            if(receivers.size()==0){
                continue;
            }
            messageService.sendMessageWithFile(iRequest, templateCode, templateData, receivers, attachmentIds);

        }

    }
}
