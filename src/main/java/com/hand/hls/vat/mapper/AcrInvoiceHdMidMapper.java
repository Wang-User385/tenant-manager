package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.vat.dto.AcrInvoiceHdMid;

import java.util.List;

public interface AcrInvoiceHdMidMapper extends Mapper<AcrInvoiceHdMid>{

    /**
     * 查询待回传的发票代码和发票号码
     * @return
     */
    List<AcrInvoiceHdMid> queryAcrInvoiceHdMidForUpdate();
}