package com.hand.hls.ast.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.PatrolRecord;

import java.util.List;
import java.util.Map;


public interface PatrolRecordMapper extends Mapper<PatrolRecord> {

    List<PatrolRecord> allQuery(PatrolRecord var1);

    List<PatrolRecord> lonPatrolBpLovIf(Map var1);
}