package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectBpServiceImpl extends BaseServiceImpl<HlsCusPrjProjectBp> implements HlsCusPrjProjectBpService {
    //创建流水号
    private static final String WARRANTOR = "WARRANTOR";//保证信息类型
    private static final String PLEDGOR = "PLEDGOR";//质押信息类型
    private static final String MORTGAGOR = "MORTGAGOR";//抵押信息类型
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Override
    public List<HlsCusPrjProjectBp> prjProjectBpBeforeInfoQuery(IRequest request, HlsCusPrjProjectBp hlsCusPrjProjectBp, int page, int pagesize) {
        // change
        HlsCusPrjProject changeProject = new HlsCusPrjProject();
        changeProject.setProjectId(hlsCusPrjProjectBp.getProjectId());
        changeProject = hlsCusPrjProjectService.selectByPrimaryKey(request, changeProject);
        // normal
        HlsCusPrjProject normalProject = new HlsCusPrjProject();
        normalProject.setProjectId(changeProject.getRefProjectId());
        normalProject = hlsCusPrjProjectService.selectByPrimaryKey(request, normalProject);
        hlsCusPrjProjectBp.setProjectId(normalProject.getProjectId());
        // history
        HlsCusPrjProject historyProject = new HlsCusPrjProject();
        historyProject.setRefProjectId(normalProject.getProjectId());
        historyProject.setDataType("HISTORY");
        historyProject.setChangeReqId(changeProject.getChangeReqId());
        List<HlsCusPrjProject> historyProjectList = hlsCusPrjProjectService.select(request, historyProject, 1, 99999);
        if(CollectionUtils.isEmpty(historyProjectList)){
            hlsCusPrjProjectBp.setProjectId(normalProject.getProjectId());
        }else{
            hlsCusPrjProjectBp.setProjectId(historyProjectList.get(0).getProjectId());
        }
        PageHelper.startPage(page, pagesize);
        return hlsCusPrjProjectBpMapper.prjProjectBpInfoQuery(hlsCusPrjProjectBp);
    }

    @Override
    public List<HlsCusPrjProjectBp> prjProjectBpInfoQuery(IRequest iRequest, HlsCusPrjProjectBp hlsCusPrjProjectBp, int page, int pagesize){
        PageHelper.startPage(page, pagesize);
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList=new ArrayList<>();
        hlsCusPrjProjectBpList=hlsCusPrjProjectBpMapper.prjProjectBpInfoQuery(hlsCusPrjProjectBp);
        return hlsCusPrjProjectBpList;
    }
    @Override
    public List<HlsCusPrjProjectBp> selectBpByProjectIdOrderByBpCategory(HlsCusPrjProjectBp hlsCusPrjProjectBp){
        List<HlsCusPrjProjectBp> hlsCusPrjProjectBpList=new ArrayList<>();
        hlsCusPrjProjectBpList=hlsCusPrjProjectBpMapper.selecttBpByProjectIdOrderByBpCategory(hlsCusPrjProjectBp);
        return hlsCusPrjProjectBpList;
    }
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper chanceBpMapper;

    @Override
    public List<HlsCusPrjProjectBp> saveBpByChanceId(IRequest requestCt,HlsCusPrjProjectBp hlsCusPrjProjectBp) throws HlsCusException {

        HlsCusHlsCreditLineChanceBp lineChanceBp = new HlsCusHlsCreditLineChanceBp();
        lineChanceBp.setChanceId(hlsCusPrjProjectBp.getChanceId());
        List<HlsCusHlsCreditLineChanceBp> chanceBps = chanceBpMapper.selectChanceBpByChanceId(lineChanceBp);
        if (CollectionUtils.isNotEmpty(chanceBps)) {
            for (HlsCusHlsCreditLineChanceBp chanceBp : chanceBps) {
                try{
                    copyPublicFields(requestCt,hlsCusPrjProjectBp, chanceBp);

                }catch (Exception e){
                    logger.error("Failed to copy and save project", e);
                    throw new HlsCusException(e.getMessage());
                }
            }
        }

        return null;
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private void copyPublicFields(IRequest iRequest,HlsCusPrjProjectBp hlsCusPrjProjectBp, HlsCusHlsCreditLineChanceBp chanceBp) throws HlsCusException {
        try {
            if(chanceBp !=null){
                // 复制公共属性
                BeanUtils.copyProperties(chanceBp,hlsCusPrjProjectBp);
                hlsCusPrjProjectBp.setDescription(chanceBp.getNote());

                // 保存新创建的对象到数据库
                self().insertSelective(iRequest,hlsCusPrjProjectBp);
            }

        } catch (Exception e) {
           logger.error("Failed to copy and save project", e);
           throw new HlsCusException(e.getMessage());
        }

    }

    @Override
    public List<HlsCusPrjProjectBp> prjProjectBpQuery(HlsCusPrjProjectBp hlsCusPrjProjectBp, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsCusPrjProjectBpMapper.prjProjectBpQuery(hlsCusPrjProjectBp);
    }

    /**
     * 创建保证、抵押、质押信息流水号
     *
     * @param projectId
     * @param dtos
     * @param roleType  当值为null时会根据传入的bpRoleType创建流水号，当不为空时会将bpRoleType赋值为ROLETYPE去创建流水号
     * @return
     */
    @Override
    public List<HlsCusPrjProjectBp> createSerialNumber(IRequest iRequest, Long projectId, List<HlsCusPrjProjectBp> dtos, String roleType) {
        if (dtos.size() > 0) {
            HlsCusPrjProject prjProject = new HlsCusPrjProject();

            prjProject.setProjectId(projectId);
            prjProject.setDataClass("VIRTUAL_CON");
            prjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, prjProject);
            prjProject.setGuarantorSum(0L);
            prjProject.setMortgagorSum(0L);
            prjProject.setPledgorSum(0L);
            //  databaseLockProvider.lock(fctProject);
            for (HlsCusPrjProjectBp dt : dtos) {
                if (StringUtils.isNotBlank(roleType)) {
                    dt.setBpRoleType(roleType);
                }
                //     if (StringUtils.isBlank(dt.getSerialNumber())) {
                    StringBuffer sbf = new StringBuffer();
                    if (WARRANTOR.equalsIgnoreCase(dt.getBpRoleType())) {
                        if (prjProject.getGuarantorSum() == null || prjProject.getGuarantorSum() == 0) {
                            prjProject.setGuarantorSum(1L);
                        } else {
                            prjProject.setGuarantorSum(prjProject.getGuarantorSum() + 1L);
                        }
                        sbf.append("BZ-").append(prjProject.getApprovalNumber()).append("-").append(StringUtils.leftPad(String.valueOf(prjProject.getGuarantorSum()), 4, "0"));
                    } else if (PLEDGOR.equalsIgnoreCase(dt.getBpRoleType())) {
                        if (prjProject.getPledgorSum() == null || prjProject.getPledgorSum() == 0) {
                            prjProject.setPledgorSum(1L);
                        } else {
                            prjProject.setPledgorSum(prjProject.getPledgorSum() + 1L);
                        }
                        sbf.append("ZY-").append(prjProject.getApprovalNumber()).append("-").append(StringUtils.leftPad(String.valueOf(prjProject.getPledgorSum()), 4, "0"));
                    } else if (MORTGAGOR.equalsIgnoreCase(dt.getBpRoleType())) {
                        if (prjProject.getMortgagorSum() == null || prjProject.getMortgagorSum() == 0) {
                            prjProject.setMortgagorSum(1L);
                        } else {
                            prjProject.setMortgagorSum(prjProject.getMortgagorSum() + 1L);
                        }
                        sbf.append("DY-").append(prjProject.getApprovalNumber()).append("-").append(StringUtils.leftPad(String.valueOf(prjProject.getMortgagorSum()), 4, "0"));
                    }
                    dt.setSerialNumber(sbf.toString());

            }
            hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
        }
        return dtos;

    }

    @Override
    public List<HlsCusPrjProjectBp> conProjectBpUpdate(IRequest requestCtx, List<HlsCusPrjProjectBp> dto) {
        if (dto != null) {
            dto = createSerialNumber(requestCtx, dto.get(0).getProjectId(), dto, null);
            for (HlsCusPrjProjectBp dt : dto) {
                if (dt.getPrjBpId() == null || dt.getPrjBpId() == 0) {
                    dt.set__status("add");
                } else {
                    if ("delete".equalsIgnoreCase(dt.get__status())) {
                        dt.set__status("delete");
                    } else {
                        dt.set__status("update");
                    }
                }
            }
        }
        return this.batchUpdate(requestCtx, dto);
    }

    @Override
    public int deleteByPrjBpIds(List<Long> prjBpIds,Long projectId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("prjBpIds",prjBpIds);
        map.put("projectId",projectId);
        //return hlsCusPrjProjectBpMapper.deleteByPrjBpIds(prjBpIds,projectId);
        return hlsCusPrjProjectBpMapper.deleteByPrjBpIds(map);
    }

    @Override
    public HlsCusPrjProjectBp selectByBpId(HlsCusPrjProjectBp hlsCusPrjProjectBp) {
        HlsCusPrjProjectBp hlsCusPrjProjectBp2 = hlsCusPrjProjectBpMapper.selectByBpId(hlsCusPrjProjectBp);
        return hlsCusPrjProjectBp2;
    }

    @Override
    public List<Map> selectPledgeAndMortgagor(IRequest iRequest, HlsCusPrjProjectBp bp, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        Map map = new HashMap();
        map.put("projectId",bp.getProjectId());
        return hlsCusPrjProjectBpMapper.selectPledgeAndMortgagor(map);
    }

}