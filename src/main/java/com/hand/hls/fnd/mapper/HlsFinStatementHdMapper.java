package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsFinStatementHd;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/23 - 13:58
 */
public interface HlsFinStatementHdMapper extends Mapper<HlsFinStatementHd> {
    List<HlsFinStatementHd> hdQuery(HlsFinStatementHd var1);

    List<HlsFinStatementHd> hdDistinctQuery(HlsFinStatementHd var1);

    List<HlsFinStatementHd> hdYearQuery(HlsFinStatementHd var1);

    List<HlsFinStatementHd> hdQueryByTime(HlsFinStatementHd var1);

    List<HlsFinStatementHd> hdQueryBasicBp(HlsFinStatementHd var1);

    Long hdHeaderIdQuery(HlsFinStatementHd var1);

    int updateCheckStatus(HlsFinStatementHd hlsFinStatementHd);

    int updateHeaderIdNull(HlsFinStatementHd hlsFinStatementHd);

    void deleteBpHdInfo(HlsFinStatementHd hlsFinStatementHd);


    List<HlsFinStatementHd> hdReportYearQuery(HlsFinStatementHd var1);

}
