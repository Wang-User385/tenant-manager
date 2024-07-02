package com.hand.hls.plm.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.dto.HlsCusPlmAttachment;
import com.hand.hls.plm.dto.PlmRiskFeedback;
import com.hand.hls.plm.mapper.HlsCusPlmAttachmentMapper;
import com.hand.hls.plm.mapper.PlmRiskFeedbackMapper;
import com.hand.hls.plm.service.PlmRiskFeedbackService;
import com.hand.hls.utils.HlsCusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmRiskFeedbackServiceImpl extends BaseServiceImpl<PlmRiskFeedback> implements PlmRiskFeedbackService {

    @Autowired
    private PlmRiskFeedbackMapper mapper;
    @Autowired
    private HlsCusPlmAttachmentMapper hlsCusPlmAttachmentMapper;

    @Override
    public List<PlmRiskFeedback> plmRiskFeedbackQuery(IRequest iRequest, PlmRiskFeedback plmRiskFeedback, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.plmRiskFeedbackQuery(plmRiskFeedback);
    }

    @Override
    public List<PlmRiskFeedback> attachmentBatchUpdate(IRequest iRequest, List<PlmRiskFeedback> plmRiskFeedbacks) {
        Long LAST_UPDATED_BY = iRequest.getUserId();

        for (PlmRiskFeedback plmRiskFeedback : plmRiskFeedbacks){
            if (plmRiskFeedback.getFeedbackId()!=null && plmRiskFeedback.getFeedbackId()!=0){
                //更新反馈信息
                plmRiskFeedback.setCreatedBy(LAST_UPDATED_BY);
                plmRiskFeedback.setLastUpdatedBy(LAST_UPDATED_BY);
                plmRiskFeedback.setLastUpdateLogin(LAST_UPDATED_BY);
                plmRiskFeedback.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);
                mapper.updateByPrimaryKeySelective(plmRiskFeedback);

                //更新附件信息
                HlsCusPlmAttachment hlsCusPlmAttachment = new HlsCusPlmAttachment();
                hlsCusPlmAttachment.setCreatedBy(LAST_UPDATED_BY);
                hlsCusPlmAttachment.setLastUpdatedBy(LAST_UPDATED_BY);
                hlsCusPlmAttachment.setLastUpdateLogin(LAST_UPDATED_BY);
                hlsCusPlmAttachment.setPlmAttachmentId(plmRiskFeedback.getPlmAttachmentId());
                hlsCusPlmAttachment.setDocumentName(plmRiskFeedback.getDocumentName());
                hlsCusPlmAttachment.setPlmId(plmRiskFeedback.getRiskWarningId());
                hlsCusPlmAttachment.setDocumentName(plmRiskFeedback.getRiskInfo());
                hlsCusPlmAttachment.setPlmAttachmentCategory(plmRiskFeedback.getPlmAttachmentCategory());
                hlsCusPlmAttachment.setPlmSourceType(plmRiskFeedback.getPlmSourceType());
                hlsCusPlmAttachment.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);
                hlsCusPlmAttachmentMapper.updateByPrimaryKeySelective(hlsCusPlmAttachment);
            }else {
                //保存反馈信息
                HlsCusPlmAttachment hlsCusPlmAttachment = new HlsCusPlmAttachment();
                hlsCusPlmAttachment.setLastUpdatedBy(LAST_UPDATED_BY);
                hlsCusPlmAttachment.setLastUpdateLogin(LAST_UPDATED_BY);
                hlsCusPlmAttachment.setDocumentName(plmRiskFeedback.getDocumentName());
                hlsCusPlmAttachment.setPlmId(plmRiskFeedback.getRiskWarningId());
                hlsCusPlmAttachment.setDocumentName(plmRiskFeedback.getRiskInfo());
                hlsCusPlmAttachment.setPlmAttachmentCategory(plmRiskFeedback.getPlmAttachmentCategory());
                hlsCusPlmAttachment.setChangeIq(plmRiskFeedback.getChangeIq());
                hlsCusPlmAttachment.setSourceType(plmRiskFeedback.getSourceType());
                hlsCusPlmAttachment.setPlmType(plmRiskFeedback.getPlmType());
                hlsCusPlmAttachment.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);
                hlsCusPlmAttachment.setPlmSourceType(plmRiskFeedback.getPlmSourceType());
                hlsCusPlmAttachmentMapper.insertSelective(hlsCusPlmAttachment);

                //保存附件信息
                plmRiskFeedback.setLastUpdatedBy(LAST_UPDATED_BY);
                plmRiskFeedback.setLastUpdateLogin(LAST_UPDATED_BY);
                //获取附件主键
                plmRiskFeedback.setPlmAttachmentId(hlsCusPlmAttachment.getPlmAttachmentId());
                //plmRiskFeedback.setFeedbackDate(new Date());
                plmRiskFeedback.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);
                mapper.insertSelective(plmRiskFeedback);
            }
        }
        return plmRiskFeedbacks;
    }
}
