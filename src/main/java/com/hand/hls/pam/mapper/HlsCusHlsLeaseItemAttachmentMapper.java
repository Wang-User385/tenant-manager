package com.hand.hls.pam.mapper;


import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemAttachment;

import java.util.List;

public interface HlsCusHlsLeaseItemAttachmentMapper extends HlsLeaseItemAttachmentMapper<HlsCusHlsLeaseItemAttachment> {

    List<HlsCusHlsLeaseItemAttachment> queryLeaseAttachment(HlsCusHlsLeaseItemAttachment hlsCusHlsLeaseItemAttachment);

}