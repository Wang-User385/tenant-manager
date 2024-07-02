package hls.core.hls.service.impl;

import com.hand.hap.core.AppContextInitListener;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.exception.DayEndDateNullException;
import com.hand.hls.exception.DayEndTypeNullException;
import hls.core.hls.service.HlsDayEndCommon;
import hls.core.hls.service.HlsDayEndService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(
        rollbackFor = {Exception.class, RuntimeException.class}
)
public class HlsDayEndServiceImpl implements HlsDayEndService, AppContextInitListener {
    @Autowired
    HlsCusConContractCashflowMapper cashFlowMapper;
    private Map<String, HlsDayEndCommon> dayEndReg = new HashMap();

    public HlsDayEndServiceImpl() {
    }

    public void excEndDay(Map map) throws DayEndTypeNullException, DayEndDateNullException {
        String dayEndType = map.get("dayEndType").toString();
        if (dayEndType == "") {
            throw new DayEndTypeNullException();
        } else if (map.get("dayEndDate") == null) {
            throw new DayEndDateNullException();
        } else {
            if (map.get("jobExecute") == null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    Date date = sdf.parse(map.get("dayEndDate").toString());
                    map.put("dayEndDate", date);
                } catch (ParseException var5) {
                    var5.printStackTrace();
                }
            }

            HlsDayEndCommon hlsDayEndCommon = (HlsDayEndCommon)this.dayEndReg.get(dayEndType);
            hlsDayEndCommon.process(map);
        }
    }

    public void contextInitialized(ApplicationContext appCtx) {
        Map<String, HlsDayEndCommon> map = appCtx.getBeansOfType(HlsDayEndCommon.class);
        map.forEach((k, v) -> {
            this.dayEndReg.put(v.getDayEndType(), v);
        });
    }
}
