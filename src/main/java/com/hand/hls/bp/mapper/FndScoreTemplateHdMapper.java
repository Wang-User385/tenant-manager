//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.FndScoreTemplateHd;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import com.hand.hls.fnd.dto.HlsDbDataSourceColumn;
import java.util.List;

public interface FndScoreTemplateHdMapper extends Mapper<FndScoreTemplateHd> {
    List<HlsDbDataSourceColumn> dbCloumnData(FndScoreTemplateHd var1);

    List<FndScoreTemplateHd> selectList(FndScoreTemplateHd var1);

    List<FndScoreTemplateHd> query1(FndScoreTemplateHd var1);

    List<HlsDbDataSourceColumn> selectDbLov(FndScoreTemplateLn var1);

    List<FndScoreTemplateHd> selectScoreTemplateHd();
}
