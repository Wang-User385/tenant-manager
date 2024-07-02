package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.JeLine;
import java.util.List;
import java.util.Map;

public interface JeLineMapper extends Mapper<JeLine> {
    List<JeLine> query(JeLine var1);

    List<JeLine> query(Map var1);

    List<JeLine> queryCompany(JeLine var1);

    List<Map> selectJeLineInfoGroupAccount(JeLine line);

    List<Map> selectJeLineDetail(JeLine line);

    List<Map> selectJeLineInfo(JeLine jeLine);
    List<JeLine> selectJeLineExportInfo(JeLine jeLine);

    List<JeLine> selectJeLineByHead(JeLine jeLine);
}
