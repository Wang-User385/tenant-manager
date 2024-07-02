//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.FndCodingRuleValues;
import java.util.Date;
import java.util.Map;

public interface FndCodingRuleValuesService extends IBaseService<FndCodingRuleValues>, ProxySelf<FndCodingRuleValuesService> {
    String getCodeRuleValue(IRequest var1, String var2, String var3, String var4, Map<String, String> var5);

    String getCodeRuleValue(IRequest var1, String var2, String var3, String var4, Date var5, Map<String, String> var6);
}
