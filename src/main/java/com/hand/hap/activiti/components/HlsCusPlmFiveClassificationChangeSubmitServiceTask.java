package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;

import com.hand.hls.plm.dto.HlsCusPlmAttachment;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.plm.mapper.HlsCusPlmAttachmentMapper;
import com.hand.hls.plm.service.HlsCusPlmIAttachmentService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:工作流结束保存事件
 * @Author: wty
 * @Date: Created in 16:51 2018/5/21
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmFiveClassificationChangeSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusIFiveClassificationService fcService;


    @Autowired
    private HlsCusPlmIAttachmentService plmIAttachmentService;

    @Autowired
    private HlsCusPlmAttachmentMapper plmAttachmentMapper;

    @Autowired
    private HlsCusIFiveClassificationContractService fiveClassificationContractService;

    @Autowired
    private HlsCusFiveClassificationContractMapper fiveClassificationContractMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String fiveClassification = (String) delegateExecution.getVariable("fiveClassification");
        HlsCusFiveClassification dto = JSON.parseObject(fiveClassification, HlsCusFiveClassification.class);
        HlsCusFiveClassification newDto = new HlsCusFiveClassification();
        newDto.setFiveClassificationId(dto.getFiveClassificationId());

        HlsCusFiveClassificationContract classificationContract = new HlsCusFiveClassificationContract();
        HlsCusPlmAttachment plmAttachment = new HlsCusPlmAttachment();

        classificationContract.setFiveClassificationId(dto.getFiveClassificationId());
        plmAttachment.setPlmId(dto.getFiveClassificationId());
        plmAttachment.setPlmType("FC");

        if ("APPROVED".equalsIgnoreCase(result)) {
            newDto.setStatus("APPROVED");
            //合同更新NORMAL 到 HISTORY
            classificationContract.setChangeIq("HISTORY");
            classificationContract.setChangeIqOld("NORMAL");
            fiveClassificationContractMapper.updateContractsChangeIq(classificationContract);
            //合同更新CHANGE 到NORMAL
            classificationContract.setChangeIq("NORMAL");
            classificationContract.setChangeIqOld("CHANGE");
            fiveClassificationContractMapper.updateContractsChangeIq(classificationContract);

            //附件更新NORMAL 到 HISTORY
            plmAttachment.setChangeIq("HISTORY");
            plmAttachment.setChangeIqOld("NORMAL");
            plmAttachmentMapper.updateAttachmentChangeIq(plmAttachment);
            //附件更新CHANGE 到NORMAL
            plmAttachment.setChangeIq("NORMAL");
            plmAttachment.setChangeIqOld("CHANGE");
            plmAttachmentMapper.updateAttachmentChangeIq(plmAttachment);

        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            //审批不通过不改变
            newDto.setStatus("APPROVED");
        }
        fcService.updateByPrimaryKeySelective(iRequest, newDto);
    }
}
