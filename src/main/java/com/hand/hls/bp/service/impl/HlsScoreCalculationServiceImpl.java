package com.hand.hls.bp.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.FndScoreTemplateHd;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.bp.service.IFndScoreTemplateHdService;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.HlsFinStatementLn;
import com.hand.hls.fnd.mapper.HlsFinStatementLnMapper;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsScoreCalculationServiceImpl extends BaseServiceImpl<HlsScoreCalculation> implements IHlsScoreCalculationService{
    @Autowired
    HlsScoreCalculationMapper hlsScoreCalculationMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private IHlsScoreCalculationService hlsScoreCalculationService;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;
    @Autowired
    private IFndScoreTemplateHdService fndScoreTemplateHdService;
    @Autowired
    FndCompanyMapper fndCompanyMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsFinStatementLnMapper hlsFinStatementLnMapper;
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";

    public HlsScoreCalculationServiceImpl() {
    }

    public List<HlsScoreCalculation> selectListOfHsc(HlsScoreCalculation hlsScoreCalculation) {
        return this.hlsScoreCalculationMapper.selectListOfHsc1(hlsScoreCalculation);
    }

    private void approveWfl(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation) throws ResMessageException {
        hlsScoreCalculation = hlsScoreCalculationMapper.selectByPrimaryKey(hlsScoreCalculation);
        if (APPROVED.equals(hlsScoreCalculation.getGradeStatus()) || APPROVING.equals(hlsScoreCalculation.getGradeStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(hlsScoreCalculation);

        List<HlsScoreCalculation> hlsScoreCalculationList = new ArrayList<>();
        hlsScoreCalculationList.add(hlsScoreCalculation);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "CUSTOMER_RATE_WFL");
        params.put("scoreId", hlsScoreCalculation.getScoreId());
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(hlsScoreCalculation.getBpId());
        HlsCusBpMaster hlsCusBpMaster1 = hlsCusBpMasterService.selectByPrimaryKey(iRequest,hlsCusBpMaster);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //商业伙伴名称+评级时间+信用评级
        params.put("documentName", hlsCusBpMaster1.getBpName()+formatter.format(hlsScoreCalculation.getScoreDate())+"信用评级");
        //params.put("documentNumber", hlsScoreCalculation.getScoreId());

        params.put(IActivitiCommonService.WORK_FLOW_NAME, "CUSTOMER_RATE_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "CUSTOMER_RATE_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, hlsScoreCalculation.getScoreId());
        params.put("documentCategory", "CUSTOMER_RATE_WFL"); //
        params.put("hlsScoreCalculation", JSON.toJSONString(hlsScoreCalculation));
        params.put("startUserName", iRequest.getUserName());

        params.put("financialFlag", hlsScoreCalculation.getFinancialFlag());

        //通过bpId拿到bpCode/bpName
        params.put("bpId", hlsScoreCalculation.getBpId());
        params.put("bpCode", hlsCusBpMaster1.getBpCode());
        params.put("bpName", hlsCusBpMaster1.getBpName());
        params.put("objectId", hlsScoreCalculation.getBpId());
        //评级模板名称
        FndScoreTemplateHd fndScoreTemplateHd = new FndScoreTemplateHd();
        fndScoreTemplateHd.setScoreTemplateHdId(hlsScoreCalculation.getScoreTemplateHdId());
        FndScoreTemplateHd fndScoreTemplateHd1 = fndScoreTemplateHdService.selectByPrimaryKey(iRequest,fndScoreTemplateHd);
        params.put("scoreTemplateHdName", fndScoreTemplateHd1.getScoreTemplateHdName());
        params.put("scoreTemplateHdId", hlsScoreCalculation.getScoreTemplateHdId());


        params.put("scoreResultId", hlsScoreCalculation.getScoreResultId());
        params.put("scoreId", hlsScoreCalculation.getScoreId());
        params.put("fiscalYear", hlsScoreCalculation.getFiscalYear());

        activitiStartService.start(iRequest, hlsScoreCalculationList, params);

        HlsScoreCalculation contractInsure = new HlsScoreCalculation();
        contractInsure.setScoreId(hlsScoreCalculation.getScoreId());
        contractInsure.setGradeStatus(APPROVING);
        hlsScoreCalculationService.updateByPrimaryKeySelective(iRequest, contractInsure);

    }

    private void checkScore(HlsScoreCalculation hlsScoreCalculation) throws ResMessageException{
        String isTurnUp = hlsScoreCalculation.getIsTurnUp();
        String riskWarningId = hlsScoreCalculation.getRiskWarningId();
        List<HlsScoreCalculation> scoreCalculations = hlsScoreCalculationMapper.selectScoreCount(hlsScoreCalculation);
        if(scoreCalculations.size() >= 1 && ("N".equalsIgnoreCase(isTurnUp)|| isTurnUp == null) && riskWarningId == null ){
            throw new ResMessageException("非预警评级及上翻评级，同一年度不能二次提交！");
        }
    }

    @Override
    public List<HlsScoreCalculation> conInceptSubmit(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation) throws ResMessageException, ParameterNullException {
        //评级提交校验
        checkScore(hlsScoreCalculation);
        //启动工作流
        approveWfl(iRequest, hlsScoreCalculation);

        List<HlsScoreCalculation> contractInsure = new ArrayList<>();
        contractInsure.add(hlsScoreCalculation);
        return contractInsure;
    }

    @Override
    public String generateAuthorityString(IRequest iRequest) {
        Long companyId = iRequest.getCompanyId();
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        String unitCode = (String) session.getAttribute("unitCode");
        String positionCode = (String) session.getAttribute("positionCode");
        String empCode = iRequest.getEmployeeCode();
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';

        return authorityString;
    }

    @Override
    public Map clacRiskMoney(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation) throws ResMessageException, ParameterNullException {
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(hlsScoreCalculation.getBpId());
        HlsCusBpMaster hlsCusBpMaster1 = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        HlsScoreCalculation scoreCalculation = new HlsScoreCalculation();
        scoreCalculation.setBpId(hlsScoreCalculation.getBpId());
        scoreCalculation.setScoreId(hlsScoreCalculation.getScoreId());
        scoreCalculation.setScoreTemplateHdId(hlsScoreCalculation.getScoreTemplateHdId());
        List<HlsScoreCalculation> hlsScoreCalculationList = hlsScoreCalculationMapper.selectScoreCalculation(scoreCalculation);
        HashMap map = new HashMap();
        Double riskAmount = 0.0;  // 风险限额
        String rate = "";
        //企业规模  ：微小型(MINIATURE)、小型(SMALL_SIZED)、中小型(SMEDIUM)
        if("MINIATURE".equals(hlsCusBpMaster1.getEnterpriseScale())||"SMALL_SIZED".equals(hlsCusBpMaster1.getEnterpriseScale())
                                        ||"SMEDIUM".equals(hlsCusBpMaster1.getEnterpriseScale())){
            List<HlsFinStatementLn> hlsFinStatementLnList = hlsFinStatementLnMapper.lnQueryAllAssetsToall(hlsScoreCalculation.getBpId());
            for(HlsFinStatementLn hlsFinStatementLn:hlsFinStatementLnList){
                if(hlsFinStatementLn.getAmount() != null){
                    riskAmount += hlsFinStatementLn.getAmount();
                }
            }
            //selectScoreCalculation

            Double newRiskAmount = riskAmount*0.5;
            for(HlsScoreCalculation hlsScoreCalculation1:hlsScoreCalculationList){
                //FINAL_SCORE_GRADE
                if(hlsScoreCalculation1.getScoreGrade()==null){
                    riskAmount = 0.0;
                    break;
                }
                if(hlsScoreCalculation1.getFinalScoreGrade()==null){
                    hlsScoreCalculation1.setFinalScoreGrade(hlsScoreCalculation1.getScoreGrade());
                }
                if("AAA".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.7;
                }else if("AA".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.6;
                }else if("A".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.5;
                }else if("BBB".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.4;
                }else if("BB".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.3;
                }else if("B".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.1;
                }else {
                    riskAmount = newRiskAmount*0.02;
                }


                //

            }


        //中型(MEDIUM)、大型(LARGE_SCALE)
        }else if("MEDIUM".equals(hlsCusBpMaster1.getEnterpriseScale())||"LARGE_SCALE".equals(hlsCusBpMaster1.getEnterpriseScale())){
            List<HlsFinStatementLn> hlsFinStatementLnList = hlsFinStatementLnMapper.lnQueryAllQuanYiToall(hlsScoreCalculation.getBpId());
            for(HlsFinStatementLn hlsFinStatementLn:hlsFinStatementLnList){
                if(hlsFinStatementLn.getAmount() != null){
                    riskAmount += hlsFinStatementLn.getAmount();
                }
            }
            Double newRiskAmount = riskAmount*0.5;
            for(HlsScoreCalculation hlsScoreCalculation1:hlsScoreCalculationList){
                //FINAL_SCORE_GRADE
                if(hlsScoreCalculation1.getFinalScoreGrade()==null){
                    hlsScoreCalculation1.setFinalScoreGrade(hlsScoreCalculation1.getScoreGrade());
                }
                if("AAA".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*2.0;
                }else if("AA".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*1.8;
                }else if("A".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*1.5;
                }else if("BBB".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*1.0;
                }else if("BB".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.5;
                }else if("B".equals(hlsScoreCalculation1.getFinalScoreGrade())){
                    riskAmount = newRiskAmount*0.3;
                }else {
                    riskAmount = newRiskAmount*0.1;
                }
            }
        }

        map.put("riskAmount",riskAmount);
        //SCORE_VALUE
        for(HlsScoreCalculation hlsScoreCalculation1:hlsScoreCalculationList) {
            if(hlsScoreCalculation1.getScoreValue()==null){
                break;
            }
            if (hlsScoreCalculation1.getScoreValue() >= 90) {
                rate = "0.1%-0.5%";
            } else if (hlsScoreCalculation1.getScoreValue() >=80&&hlsScoreCalculation1.getScoreValue()<90) {
                rate = "0.3%-1.0%";
            } else if (hlsScoreCalculation1.getScoreValue() >=70&&hlsScoreCalculation1.getScoreValue()<80) {
                rate = "0.5%-1.5%";
            } else if (hlsScoreCalculation1.getScoreValue() >=60&&hlsScoreCalculation1.getScoreValue()<70) {
                rate = "0.5%-2.0%";
            } else if (hlsScoreCalculation1.getScoreValue() >=50&&hlsScoreCalculation1.getScoreValue()<60) {
                rate = "1%-5%";
            } else if (hlsScoreCalculation1.getScoreValue() >=40&&hlsScoreCalculation1.getScoreValue()<50) {
                rate = "2%-5%";
            } else if (hlsScoreCalculation1.getScoreValue() >=30&&hlsScoreCalculation1.getScoreValue()<40) {
                rate = "3%-8.6%";
            } else if (hlsScoreCalculation1.getScoreValue() >=20&&hlsScoreCalculation1.getScoreValue()<30) {
                rate = "10%-20%";
            } else if (hlsScoreCalculation1.getScoreValue() >=10&&hlsScoreCalculation1.getScoreValue()<20) {
                rate = "34.97%-50%";
            } else {
                rate = "50%-100%";
            }
        }
        map.put("rate",rate);
        return map;
    }

    @Override
    public HlsScoreCalculation hlsScoreCalculation(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation, String reCalcFlag) {
        return null;
    }
}