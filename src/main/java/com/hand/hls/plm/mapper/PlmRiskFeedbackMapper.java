package com.hand.hls.plm.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.dto.PlmRiskFeedback;

import java.util.List;

public interface PlmRiskFeedbackMapper extends Mapper<PlmRiskFeedback> {

    List<PlmRiskFeedback> plmRiskFeedbackQuery(PlmRiskFeedback plmRiskFeedback);

}
