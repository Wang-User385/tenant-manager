package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckPutPay;
import com.hand.hls.hn.dto.PrjCheckQuestionSituation;

import java.util.List;

public interface PrjCheckPutPayMapper extends Mapper<PrjCheckPutPay>{
    List<PrjCheckPutPay> queryList(PrjCheckPutPay prjCheckPutPay);
}