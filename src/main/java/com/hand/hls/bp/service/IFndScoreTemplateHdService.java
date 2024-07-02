//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.FndScoreTemplateHd;
import com.hand.hls.fnd.dto.HlsDbDataSourceColumn;
import java.util.List;

public interface IFndScoreTemplateHdService extends IBaseService<FndScoreTemplateHd>, ProxySelf<IFndScoreTemplateHdService> {
    List<HlsDbDataSourceColumn> dbCloumnData(FndScoreTemplateHd var1);

    List<FndScoreTemplateHd> query(IRequest var1, FndScoreTemplateHd var2, int var3, int var4);

    void deleteChild(List<FndScoreTemplateHd> var1);

    List<FndScoreTemplateHd> selectList(FndScoreTemplateHd var1);

    List<FndScoreTemplateHd> selectScoreTemplateHd();
}
