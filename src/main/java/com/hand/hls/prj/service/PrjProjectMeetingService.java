package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.prj.dto.HlsCusPrjProjectMeeting;

import java.util.List;
import java.util.Map;

public interface PrjProjectMeetingService extends IBaseService<HlsCusPrjProjectMeeting>, ProxySelf<PrjProjectMeetingService> {
    HlsCusPrjProjectMeeting queryMeetingByMeetingId(HlsCusPrjProjectMeeting p);

    void save(IRequest requestCtx, Map<String, Object> maps);

    List<HlsCusPrjProjectMeeting> queryInfo(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize);

    List<HlsCusPrjProjectMeeting> queryInfoReply(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize);

    List<HlsCusPrjProjectMeeting> queryConfirmMeetingInfo(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize);

    List<HlsCusPrjProjectMeeting> queryConfirmMeetingSubmitInfo(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize);

    public void createContractNoticeFile(IRequest iRequest, HlsCusPrjProjectMeeting hlsCusPrjProjectMeeting) throws Exception;

}