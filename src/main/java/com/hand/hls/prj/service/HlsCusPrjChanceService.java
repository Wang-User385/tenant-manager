package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjChance;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.sys.dto.HlsSystemNotice;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjChanceService extends IBaseService<HlsCusPrjChance>, ProxySelf<HlsCusPrjChanceService> {

    /*HlsCusPrjChance prjChanceSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo);*/

    List<Map> prjHomePageGetAllStatusProjectCount(IRequest iRequest, HlsCusPrjChance hlsCusPrjChance);

    List<HlsCusPrjChance> prjHomePageProjectInfoGrid(IRequest iRequest, HlsCusPrjChance hlsCusPrjChance, int page, int pageSize);

/*
    HlsCusPrjChance prjChanceSubmitWfl(IRequest iRequest, HlsCusPrjProjectInfo HlsCusPrjProjectInfo);
*/

    //查询立项动态信息
    List<HlsSystemNotice> queryPrjChanceNotice(HlsSystemNotice hlsSystemNotice, int page, int pageSize);


}