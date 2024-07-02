package com.hand.hls.bp.service.impl;

import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.fnd.dto.FndCodingRuleValues;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional

public class HlsSysFileServiceImpl extends BaseServiceImpl<HlsCusSysFile> implements HlsSysFileService {

    @Override
    public List<HlsCusSysFile> queryAll(IRequest var1, HlsCusSysFile var2, int var3, int var4) {
        return null;
    }

    @Override
    public void insertFileAndAttach(IRequest var1, Attachment var2, SysFile var3) {

    }

    @Override
    public HlsCusSysFile queryByAttachmentId(Long var1) {
        return null;
    }

    @Override
    public List<HlsCusSysFile> selectAllContent(IRequest var1, HlsCusSysFile var2, int var3, int var4) {
        return null;
    }

    @Override
    public List<HlsCusSysFile> selectAllConContent(IRequest var1, HlsCusSysFile var2, int var3, int var4) {
        return null;
    }

    @Override
    public List<HlsCusSysFile> queryUka(IRequest var1, HlsCusSysFile var2, int var3, int var4) {
        return null;
    }

    @Override
    public List<HlsCusSysFile> selectByAttachmentId(Long var1) {
        return null;
    }
}
