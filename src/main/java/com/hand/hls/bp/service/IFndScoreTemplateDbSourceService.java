//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.FndScoreTemplateDbSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IFndScoreTemplateDbSourceService extends IBaseService<FndScoreTemplateDbSource>, ProxySelf<IFndScoreTemplateDbSourceService> {
    List<HashMap> combDs();

    List<FndScoreTemplateDbSource> selectDbSourceName(IRequest var1, FndScoreTemplateDbSource var2, int var3, int var4);

    Long fndScoreTemplateDbSource(IRequest var1, Long var2, Map<String, Object> var3);

    int batchDelete(List<FndScoreTemplateDbSource> var1);
}
