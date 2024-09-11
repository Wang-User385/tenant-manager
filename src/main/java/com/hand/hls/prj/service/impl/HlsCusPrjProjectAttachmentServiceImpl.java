package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.cont.dto.HlsCusConContractAttachment;
import com.hand.hls.cont.dto.HlsCusContractAttachment;
import com.hand.hls.cont.service.HlsCusContractAttachmentService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCreditLineAttachMapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.utils.HlsCusConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectAttachmentServiceImpl extends BaseServiceImpl<HlsCusPrjProjectAttachment> implements HlsCusPrjProjectAttachmentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;

    @Autowired
    private HlsSysFileService hlsSysFileService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusContractAttachmentService hlsCusContractAttachmentService;
    @Autowired
    private IFndAttachmentMultiService iFndAttachmentMultiService;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Autowired
    private IFndAttachmentService iFndAttachmentService;

    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;

    private static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";

    private static final String CON_CONTRACT_ATTACHMENT = "CON_CONTRACT_ATTACHMENT";

    @Override
    public List<HlsCusPrjProjectAttachment> prjProjectAttachmentDetailQuery(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize) {
        //PageHelper.startPage(page, pageSize);
        return hlsCusPrjProjectAttachmentMapper.prjProjectAttachmentDetailQuery(hlsCusPrjProjectAttachment);

    }

    /**
     * 保理项目评审附件
     */
    @Override
    public List<HlsCusPrjProjectAttachment> selectInProjectAttachmentService(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize) {
        if (page != 0 && pageSize != 0) {
            PageHelper.startPage(page, pageSize);
        }

        List<HlsCusPrjProjectAttachment> list = new ArrayList<>();

        SysFile file1 = new SysFile();

        //这层判断是为解决SQL出现无效数字的问题
        if (hlsCusPrjProjectAttachment == null || StringUtils.isBlank(hlsCusPrjProjectAttachment.getProjectAttachmentCategory()) ||
                hlsCusPrjProjectAttachment.getProjectId() == null || hlsCusPrjProjectAttachment.getProjectId() == 0) {
        } else {

            List<HlsCusPrjProjectAttachment> listPrj = hlsCusPrjProjectAttachmentMapper.selectByprojectIdAndCategory(hlsCusPrjProjectAttachment);
            if (listPrj.size() > 0) {
                list.addAll(listPrj);
            }
        }
        return list;
    }

    /**
     * 二期功能：进件投放审查通过后，复制附件
     *
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */
    @Override
    public List<HlsCusPrjProjectAttachment> saveAttachmentFromPrj(IRequest iRequest, Long contractId, Long projectId) {
        HlsCusPrjProjectAttachment attachment = new HlsCusPrjProjectAttachment();
        attachment.setProjectId(projectId);
        List<HlsCusPrjProjectAttachment> attachmentList =  hlsCusPrjProjectAttachmentMapper.queryCopySignFileToContract(attachment);
        for (HlsCusPrjProjectAttachment prjProjectAttachment : attachmentList) {
            HlsCusContractAttachment contractAttachment = new HlsCusContractAttachment();
            BeanRefUtils.beanToBean(prjProjectAttachment, contractAttachment, this.hlsBeanRefUtilService);
            contractAttachment.setContractId(contractId);
            contractAttachment.setContractAttachmentCategory(prjProjectAttachment.getProjectAttachmentCategory());
            hlsCusContractAttachmentService.insert(iRequest, contractAttachment);
            //复制fnd_atm_attachment_multi
            if (prjProjectAttachment.getProjectAttachmentCategory() != null && prjProjectAttachment.getProjectAttachmentId() != null) {
                FndAttachmentMulti multiDto = new FndAttachmentMulti();
                multiDto.setTableName(prjProjectAttachment.getProjectAttachmentCategory());
                multiDto.setTablePkValue(String.valueOf(prjProjectAttachment.getProjectAttachmentId()));
                List<FndAttachmentMulti> multiDtoList = fndAttachmentMultiMapper.select(multiDto);
                for (FndAttachmentMulti multi : multiDtoList) {
                    String recordId = String.valueOf(multi.getRecordId());
                    multi.setTablePkValue(String.valueOf(contractAttachment.getContractAttachmentId()));
                    multi.setTableName(CON_CONTRACT_ATTACHMENT);
                    iFndAttachmentMultiService.insert(iRequest, multi);

                    FndAttachment fndAttachment = new FndAttachment();
                    fndAttachment.setSourceTypeCode(FND_ATM_ATTACHMENT_MULTI);
                    fndAttachment.setSourcePkValue(recordId);
                    try {
                        fndAttachment = fndAttachmentMapper.selectOne(fndAttachment);
                        Long sourceAttachmentId = fndAttachment.getAttachmentId();

                        fndAttachment.setSourcePkValue(String.valueOf(multi.getRecordId()));
                        iFndAttachmentService.insert(iRequest, fndAttachment);

                        multi.setAttachmentId(fndAttachment.getAttachmentId());
                        fndAttachmentMultiMapper.updateByPrimaryKey(multi);
                        Long newAttachmentId = fndAttachment.getAttachmentId();

                        //复制UFS记录
                        //ufsFileService.copyUfsByAttachmentId(sourceAttachmentId, newAttachmentId);

                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        return attachmentList;
    }


    @Override
    public int selectAttachmentCodeNullCount(Long projectId, String projectAttachmentCategory) {
        return hlsCusPrjProjectAttachmentMapper.selectAttachmentCodeNullCount(projectId, projectAttachmentCategory);
    }

    @Override
    public int selectAttachmentExistCount(Long projectId, String projectAttachmentCategory) {
        return hlsCusPrjProjectAttachmentMapper.selectAttachmentExistCount(projectId, projectAttachmentCategory);
    }
    @Autowired
    private HlsCreditLineAttachMapper hlsCreditLineAttachMapper;

    @Override
    public int saveProjectAttachment(IRequest requestContext,HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment) throws HlsCusException {
        HlsCreditLineAttach lineAttach = new HlsCreditLineAttach();
        lineAttach.setChanceId(hlsCusPrjProjectAttachment.getSourceId());
        List<HlsCreditLineAttach> lineAttaches = hlsCreditLineAttachMapper.queryCredAttachmentByChanceId(lineAttach);
        if (CollectionUtils.isNotEmpty(lineAttaches)) {
            for (HlsCreditLineAttach chanceBp : lineAttaches) {
                if(chanceBp != null){
                    copyPublicFields(requestContext,hlsCusPrjProjectAttachment, chanceBp);
                    // 复制附件
                    //获取原来的附件
                    FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
                    fndAttachmentMulti.setTableName("hls_credit_line_attach");
                    fndAttachmentMulti.setTablePkValue(chanceBp.getChanceAttachmentId().toString());
                    List<FndAttachmentMulti> fndAttachmentMultis = fndAttachmentMultiMapper.select(fndAttachmentMulti);
                    if (!fndAttachmentMultis.isEmpty()){
                        fndAttachmentMultis.forEach(attachmentMulti -> {
                            FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentMulti.getAttachmentId());
                            if(fndAttachment==null){
                                return;
                            }
                            FndAttachment newAttachment = new FndAttachment();
                            BeanUtils.copyProperties(fndAttachment,newAttachment);
                            newAttachment.setAttachmentId(null);
                            fndAttachmentMapper.insertSelective(newAttachment);
                            FndAttachmentMulti newMulti = new FndAttachmentMulti();
                            BeanUtils.copyProperties(attachmentMulti,newMulti);
                            newMulti.setTablePkValue(hlsCusPrjProjectAttachment.getProjectAttachmentId().toString());
                            newMulti.setRecordId(null);
                            newMulti.setAttachmentId(newAttachment.getAttachmentId());
                            newMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
                            fndAttachmentMultiMapper.insertSelective(newMulti);
                            newAttachment.setSourcePkValue(newMulti.getRecordId().toString());
                            fndAttachmentMapper.updateByPrimaryKeySelective(newAttachment);
                        });
                    }
                }

            }
        }

        return 0;
    }


    private void copyPublicFields(IRequest request, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, HlsCreditLineAttach chanceBp) throws HlsCusException {
        try {
            hlsCusPrjProjectAttachment.setSourceId(null);
            // 复制公共属性
            BeanUtils.copyProperties(chanceBp,hlsCusPrjProjectAttachment);
            hlsCusPrjProjectAttachment.setProjectAttachmentCategory(chanceBp.getAttachmentCategory());

            // 保存新创建的对象到数据库
            self().insertSelective(request,hlsCusPrjProjectAttachment);
        } catch (Exception e) {
            logger.error("Failed to copy and save project attachment", e);
            throw new HlsCusException(e.getMessage());
        }
    }


    @Override
    public synchronized void batchUpdateAttachemnt(IRequest iRequest, List<HlsCusPrjProjectAttachment> list) {
        Long orderNumberMax = hlsCusPrjProjectAttachmentMapper.selectOrderNumberMax(list.get(0));
        int count = 0;
        for (HlsCusPrjProjectAttachment dt : list) {
            if (HlsCusConstant.projectText.FCT_APPROVAL_NOTICE_REPORT.equalsIgnoreCase(dt.getProjectAttachmentCategory())) {
                continue;
            } else {
                if (dt.getProjectAttachmentId() != null) {

                    if (dt.getFileId() != null) {
                        HlsCusSysFile sysFile = new HlsCusSysFile();
                        sysFile.setFileId(Long.parseLong(dt.getFileId()));
                        sysFile.setFileName(dt.getFileName());
                        sysFile.setDescription(dt.getDescription());
                        hlsSysFileService.updateByPrimaryKeySelective(iRequest, sysFile);
                    }

                    dt.set__status("update");
                    dt = self().updateByPrimaryKeySelective(iRequest, dt);
                } else {
                    dt.set__status("add");
                    dt.setUploadDate(new Date());
                    dt.setOrderNumber(orderNumberMax + count);
                    dt = self().insertSelective(iRequest, dt);
                    count++;
                }
            }
        }
    }

    @Override
    public boolean deleteAttachment(Long attachmentId) {
        if (attachmentId == null) {
            return false;
        }
        FndAttachmentMulti multi = new FndAttachmentMulti();
        multi.setAttachmentId(attachmentId);
        fndAttachmentMultiMapper.delete(multi);
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
        hlsCusPrjProjectAttachment.setProjectAttachmentId(attachmentId);
        hlsCusPrjProjectAttachmentMapper.deleteByPrimaryKey(hlsCusPrjProjectAttachment);
        return true;
    }

    @Override
    public List<HlsCusPrjProjectAttachment> selectContractAttachmentInfo(IRequest iRequest, HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusPrjProjectAttachment> list = hlsCusPrjProjectAttachmentMapper.selectContractAttachmentInfo(hlsCusPrjProjectAttachment);
        return list;
    }

    @Override
    public HlsCusPrjProjectAttachmentService self() {
        return HlsCusPrjProjectAttachmentService.super.self();
    }
}
