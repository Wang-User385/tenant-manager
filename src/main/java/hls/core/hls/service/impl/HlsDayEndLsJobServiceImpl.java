package hls.core.hls.service.impl;

import com.hand.hap.core.AppContextInitListener;
import com.hand.hls.exception.DayEndDateNullException;
import com.hand.hls.exception.DayEndTypeNullException;
import com.hand.hls.service.HlsDayEndExecutor;
import hls.core.hls.service.HlsDayEndCommon;
import hls.core.hls.service.HlsDayEndLsExecutor;
import hls.core.hls.service.HlsDayEndLsJobService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * @author PC
 */
@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class HlsDayEndLsJobServiceImpl implements HlsDayEndLsJobService,AppContextInitListener {

    private Map<String, HlsDayEndLsExecutor> executorMap = new HashMap();
    public HlsDayEndLsJobServiceImpl(){}

    @Override
    public void execute(Map param) throws Exception {
        Object dayEndTypeObj = param.get("dayEndType");
        Object dayEndDateObj = param.get("dayEndDate");
        String dayEndType = param.get("dayEndType").toString();
        if (!(dayEndTypeObj instanceof String)) {
            throw new DayEndTypeNullException();
        } else if (!(dayEndDateObj instanceof Date)) {
            throw new DayEndDateNullException();
        } else {
            if (this.executorMap != null) {
//                ((HlsDayEndLsExecutor)this.executorMap.get(dayEndTypeObj)).process(param);
//                this.executorMap.get(dayEndType).process(param);
                HlsDayEndLsExecutor hlsDayEndLsExecutor = (HlsDayEndLsExecutor)this.executorMap.get(dayEndType);
                hlsDayEndLsExecutor.process(param);
            }

        }
    }
    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
//        Map<String, HlsDayEndLsExecutor> map = applicationContext.getBeansOfType(HlsDayEndLsExecutor.class);
//        map.forEach((k, v) -> {
//            HlsDayEndLsExecutor var10000 = (HlsDayEndLsExecutor)this.executorMap.put(v.getDayEndType(), v);
//        });
        Map<String, HlsDayEndLsExecutor> map = applicationContext.getBeansOfType(HlsDayEndLsExecutor.class);
        map.forEach((k, v) -> {
            HlsDayEndLsExecutor var10000 = (HlsDayEndLsExecutor)this.executorMap.put(v.getDayEndType(), v);
        });
    }
}
