package com.hand.hls.plm.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.dto.PlmRiskFeedback;

import java.util.List;

public interface PlmRiskFeedbackService extends IBaseService<PlmRiskFeedback>, ProxySelf<PlmRiskFeedbackService> {
    List<PlmRiskFeedback> plmRiskFeedbackQuery(IRequest iRequest, PlmRiskFeedback plmRiskFeedback, int page, int pageSize);
    List<PlmRiskFeedback> attachmentBatchUpdate(IRequest iRequest, List<PlmRiskFeedback> plmRiskFeedback);
}
