//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import java.util.List;
import java.util.Map;

public interface IFndScoreTemplateLnService extends IBaseService<FndScoreTemplateLn>, ProxySelf<IFndScoreTemplateLnService> {
    List<FndScoreTemplateLn> selectLevelOne(IRequest var1, FndScoreTemplateLn var2, int var3, int var4);

    void deleteChild(List<FndScoreTemplateLn> var1);

    List<FndScoreTemplateLn> queryScoreTemplateLn(FndScoreTemplateLn var1);

    List<FndScoreTemplateLn> queryScoreTemplateLn2(Long [] var1,Long var2);

    List<FndScoreTemplateLn> selectLnRoot(Long var1);
}
