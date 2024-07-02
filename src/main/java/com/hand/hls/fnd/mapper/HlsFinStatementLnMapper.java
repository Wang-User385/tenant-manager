package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsFinStatementLn;

import java.util.List;

/**
 * Created by haibin on 2017/6/27.
 */
public interface HlsFinStatementLnMapper extends Mapper<HlsFinStatementLn> {
    List<HlsFinStatementLn> lnQuery(HlsFinStatementLn hlsFinStatementLn);

    List<Long> lnQueryCount(HlsFinStatementLn hlsFinStatementLn);

    Long lnHeaderIdQuery(HlsFinStatementLn hlsFinStatementLn);

    List<HlsFinStatementLn> fndLnColumnQueryFinancialIndicator(HlsFinStatementLn ln);

    //
    List<HlsFinStatementLn> lnQueryAllAssetsToall(Long bpId);
    //
    List<HlsFinStatementLn> lnQueryAllQuanYiToall(Long bpId);
}
