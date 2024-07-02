package com.hand.hls.bp.service;

import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.fin.dto.HlsCusCtAbsContract;

import java.util.List;

public interface HlsSysFileService extends IBaseService<HlsCusSysFile>, ProxySelf<HlsSysFileService> {
    List<HlsCusSysFile> queryAll(IRequest var1, HlsCusSysFile var2, int var3, int var4);

    void insertFileAndAttach(IRequest var1, Attachment var2, SysFile var3);

    HlsCusSysFile queryByAttachmentId(Long var1);

    List<HlsCusSysFile> selectAllContent(IRequest var1, HlsCusSysFile var2, int var3, int var4);

    List<HlsCusSysFile> selectAllConContent(IRequest var1, HlsCusSysFile var2, int var3, int var4);

    List<HlsCusSysFile> queryUka(IRequest var1, HlsCusSysFile var2, int var3, int var4);

    List<HlsCusSysFile> selectByAttachmentId(Long var1);
}
