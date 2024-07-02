package com.hand.hls.ecif.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.intergration.dto.HapInterfaceLine;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;

public interface HlsCusBpMasterRequestRecordsService extends IBaseService<HlsCusBpMasterRequestRecords>, ProxySelf<HlsCusBpMasterRequestRecordsService>{

    HlsCusBpMasterRequestRecords wsEcifBatchCreateUpdate(IRequest iRequest, HlsCusBpMasterRequestRecords dto);

    HlsCusBpMasterRequestRecords wsEcifSignalQuery(IRequest iRequest, HlsCusBpMasterRequestRecords dto);


    HapInterfaceLine getInterfaceUrl(IRequest iRequest, String interfaceCode, String lineCode);

    HlsCusHapInterfaceOutbound outboundInvokeInsert(IRequest iRequest, HlsCusHapInterfaceOutbound outbound);


    HlsCusBpMasterRequestRecords ecifSignalUpdate(IRequest iRequest, HlsCusBpMasterRequestRecords dto);

     void wsEcifChangeQuery(IRequest iRequest, String changeDate);

    void dataBackBpMaster(IRequest iRequest, HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange);

}


