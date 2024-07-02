package com.hand.hls.bp.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusSysAttachment;
import com.hand.hls.bp.mapper.HlsCusSysAttachmentMapper;
import com.hand.hls.bp.service.HlsSysAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HlsSysAttachmentServiceImpl extends BaseServiceImpl<HlsCusSysAttachment> implements HlsSysAttachmentService {
    @Autowired
    private HlsCusSysAttachmentMapper hlsCusSysAttachmentMapper;

    public HlsSysAttachmentServiceImpl() {
    }

    public List<HlsCusSysAttachment> sysAttachmentQuery(HlsCusSysAttachment hlsCusSysAttachment) {
        return this.hlsCusSysAttachmentMapper.sysAttachmentQuery(hlsCusSysAttachment);
    }
}

