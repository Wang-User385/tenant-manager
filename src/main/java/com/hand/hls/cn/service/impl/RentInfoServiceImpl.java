package com.hand.hls.cn.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mail.ReceiverTypeEnum;
import com.hand.hap.mail.controllers.MessageController;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.mapper.MessageReceiverMapper;
import com.hand.hap.mail.mapper.MessageTransactionMapper;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.cn.dto.RentInfo;
import com.hand.hls.cn.service.IRentInfoService;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class RentInfoServiceImpl extends BaseServiceImpl<RentInfo> implements IRentInfoService {
    private static final String TEMPLATE_CODE = "JC_CSH";
    private static final String FILE_NAME = "担保人告知函";
    @Autowired
    private IMessageService messageService;
    @Autowired
    private MessageController messageController;
    @Autowired
    private MessageTransactionMapper messageTransactionMapper;
    @Autowired
    private MessageReceiverMapper messageReceiverMapper;
    @Autowired
    private HlsCusPrjProjectMapper projectMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    /*租金催收邮件发送*/
    @Override
    public void sendEmail(IRequest request, List<HlsCusPrjProjectAttachment> attachments) throws hls.core.utils.exception.HlsCusException {
        //1.获取承租人id以及附件ID
        Long tenantId = attachments.get(0).getTenantId();
        Long projectId = attachments.get(0).getProjectId();
        List<Long> attachmentList = new ArrayList();
        List<File> fileList = new ArrayList<>(1);
        for (HlsCusPrjProjectAttachment attachment : attachments) {
            Long attachmentId = attachment.getAttachmentIdList();
            attachmentList.add(attachmentId);
            //查找物理文件
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("HLS_PRJ_PROJECT_DOCX4");
            fndAttachmentMulti.setTablePkValue(attachment.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> selectdFndAttachmentMultis = this.fndAttachmentMultiMapper.select(fndAttachmentMulti);
            fndAttachmentMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
            fndAttachmentMulti.setTablePkValue(attachment.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> selectdFndAttachmentMultis1 = this.fndAttachmentMultiMapper.select(fndAttachmentMulti);
            selectdFndAttachmentMultis.addAll(selectdFndAttachmentMultis1);
            if (CollectionUtils.isNotEmpty(selectdFndAttachmentMultis)) {
                for (FndAttachmentMulti multi : selectdFndAttachmentMultis) {
                    FndAttachment fndAttachment = new FndAttachment();
                    fndAttachment.setAttachmentId(multi.getAttachmentId());
                    fndAttachment = fndAttachmentMapper.selectByPrimaryKey(fndAttachment);
                    File file = null;
                    try {
                        file = new File(fndAttachment.getFilePath());
                    } catch (Exception e) {
                        //附件不存在或者获取不到附件，不报错
                        continue;
                    }
                    if (file.canRead()) {
                        fileList.add(file);
                    }
                }
            }
        }
        //2.邮箱获取
        List<HlsCusPrjProject> prjProjects  = new ArrayList<>();
        if (FILE_NAME.equalsIgnoreCase(attachments.get(0).getDescription())) {
            //担保函则发给担保人
            prjProjects = projectMapper.conRentEmailQueryAllW(projectId);
        } else {
            prjProjects = projectMapper.conRentEmailQueryAll(tenantId);
        }
        if (prjProjects.size() > 0) {
            for (HlsCusPrjProject prjProject : prjProjects) {
                if (prjProject.getEmail().length() > 0) {
                    List<MessageReceiver> receiverList = new ArrayList();
                    //3.确认收件人邮箱
                    String str = prjProject.getEmail();
                    String[] receivers1 = StringUtils.split(str, ";");
                    String[] var10 = receivers1;
                    int var11 = receivers1.length;
                    for (int var12 = 0; var12 < var11; ++var12) {
                        String r = var10[var12];
                        MessageReceiver mr = new MessageReceiver();
                        mr.setMessageAddress(r);
                        mr.setMessageType(ReceiverTypeEnum.NORMAL.getCode());
                        receiverList.add(mr);
                        //3.调用邮件  List<Long> attachmentIds 附件参数
                        try {
                            messageService.sendMessageWithFileJc(request, TEMPLATE_CODE, (Map) null, receiverList, attachmentList, fileList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else{
            throw new IllegalArgumentException("该承租人或担保人邮箱未维护，请维护后在发送");
        }

    }
}