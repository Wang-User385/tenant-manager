package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;

public interface HlsCusPrjProjectLeaseItemService extends IBaseService<HlsCusPrjProjectLeaseItem>, ProxySelf<HlsCusPrjProjectLeaseItemService> {

    void excelImport(IRequest iRequest, Long headerId, Long projectId);

    void prjLeaseItemExcelImport(IRequest iRequest, Long headerId, Long projectId, String sheetName);

}