package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.prj.mapper.HlsCusPrjProjectInsureMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectInsureService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hand.hls.wfl.service.IActivitiCommonService.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectInsureServiceImpl extends BaseServiceImpl<HlsCusPrjProjectInsure> implements HlsCusPrjProjectInsureService {

    private static final String APPROVING = "APPROVING";

    private static final String APPROVED = "APPROVED";

    private static final String WORK_FLOW_TYPE = "CON_INSURANCE_WFL";

    @Autowired
    private HlsCusPrjProjectInsureMapper hlsCusPrjProjectInsureMapper;
    @Autowired
    private HlsCusPrjProjectInsureService hlsCusPrjProjectInsureService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Override
    public List<HlsCusPrjProjectInsure> queryContractInsureInfo(IRequest requestContext, HlsCusPrjProjectInsure hlsCusPrjProjectInsure, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsCusPrjProjectInsureMapper.queryContractInsureInfo(hlsCusPrjProjectInsure);
    }

    @Override
    public void insureApproveWfl(IRequest requestContext, HlsCusPrjProjectInsure dto) throws ResMessageException {
        databaseLockProvider.lock(dto);


        HlsCusPrjProjectInsure prjProjectInsure = new HlsCusPrjProjectInsure();
        prjProjectInsure.setInsureId(dto.getInsureId());
        HlsCusPrjProjectInsure insure = hlsCusPrjProjectInsureMapper.selectByPrimaryKey(prjProjectInsure);
        if (APPROVING.equals(insure.getApprovalStatus()) || APPROVED.equals(insure.getApprovalStatus())) {
            throw new ResMessageException("已经提交了申请,无需重复提交!");
        }

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(dto.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestContext, hlsCusPrjProject);

        //提交工作流
        List<HlsCusPrjProjectInsure> list = new ArrayList<>();
        list.add(dto);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("workFlowType", WORK_FLOW_TYPE);
        params.put("hlsCusPrjProjectInsure", dto);
        params.put("documentCategory", WORK_FLOW_TYPE);
        params.put("documentType", WORK_FLOW_TYPE);
        params.put("projectId", dto.getProjectId());
        params.put(WORK_FLOW_NAME, WORK_FLOW_TYPE);
        params.put(BUSINESS_KEY, dto.getInsureId());
        params.put(DEMO_NAME, WORK_FLOW_TYPE);
        params.put("documentName", hlsCusPrjProject.getContractName());
        params.put("documentNumber", hlsCusPrjProject.getContractNumber());
        activitiStartService.start(requestContext, list, params);
        //更改状态
        dto.setApprovalStatus("APPROVING");
        hlsCusPrjProjectInsureService.updateByPrimaryKeySelective(requestContext, dto);
    }

}