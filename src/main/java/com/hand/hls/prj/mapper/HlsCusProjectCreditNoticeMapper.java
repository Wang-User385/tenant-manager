package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;

import java.util.List;

public interface HlsCusProjectCreditNoticeMapper extends Mapper<HlsCusProjectCreditNotice>{

    List<HlsCusProjectCreditNotice> QueryAllByInstanceId(HlsCusProjectCreditNotice hlsCusProjectCreditNotice);

    List<HlsCusProjectCreditNotice> quertNoticeList(HlsCusProjectCreditNotice hlsCusProjectCreditNotice);

    List<HlsCusProjectCreditNotice> quertBpNoticeList(HlsCusProjectCreditNotice hlsCusProjectCreditNotice);

    void deleteNoticeByProjectId(HlsCusProjectCreditNotice hlsCusProjectCreditNotice);
}