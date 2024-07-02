package com.hand.hls.pam.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTemplateHd;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.pam.mapper.AssetsDisposalDetailMapper;
import com.hand.hls.pam.mapper.AssetsDisposalMapper;
import com.hand.hls.pam.service.IAssetsDisposalDetailService;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.service.IAssetsDisposalService;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class AssetsDisposalServiceImpl extends BaseServiceImpl<AssetsDisposal> implements IAssetsDisposalService{
    @Autowired
    private AssetsDisposalMapper assetsDisposalMapper;
    @Autowired
    private AssetsDisposalDetailMapper assetsDisposalDetailMapper;
    @Autowired
    private IAssetsDisposalService assetsDisposalService;
    @Autowired
    private IAssetsDisposalDetailService assetsDisposalDetailService;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    @Override
    public AssetsDisposal assetsDisposalCreate(IRequest requestCtx, AssetsDisposal dto) throws HlsCusException {

        //保存资产处置数据
        AssetsDisposal assetsDisposal = new AssetsDisposal();
        assetsDisposal.setProjectId(dto.getProjectId());
        assetsDisposal.setAssetsApplNumber(dto.getAssetsApplNumber());
        assetsDisposal.setContractNumber(dto.getContractNumber());
        assetsDisposal.setEmployeeId(dto.getEmployeeId());
        assetsDisposal.setFromDate(dto.getFromDate());
        assetsDisposal.setApprovalStatus("NEW");
       // assetsDisposal.setContractStatus(dto.getContractStatus());
        assetsDisposal= this.insertSelective(requestCtx, assetsDisposal);
        //创建资产处置明细
        //测试

        List<AssetsDisposalDetail> assetsDisposalDetailList = assetsDisposalDetailMapper.queryLeaseItemByProjectId(dto.getProjectId());
        for(AssetsDisposalDetail disposalDetail:assetsDisposalDetailList){
            AssetsDisposalDetail assetsDisposalDetail = new AssetsDisposalDetail();
            assetsDisposalDetail.setAssetsDisposalId(assetsDisposal.getAssetsDisposalId());
            assetsDisposalDetail.setFullName(disposalDetail.getFullName());
            assetsDisposalDetail.setPrice(disposalDetail.getPrice());
            assetsDisposalDetail.setQuantity(disposalDetail.getQuantity());
            assetsDisposalDetail.setSpecification(disposalDetail.getSpecification());
            assetsDisposalDetail.setInstallationSite(disposalDetail.getInstallationSite());
            assetsDisposalDetail.setManufacturer(disposalDetail.getManufacturer());
            assetsDisposalDetail.setAssetNum(disposalDetail.getAssetNum());
            assetsDisposalDetail.setInvoiceNum(disposalDetail.getInvoiceNum());
            assetsDisposalDetail.setAssetsDisposalStauts("UNDISPOSED");
            assetsDisposalDetail.setLeaseItemId(disposalDetail.getLeaseItemId());
            assetsDisposalDetail.setHlsLeaseItemListId(disposalDetail.getHlsLeaseItemListId());
            assetsDisposalDetailService.insertSelective(requestCtx,assetsDisposalDetail);
        }


        return assetsDisposal;
    }

    private void approveWfl(IRequest iRequest, AssetsDisposal assetsDisposal) throws ResMessageException {
        assetsDisposal = assetsDisposalMapper.selectByPrimaryKey(assetsDisposal);
        if (APPROVED.equals(assetsDisposal.getApprovalStatus()) || APPROVING.equals(assetsDisposal.getApprovalStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(assetsDisposal);

        List<AssetsDisposal> assetsDisposalArrayList = new ArrayList<>();
        assetsDisposalArrayList.add(assetsDisposal);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "ASSETS_DISPOSAL_WFL");
        params.put("assetsDisposalId", assetsDisposal.getAssetsDisposalId());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "ASSETS_DISPOSAL_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "ASSETS_DISPOSAL_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, assetsDisposal.getAssetsDisposalId());
        params.put("documentCategory", "ASSETS_DISPOSAL_WFL"); //
        params.put("documentName","资产处置审批流程"+assetsDisposal.getAssetsApplNumber());
        params.put("documentNumber", assetsDisposal.getAssetsApplNumber());
        params.put("assetsDisposal", JSON.toJSONString(assetsDisposal));
        params.put("startUserName", iRequest.getUserName());



        activitiStartService.start(iRequest, assetsDisposalArrayList, params);

        AssetsDisposal assetsDisposal1 = new AssetsDisposal();
        assetsDisposal1.setAssetsDisposalId(assetsDisposal.getAssetsDisposalId());
        assetsDisposal1.setApprovalStatus(APPROVING);
        assetsDisposalService.updateByPrimaryKeySelective(iRequest, assetsDisposal1);

    }

    @Override
    public List<AssetsDisposal> conInceptSubmit(IRequest iRequest, AssetsDisposal assetsDisposal) throws ResMessageException, ParameterNullException {
        //启动工作流
        approveWfl(iRequest, assetsDisposal);

        List<AssetsDisposal> contractInsure = new ArrayList<>();
        contractInsure.add(assetsDisposal);
        return contractInsure;
    }
}