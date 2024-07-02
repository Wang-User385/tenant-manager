package com.hand.hls.sign.mapper;

import java.util.List;
import java.util.Map;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sign.dto.SignRecord;
import org.apache.ibatis.annotations.Param;

/**
 * description
 *
 * @author shigure 2022/11/15 18:46
 */
public interface SignRecordMapper extends Mapper<SignRecord> {

    /**
     * 查询可以进行法人电子签的进件
     */
    List<Map> orgSignProjectQuery(Map<String, Object> prjProject);

    SignRecord queryLatestSign(@Param("docCategory") String sourceDocCategory,@Param("docId") Long sourceDocId);
}
