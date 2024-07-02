package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;

import java.util.List;

public interface HlsBpFinancialHeaderMapper extends Mapper<HlsBpFinancialHeader>{

    void deleteHlsBpFinStatementHdByHeaderId(HlsBpFinancialHeader hlsBpFinancialHeader);

    void deleteHlsBpFinStatementLnByHeaderId(HlsBpFinancialHeader hlsBpFinancialHeader);

    void deleteHlsWebExcelCalcResult(HlsBpFinancialHeader hlsBpFinancialHeader);

    List<HlsBpFinancialHeader> selectBpExportInfo(HlsBpFinancialHeader hlsBpFinancialHeader);
}