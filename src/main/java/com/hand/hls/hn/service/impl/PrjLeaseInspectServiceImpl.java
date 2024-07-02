package com.hand.hls.hn.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjLeaseInfo;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.mapper.PrjLeaseCheckMapper;
import com.hand.hls.hn.mapper.PrjLeaseInfoMapper;
import com.hand.hls.hn.mapper.PrjLeaseInspectMapper;
import com.hand.hls.hn.service.IPrjLeaseInfoService;
import com.hand.hls.hn.service.IPrjLeaseInspectService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjLeaseInspectServiceImpl extends BaseServiceImpl<PrjLeaseInspect> implements IPrjLeaseInspectService{


    @Autowired
    private IPrjLeaseInspectService prjLeaseInspectService;

    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    FndCompanyMapper fndCompanyMapper;

    @Autowired
    private PrjLeaseInspectMapper prjLeaseInspectMapper;
    @Autowired
    private PrjLeaseInfoMapper prjLeaseInfoMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private IPrjLeaseInfoService prjLeaseInfoService;

    /**
     * 提交合同文本变更和追加担保工作流
     */
    @Override
    public void submitPrjContractChange(IRequest request, PrjLeaseInspect prjLeaseInspect) {
        List<PrjLeaseInspect> prjLeaseInspectList = new ArrayList<>();
        PrjLeaseInspect prjLeaseInspects = prjLeaseInspectService.selectByPrimaryKey(request, prjLeaseInspect);
        prjLeaseInspectList.add(prjLeaseInspects);
        //databaseLockProvider.lock(hlsCusPrjProject);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "LEASE_INSPECT_WFL");
        activitiStartService.start(request, prjLeaseInspectList, params);

        //更新审批信息表
        //PrjLeaseInspect hlsCusChangeReqInfo = new PrjLeaseInspect();
        //hlsCusChangeReqInfo.setChangeReqId(prjProject.getChangeReqId());
        //hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(request, hlsCusChangeReqInfo);
        prjLeaseInspects.setApproveStatus("APPROVING");
        prjLeaseInspects.set__status(DTOStatus.UPDATE);
        prjLeaseInspectMapper.updateByPrimaryKeySelective(prjLeaseInspects);

        //sysEventService.eventSave(request, hlsCusPrjProject.getProjectId(), hlsCusPrjProject.getDocumentCategory(), hlsCusPrjProject.getDocumentType(), "BAC", "PRJ_CONTRACT_CHANGE", "P2D", paramsEvent);
    }
    @Override
    public String generateAuthorityString(IRequest iRequest) {
        Long companyId = iRequest.getCompanyId();
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        String unitCode = (String) session.getAttribute("unitCode");
        String positionCode = (String) session.getAttribute("positionCode");
        String empCode = iRequest.getEmployeeCode();
//        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' +  positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"'  + positionCode + '"' + "." + '"'+  empCode + '"';
        return authorityString;
    }

    @Override
    public PrjLeaseInspect prjLeaseInspectCreate(IRequest requestCtx, PrjLeaseInspect dto) throws HlsCusException {
        //保存资产处置数据
        PrjLeaseInspect prjLeaseInspect = new PrjLeaseInspect();
        prjLeaseInspect.setProjectId(dto.getProjectId());
        prjLeaseInspect.setLeaseInspectNumber(dto.getLeaseInspectNumber());
        prjLeaseInspect.setContractNumber(dto.getContractNumber());
        prjLeaseInspect.setUserId(dto.getUserId());
        prjLeaseInspect.setLeaseInspectDate(dto.getLeaseInspectDate());
        prjLeaseInspect.setApproveStatus("NEW");
        //
        // prj_lease_inspect 此合同在此日期下重复创建校验
        List<PrjLeaseInfo> prjLeaseInfoQuery = prjLeaseInfoMapper.selectLeaseInfoCount(prjLeaseInspect);
        if(prjLeaseInfoQuery.size()>0){
            throw new HlsCusException("此合同在90天内重复创建,请确认！");
        }
        prjLeaseInspect= this.insertSelective(requestCtx, prjLeaseInspect);

        List<PrjLeaseInfo> prjLeaseInfos = prjLeaseInfoMapper.selectLeaseItemByProjectId(prjLeaseInspect.getProjectId());
        for(PrjLeaseInfo prjProjectLeaseItem:prjLeaseInfos){
            PrjLeaseInfo prjLeaseInfo = new PrjLeaseInfo();
            prjLeaseInfo.setPrjLeaseId(prjLeaseInspect.getPrjLeaseId());
            prjLeaseInfo.setAssetName(prjProjectLeaseItem.getAssetName());
            prjLeaseInfo.setAssetNum(prjProjectLeaseItem.getAssetNum());
            prjLeaseInfo.setSpecification(prjProjectLeaseItem.getSpecification());
            prjLeaseInfo.setInstallationSite(prjProjectLeaseItem.getInstallationSite());
            prjLeaseInfo.setManufacturer(prjProjectLeaseItem.getVenderIdN());
            prjLeaseInfo.setCheckStatus("nocheck");
            prjLeaseInfo.setLeaseItemCode(prjProjectLeaseItem.getLeaseItemCode());
            prjLeaseInfo.setLeaseItemId(prjProjectLeaseItem.getLeaseItemId());
            prjLeaseInfoService.insertSelective(requestCtx,prjLeaseInfo);
        }


        return prjLeaseInspect;
    }
}