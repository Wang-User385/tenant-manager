package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeLine;
import com.hand.hls.gld.dto.JeTrxDtl;
import com.hand.hls.gld.mapper.HlsCusJeHeadMapper;
import com.hand.hls.gld.mapper.JeLineMapper;
import com.hand.hls.gld.service.IJeHeadService;
import com.hand.hls.gld.service.IJeLineService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by fjm on 2017/8/31.
 */
@Component
public class FinIncomeRecognitionJeTrxService extends AbstractJeTrxService {

    @Autowired
    private JeLineMapper jeLineMapper;
    @Autowired
    private IJeLineService iJeLineService;

    @Autowired
    private HlsCusJeHeadMapper hlsCusJeHeadMapper;

    @Autowired
    private IJeHeadService iJeHeadService;

    private static final String JE_TRX_DTL = "JE_TRX_DTL";

    @Override
    public String getJeTrx() {
        return "FIN_INCOME_RECOGNITION";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {

    }
}
