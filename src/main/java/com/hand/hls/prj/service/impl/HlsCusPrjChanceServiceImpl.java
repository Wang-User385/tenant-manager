package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjChanceMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjChanceServiceImpl extends BaseServiceImpl<HlsCusPrjChance> implements HlsCusPrjChanceService {

    @Autowired
    private HlsCusPrjChanceMapper hlsCusPrjChanceMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusPrjBpFinancingSituationService hlsCusPrjBpFinancingSituationService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsSystemNoticeMapper hlsSystemNoticeMapper;
    /*@Autowired
    private HlsCusSysUserAuthorityTrxService hlsCusSysUserAuthorityTrxService;*/


    private static final String DOCUMENT_CATEGORY="PRJ_PROJECT";//立项单据类别

    private static final String DOCUMENT_CHANCE_CATEGORY="PRJ_CHANCE";//立项单据类别

    private static final String DOCUMENT_TYPE="PRJ_PROJECT";//立项单据类型

    private static final String PROJECT_STATUS_NEW="NEW";//立项单据类型

    private static final String PROJECT_STATUS_APPROVING="APPROVING";//立项单据类型

    private static final String PROJECT_WORKFLOW_TYPE="PRJ_CHANCE_WFL";//立项工作流TYPE


  /*  @Override
    public HlsCusPrjChance prjChanceSave(IRequest iRequest, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) {
        //项目立项明细
        HlsCusPrjChance hlsCusPrjChance=new HlsCusPrjChance();
        hlsCusPrjChance=hlsCusPrjProjectInfo.getHlsCusPrjChance();
        if(hlsCusPrjChance.getChanceId()==null||hlsCusPrjChance.getChanceId()==0){
            hlsCusPrjChance.setDocumentCategory(DOCUMENT_CATEGORY);
            hlsCusPrjChance.setProjectStatus(PROJECT_STATUS_NEW);
            hlsCusPrjChance.setCompanyId(iRequest.getCompanyId());
            hlsCusPrjChance.setDocumentType(DOCUMENT_TYPE);
            Map<String, String> params = new HashMap<String, String>();
            hlsCusPrjChance.setCreatedBy(iRequest.getUserId());
            hlsCusPrjChance.setProjectNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest,hlsCusPrjChance.getDocumentCategory(), hlsCusPrjChance.getDocumentType(), hlsCusPrjChance.getBusinessType(), params));
            hlsCusPrjChance=self().insertSelective(iRequest,hlsCusPrjChance);
            //自动插入权限
//            hlsCusSysUserAuthorityTrxService.insertUserAuthor(iRequest,"PRJ_CHANCE",hlsCusPrjChance.getChanceId(),"RISK");
//            hlsCusSysUserAuthorityTrxService.insertChiefUserAuthor(iRequest,"PRJ_CHANCE",hlsCusPrjChance.getChanceId());
        }else{
            HlsCusPrjChance oldPrjChance=new HlsCusPrjChance();
            oldPrjChance.setChanceId(hlsCusPrjChance.getChanceId());
            oldPrjChance=self().selectByPrimaryKey(iRequest,oldPrjChance);
            hlsCusPrjChance.setObjectVersionNumber(oldPrjChance.getObjectVersionNumber());
            hlsCusPrjChance=self().updateByPrimaryKey(iRequest,hlsCusPrjChance);
        }
        //项目立项客户信息
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList=new ArrayList<>();
        hlsCusPrjProjectBpList=hlsCusPrjProjectInfo.getHlsCusPrjProjectBpList();
        for(HlsCusPrjProjectBp dt:hlsCusPrjProjectBpList){
            if(dt.getPrjBpId()==null||dt.getPrjBpId()==0){
                dt.setChanceId(hlsCusPrjChance.getChanceId());
                dt.setProjectId(-1L);
                dt.set__status(DTOStatus.ADD);
                dt.setBpCategroy("PRJ_CHANCE");
                dt=hlsCusPrjProjectBpService.insertSelective(iRequest,dt);
            }else{
                dt.set__status(DTOStatus.UPDATE);
                dt.setChanceId(hlsCusPrjChance.getChanceId());
                dt=hlsCusPrjProjectBpService.updateByPrimaryKey(iRequest,dt);
            }
        }
        //租赁立项报价信息
        HlsCusPrjQuotation hlsCusPrjQuotation=new HlsCusPrjQuotation();
        hlsCusPrjQuotation=hlsCusPrjProjectInfo.getHlsCusPrjQuotation();
        if(hlsCusPrjQuotation.getQuotationId()==null||hlsCusPrjQuotation.getQuotationId()==0){
            hlsCusPrjQuotation.setProjectId(-1L);
            hlsCusPrjQuotation.setSourceDocumentId(hlsCusPrjChance.getChanceId());
            hlsCusPrjQuotation.setSourceDocumentCategory(DOCUMENT_CATEGORY);
            hlsCusPrjQuotation.setDataClass(DOCUMENT_CHANCE_CATEGORY);
            hlsCusPrjQuotation=hlsCusPrjQuotationService.insertSelective(iRequest,hlsCusPrjQuotation);
        }else{
            HlsCusPrjQuotation oldPrjQuotation=new HlsCusPrjQuotation();
            oldPrjQuotation.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            oldPrjQuotation=hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,oldPrjQuotation);
            hlsCusPrjQuotation.setObjectVersionNumber(oldPrjQuotation.getObjectVersionNumber());
            hlsCusPrjQuotation=hlsCusPrjQuotationService.updateByPrimaryKey(iRequest,hlsCusPrjQuotation);
        }
        //租赁立项现金流信息
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList=new ArrayList<>();
        hlsCusPrjQuotationCashflowList=hlsCusPrjProjectInfo.getHlsCusPrjQuotationCashflowList();
        for(HlsCusPrjQuotationCashflow dt:hlsCusPrjQuotationCashflowList){
            if(dt.getQuotationCashflowId()==null||dt.getQuotationCashflowId()==0){
                dt.set__status(DTOStatus.ADD);
                dt.setProjectId(-1L);
                dt.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                dt=hlsCusPrjQuotationCashflowService.insertSelective(iRequest,dt);
            }else{
                dt.set__status(DTOStatus.UPDATE);
                dt.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                dt=hlsCusPrjQuotationCashflowService.updateByPrimaryKey(iRequest,dt);
            }
        }
        //租赁立项他行融资信息

        //删除旧的融资情况
        HlsCusPrjBpFinancingSituation oldPrjFin=new HlsCusPrjBpFinancingSituation();
        oldPrjFin.setChanceId(hlsCusPrjChance.getChanceId());
        oldPrjFin.setProjectId(-1L);
        List<HlsCusPrjBpFinancingSituation> oldList=hlsCusPrjBpFinancingSituationService.select(iRequest,oldPrjFin,1,10000);
        hlsCusPrjBpFinancingSituationService.batchDelete(oldList);

        //插入新的
        List<HlsCusPrjBpFinancingSituation> hlsCusPrjBpFinancingSituationList=new ArrayList<>();
        hlsCusPrjBpFinancingSituationList=hlsCusPrjProjectInfo.getHlsCusPrjBpFinancingSituationList();
        for(HlsCusPrjBpFinancingSituation dt:hlsCusPrjBpFinancingSituationList){
            dt.set__status(DTOStatus.ADD);
            dt.setProjectId(-1L);//给个默认值 不能为空
            dt.setFinancingSituationId(null);
            dt.setChanceId(hlsCusPrjChance.getChanceId());
            dt=hlsCusPrjBpFinancingSituationService.insertSelective(iRequest,dt);
        }
        return hlsCusPrjChance;
    }
*/
    @Override
    public List<Map> prjHomePageGetAllStatusProjectCount(IRequest iRequest, HlsCusPrjChance hlsCusPrjChance) {
        return hlsCusPrjChanceMapper.prjHomePageGetAllStatusProjectCount(hlsCusPrjChance);
    }

    @Override
    public List<HlsCusPrjChance> prjHomePageProjectInfoGrid(IRequest iRequest, HlsCusPrjChance hlsCusPrjChance, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusPrjChanceMapper.prjHomePageProjectInfoGrid(hlsCusPrjChance);
    }

   /* @Override
    public HlsCusPrjChance prjChanceSubmitWfl(IRequest iRequest, HlsCusPrjProjectInfo HlsCusPrjProjectInfo){
        //保存前先更新
        HlsCusPrjChance hlsCusPrjChance=new HlsCusPrjChance();
        hlsCusPrjChance=self().prjChanceSave(iRequest,HlsCusPrjProjectInfo);
        List<HlsCusPrjChance> hlsCusPrjChanceList=new ArrayList<>();
        hlsCusPrjChanceList.add(hlsCusPrjChance);
        databaseLockProvider.lock(hlsCusPrjChance);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //插入事件
        Map<String,Object> paramsEvent = new HashMap<String,Object>();
        String userName ="";
        if(loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size()>0){
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg=userName+"创建了"+hlsCusPrjChance.getProjectName()+"项目的立项审核"+hlsCusPrjChance.getProjectNumber();
        paramsEvent.put("message",msg);
        paramsEvent.put("noticeTitle","租赁项目立项审批");
        paramsEvent.put("noticeType","NOTICE");
        paramsEvent.put("url","");
        paramsEvent.put("level",1L);
        sysEventService.eventSave(iRequest,hlsCusPrjChance.getChanceId(),hlsCusPrjChance.getDocumentCategory(),hlsCusPrjChance.getDocumentType(),"PRJ","PRJ_CHANCE","P2D",paramsEvent);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", PROJECT_WORKFLOW_TYPE);
        activitiStartService.start(iRequest,hlsCusPrjChanceList, params);

        //启动完成后更新立项单据状态
        hlsCusPrjChance.setProjectStatus(PROJECT_STATUS_APPROVING);
        HlsCusPrjChance getPrjChance=new HlsCusPrjChance();
        getPrjChance.setChanceId(hlsCusPrjChance.getChanceId());
        hlsCusPrjChance.setObjectVersionNumber(self().selectByPrimaryKey(iRequest,getPrjChance).getObjectVersionNumber());
        hlsCusPrjChance=self().updateByPrimaryKeySelective(iRequest,hlsCusPrjChance);
        return hlsCusPrjChance;
    }
*/
    /**
     * 查询立项对应动态信息
     * @param hlsSystemNotice
     * @return
     */
    @Override
    public List<HlsSystemNotice> queryPrjChanceNotice(HlsSystemNotice hlsSystemNotice, int page, int pageSize){
        PageHelper.startPage(page, pageSize);
        //return hlsSystemNoticeMapper.selectDocumentNotice(hlsSystemNotice);
        return null;
    }
}