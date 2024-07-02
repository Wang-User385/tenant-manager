package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.FndRiskRatio;
import com.hand.hls.fnd.dto.FndRiskRatioSet;

import java.util.List;

public interface IFndRiskRatioSetService extends IBaseService<FndRiskRatioSet>, ProxySelf<IFndRiskRatioSetService>{
    void batchChildUpdate(IRequest iRequest, FndRiskRatioSet fndRiskRatioSet, List<FndRiskRatio> fndRiskRatio, List<FndRiskRatioSet> datas);

    void saveHeadAndLine(IRequest iRequest, List<FndRiskRatioSet> datas);
    void copyHeadAndLine(IRequest iRequest, List<FndRiskRatioSet> datas);

    //级联删除对应的行表
    void batchDeleteLine(List<FndRiskRatioSet> fndRiskRatioSetList);
}