package com.hand.hls.archive.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.archive.dto.HlsCusArchive;
import com.hand.hls.archive.mapper.HlsCusArchiveMapper;
import com.hand.hls.archive.service.HlsCusArchiveService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.archive.dto.HlsCusArchiveAttachment;
import com.hand.hls.archive.service.HlsCusArchiveAttachmentService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author CONGWEIJING
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveAttachmentServiceImpl extends BaseServiceImpl<HlsCusArchiveAttachment> implements HlsCusArchiveAttachmentService{

    @Autowired
    private HlsCusArchiveService hlsCusArchiveService;
    @Autowired
    private HlsCusArchiveMapper hlsCusArchiveMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Override
    public void archiveArrangement(IRequest iRequest, List<HlsCusArchiveAttachment> list) throws HlsCusException{
        for (HlsCusArchiveAttachment hlsCusArchiveAttachment : list) {
            if(hlsCusArchiveAttachment.getArchiveAttachmentId()!=null){
                hlsCusArchiveAttachment.set__status("update");
            }else{
                hlsCusArchiveAttachment.set__status("add");
            }
            if(hlsCusArchiveAttachment.getArchiveType() == null){
                throw new HlsCusException("档案类型不能为空！");
            }
            //插入分类数据
            HlsCusArchive hlsCusArchive = new HlsCusArchive();
            hlsCusArchive.setArchiveType(hlsCusArchiveAttachment.getArchiveType());
            hlsCusArchive.setUuid(hlsCusArchiveAttachment.getUuid());
            hlsCusArchive.setProjectId(hlsCusArchiveAttachment.getProjectId());
            List<HlsCusArchive> archives = hlsCusArchiveMapper.select(hlsCusArchive);
            if(archives.size()>0){
                hlsCusArchiveAttachment.setArchiveId(archives.get(0).getArchiveId());
            }else{
                HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.queryPrjProjectBasicInfo(hlsCusArchiveAttachment.getProjectId());
                hlsCusArchive.setProjectId(hlsCusArchiveAttachment.getProjectId());
                hlsCusArchive.setProjectNumber(hlsCusPrjProject.getProjectNumber());
                hlsCusArchive.setProjectName(hlsCusPrjProject.getProjectName());
                hlsCusArchive.setBpName(hlsCusPrjProject.getBpName());
                hlsCusArchive.setArchivedDate(hlsCusArchiveAttachment.getCreationDate());
                hlsCusArchive.setArchivedFlag("N");
                hlsCusArchive.setUuid(hlsCusArchiveAttachment.getUuid());
                hlsCusArchive.setHandoverStatus("NEW");
                hlsCusArchiveService.insert(iRequest,hlsCusArchive);
                hlsCusArchiveAttachment.setArchiveId(hlsCusArchive.getArchiveId());
            }
        }
        self().batchUpdate(iRequest,list);

        //删除没有分类的数据
        List<HlsCusArchive> deleteArchives = hlsCusArchiveMapper.selectNnusedArchive();
        hlsCusArchiveService.batchDelete(deleteArchives);
    }
}