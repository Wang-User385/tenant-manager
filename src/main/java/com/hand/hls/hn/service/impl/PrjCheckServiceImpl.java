package com.hand.hls.hn.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hn.dto.*;
import com.hand.hls.hn.mapper.*;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.sys.dto.SysDocumentList;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjCheckServiceImpl extends BaseServiceImpl<PrjCheck> implements IPrjCheckService {
    @Autowired
    private IPrjCheckService prjCheckService;

    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusEmployeeMapper employeeMapper;
    @Autowired
    FndCompanyMapper fndCompanyMapper;
    @Autowired
    private PrjCheckMapper prjCheckMapper;
    @Autowired
    private PrjCheckAssetClassMapper prjCheckAssetClassMapper;
    @Autowired
    private PrjCheckGuarantMortgageMapper prjCheckGuarantMortgageMapper;
    @Autowired
    private PrjCheckFinanceSituationMapper prjCheckFinanceSituationMapper;
    @Autowired
    private PrjCheckSubjectChangeMapper prjCheckSubjectChangeMapper;

    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private PrjCheckProjectSituationMapper prjCheckProjectSituationMapper;

    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsCheckItemsMapper hlsCheckItemsMapper;
    @Autowired
    private PrjCheckItemLnMapper prjCheckItemLnMapper;
    @Autowired
    private PrjCheckItemHdMapper prjCheckItemHdMapper;

    /**
     * 提交租后检查
     */
    @Override
    public void submitPrjContractChange(IRequest request, PrjCheck prjCheck)  throws HlsCusException {
        List<PrjCheck> prjLeaseInspectList = new ArrayList<>();
        PrjCheck prjLeaseInspects = prjCheckService.selectByPrimaryKey(request, prjCheck);
        if("APPROVING".equals(prjLeaseInspects.getApproveSuggest())){
            throw new HlsCusException("审批中单据不能再次提交！");
        }
        prjLeaseInspectList.add(prjLeaseInspects);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "RENT_CHECK_WFL");
        params.put("unitId",request.getAttribute("unitId"));
        params.put("companyId",request.getAttribute("companyId"));
        activitiStartService.start(request, prjLeaseInspectList, params);

        prjLeaseInspects.setApproveSuggest("APPROVING");
        prjLeaseInspects.set__status(DTOStatus.UPDATE);
        prjCheckMapper.updateByPrimaryKeySelective(prjLeaseInspects);
    }


    @Override
    public void submitPrjCheckLs(IRequest request, PrjCheck prjCheck) throws HlsCusException {
        List<PrjCheck> prjLeaseInspectList = new ArrayList<>();
        PrjCheck prjLeaseInspects = prjCheckService.selectByPrimaryKey(request, prjCheck);
        if("APPROVING".equals(prjLeaseInspects.getApproveSuggest())){
            throw new HlsCusException("审批中单据不能再次提交！");
        }
        prjLeaseInspectList.add(prjLeaseInspects);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "RENT_CHECK_LS_WFL");
        params.put("unitId",request.getAttribute("unitId"));
        params.put("companyId",request.getAttribute("companyId"));
        activitiStartService.start(request, prjLeaseInspectList, params);

        prjLeaseInspects.setApproveSuggest("APPROVING");
        prjLeaseInspects.set__status(DTOStatus.UPDATE);
        prjCheckMapper.updateByPrimaryKeySelective(prjLeaseInspects);
    }

    @Override
    public List<PrjCheck> queryContract(PrjCheck prjCheck) {
        return prjCheckMapper.queryList(prjCheck);
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
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        return authorityString;
    }


    @Override
    public PrjCheck prjCheckCreate(IRequest requestCtx, PrjCheck dto) throws HlsCusException {

        //保存租后检查创建数据
        PrjCheck prjCheck = new PrjCheck();
        String checkNumber=codingRuleValuesService.getCodeRuleValue(requestCtx, "RENT_CHECK","RENT_CHECK", "RENT_CHECK", null);
        prjCheck.setCheckNumber(checkNumber);
        prjCheck.setContractId(dto.getContractId());
        prjCheck.setUnitId(dto.getUnitId());
        prjCheck.setContractNumber(dto.getContractNumber());
        prjCheck.setEmployeeId(dto.getEmployeeId());
        prjCheck.setCheckDate(dto.getCheckDate());
        prjCheck.setApproveSuggest("NEW");
        prjCheck.setBpId(dto.getBpId());
        prjCheck.setPlanId(dto.getPlanId());
        prjCheck.setWriteCompany(248L);
        prjCheck.setManualFlag(1L);
        prjCheck.setCreatedBy(requestCtx.getUserId());
        prjCheck = this.insertSelective(requestCtx, prjCheck);

        //事项新增 风险预警、客户基本信息、客户主体信用资质状况、客户财报、标的、抵质押品
        List<HlsCheckItems> hlsCheckItemsList = new ArrayList<>();
        hlsCheckItemsList = hlsCheckItemsMapper.selectAll();
        List<String> categorys = (List) hlsCheckItemsList.stream().map(HlsCheckItems::getCheckCategory).distinct().collect(Collectors.toList());

        for (String category : categorys) {
            PrjCheckItemHd prjCheckItemHdInsert = new PrjCheckItemHd();
            prjCheckItemHdInsert.setCheckId(prjCheck.getCheckId());
            prjCheckItemHdInsert.setCheckCategory(category);
            prjCheckItemHdInsert.setCheckDate(dto.getCheckDate());

            List<PrjCheckItemHd> prjCheckItemHdList = prjCheckItemHdMapper.select(prjCheckItemHdInsert);
            if (prjCheckItemHdList.size() <= 0) {
                prjCheckItemHdMapper.insertSelective(prjCheckItemHdInsert);
            }
            PrjCheckItemHd hd= prjCheckItemHdMapper.select(prjCheckItemHdInsert).get(0);

            List<HlsCheckItems> hlsCheckItemsLnList=hlsCheckItemsList.stream().filter(item -> item.getCheckCategory().equals(category)).collect(Collectors.toList());
            for(HlsCheckItems item:hlsCheckItemsLnList){
                PrjCheckItemLn prjCheckItemLnInsert = new PrjCheckItemLn();
                prjCheckItemLnInsert.setHdId(hd.getHdId());
                prjCheckItemLnInsert.setItems(item.getItems());
                prjCheckItemLnInsert.setCheckType(item.getCheckType());
                prjCheckItemLnInsert.setCheckId(prjCheck.getCheckId());
                prjCheckItemLnInsert.setCheckY("Y");
                prjCheckItemLnInsert.setCheckN("N");
                prjCheckItemLnInsert.setRequiredFlag(item.getRequiredFlag());
                List<PrjCheckItemLn> prjCheckItemLnList = prjCheckItemLnMapper.select(prjCheckItemLnInsert);
                if (prjCheckItemLnList.size() <= 0) {
                    prjCheckItemLnMapper.insertSelective(prjCheckItemLnInsert);
                }
            }
        }

        return prjCheck;
    }

}