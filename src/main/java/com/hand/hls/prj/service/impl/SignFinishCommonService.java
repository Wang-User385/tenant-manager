package com.hand.hls.prj.service.impl;

import java.util.HashMap;
import java.util.Map;
import com.hand.hap.core.AppContextInitListener;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.prj.service.ISignFinish;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author Lenovo 2023/08/09 15:21
 */
@Component
public class SignFinishCommonService implements AppContextInitListener {

    private static final Map<String, ISignFinish> MAP = new HashMap<>();

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, ? extends ISignFinish> signFinishMap = applicationContext.getBeansOfType(ISignFinish.class);
        signFinishMap.forEach((k, v) -> MAP.put(v.getSourceDocCategory(), v));
    }

    public static ISignFinish getFinishService(String sourceDocCategory){
        return MAP.get(sourceDocCategory);
    }
}
