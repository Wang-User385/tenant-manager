//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.fnd.mapper;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.FndInterfaceLines;

import java.util.List;

public interface FndInterfaceLinesMapper extends Mapper<FndInterfaceLines> {
    List<FndInterfaceLines> fndInterfaceLinesDetailQuery(FndInterfaceLines var1);
    List<FndInterfaceLines> fndInterfaceLinesDetailQueryC(FndInterfaceLines var1);

//
//    List<FndInterfaceLines> acpInvoiceHdQuery(FndInterfaceLines var1);
//
//    List<FndInterfaceLines> acpInvoiceLnQuery(FndInterfaceLines var1);
//
//    List<HlsCusAcpInvoiceLn> acpInvoiceLnQuerySec(FndInterfaceLines var1);

    @Deprecated
    List<FndInterfaceLines> fndInterfeLacinesQuery(FndInterfaceLines var1);
//
    List<FndInterfaceLines> fndInterColumnQuery(FndInterfaceLines var1);
//
//    List<FndInterfaceLines> fndInterfaceLinesTxtQuery(FndInterfaceLines var1);

    List<FndInterfaceLines> fndInterfaceByHeaderIdAndSheetName(FndInterfaceLines var1);

//    List<FndInterfaceLines> selectCshTransationImportData(FndInterfaceLines var1);
//
    List<FndInterfaceLines> acrInvoiceHdQuery(FndInterfaceLines var1);
//    List<FndInterfaceLines> fndInterFaceColumnQuery(FndInterfaceLines fndInterfaceLines);


    List<FndInterfaceLines> fndInterColumnQueryDetail(FndInterfaceLines var1);
    List<FndInterfaceLines> fndInterColumnQueryDetailDouble(FndInterfaceLines var1);

    List<FndInterfaceLines> fndInterColumnQueryFinancialIndicator(FndInterfaceLines var1);

    List<FndInterfaceLines> fndInterfaceLinesQuery(FndInterfaceLines var1);




}
