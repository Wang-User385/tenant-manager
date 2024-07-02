package com.hand.hls.rw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;
import com.hand.hls.rw.mapper.HlsCusRiskWarningInfoMapper;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.rw.service.HlsCusIRiskWarningInfoService;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusRiskWarningInfoServiceImpl extends BaseServiceImpl<HlsCusRiskWarningInfo> implements HlsCusIRiskWarningInfoService {

    @Autowired
    private HlsCusRiskWarningInfoMapper mapper;

    @Autowired
    private HlsCusRiskWarningMapper hlsCusRiskWarningMapper;

    private static final String NORMAL = "NORMAL";
    @Autowired
    private HlsCusIRiskWarningService service;
    @Autowired
    private IExportService excelService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    HlsCusIRiskWarningInfoService infoService;

    @Override
    public void saveWarning(HlsCusRiskWarning  warning ,IRequest iRequest){
        List<HlsCusRiskWarning> warnings = new ArrayList<HlsCusRiskWarning>();
        if (warning.getBpId() != null) {
            //预警主表信息插入
            HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
            riskWarning.setBpId(warning.getBpId());
            Map<String, String> params1 = new HashMap<String, String>();
            String riskWarningNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "RISK_WARNING", "RISK_WARNING", "RISK_WARNING", params1);
            riskWarning.setRiskWarningNumber(riskWarningNumber);
            riskWarning.setApplyDate(new Date());
            riskWarning.setStatus("NEW");
            riskWarning.setRiskType(warning.getRiskType());
            riskWarning.setBusinessType("RISK_WARNING");
            riskWarning.setDocumentCategory("RISK_WARNING");
            riskWarning.setDocumentType("RISK_WARNING");
            riskWarning.setCompanyId(iRequest.getCompanyId());
            service.insertSelective(iRequest,riskWarning);
            //预警明细
            HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
            riskWarningInfo.setBpId(warning.getBpId());
            List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
            if(riskWarningInfos.size() > 0){
                for(HlsCusRiskWarningInfo info : riskWarningInfos){
                    HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                    warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                    warningInfo.setProjectId(info.getProjectId());
                    warningInfo.setBpId(warning.getBpId());
                    warningInfo.setOverdueAmount(info.getOverdueAmount());
                    warningInfo.setOverdueTimes(info.getOverdueTimes());
                    warningInfo.setOverdueDays(info.getOverdueDays());
                    warningInfo.setFinanceAmount(info.getFinanceAmount());
                    warningInfo.setReceivedTimes(info.getReceivedTimes());
                    warningInfo.setFiveClassResult(info.getFiveClassResult());
                    warningInfo.setTotalTimes(info.getTotalTimes());
                    warningInfo.setBpId(warning.getBpId());
                    infoService.insertSelective(iRequest,warningInfo);
                }
            }
            if(warning.getRiskType().equals("AUTOMATIC_WARNING")){
                //天眼查 接口  接口返回信息存入表  HlsCusRiskWarningInfo
                String str = "str";
            }
            //添加返回参数
            warnings.add(riskWarning);

        }
    }
    @Override
    public List<HlsCusRiskWarningInfo>  queryAll(IRequest iRequest, HlsCusRiskWarningInfo riskWarningInfo) {
        return mapper.queryAll(riskWarningInfo);
    }
    @Override
    public List<HlsCusRiskWarningInfo>  queryPrjCon(IRequest iRequest, HlsCusRiskWarningInfo riskWarningInfo) {
        return mapper.queryPrjCon(riskWarningInfo);
    }

    /**
     * @Description:保存风险预警info
     * @Author: Wty
     * @Date: Created om 11:00 2018/6/4
     */
    @Override
    public HlsCusRiskWarningInfo submit(IRequest iRequest, HlsCusRiskWarningInfo riskWarningInfo) {
        if(riskWarningInfo!=null){
            String riskInfoSource = "";
            if(StringUtils.isNotBlank(riskWarningInfo.getBreachContractRiskInfo())){
                riskInfoSource += "违约风险信息,";
            }
            if(StringUtils.isNotBlank(riskWarningInfo.getFinancialRiskInfo())){
                riskInfoSource += "财务风险信息,";
            }
            if(StringUtils.isNotBlank(riskWarningInfo.getGuaranteeRiskInfo())){
                riskInfoSource += "担保风险信息,";
            }
            if(StringUtils.isNotBlank(riskWarningInfo.getManagementRiskInfo())){
                riskInfoSource += "经营管理风险信息,";
            }
            if(StringUtils.isNotBlank(riskWarningInfo.getAssociatedRiskInfo())){
                riskInfoSource += "关联风险信息,";
            }
            if(StringUtils.isNotBlank(riskWarningInfo.getLitigationRiskInfo())){
                riskInfoSource += "涉诉风险信息,";
            }
            if(StringUtils.isNotBlank(riskWarningInfo.getOtherRiskInfo())){
                riskInfoSource += "其他预警信息,";
            }

            if(StringUtils.isNotBlank(riskInfoSource)){
                HlsCusRiskWarning riskWarning = new  HlsCusRiskWarning();
                riskWarning.setRiskWarningId(riskWarningInfo.getRiskWarningId());
                riskWarning = hlsCusRiskWarningMapper.selectByPrimaryKey(riskWarning);
                if(riskWarning!=null){
                    riskInfoSource = riskInfoSource.substring(0, riskInfoSource.length()-1);
                    riskWarning.setRiskInfoSource(riskInfoSource);
                    hlsCusRiskWarningMapper.updateByPrimaryKeySelective(riskWarning);
                }

            }
        }
        riskWarningInfo.setChangeIq(NORMAL);
        if (riskWarningInfo.getRiskWarningInfoId() != null && riskWarningInfo.getRiskWarningInfoId() != 0) {
            return self().updateByPrimaryKeySelective(iRequest, riskWarningInfo);
        } else {
            return self().insertSelective(iRequest, riskWarningInfo);
        }
    }

    /**
     * @Description:查询风险预警信息
     * @Author: Wty
     * @Date: Created om 9:55 2018/6/5
     */
    @Override
    public List<HlsCusRiskWarningInfo> selectRiskWarningInfo(IRequest iRequest, HlsCusRiskWarningInfo riskWarningInfo) {
        return mapper.selectRiskWarningInfo(riskWarningInfo);
    }
}
