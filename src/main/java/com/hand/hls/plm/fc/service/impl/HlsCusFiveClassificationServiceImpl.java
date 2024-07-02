package com.hand.hls.plm.fc.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassificationContract;
import com.hand.hls.plm.fc.dto.PlmFiveClassifyMeet;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.plm.fc.service.PlmFiveClassifyMeetService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFiveClassificationServiceImpl extends BaseServiceImpl<HlsCusFiveClassification> implements HlsCusIFiveClassificationService {

    @Autowired
    private HlsCusFiveClassificationMapper mapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusIFiveClassificationContractService fcContractService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    /*@Autowired
    private HlsCusPlmAttachmentMapper attachmentMapper;*/

    @Autowired
    private HlsCusFiveClassificationContractMapper fiveClassificationContractMapper;

    /*@Autowired
    private HlsCusPlmIAttachmentService attachmentService;*/

    @Autowired
    private PlmFiveClassifyMeetService plmFiveClassifyMeetService;

    @Autowired
    private HlsCusIFiveClassificationContractService fiveClassificationContractService;

    private Logger logger = LoggerFactory.getLogger(HlsCusFiveClassificationServiceImpl.class);

    /**
     * @Description:五级分类保存
     * @Author: Wty
     * @Date: Created om 15:54 2018/5/21
     */
    @Override
    public HlsCusFiveClassification save(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        HlsCusFiveClassification returnDto;
        if (fiveClassification.getFiveClassificationId() == null || fiveClassification.getFiveClassificationId() == 0) {
            fiveClassification.setDocumentCategory("FIVE_CLASSIFICATION");
            fiveClassification.setDocumentType("FIVE_CLASSIFICATION");
            fiveClassification.setBusinessType("FIVE_CLASSIFICATION");
            fiveClassification.setStatus("NEW");
            fiveClassification.setCompanyId(iRequest.getCompanyId());
            Map<String, String> params = new HashMap<String, String>();
            fiveClassification.setFiveClassificationNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, fiveClassification.getDocumentCategory(), fiveClassification.getDocumentType(), fiveClassification.getBusinessType(), params));
            returnDto = self().insertSelective(iRequest, fiveClassification);
            return returnDto;
        } else {
            returnDto = self().updateByPrimaryKeySelective(iRequest, fiveClassification);
            StringBuilder builder=new StringBuilder();
            //更新
            if (CollectionUtils.isNotEmpty(fiveClassification.getFiveClassificationContracts())) {
                for (HlsCusFiveClassificationContract classificationContract:fiveClassification.getFiveClassificationContracts()) {
                    fcContractService.updateByPrimaryKeySelective(iRequest,classificationContract);
                    if(StringUtils.isNotBlank(builder.toString())){
                        builder.append(",").append(classificationContract.getContractName());
                    }else{
                        builder.append(classificationContract.getContractName());
                    }
                }
            }
            returnDto.setContractNames(builder.toString());
            return returnDto;
        }
    }


    @Override
    public HlsCusFiveClassification contractSave(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        HlsCusFiveClassification returnDto=fiveClassification;
        List<HlsCusFiveClassificationContract> hlsCusFiveClassificationContractList = fiveClassification.getFiveClassificationContracts();
        if (CollectionUtils.isNotEmpty(hlsCusFiveClassificationContractList)) {
            for (HlsCusFiveClassificationContract dt : hlsCusFiveClassificationContractList) {
                if (dt.getFiveClassifyConId() == null) {
                    dt.setFiveClassificationId(fiveClassification.getFiveClassificationId());
                    dt = fiveClassificationContractService.insertSelective(iRequest, dt);
                } else {
                    dt.setFiveClassificationId(fiveClassification.getFiveClassificationId());
                    dt = fiveClassificationContractService.updateByPrimaryKeySelective(iRequest, dt);
                }
            }
        }

        return returnDto;
    }

    /**
     * @Description:五级分类工作流提交
     * @Author: Wty
     * @Date: Created om 16:17 2018/5/21
     */
    @Override
    public HlsCusFiveClassification submitWfl(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        HlsCusFiveClassification hlsCusFiveClassificationdto = self().save(iRequest, fiveClassification);
        databaseLockProvider.lock(hlsCusFiveClassificationdto);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        List<HlsCusFiveClassification> list = new ArrayList<>();
        list.add(hlsCusFiveClassificationdto);
        params.put("workFlowType", "PLM_FC_WORK_FLOW");
        activitiStartService.start(iRequest, list, params);
        //修改五级分类状态
        hlsCusFiveClassificationdto.setStatus("APPROVING");
        HlsCusFiveClassification fiveClass = new HlsCusFiveClassification();
        fiveClass.setFiveClassificationId(hlsCusFiveClassificationdto.getFiveClassificationId());
        fiveClass.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, fiveClass).getObjectVersionNumber());
        self().updateByPrimaryKeySelective(iRequest, hlsCusFiveClassificationdto);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        //发送系统消息
        String msg = userName + "创建了" + hlsCusFiveClassificationdto.getFiveClassificationNumber() + "五级分类审批流程";
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "五级分类审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, hlsCusFiveClassificationdto.getFiveClassificationId(), "FIVE_CLASSIFICATION", "FIVE_CLASSIFICATION", "PLM_FIVE_CLASSIFICATION", "PLM_FIVECLASSIFY_WFL", "P2D", paramsEvent);
        return hlsCusFiveClassificationdto;
    }

    /**
     * @Description:五级分类首页rollTable查询
     * @Author: Wty
     * @Date: Created om 10:20 2018/5/22
     */
    @Override
    public List<HlsCusFiveClassification> homeRollTableQuery(IRequest iRequest, HlsCusFiveClassification fiveClassification, int page, int pageSize) {
        if (fiveClassification.getDocumentStatus() != null && !"".equals(fiveClassification.getDocumentStatus())) {
            fiveClassification.setDocumentStatusArray(fiveClassification.getDocumentStatus().split(","));
        }
        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("five_classification_number desc");
        return mapper.homeRollTableQuery(fiveClassification);
    }

    /**
     * @Description:五级分类判断合同或附件是否有变更
     * @Author: Wty
     * @Date: Created om 10:54 2018/5/23
     */
    @Override
    public Map<String, Boolean> contractOrAttachmentIsChange(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        Map<String, Boolean> map = new HashMap<>();
       /* HlsCusPlmAttachment attachment = new HlsCusPlmAttachment();
        attachment.setChangeIq("CHANGE");
        attachment.setPlmId(fiveClassification.getFiveClassificationId());
        attachment.setPlmType(fiveClassification.getFiveClassificationType());
        if (CollectionUtils.isNotEmpty(attachmentMapper.selectAttachment(attachment))) {
            map.put("attachmentChange", true);
        } else {
            map.put("attachmentChange", false);
        }*/
        HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
        fiveClassificationContract.setFiveClassificationId(fiveClassification.getFiveClassificationId());
        fiveClassificationContract.setChangeIq("CHANGE");
        if (CollectionUtils.isNotEmpty(fiveClassificationContractMapper.selectFiveClassificationContracts(fiveClassificationContract))) {
            map.put("contractChange", true);
        } else {
            map.put("contractChange", false);
        }
        return map;
    }

    /**
     * @Description:五级分类变更工作流提交
     * @Author: Wty
     * @Date: Created om 14:07 2018/5/23
     */
    @Override
    public HlsCusFiveClassification submitChangeWfl(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        HlsCusFiveClassification dto = self().save(iRequest, fiveClassification);
        databaseLockProvider.lock(dto);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        List<HlsCusFiveClassification> list = new ArrayList<>();
        list.add(dto);
        params.put("workFlowType", "PLM_FC_CHANGE_WORK_FLOW");
        activitiStartService.start(iRequest, list, params);
        dto.setStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, dto);
        return dto;
    }

    /**
     * @Description:五级分类copy备份数据
     * @Author: Wty
     * @Date: Created om 11:23 2018/6/13
     */
    @Override
    public void copyChangeData(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
        fiveClassificationContract.setFiveClassificationId(fiveClassification.getFiveClassificationId());
        fiveClassificationContract.setChangeIq("CHANGE");

        //查找变更数据
        List<HlsCusFiveClassificationContract> changeContractsList = fiveClassificationContractMapper.selectFiveClassificationContracts(fiveClassificationContract);
        //判断是否有CHANGE的数据，如果有就不保存，如果没有CHANGE数据则进行copy
        if (CollectionUtils.isEmpty(changeContractsList)) {
            Long maxChangeTime = 1L;
            fiveClassificationContract.setChangeIq("NORMAL");
            //查询最大的changeTime
            List<HlsCusFiveClassificationContract> changeTimeList = fiveClassificationContractMapper.selectMaxChangeTime(fiveClassificationContract);
            //不为空则说明有变更过，否则的话就默认1
            if (CollectionUtils.isNotEmpty(changeTimeList)) {
                maxChangeTime = changeTimeList.get(0).getChangeTime() + 1;
            }
            List<HlsCusFiveClassificationContract> fiveClassificationContractList = fiveClassificationContractMapper.selectFiveClassificationContracts(fiveClassificationContract);
            if (CollectionUtils.isNotEmpty(fiveClassificationContractList)) {
                //copy五级分类合同
                for (int i = 0; i < fiveClassificationContractList.size(); i++) {
                    fiveClassificationContractList.get(i).setChangeIq("CHANGE");
                    fiveClassificationContractList.get(i).setChangeTime(maxChangeTime);
                    fiveClassificationContractList.get(i).setFiveClassifyConId(null);
                    fiveClassificationContractList.get(i).set__status("add");
                }
                fiveClassificationContractService.batchUpdate(iRequest, fiveClassificationContractList);
            }
            //备份附件信息
           /* HlsCusPlmAttachment attachment = new HlsCusPlmAttachment();
            attachment.setChangeIq("NORMAL");
            attachment.setPlmType("FC");
            attachment.setPlmId(fiveClassification.getFiveClassificationId());
            List<HlsCusPlmAttachment> attachmentList = attachmentMapper.selectAttachment(attachment);
            if (CollectionUtils.isNotEmpty(attachmentList)) {
                for (int i = 0; i < attachmentList.size(); i++) {
                    attachmentList.get(i).setAttachmentId(null);
                    attachmentList.get(i).setChangeIq("CHANGE");
                    attachmentList.get(i).setChangeTime(maxChangeTime);
                    attachmentList.get(i).set__status("add");
                }
                attachmentService.batchUpdate(iRequest, attachmentList);
            }*/
        }
    }

    /**
     * @Description:查找五级分类
     * @Author: Wty
     * @Date: Created om 下午2:10 2018/7/16
     */
    @Override
    public List<HlsCusFiveClassification> queryAll(IRequest iRequest, HlsCusFiveClassification fiveClassification, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryAll(fiveClassification);
    }

    /**
     * 审批过程中保存参会人员
     *
     * @param iRequest
     * @param fiveClassification
     * @return
     */
    @Override
    public List<HlsCusFiveClassification> meetwfl(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        //保存参会人员
        if (fiveClassification.getPlmFiveClassifyMeetList() != null && fiveClassification.getPlmFiveClassifyMeetList().size() > 0) {
            List<PlmFiveClassifyMeet> plmFiveClassifyMeets = fiveClassification.getPlmFiveClassifyMeetList();
            for (PlmFiveClassifyMeet dt : plmFiveClassifyMeets) {
                dt.setFiveClassificationId(fiveClassification.getFiveClassificationId());
                if (dt.getFcMeetId() == null || dt.getFcMeetId() == 0) {
                    dt.set__status(HlsCusConstant.DATA_STATUS.STATUS_ADD);
                    plmFiveClassifyMeetService.insertSelective(iRequest, dt);
                } else {
                    dt.set__status(HlsCusConstant.DATA_STATUS.STATUS_UPDATE);
                    plmFiveClassifyMeetService.updateByPrimaryKeySelective(iRequest, dt);
                }
            }
        }
        mapper.updateByPrimaryKeySelective(fiveClassification);
        return mapper.queryAll(fiveClassification);
    }

}