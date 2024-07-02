package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;

import java.util.List;

public interface HlsDurationHdMapper extends Mapper<HlsDurationHd> {
    List<HlsDurationHd> hlsDurationHdDetailQuery(HlsDurationHd hd);

    List<HlsDurationHd> prjProjectApprovalConQuery(HlsDurationHd hd);

    List<HlsDurationHd> hlsDurationHdDetailQueryNew(HlsDurationHd hd);

    List<HlsDurationHd> hlsDurationHdDetailQueryReqNew(HlsDurationHd hd);

    List<HlsDurationHd> hlsDurationHdEtDetailQueryNew(HlsDurationHd hd);

    List<HlsDurationHd> contractCheckList(HlsDurationHd hd);

    List<HlsDurationHd> selectDurationHdCheck(HlsDurationHd hd);

    List<HlsDurationHd> queryHlsDurationHdForPrjLov(HlsDurationHd hd);

    List<HlsDurationHd> hlsDurationHdExistCheckForLeaseItem(HlsDurationLn ln);

    List<HlsDurationHd> hlsDurationHdHomeQuery(HlsDurationHd hd);

    List<HlsDurationHd> hlsDurationHdHomeReqQuery(HlsDurationHd hd);

    List<HlsDurationHd> createSelectHdAll(HlsDurationHd hd);

    List<HlsDurationHd> hlsDurationHdDetailQueryNew1(HlsDurationHd durationHd);
}
