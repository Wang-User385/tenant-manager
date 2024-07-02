package com.hand.hls.plm.fc.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;

import java.util.List;
import java.util.Map;

public interface HlsCusIFiveClassificationService extends IBaseService<HlsCusFiveClassification>, ProxySelf<HlsCusIFiveClassificationService> {
    HlsCusFiveClassification save(IRequest iRequest, HlsCusFiveClassification fiveClassification);

    HlsCusFiveClassification contractSave(IRequest iRequest, HlsCusFiveClassification fiveClassification);

    HlsCusFiveClassification submitWfl(IRequest iRequest, HlsCusFiveClassification fiveClassification);

    List<HlsCusFiveClassification> homeRollTableQuery(IRequest iRequest, HlsCusFiveClassification fiveClassification, int page, int pageSize);

    Map<String, Boolean> contractOrAttachmentIsChange(IRequest iRequest, HlsCusFiveClassification fiveClassification);

    HlsCusFiveClassification submitChangeWfl(IRequest iRequest, HlsCusFiveClassification fiveClassification);

    void copyChangeData(IRequest iRequest, HlsCusFiveClassification fiveClassification);

    List<HlsCusFiveClassification> queryAll(IRequest iRequest, HlsCusFiveClassification fiveClassification, int page, int pageSize);

    List<HlsCusFiveClassification> meetwfl(IRequest iRequest, HlsCusFiveClassification fiveClassification);
}