package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.*;
import com.hand.hls.fct.mapper.HlsCusFctProjectFinStatementMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMapper;
import com.hand.hls.fct.service.HlsCreditLineGuarantorService;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineMortgageService;
import com.hand.hls.fct.service.HlsCusHlsCreditLinePledgeService;
import com.hand.hls.prj.dto.HlsCusCreditInfo;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditLineServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLine> implements HlsCreditLineService {

    @Autowired
    private HlsCusHlsCreditLineMapper mapper;
    @Autowired
    private HlsCusFctProjectFinStatementMapper hlsCusFctProjectFinStatementMapper;
    @Autowired
    private HlsCreditLineGuarantorService hlsCreditLineGuarantorService;
    @Autowired
    private HlsCusHlsCreditLineMortgageService hlsCusHlsCreditLineMortgageService;
    @Autowired
    private HlsCusHlsCreditLinePledgeService hlsCusHlsCreditLinePledgeService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Override
    public List<HlsCusFctProjectFinStatement> queryFinStatementByBpId(HlsCusHlsCreditLine creditLine) {
        return hlsCusFctProjectFinStatementMapper.queryFinStatementByBpId(creditLine);
    }

    @Override
    public List<HlsCusHlsCreditLine> selectCreditLineInfo(HlsCusHlsCreditLine hlsCusHlsCreditLine, int page, int pageSize) throws ParseException {
        PageHelper.startPage(page, pageSize);
        String beginDate = hlsCusHlsCreditLine.getDate_startup();
        String endDate = hlsCusHlsCreditLine.getDate_out();
        String limitFrom = hlsCusHlsCreditLine.getLimit_from();
        String limitTo = hlsCusHlsCreditLine.getLimit_to();
        String creditObjectName = hlsCusHlsCreditLine.getCredit_object_name();
        String creditLineStatus = hlsCusHlsCreditLine.getCreditLineStatus();
        String businessType = hlsCusHlsCreditLine.getBusinessType();
        String fctCreditLineStatus = hlsCusHlsCreditLine.getFctCreditLineStatus();

        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");

        if (beginDate != null && beginDate != "") {
            hlsCusHlsCreditLine.setBeginDate(dft.parse(beginDate));
        }
        if (endDate != null && endDate != "") {
            hlsCusHlsCreditLine.setEndDate(dft.parse(endDate));
        }
        if (limitFrom != null && limitFrom != "") {
            hlsCusHlsCreditLine.setLimitFrom(limitFrom);
        }
        if (limitTo != null && limitTo != "") {
            hlsCusHlsCreditLine.setLimitTo(limitTo);
        }
        if (creditObjectName != null && creditObjectName != "") {
            hlsCusHlsCreditLine.setCreditObjectName(creditObjectName);
        }
        if (creditLineStatus != null && creditLineStatus != "") {
            hlsCusHlsCreditLine.setCreditLineStatus(creditLineStatus);
        }
        if (businessType != null && businessType != "") {
            hlsCusHlsCreditLine.setBusinessType(businessType);
        }
        if (fctCreditLineStatus != null && fctCreditLineStatus != "") {
            hlsCusHlsCreditLine.setFctCreditLineStatus(fctCreditLineStatus);
        }
        List<HlsCusHlsCreditLine> list = mapper.selectCreditLineInfo(hlsCusHlsCreditLine);
        return list;
    }

    @Override
    public List<HlsCusHlsCreditLine> creditLineDetailQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine) {
        return mapper.creditLineDetailQuery(hlsCusHlsCreditLine);
    }

    /**
     * 保存授信评审基本信息及从表信息
     *
     * @param iRequest
     * @param hlsCusCreditInfo
     * @return
     */

    /**
     * HLS_CREDIT_LINE_DETAIL_ATT 授信审查附件类型
     */
    private static final String HLS_CREDIT_LINE_DETAIL_ATT = "HLS_CREDIT_LINE_DETAIL_ATT";
    @Override
    public HlsCusHlsCreditLine hlscreditLineSave(IRequest iRequest, HlsCusCreditInfo hlsCusCreditInfo) {
        if (hlsCusCreditInfo != null) {
            HlsCusHlsCreditLine hlsCusHlsCreditLine = hlsCusCreditInfo.getHlsCusHlsCreditLine();
            if (hlsCusHlsCreditLine != null) {
                /*保存主表信息*/
                if (hlsCusHlsCreditLine.getCreditLineId() == null) {
                    hlsCusHlsCreditLine = self().insertSelective(iRequest, hlsCusHlsCreditLine);
                } else {
                    hlsCusHlsCreditLine = self().updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLine);
                }
                /*保存财报信息*/
//                HlsCusFctProjectFinStatement hlsCusFctProjectFinStatement=hlsCusCreditInfo.getFctProjectFinStatements();
//                if(hlsCusFctProjectFinStatement!=null&&hlsCusFctProjectFinStatement.getFinStatementId()==null){
//                    hlsCusFctProjectFinStatement.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
//                    hlsCusFctProjectFinStatement.setProjectId(-1l);
//                    hlsCusFctProjectFinStatementMapper.insertSelective(hlsCusFctProjectFinStatement);
//                }else{
//                    hlsCusFctProjectFinStatement.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
//                    hlsCusFctProjectFinStatement.setProjectId(-1l);
//                    hlsCusFctProjectFinStatementMapper.updateByPrimaryKeySelective(hlsCusFctProjectFinStatement);
//                }
                /*保存保证信息*/
                List<HlsCusHlsCreditLineGuarantor> hlsCusHlsCreditLineGuarantorList = hlsCusCreditInfo.getHlsCreditLineGuarantors();
                if (CollectionUtils.isNotEmpty(hlsCusHlsCreditLineGuarantorList)) {
                    for (HlsCusHlsCreditLineGuarantor dt : hlsCusHlsCreditLineGuarantorList) {
                        if (dt.getCreditLineGuarantorId() == null) {
                            dt.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectId(-1L);
                            dt = hlsCreditLineGuarantorService.insertSelective(iRequest, dt);
                        } else {
                            dt.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectId(-1L);
                            dt = hlsCreditLineGuarantorService.updateByPrimaryKeySelective(iRequest, dt);
                        }
                    }
                }

                List<HlsCusHlsCreditLineMortgage> hlsCusHlsCreditLineMortgageList = hlsCusCreditInfo.getHlsCusHlsCreditLineMortgages();
                if (CollectionUtils.isNotEmpty(hlsCusHlsCreditLineMortgageList)) {
                    for (HlsCusHlsCreditLineMortgage dt : hlsCusHlsCreditLineMortgageList) {
                        if (dt.getCreditLineMortgageId() == null) {
                            dt.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectId(-1L);
                            dt = hlsCusHlsCreditLineMortgageService.insertSelective(iRequest, dt);
                        } else {
                            dt.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectId(-1L);
                            dt = hlsCusHlsCreditLineMortgageService.updateByPrimaryKeySelective(iRequest, dt);
                        }
                    }
                }

                List<HlsCusHlsCreditLinePledge> hlsCusHlsCreditLinePledgeList = hlsCusCreditInfo.getHlsCusHlsCreditLinePledges();
                if (CollectionUtils.isNotEmpty(hlsCusHlsCreditLinePledgeList)) {
                    for (HlsCusHlsCreditLinePledge dt : hlsCusHlsCreditLinePledgeList) {
                        if (dt.getCreditLinePledgeId() == null) {
                            dt.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectId(-1L);
                            dt = hlsCusHlsCreditLinePledgeService.insertSelective(iRequest, dt);
                        } else {
                            dt.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectId(-1L);
                            dt = hlsCusHlsCreditLinePledgeService.updateByPrimaryKeySelective(iRequest, dt);
                        }
                    }
                }

                List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusCreditInfo.getHlsCusPrjProjectAttachmentList();
                if (CollectionUtils.isNotEmpty(hlsCusPrjProjectAttachments)) {
                    for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachments) {
                        if (dt.getProjectAttachmentId() == null) {
                            dt.setProjectId(hlsCusHlsCreditLine.getCreditLineId());
                            dt.setProjectAttachmentCategory(HLS_CREDIT_LINE_DETAIL_ATT);
                            dt = hlsCusPrjProjectAttachmentService.insertSelective(iRequest, dt);
                        } else {
                            dt.setProjectId(hlsCusHlsCreditLine.getCreditLineId());
                            dt = hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(iRequest, dt);
                        }
                    }
                }
            }

            return hlsCusHlsCreditLine;
        }
        return null;
    }


    /*提交授信评审信息*/
    @Override
    public HlsCusHlsCreditLine hlscreditLineSubmit(IRequest iRequest, HlsCusCreditInfo hlsCusCreditInfo) {
        /*基本信息保存*/
//        HlsCusHlsCreditLine hlsCusHlsCreditLine = self().hlscreditLineSave(iRequest, hlsCusCreditInfo);
        HlsCusHlsCreditLine hlsCusHlsCreditLine = hlsCusCreditInfo.getHlsCusHlsCreditLine();
        List<HlsCusHlsCreditLine> hlsCusHlsCreditLines = new ArrayList<>();
        hlsCusHlsCreditLines.add(hlsCusHlsCreditLine);
        databaseLockProvider.lock(hlsCusHlsCreditLine);

        //插入事件
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + hlsCusHlsCreditLine.getCreditLineName() + "授信评审的授信审核" + hlsCusHlsCreditLine.getCreditLineNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "授信评审审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, hlsCusHlsCreditLine.getCreditLineId(), hlsCusHlsCreditLine.getDocumentCategory(), hlsCusHlsCreditLine.getDocumentType(), "HLS_CREDIT_LINE", "HLS_CREDIT_LINE", "P2D", paramsEvent);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "HLS_CREDIT_LINE_SUBMIT");
        activitiStartService.start(iRequest, hlsCusHlsCreditLines, params);
        hlsCusHlsCreditLine.setCreditLineStatus("APPROVING");
        hlsCusHlsCreditLine = self().updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLine);
        return hlsCusHlsCreditLine;
    }

    @Override
    public int updateWithdrawBalance(Long creditLineId) {
        return mapper.updateWithdrawBalance(creditLineId);
    }

    @Override
    public int updateCreditExposureAmt(Long creditLineId) {
        return mapper.updateCreditExposureAmt(creditLineId);
    }


}
