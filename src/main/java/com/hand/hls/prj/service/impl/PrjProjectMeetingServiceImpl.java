package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.mapper.HlsCusContractAttachmentMapper;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.prj.dto.HlsCusPrjProjectMeeting;
import com.hand.hls.prj.dto.HlsCusPrjProjectMeetingJudge;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMeetingMapper;
import com.hand.hls.prj.service.PrjProjectMeetingJudgeService;
import com.hand.hls.prj.service.PrjProjectMeetingService;
import com.hand.hls.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class PrjProjectMeetingServiceImpl extends BaseServiceImpl<HlsCusPrjProjectMeeting> implements PrjProjectMeetingService {
    @Autowired
    private PrjProjectMeetingJudgeService projectMeetingJudgeService;
    @Autowired
    private HlsCusPrjProjectMeetingMapper mapper;

    @Override
    public void save(IRequest requestCtx, Map<String, Object> maps) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        HlsCusPrjProjectMeeting projectMeeting = new HlsCusPrjProjectMeeting();
        Map map1 = (Map) maps.get("form");
        if (map1 != null & !"".equalsIgnoreCase(map1.get("meetingName").toString()) && !"".equalsIgnoreCase(map1.get("meetingLocation").toString())) {
            try {
                Date date = sdf.parse(map1.get("meetingDate").toString().replace("Z", " UTC"));
                projectMeeting.setMeetingDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            projectMeeting.setProjectId(Long.parseLong(map1.get("projectId").toString()));
            projectMeeting.setMeetingStatus("NEW");
            projectMeeting.setMeetingName((String) map1.get("meetingName"));
            projectMeeting.setMeetingLocation((String) map1.get("meetingLocation"));
            projectMeeting.setNumberOfJudges(Long.parseLong(map1.get("numberOfJudges").toString()));
            projectMeeting.setAnonymousFlag((String) map1.get("anonymousFlag"));
            HlsCusPrjProjectMeeting meeting = mapper.selectTimes(projectMeeting.getProjectId());
            if (meeting == null) {
                projectMeeting.setMeetingTimes(1L);
            } else {
                projectMeeting.setMeetingTimes(meeting.getMeetingTimes() + 1);
            }

            if ("".equalsIgnoreCase(map1.get("projectMeetingId").toString())) {
                projectMeeting = self().insertSelective(requestCtx, projectMeeting);
            } else {
                projectMeeting.setProjectMeetingId(Long.parseLong(map1.get("projectMeetingId").toString()));
                projectMeeting = self().updateByPrimaryKeySelective(requestCtx, projectMeeting);
            }
        }
        if (!("").equals(map1.get("projectMeetingId").toString())) {
            projectMeetingJudgeService.deleteByMeetId(Long.parseLong(map1.get("projectMeetingId").toString()));
        }
        List list = (ArrayList) maps.get("grid");
        Map map2 = null;
        HlsCusPrjProjectMeetingJudge projectMeetingJudge;
        for (int j = 0; j < list.size(); j++) {
            map2 = (Map) list.get(j);
            if (map2 != null) {
                Map map2Camel = JSON.parseObject(JsonUtils.toCamelJsonString(map2));
                projectMeetingJudge = new HlsCusPrjProjectMeetingJudge();
                projectMeetingJudge.setProjectMeetingId(projectMeeting.getProjectMeetingId());
                if (map2Camel.get("judgeUserAllocationId") != null) {
                    projectMeetingJudge.setJudgeUserAllocationId(Long.parseLong(map2Camel.get("judgeUserAllocationId").toString()));
                }
                if (map2Camel.get("judgeUserId") != null) {
                    projectMeetingJudge.setJudgeUserId(Long.parseLong(map2Camel.get("judgeUserId").toString()));
                }
                projectMeetingJudge.setProcessInstanceId(Long.valueOf(map2Camel.get("processInstanceId").toString()));
                projectMeetingJudge.setDirectorFlag(map2Camel.get("directorFlag").toString());
                projectMeetingJudge.setProjectId(projectMeeting.getProjectId());
                projectMeetingJudgeService.insertSelective(requestCtx, projectMeetingJudge);
            }
        }
    }

    @Override
    public List<HlsCusPrjProjectMeeting> queryInfo(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize) {
        return mapper.queryInfo(dto);
    }
    @Override
    public List<HlsCusPrjProjectMeeting> queryInfoReply(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize) {
        return mapper.queryInfoChange(dto);
    }

    @Override
    public HlsCusPrjProjectMeeting queryMeetingByMeetingId(HlsCusPrjProjectMeeting p) {
        return mapper.queryMeetingByMeetingId(p);
    }

    @Override
    public List<HlsCusPrjProjectMeeting> queryConfirmMeetingInfo(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize) {
        return mapper.queryConfirmMeetingInfo(dto.getProjectId(), dto.getProcessInstanceId());
    }

    @Override
    public List<HlsCusPrjProjectMeeting> queryConfirmMeetingSubmitInfo(IRequest requestContext, HlsCusPrjProjectMeeting dto, int page, int pageSize) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<HlsCusPrjProjectMeeting> list = new ArrayList<>();
        list = mapper.queryConfirmMeetingInfo(dto.getProjectId(), dto.getProcessInstanceId());
        try {
            for (HlsCusPrjProjectMeeting hlsCusPrjProjectMeeting : list) {
                if (hlsCusPrjProjectMeeting.getMeetingDate() != null) {
                    String s = dateFormat.format(hlsCusPrjProjectMeeting.getMeetingDate());
                    Date meetingDate = dateFormat.parse(s);
                    hlsCusPrjProjectMeeting.setMeetingDate(meetingDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    @Autowired
    HlsCusContractAttachmentMapper hlsCusContractAttachmentMapper;

    @Override
    public void createContractNoticeFile(IRequest iRequest, HlsCusPrjProjectMeeting hlsCusPrjProjectMeeting) throws Exception {
        Long projectId = hlsCusPrjProjectMeeting.getProjectId();
        HlsDocFileTemplet templet = new HlsDocFileTemplet();
        templet.setTempletCode("PRJ_COMMITTEE_COMPREHENSIVE_OPINION");
        List<HlsDocFileTemplet> docFileTemplets = hlsDocFileTempletService.select(iRequest, templet, 1, 1);

/*        HlsCusContractAttachment param = new HlsCusContractAttachment();
//        param.setContractId(hlsCusConContract.getContractId());
        List<HlsCusContractAttachment> list = hlsCusContractAttachmentMapper.contractAttachmentLendDocumentListQuery(param);
        if(CollectionUtils.isNotEmpty(list)){
            return;
        }*/
       /* Map<String, Object> params = new HashMap<String, Object>();
        //前台传入的参数需要这里接收
        params.put("projectId", attachment.getProjectId());
        params.put("templetId", attachment.getTemplateId());
        params.put("sourceType", attachment.getSourceType());
        params.put("projectAttachmentId", attachment.getProjectAttachmentId());
        params.put("companyId", iRequest.getCompanyId());
        params.put("bpId", attachment.getBpId());
        hlsCusPrjContractDocxService.process(iRequest, params);*/

        if (docFileTemplets.size() == 1) {
            docxCreateMethod(iRequest, docFileTemplets.get(0).getTempletId(), hlsCusPrjProjectMeeting);
        } else {
            throw new HlsCusException("不存在模板,请配置");
        }
    }

    @Autowired
    private HlsCusPrjContractDocxService hlsCusPrjContractDocxService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    public void docxCreateMethod(IRequest iRequest, Long templetId, HlsCusPrjProjectMeeting hlsCusConContract) throws Exception {
       /* Long contractId = hlsCusConContract.getContractId();
        String sourceType = "hls_doc_file_templet";
        FndAttachmentMulti fileParam = new FndAttachmentMulti();
        fileParam.setTableName(sourceType);
        fileParam.setTablePkValue(templetId.toString());
        FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(iRequest, fileParam).get(0);
        FndAttachment templateFileParam = new FndAttachment();
        templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
        templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
        List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(iRequest, templateFileParam);
        Validate.notEmpty(fndAttachments, "文件模版不存在");
        FndAttachment sysFile = fndAttachments.get(0);
        if (sysFile == null || StringUtils.isBlank(sysFile.getFilePath())) {
            throw new HlsCusException("文件模版不存在");
        }
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new HlsCusException("文件模版不存在");
        }
        int fileLength = (int) file.length();//先获取模板文件的大小
        int fileBackLength = 0;//定义备份文件的大小
        FndAttachment conDocFile = null;
        if (fileLength > 0) {
            InputStream inStream = new FileInputStream(file);//用inputStram输入流读取本地的docx文件
            String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());//定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            this.copyModel(copyPath, inStream);//复制模板

            InputStream modelIs = new FileInputStream(copyPath);//用输入流读取复制后的模板
            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templetId);
            params.put("contractId", contractId);
            params.put("projectId", hlsCusConContract.getProjectId());
            createDocx(iRequest, modelIs, new File(copyPath), params);
            fileBackLength = (int) new File(copyPath).length();
            HlsCusContractAttachment hlsCusContractAttachment = new HlsCusContractAttachment();
            hlsCusContractAttachment.setContractAttachmentCategory(hlsCusConContract.getDocumentType());
            hlsCusContractAttachment.setContractId(contractId);
            hlsCusContractAttachment.setDocumentName(hlsCusConContract.getDocDocumentName() + ".docx");
            hlsCusContractAttachment.setSourceType(hlsCusConContract.getDocumentType());
            hlsCusContractAttachment = self().insertSelective(iRequest, hlsCusContractAttachment);

            conDocFile = new FndAttachment();
            String fileName = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5).concat(".docx");
            conDocFile.setFileName(fileName);
            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName("CON_CONTRACT_ATTACHMENT");
            conDocFileMulti.setTablePkValue(hlsCusContractAttachment.getContractAttachmentId().toString());
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            conDocFileMulti.setCreatedBy(iRequest.getUserId());
            conDocFileMulti.setCreationDate(new Date());
            conDocFileMulti.setLastUpdateDate(new Date());
            conDocFileMulti.setLastUpdatedBy(iRequest.getUserId());
            fndAttachmentMultiService.insertSelective(iRequest, conDocFileMulti);
            conDocFile.setSourceTypeCode("fnd_atm_attachment_multi");
            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
            //上传
            conDocFile.setFilePath(copyPath);
            conDocFile.setFileTypeCode(".docx");
            conDocFile.setFileSize(((Integer) fileBackLength).longValue());
            conDocFile.setCreationDate(new Date());
            conDocFile.setCreatedBy(iRequest.getUserId());
            conDocFile.setLastUpdateDate(new Date());
            conDocFile.setLastUpdatedBy(iRequest.getUserId());
            fndAttachmentService.insertSelective(iRequest, conDocFile);
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            fndAttachmentMultiService.updateByPrimaryKey(iRequest, conDocFileMulti);
        }*/
    }

}