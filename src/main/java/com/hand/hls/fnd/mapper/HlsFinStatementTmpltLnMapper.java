package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsFinStatementTmpltLn;

import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/29 - 9:51
 */
public interface HlsFinStatementTmpltLnMapper extends Mapper<HlsFinStatementTmpltLn> {
    List<HlsFinStatementTmpltLn> query(HlsFinStatementTmpltLn var1);

    List<HlsFinStatementTmpltLn> queryByFormula(HlsFinStatementTmpltLn var1);

    List<HlsFinStatementTmpltLn> queryByItemName(HlsFinStatementTmpltLn var1);
}
