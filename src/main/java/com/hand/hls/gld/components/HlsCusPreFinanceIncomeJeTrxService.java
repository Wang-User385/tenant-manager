package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.dto.JeHead;
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


@Component
public class HlsCusPreFinanceIncomeJeTrxService extends AbstractJeTrxService {

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
        return "PRE_FIN_INCOME_RECOGNITION";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {
        if(result.getJeTrxId() != null) {
            JeLine jeLine = new JeLine();
            jeLine.setSourceType(JE_TRX_DTL);
            jeLine.setSourceId(result.getJeTrxDtlId());

            List<JeLine> jeLineList = jeLineMapper.select(jeLine);
            if(CollectionUtils.isNotEmpty(jeLineList)){
                //获取当月最后一天
                Calendar cale = Calendar.getInstance();
                cale.add(Calendar.MONTH, 1);
                cale.set(Calendar.DAY_OF_MONTH, 0);
                for(JeLine line:jeLineList){

                    line.setJeCreationDate(cale.getTime());
                    iJeLineService.updateByPrimaryKeySelective(request,line);
                }

                //更新头
                HlsCusJeHead jeHead = new HlsCusJeHead();
                jeHead.setJeHeadId(jeLineList.get(0).getJeHeadId());
                jeHead.setJeCreationDate(cale.getTime());
                iJeHeadService.updateByPrimaryKeySelective(request,jeHead);
            }
        }
    }
}
