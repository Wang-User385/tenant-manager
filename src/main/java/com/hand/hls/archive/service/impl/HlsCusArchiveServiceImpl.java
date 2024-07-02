package com.hand.hls.archive.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.archive.dto.HlsCusArchiveAttachment;
import com.hand.hls.archive.mapper.HlsCusArchiveAttachmentMapper;
import com.hand.hls.archive.service.HlsCusArchiveAttachmentService;
import com.hand.hls.bp.mapper.HlsCusBpLiabilitiesMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import hls.core.sys.event.service.SysEventService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.archive.dto.HlsCusArchive;
import com.hand.hls.archive.service.HlsCusArchiveService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveServiceImpl extends BaseServiceImpl<HlsCusArchive> implements HlsCusArchiveService{

    @Autowired
    private HlsCusArchiveAttachmentMapper hlsCusArchiveAttachmentMapper;
    @Autowired
    private HlsCusArchiveAttachmentService hlsCusArchiveAttachmentService;
    @Autowired
    private HlsCusBpLiabilitiesMapper hlsCusBpLiabilitiesMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    static final String JC_FILE_TYPE = "JC_FILE_TYPE";

    @Override
    public List<HlsCusArchive> archivePooling(IRequest iRequest, HlsCusArchive hlsCusArchive) throws HlsCusException{
        List<HlsCusArchive> hlsCusArchives = new ArrayList<>();
        String uuid = UUID.randomUUID().toString();
        if(hlsCusArchive.getProjectId() == null){
            throw new HlsCusException("未获取到所选项目！");
        }
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.queryPrjProjectBasicInfo(hlsCusArchive.getProjectId());
        //查询附件
        HlsCusArchiveAttachment para = new HlsCusArchiveAttachment();
        para.setProjectId(hlsCusArchive.getProjectId());
        List<HlsCusArchiveAttachment> hlsCusArchiveAttachments = hlsCusArchiveAttachmentMapper.queryAttachmentByProject(para);
        Boolean fileEnable = false;
        for (HlsCusArchiveAttachment hlsCusArchiveAttachment : hlsCusArchiveAttachments) {
            para.setSourceId(hlsCusArchiveAttachment.getSourceId());
            para.setSourceType(hlsCusArchiveAttachment.getSourceType());
            //先检查当前附件是否归档过，归档过就不插入
            List<HlsCusArchiveAttachment> results = hlsCusArchiveAttachmentService.selectSelective(iRequest,para);
            if(results.size()<=0){
                fileEnable = true;
                hlsCusArchiveAttachment.setProjectId(hlsCusArchive.getProjectId());
                hlsCusArchiveAttachment.setUuid(uuid);
                hlsCusArchiveAttachmentService.insert(iRequest,hlsCusArchiveAttachment);
            }

        }
        if(!fileEnable){
            throw new HlsCusException("未查询到可归集附件，不能归集");
        }
        if(hlsCusArchiveAttachments.size()>0){
            //查询档案类型
            List<Map> fileTypes = hlsCusBpLiabilitiesMapper.selectSysCodeValue(JC_FILE_TYPE);
            for (Map fileType : fileTypes) {
                HlsCusArchive newHlsCusArchive = new HlsCusArchive();
                newHlsCusArchive.setProjectId(hlsCusArchive.getProjectId());
                newHlsCusArchive.setProjectNumber(hlsCusPrjProject.getProjectNumber());
                newHlsCusArchive.setProjectName(hlsCusPrjProject.getProjectName());
                newHlsCusArchive.setBpName(hlsCusPrjProject.getBpName());
                newHlsCusArchive.setArchivedDate(new Date());
                newHlsCusArchive.setArchiveType(fileType.get("value").toString());
                newHlsCusArchive.setArchivedFlag("N");
                newHlsCusArchive.setUuid(uuid);
                newHlsCusArchive.setHandoverStatus("NEW");
                self().insert(iRequest,newHlsCusArchive);
            }
        }
        hlsCusArchive.setUuid(uuid);
        hlsCusArchives.add(hlsCusArchive);
        return hlsCusArchives;
    }

    @Override
    public void approvedWfl(IRequest iRequest, Long projectId) throws com.hand.hls.exception.HlsCusException {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);
        //综管档案管理员
        List<Long> allocationIds = sysUserMapper.selectAllocationIdByRoleCode("B0850");
        for (Long allocationId : allocationIds) {
            SysUserAllocation sysUserAllocation = sysUserAllocationMapper.selectByPrimaryKey(allocationId);
            Map<String, Object> evenParams = new HashMap();
            evenParams.put("message", "项目"+ hlsCusPrjProject.getProjectName() + "已移交，请进行归档处理！");
            evenParams.put("noticeTitle", "档案移交申请结束");
            evenParams.put("url", "");
            evenParams.put("level", 1L);
            evenParams.put("noticeType", "NOTICE");
            evenParams.put("allocation_id", allocationId);

            String documentCategory = "ARCHIVES_MANAGE";
            String documentType = "ARCHIVES_MANAGE";
            iRequest.setUserId(sysUserAllocation.getUserId());
            this.sysEventService.createEvent(iRequest, hlsCusPrjProject.getProjectId(), documentCategory, documentType, evenParams);
        }
    }

    @Override
    public void archiveConfirm(IRequest iRequest, List<HlsCusArchive> hlsCusArchives) {
        for (HlsCusArchive hlsCusArchive : hlsCusArchives) {
            //更新档案管理员和归档状态
            hlsCusArchive.setArchivistId(iRequest.getUserId());
            hlsCusArchive.setArchivedFlag("Y");
            self().updateByPrimaryKeySelective(iRequest,hlsCusArchive);
        }
    }
}