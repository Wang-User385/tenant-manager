package com.hand.hls.bp.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.*;
import com.hand.hls.bp.mapper.*;
import com.hand.hls.bp.service.*;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.prj.mapper.HlsBpMasterRelationMapper;
import com.hand.hls.prj.mapper.HlsBpMasterRoleMapper;
import com.alibaba.fastjson.JSON;
import com.hand.hls.sys.service.IFndCompanyService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.apache.commons.lang3.StringUtils;
import com.hand.hls.ty.dto.JcTianyanchaInterfaceInfo;
import com.hand.hls.ty.mapper.JcTianyanchaInterfaceInfoMapper;
import leaf.utils.ConfigUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterServiceImpl extends BaseServiceImpl<HlsCusBpMaster> implements HlsCusBpMasterService {

    @Autowired
    private HlsCusBpMasterMapper mapper;
    @Autowired
    private BpHeadOfficeListMapper BpHeadOfficeListMapper;
    @Autowired
    private HlsCusBpMasterService service;
    @Autowired
    private GovernmentFinancPlatformMapper governmentFinancPlatformMapper;

    @Autowired
    private HlsBpShareholderInfoService hlsBpShareholderInfoService;

    @Autowired
    private IBpShareholderInfoHtService bpShareholderInfoHtService;

    @Autowired
    private HlsBpOutInvestmentService hlsBpOutInvestmentService;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private HlsBpLitigationService hlsBpLitigationService;
    @Autowired
    private HlsBpLitigationHtService hlsBpLitigationHtService;
    @Autowired
    private HlsBpLitigationFinalService hlsBpLitigationFinalService;
    @Autowired
    private HlsBpChangeReqService hlsBpChangeReqService;
    @Autowired
    private HlsBpSanctionService hlsBpSanctionService;
    @Autowired
    private HlsBpIllegalTaxationService hlsBpIllegalTaxationService;
    @Autowired
    private HlsBpCustomerRelationshipService hlsBpCustomerRelationshipService;
    @Autowired
    private IFndCompanyService fndCompanyService;

    @Autowired
    private JcTianyanchaInterfaceInfoMapper jcTianyanchaInterfaceInfoMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusBpSeniorPersionMapper hlsCusBpSeniorPersionMapper;

    @Autowired
    private HlsBpLitigationFinalMapper hlsBpLitigationFinalMapper;

    @Autowired
    private HlsBpMasterRoleMapper hlsBpMasterRoleMapper;

    @Autowired
    private HlsCusBpMasterRelationMapper hlsCusBpMasterRelationMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    /*企业基本信息（含主要人员) 819 ->1116  （未更换 使用原接口）*/
    public static final String URL1 = "https://open.api.tianyancha.com/services/open/ic/baseinfoV3/2.0?keyword=";
    //    public static final String URL1 = "https://open.api.tianyancha.com/services/open/ic/baseinfo/normal?keyword=";
    /*企业股东hls_bp_shareholder_info*/
    public static final String URL2 = "https://open.api.tianyancha.com/services/open/ic/holder/2.0?pageSize=20&pageNum=1&keyword=";
    //    历史股东信息 hls_bp_shareholder_info_ht
    public static final String URL3 = "https://open.api.tianyancha.com/services/open/hi/holder/2.0?pageSize=20&pageNum=1&keyword=";
    /*对外投资*/
    public static final String URL4 = "https://open.api.tianyancha.com/services/open/ic/inverst/2.0?pageSize=20&pageNum=1&keyword=";
    /*分支机构*/
    public static final String URL5 = "https://open.api.tianyancha.com/services/open/ic/branch/2.0?pageSize=20&pageNum=1&keyword=";
    /*总公司*/
    public static final String URL6 = "https://open.api.tianyancha.com/services/open/ic/parentCompany/2.0?pageSize=20&pageNum=1&keyword=";
    /*变更记录*/
    public static final String URL7 = "https://open.api.tianyancha.com/services/open/ic/changeinfo/2.0?pageSize=";
    /*法律诉讼 842->1114  （未更换 使用原接口）*/
    public static final String URL8 = "https://open.api.tianyancha.com/services/open/jr/lawSuit/2.0?pageSize=";
    //    public static final String URL8 = "https://open.api.tianyancha.com/services/open/jr/lawSuit/3.0?pageSize=";
    /*历史法律诉讼  874->1115 （未更换 使用原接口）*/
    public static final String URL9 = "https://open.api.tianyancha.com/services/open/hi/lawsuit/2.0?pageSize=";
    //    public static final String URL9 = "https://open.api.tianyancha.com/services/open/hi/lawSuit/3.0?pageSize=";
    /*终本案件*/
    public static final String URL10 = "https://open.api.tianyancha.com/services/open/jr/endCase/2.0?pageSize=";
    /*行政处罚信息*/
    public static final String URL11 = "https://open.api.tianyancha.com/services/open/mr/punishmentInfo/2.0?pageSize=";
    /*税收违法信息*/
    public static final String URL12 = "https://open.api.tianyancha.com/services/open/mr/taxContravention/2.0?pageSize=";
    /*主要人员*/
    public  static final String URL13 = "http://open.api.tianyancha.com/services/open/ic/staff/2.0?pageSize=";

    private static final String WORKFLOW_TYPE = "BP_ADMIT_WORK_FLOW";

    private static final String DISTRIBUTOR_WORKFLOW_TYPE = "BP_DISTRU_ADMIT_WORK_FLOW";

    private static final String DISTRIBUTOR = "DISTRIBUTOR";

    private static final String VENDER = "VENDER";

    private static final String DEALER = "DEALER";

    public static final String BP_DOCUMENT_CATEGORY = "HLS_BP_MASTER";

    public static final String ERROR = "E";



    @Override
    public List<HlsCusBpMaster> queryHlsBpMasterCreditInfoAll(HlsCusBpMaster dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusBpMaster> list = mapper.queryHlsBpMasterCreditInfoAll(dto);
        return list;
    }

    @Override
    public List<HlsCusBpMaster> queryCusBpMasterDetailsForPrj(IRequest iRequest,HlsCusBpMaster dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusBpMaster> list = mapper.queryCusBpMasterDetailsForPrj(dto);
        return list;
    }

    @Override
    public List<HlsCusBpMaster> queryBpLov(HlsCusBpMaster hlsCusBpMaster, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusBpMaster> list = mapper.queryBpLov(hlsCusBpMaster);
        return list;
    }

    @Override
    public ResponseData queryPlatformRisk(IRequest iRequest, HlsCusBpMaster dto) {

        dto = mapper.selectByPrimaryKey(dto);
        String registrationNumType = dto.getRegistrationNumType();
//        String registerCertNum = dto.getRegisterCertNum();
        String type = "07";
        Long bpId = dto.getBpId();
        String bpName = dto.getBpName();
        String bpClass = dto.getBpClass();
//        if (type.equals(registrationNumType)) {
        GovernmentFinancPlatform governmentFinancPlatform = new GovernmentFinancPlatform();
        List<GovernmentFinancPlatform> list = governmentFinancPlatformMapper.queryAll(governmentFinancPlatform);
        for (GovernmentFinancPlatform item : list) {
            String platformName = item.getPlatformName();
            if (bpName.equals(platformName)) {
                HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
                hlsCusBpMaster.setPlatformTime(item.getPlatformDate());
                hlsCusBpMaster.setPlatformRisk(item.getPlatformRiskN());
                hlsCusBpMaster.setBpId(bpId);
                hlsCusBpMaster.setBpName(bpName);
                hlsCusBpMaster.setBpClass(bpClass);
                service.updateByPrimaryKeySelective(iRequest, hlsCusBpMaster);
            }
        }
//        }
        BpHeadOfficeList bpHeadOfficeList = new BpHeadOfficeList();
        List<BpHeadOfficeList> officeList = BpHeadOfficeListMapper.queryAll(bpHeadOfficeList);
        officeList.forEach(item -> {
            if (item.getBpName().equals(bpName)) {
                HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
                hlsCusBpMaster.setHeadOfficeNameList(item.getListTypeN());
                hlsCusBpMaster.setBpId(bpId);
                service.updateByPrimaryKeySelective(iRequest, hlsCusBpMaster);
            }
        });
        return new ResponseData(service.selectSelective(iRequest, dto));
    }


    @Override
    public List<CompositeMap> queryCreatedName(CompositeMap var1, String var2) {
        return mapper.queryCreatedName(var1, var2);
    }
    @Override
    public ResponseData queryCreatedId(CompositeMap map, String var2){
        List answer;

        answer = this.mapper.queryCreatedId(map, var2);


        return new ResponseData(answer);
    }

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    //    private static final String token = "25f2139a-3e93-4abf-a9bf-cb8a592233ea";  //测试环境token可能需要调整
    private static final String token = "472ca7dd-b5b4-41d7-a3d6-58086f1f7476";  //正式环境token
    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";
    //企业基本信息（含主要人员)     hls_bp_master          bp_senior_persion
    public HlsCusBpMaster updataInfo(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HlsCusBpMaster bpMaster = new HlsCusBpMaster();
        bpMaster.setBpName(hlsCusBpMaster.getBpName());
        if(hlsCusBpMaster.getBpCode()==null){
            //生成bpcode
            Map<String, String> params = new HashMap<String, String>();
            bpMaster.setBpCode(fndCodingRuleValuesService.getCodeRuleValue(iRequest,"HLS_BP_MASTER","BP","BP",params));
        }else{
            bpMaster.setBpCode(hlsCusBpMaster.getBpCode());
        }
        if(hlsCusBpMaster.getBpId()!=null){
            bpMaster.setBpId(hlsCusBpMaster.getBpId());
        }
        String result = null;
        try {
            String url = URL1 +hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置headerz
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            // HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
            JSONObject jsonObject = JSONObject.parseObject(result);
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            String data =String.valueOf(jsonObject.getJSONObject("result"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }

                //extraNam alias
                String extraNam = String.valueOf(jsonObject.getJSONObject("result").get("alias"));
                bpMaster.setExtraNam(extraNam);
                //bpEngName  property3
                String bpEngName = String.valueOf(jsonObject.getJSONObject("result").get("property3"));
                bpMaster.setBpEngName(bpEngName);
                //registerCapitalCur  regCapitalCurrency
                String registerCapitalCur = String.valueOf(jsonObject.getJSONObject("result").get("regCapitalCurrency"));
                //确定相应的scode
//                if("人民币".equals(registerCapitalCur)){
//                    hlsCusBpMaster.setRegisterCapitalCur("CNY");
//                }
                String scode = hlsCusBpMasterMapper.selectCurrencyCode(registerCapitalCur);
                bpMaster.setRegisterCapitalCur(scode);
                bpMaster.setRegisterCapitalCurN(registerCapitalCur);

                //REGISTERED_CAPITAL
                String registered_capital = String.valueOf(jsonObject.getJSONObject("result").get("regCapital"));
                if(!"".equals(registered_capital)&&!"null".equals(registered_capital)&&registered_capital!=null&&!"-".equals(registered_capital)){
                    // bpMaster.setRegisteredCapital(String.valueOf(Double.valueOf(registered_capital.substring(0,registered_capital.length()-(registerCapitalCur.length()+1)))*10000));
                    String newAmomon = registered_capital.replaceAll(REGEX_CHINESE, "");
                    bpMaster.setRegisteredCapital(String.valueOf(Double.valueOf(newAmomon)));
                }

                //PAID_UP_CAPITAL  paidUpCapital
                String paid_up_capital = String.valueOf(jsonObject.getJSONObject("result").get("actualCapital"));
                if(!"".equals(paid_up_capital)&&!"null".equals(paid_up_capital)&&paid_up_capital!=null&&!"-".equals(paid_up_capital)){
                    String newAmomon = paid_up_capital.replaceAll(REGEX_CHINESE, "");
                    bpMaster.setPaidUpCapital(String.valueOf(Double.valueOf(newAmomon)));
                }

                //REGISTERED_ADDRESS
                String registeredAddress = String.valueOf(jsonObject.getJSONObject("result").get("regLocation"));
                bpMaster.setRegisteredAddress(registeredAddress);

                //FOUNDED_DATE
                String foundedDate = String.valueOf(jsonObject.getJSONObject("result").get("estiblishTime"));
                if(!"".equals(foundedDate)&&!"null".equals(foundedDate)&&foundedDate!=null){
                    String newFoundedDate = formatter.format(new Date(Long.parseLong(foundedDate)));
                    try {
                        bpMaster.setFoundedDate(formatter.parse(newFoundedDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                //实收资本币种
                String actualCurrency = String.valueOf(jsonObject.getJSONObject("result").get("actualCapitalCurrency"));
//                if("人民币".equals(actualCurrency)){
//                    hlsCusBpMaster.setActualCurrencyN("CNY");
//                }
                String code = hlsCusBpMasterMapper.selectCurrencyCode(actualCurrency);
                bpMaster.setActualCurrency(code);

                //MANAGEMENT_END_DATE  toTime
                String managementEndDate = String.valueOf(jsonObject.getJSONObject("result").get("toTime"));
                if(!"".equals(managementEndDate)&!"null".equals(managementEndDate)){
                    String newManagementEndDate = formatter.format(new Date(Long.parseLong(managementEndDate)));
                    try {
                        bpMaster.setManagementEndDate(formatter.parse(newManagementEndDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Date newManagementEndDate = formatter.parse(managementEndDate);



                //ECONOMIC_INDU_CLASSIFY industry   FND.INDUSTRY_2
                String economicInduClassify = String.valueOf(jsonObject.getJSONObject("result").get("industry"));
                String scode1 = hlsCusBpMasterMapper.selectSCodeByVName(economicInduClassify);
                bpMaster.setEconomicInduClassify(scode1);
                bpMaster.setEconomicInduClassifyN(economicInduClassify);

                //ENTERPRISE_STATUS regStatus
                String enterpriseStatus = String.valueOf(jsonObject.getJSONObject("result").get("regStatus"));
                bpMaster.setEnterpriseStatus(enterpriseStatus);
                //STOCK_NAME bondName
                String stockName = null;
                if(jsonObject.getJSONObject("result").get("bondName")!=null){
                    stockName = String.valueOf(jsonObject.getJSONObject("result").get("bondName"));
                }
                bpMaster.setStockName(stockName);
                //ENTERPRISE_LABEL  tags
                String enterpriseLabel = String.valueOf(jsonObject.getJSONObject("result").get("tags"));
                bpMaster.setEnterpriseLabel(enterpriseLabel);
                // ENTERPRISE_TYPE companyOrgType
                String enterpriseType = String.valueOf(jsonObject.getJSONObject("result").get("companyOrgType"));
                bpMaster.setEnterpriseType(enterpriseType);
                //CONTACT_INFORMATION_ENTERPRISE  phoneNumber
                String contactInformationEnterprise = String.valueOf(jsonObject.getJSONObject("result").get("phoneNumber"));
                bpMaster.setContactInformationEnterprise(contactInformationEnterprise);
                //SOCIAL_STAFF_NUM  socialStaffNum
                String socialStaffNum = String.valueOf(jsonObject.getJSONObject("result").get("socialStaffNum"));
                bpMaster.setSocialStaffNum(socialStaffNum);
                // TY_CITY city
                String tyCity = String.valueOf(jsonObject.getJSONObject("result").get("city"));
                bpMaster.setTyCity(tyCity);
                //TY_DISTRICT  district
                String tyDistrict = String.valueOf(jsonObject.getJSONObject("result").get("district"));
                bpMaster.settyDistrict(tyDistrict);
                //BUSINESS_SCOPE  businessScope
                String businessScope = String.valueOf(jsonObject.getJSONObject("result").get("businessScope"));
                bpMaster.setBusinessScope(businessScope);
                //LEGAL_PERSON  legalPersonName
                String legalPerson = String.valueOf(jsonObject.getJSONObject("result").get("legalPersonName"));
                bpMaster.setLegalPerson(legalPerson);

                //SOCIAL_CREDIT_CODE taxNumber
                String taxNumber = String.valueOf(jsonObject.getJSONObject("result").get("taxNumber"));
                bpMaster.setSocialCreditCode(taxNumber);



                //主要人员 bp_senior_persion
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                JSONObject jsonObject2 = (JSONObject) jsonObject1.get("staffList");
                if(jsonObject2.size()>0){
                    JSONArray jsonArray = jsonObject2.getJSONArray("result");

                    for(int i=0;i<jsonArray.size();i++){
                        JSONObject newObject =(JSONObject)jsonArray.get(i);
                        HlsCusBpSeniorPersion hlsCusBpSeniorPersion = new HlsCusBpSeniorPersion();
                        String personName = String.valueOf(newObject.get("name"));
                        hlsCusBpSeniorPersion.setBpId(hlsCusBpMaster.getBpId());
                        hlsCusBpSeniorPersion.setPersonName(personName);
                        JSONArray jsonArrayList = newObject.getJSONArray("typeJoin");
                        //TYPE_JOIN typeJoin
                        StringBuffer typeJoin = new StringBuffer();
                        for(int k=0;k<jsonArrayList.size();k++){
                            //JSONObject newObject3 =(JSONObject)jsonArrayList.get(k);
                            typeJoin.append(jsonArrayList.get(k));
                            typeJoin.append(",");
                        }
                        hlsCusBpSeniorPersion.setTypeJoin(typeJoin.substring(0,typeJoin.length()-1));
                        hlsCusBpSeniorPersionMapper.insertSelective(hlsCusBpSeniorPersion);
//                    for(int j=0;j<jsonArrayList.size();j++){
//                        HlsCusBpSeniorPersion hlsCusBpSeniorPersion = new HlsCusBpSeniorPersion();
//                        JSONObject newObject2 =(JSONObject)jsonArray.get(j);
//                        // PERSON_NAME name
//                        //String personName = String.valueOf(newObject2.get("name"));
//                        hlsCusBpSeniorPersion.setBpId(hlsCusBpMaster.getBpId());
//                        hlsCusBpSeniorPersion.setPersonName(personName);
//
//
//                    }
                    }
                }
                //JSONArray jsonArray = object.getJSONArray("items");

                //
                bpMaster.setSkyStatus("Y");
                bpMaster.setBpClass("ORG");
                if("insert".equals(hlsCusBpMaster.getSkyType())){
                    bpMaster = service.insertSelective(iRequest,bpMaster);
                }else{
                    bpMaster = service.updateByPrimaryKeySelective(iRequest,bpMaster);
                }

                //
            }


            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(bpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(data);
            jcTianyanchaInterfaceInfo.setCreatedBy(iRequest.getUserId());
            jcTianyanchaInterfaceInfo.setCreationDate(new Date());
            jcTianyanchaInterfaceInfo.setLastUpdatedBy(iRequest.getUserId());
            jcTianyanchaInterfaceInfo.setLastUpdateDate(new Date());
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);

//            map.put("error_code",error_code);
//            map.put("reason",reason);
//            map.put("hlsCusBpMaster",hlsCusBpMaster);

        } catch (Exception e) {
            //接口异常存表
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(bpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(400L);
            jcTianyanchaInterfaceInfo.setStatus("接口调用失败！");
            jcTianyanchaInterfaceInfo.setTotal(0L);
            jcTianyanchaInterfaceInfo.setData("URL1:"+URL1);
            jcTianyanchaInterfaceInfo.setCreationDate(new Date());
            jcTianyanchaInterfaceInfo.setLastUpdatedBy(iRequest.getUserId());
            jcTianyanchaInterfaceInfo.setLastUpdateDate(new Date());
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return bpMaster;
    }


    //企业股东hls_bp_shareholder_info
    public Map updataShareholderInfo(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL2 +hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            Long total = 0L;
            String error_code = String.valueOf(jsonObject.get("error_code"));
            HlsBpShareholderInfo hlsBpShareholderInfo;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");

                for(int i=0;i<jsonArray.size();i++){
                    hlsBpShareholderInfo = new HlsBpShareholderInfo();
                    hlsBpShareholderInfo.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);
                    newObject.get("name");

                    //SHAREHOLDER_NAME name
                    String shareholderName = String.valueOf(newObject.get("name"));
                    hlsBpShareholderInfo.setShareholderName(shareholderName);
                    // ALIAS alias
                    String alias = String.valueOf(newObject.get("alias"));
                    hlsBpShareholderInfo.setAlias(alias);
                    //capital
                    //JSONObject capital =(JSONObject)newObject.get("capital");
                    JSONArray capitalArray = newObject.getJSONArray("capital");
                    JSONObject capital =(JSONObject)capitalArray.get(0);
                    //AMOMON amomon
                    String amomon = String.valueOf(capital.get("amomon"));
                    //strhours.substring(0,strhours.length()-2)
                    if(!"".equals(amomon)&&amomon!=null&&!"-".equals(amomon)){
                        String newAmomon = amomon.replaceAll(REGEX_CHINESE, "");
                        hlsBpShareholderInfo.setAmomon(Double.valueOf(newAmomon));
                    }
                    //hlsBpShareholderInfo.setAmomon(Double.valueOf(amomon.substring(0,amomon.length()-(hlsCusBpMaster.getRegisterCapitalCurN().length()+1)))*10000);


                    //subTime time
                    String subTime = String.valueOf(capital.get("time"));
                    if(!"".equals(subTime)&&!"null".equals(subTime)){
                        hlsBpShareholderInfo.setSubTime(formatter.parse(subTime));
                    }

                    //PERCENT percent
                    String percent = String.valueOf(capital.get("percent"));
                    hlsBpShareholderInfo.setPercent(percent);
                    //PAYMET paymet
                    String paymet = String.valueOf(capital.get("paymet"));
                    hlsBpShareholderInfo.setPaymet(paymet);
                    hlsBpShareholderInfo = hlsBpShareholderInfoService.insertSelective(iRequest,hlsBpShareholderInfo);
                }
            }

            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);



            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //历史股东信息 hls_bp_shareholder_info_ht
    public Map updataShareholderInfoHt(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL3+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            String data =String.valueOf(jsonObject.getJSONObject("result"));
            Long total = 0L;

            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    BpShareholderInfoHt bpShareholderInfoHt = new BpShareholderInfoHt();
                    bpShareholderInfoHt.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //SHAREHOLDER_NAME name SHAREHOLDER_NAME
                    String shareholderName = String.valueOf(newObject.get("name"));
                    bpShareholderInfoHt.setShareholderName(shareholderName);
                    JSONArray capitalArray = newObject.getJSONArray("capital");
                    JSONObject capital =(JSONObject)capitalArray.get(0);
                    //AMOMON amomon SUBSCRIPTION_AMOMON

                    String amomon = String.valueOf(capital.get("amomon"));
                    if(!"".equals(amomon)&&!"null".equals(amomon)&&!"-".equals(amomon)){
                        String newAmomon = amomon.replaceAll(REGEX_CHINESE, "");
                        bpShareholderInfoHt.setSubscriptionAmomon(Double.valueOf(newAmomon));
                    }

                    //bpShareholderInfoHt.setSubscriptionAmomon(Double.valueOf(amomon.substring(0,amomon.length()-(hlsCusBpMaster.getRegisterCapitalCurN().length()+1)))*10000);


                    //subTime time SUBSCRIPTION_TIME

                    String subTime = String.valueOf(capital.get("time"));
                    if(!"".equals(subTime)&&!"null".equals(subTime)&&!"-".equals(subTime)){
                        bpShareholderInfoHt.setSubscriptionTime(formatter.parse(subTime));
                    }



                    //SUBSCRIPTION_PERCEN percent

                    String percent = String.valueOf(capital.get("percent"));
                    bpShareholderInfoHt.setSubscriptionPercen(percent);


                    //PAYMET paymet SUBSCRIPTION_PAYMET

                    String paymet = String.valueOf(capital.get("paymet"));
                    bpShareholderInfoHt.setSubscriptionPaymet(paymet);


                    bpShareholderInfoHtService.insertSelective(iRequest,bpShareholderInfoHt);
                }
            }
            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(data);
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //对外投资 hls_bp_out_investment
    public Map updataOutInvestment(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL4+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpOutInvestment hlsBpOutInvestment = new HlsBpOutInvestment();
                    hlsBpOutInvestment.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //INVESTMENT_COMPANY_NAM name
                    String investmentCompanyNam = String.valueOf(newObject.get("name"));
                    hlsBpOutInvestment.setInvestmentCompanyNam(investmentCompanyNam);
                    //ORG_TYPE orgType
                    String orgType = String.valueOf(newObject.get("orgType"));
                    hlsBpOutInvestment.setOrgType(orgType);
                    //CREDIT_CODE creditCode
                    String creditCode = String.valueOf(newObject.get("creditCode"));
                    hlsBpOutInvestment.setCreditCode(creditCode);
                    //REG_CAPITAL regCapital
                    String regCapital = String.valueOf(newObject.get("regCapital"));
                    //AMOUNT_SUFFIX amountSuffix
                    String amountSuffix = String.valueOf(newObject.get("amountSuffix"));
                    hlsBpOutInvestment.setAmountSuffix(amountSuffix);
                    //PERCENT percent
                    String percent = String.valueOf(newObject.get("percent"));
                    hlsBpOutInvestment.setPercent(percent);
                    //AMOUNT amount
                    String amount = String.valueOf(newObject.get("amount"));
                    if(!"".equals(amount)&&!"null".equals(amount)&&amount!=null&&!"-".equals(amount)){
                        hlsBpOutInvestment.setAmount(Double.valueOf(amount));
                        String string = regCapital.replaceAll(REGEX_CHINESE, "");
                        hlsBpOutInvestment.setRegCapital(Double.valueOf(string));
                        // hlsBpOutInvestment.setRegCapital(Double.valueOf(regCapital.substring(0,regCapital.length()-amountSuffix.length())));
                    }

                    //REG_STATUS regStatus
                    String regStatus = String.valueOf(newObject.get("regStatus"));
                    hlsBpOutInvestment.setRegStatus(regStatus);
                    //ESTIBLISH_TIME estiblishTime
                    String estiblishTime = String.valueOf(newObject.get("estiblishTime"));
                    if(!"".equals(estiblishTime)&&!"null".equals(estiblishTime)&&estiblishTime!=null){
                        String newEstiblishTime = formatter.format(new Date(Long.parseLong(estiblishTime)));
                        try {
                            hlsBpOutInvestment.setEstiblishTime(formatter.parse(newEstiblishTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //LEGAL_PERSON_NAME legalPersonName
                    String legalPersonName = String.valueOf(newObject.get("legalPersonName"));
                    hlsBpOutInvestment.setLegalPersonName(legalPersonName);
                    //CATEGORY category
                    String category = String.valueOf(newObject.get("category"));
                    String scode = hlsCusBpMasterMapper.selectSCodeByVName(category);
                    hlsBpOutInvestment.setCategory(scode);

                    //BUSINESS_SCOPE business_scope
                    String businessScope = String.valueOf(newObject.get("business_scope"));
                    hlsBpOutInvestment.setBusinessScope(businessScope);

                    hlsBpOutInvestmentService.insertSelective(iRequest,hlsBpOutInvestment);

                }
            }

            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);



            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //分支机构  hls_bp_customer_relationship
    public Map updataCustomerRelationshipParch(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL5+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;

            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpCustomerRelationship hlsBpCustomerRelationship = new HlsBpCustomerRelationship();
                    hlsBpCustomerRelationship.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //CUSTOMER_RELATIONSHIP  分支机构
                    hlsBpCustomerRelationship.setCustomerRelationship("R02");
                    //COMPANY_NAME name
                    String companyName = String.valueOf(newObject.get("name"));
                    hlsBpCustomerRelationship.setCompanyName(companyName);
                    //LEGAL_PERSON_NAME legalPersonName
                    String legalPersonName = String.valueOf(newObject.get("legalPersonName"));
                    hlsBpCustomerRelationship.setLegalPersonName(legalPersonName);
                    //REGISTERED_CAPITAL regCapital
                    String registeredCapital = String.valueOf(newObject.get("regCapital"));
                    if(!"".equals(registeredCapital)&&!"null".equals(registeredCapital)&&registeredCapital!=null&&!"-".equals(registeredCapital)){
                        String string = registeredCapital.replaceAll(REGEX_CHINESE, "");
                        hlsBpCustomerRelationship.setRegisteredCapital(Double.valueOf(string));
                        //hlsBpCustomerRelationship.setRegisteredCapital(Double.valueOf(registeredCapital.substring(0,registeredCapital.length()-(4)))*10000);
                    }

                    //INCORPORATION_DATE estiblishTime
                    String estiblishTime = String.valueOf(newObject.get("estiblishTime"));
                    if(!"".equals(estiblishTime)&&!"null".equals(estiblishTime)&&estiblishTime!=null){
                        String newIncorporationDate = formatter.format(new Date(Long.parseLong(estiblishTime)));
                        try {
                            hlsBpCustomerRelationship.setIncorporationDate(formatter.parse(newIncorporationDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //ENTERPRISE_STATUS regStatus
                    String enterpriseStatus = String.valueOf(newObject.get("regStatus"));
                    hlsBpCustomerRelationship.setEnterpriseStatus(enterpriseStatus);
                    hlsBpCustomerRelationshipService.insertSelective(iRequest,hlsBpCustomerRelationship);
                }
            }
            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }
    //总公司 hls_bp_customer_relationship
    public Map updataCustomerRelationship(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL6 +hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)) {
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if (!"null".equals(to)) {
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                if (jsonArray == null) {
                    //记录请求天眼查询结果
                    JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
                    jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
                    jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
                    jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
                    jcTianyanchaInterfaceInfo.setStatus(reason);
                    jcTianyanchaInterfaceInfo.setTotal(total);
                    jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
                    jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);

                    map.put("error_code",error_code);
                    map.put("reason",reason);
                    return map;
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    HlsBpCustomerRelationship hlsBpCustomerRelationship = new HlsBpCustomerRelationship();
                    hlsBpCustomerRelationship.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject = (JSONObject) jsonArray.get(i);

                    //CUSTOMER_RELATIONSHIP  总公司
                    hlsBpCustomerRelationship.setCustomerRelationship("R01");
                    //COMPANY_NAME name
                    String companyName = String.valueOf(newObject.get("name"));
                    hlsBpCustomerRelationship.setCompanyName(companyName);
                    //LEGAL_PERSON_NAME legalPersonName
                    String legalPersonName = String.valueOf(newObject.get("legalPersonName"));
                    hlsBpCustomerRelationship.setLegalPersonName(legalPersonName);
                    //REGISTERED_CAPITAL regCapital
                    String registeredCapital = String.valueOf(newObject.get("regCapital"));
                    if (!"".equals(registeredCapital) && !"null".equals(registeredCapital) && registeredCapital != null && !"-".equals(registeredCapital)) {
                        String newRegisteredCapital = registeredCapital.replaceAll(REGEX_CHINESE, "");
                        hlsBpCustomerRelationship.setRegisteredCapital(Double.valueOf(newRegisteredCapital));
                    }

                    //hlsBpCustomerRelationship.setRegisteredCapital(Double.valueOf(registeredCapital.substring(0,registeredCapital.length()-(hlsCusBpMaster.getRegisterCapitalCurN().length()+1)))*10000);
                    //INCORPORATION_DATE estiblishTime
                    String estiblishTime = String.valueOf(newObject.get("estiblishTime"));
                    if (!"".equals(estiblishTime) && !"null".equals(estiblishTime) && estiblishTime != null) {
                        String newIncorporationDate = formatter.format(new Date(Long.parseLong(estiblishTime)));
                        try {
                            hlsBpCustomerRelationship.setIncorporationDate(formatter.parse(newIncorporationDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //ENTERPRISE_STATUS regStatus
                    String enterpriseStatus = String.valueOf(newObject.get("regStatus"));
                    hlsBpCustomerRelationship.setEnterpriseStatus(enterpriseStatus);
                    hlsBpCustomerRelationshipService.insertSelective(iRequest, hlsBpCustomerRelationship);
                }

            }

            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);

            map.put("error_code",error_code);
            map.put("reason",reason);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }
    //变更记录 hls_bp_change_req
    public Map updataHlsBpChangeReq(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL7+pageSize+"&pageNum="+page+"&keyword="+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpChangeReq hlsBpChangeReq = new HlsBpChangeReq();
                    hlsBpChangeReq.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //CHANGE_ITEM plaintiffs
                    String changeItem = String.valueOf(newObject.get("changeItem"));
                    hlsBpChangeReq.setChangeItem(changeItem);

                    //CHANGE_TIME createTime
                    String createTime = String.valueOf(newObject.get("createTime"));
                    hlsBpChangeReq.setChangeTime(formatter.parse(createTime));


                    //CHANGE_BEFORE contentBefore
                    String contentBefore = String.valueOf(newObject.get("contentBefore"));
                    hlsBpChangeReq.setChangeBefore(contentBefore);
                    //ChangeAfter contentAfter
                    String changeAfter = String.valueOf(newObject.get("contentAfter"));
                    hlsBpChangeReq.setChangeAfter(changeAfter);
                    hlsBpChangeReqService.insertSelective(iRequest,hlsBpChangeReq);
                }
            }

            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //法律诉讼 hls_bp_litigation
    public Map updataHlsBpLitigation(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL8 +pageSize+"&pageNum="+page+"&keyword="+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpLitigation hlsBpLitigation = new HlsBpLitigation();
                    hlsBpLitigation.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //PLAINTIFFS plaintiffs
                    String plaintiffs = String.valueOf(newObject.get("plaintiffs"));
                    hlsBpLitigation.setPlaintiffs(plaintiffs);
                    //COURT court
                    String court = String.valueOf(newObject.get("court"));
                    hlsBpLitigation.setCourt(court);
                    //CASE_REASON casereason
                    String caseReason = String.valueOf(newObject.get("casereason"));
                    hlsBpLitigation.setCaseReason(caseReason);
                    //URL lawsuitUrl
                    if(newObject.get("lawsuitUrl")!=null){
                        String url1 = String.valueOf(newObject.get("lawsuitUrl"));
                        hlsBpLitigation.setUrl(url1);
                    }

                    //SUBMIT_TIME submittime
                    String submitTime = String.valueOf(newObject.get("submittime"));
                    if(!"".equals(submitTime)&&!"null".equals(submitTime)&&submitTime!=null){
                        String newSubmitTime = formatter.format(new Date(Long.parseLong(submitTime)));
                        try {
                            hlsBpLitigation.setSubmitTime(formatter.parse(newSubmitTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //CASE_NO caseno
                    String caseNo = String.valueOf(newObject.get("caseno"));
                    hlsBpLitigation.setCaseNo(caseNo);
                    //TITLE title
                    String title = String.valueOf(newObject.get("title"));
                    hlsBpLitigation.setTitle(title);
                    //ABSTRACTS abstracts
                    String abstracts = String.valueOf(newObject.get("abstracts"));
                    hlsBpLitigation.setAbstracts(abstracts);
                    //JUDGE_TIME judgetime
                    String judgeTime = String.valueOf(newObject.get("judgetime"));
                    if(!"".equals(judgeTime)&&!"null".equals(judgeTime)&&judgeTime!=null){
                        hlsBpLitigation.setJudgeTime(formatter.parse(judgeTime));
                    }

                    //CASE_TYPE casetype 案件类型
                    String caseType = String.valueOf(newObject.get("casetype"));
                    hlsBpLitigation.setCaseType(caseType);

                    //DOC_TYPE doctype
                    String docType = String.valueOf(newObject.get("doctype"));
                    hlsBpLitigation.setDocType(docType);

                    //THIRD_PARTIES thirdParties
                    String thirdParties = String.valueOf(newObject.get("thirdParties"));
                    hlsBpLitigation.setThirdParties(thirdParties);

                    //DEFENDANTS defendants
                    String defendants = String.valueOf(newObject.get("defendants"));
                    hlsBpLitigation.setDefendants(defendants);
                    hlsBpLitigationService.insertSelective(iRequest,hlsBpLitigation);
                }
            }
            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }
    //历史法律诉讼 hls_bp_litigation_ht
    public Map updataHlsBpLitigationHt(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL9+pageSize+"&pageNum="+page+"&keyword="+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpLitigationHt hlsBpLitigationHt = new HlsBpLitigationHt();
                    hlsBpLitigationHt.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //PLAINTIFFS plaintiffs
                    String plaintiffs = String.valueOf(newObject.get("plaintiffs"));
                    hlsBpLitigationHt.setPlaintiffs(plaintiffs);
                    //COURT court
                    String court = String.valueOf(newObject.get("court"));
                    hlsBpLitigationHt.setCourt(court);
                    //CASE_REASON casereason
                    String caseReason = String.valueOf(newObject.get("casereason"));
                    hlsBpLitigationHt.setCaseReason(caseReason);
                    //URL url lawsuitUrl
                    if(newObject.get("lawsuitUrl")!=null){
                        String url1 = String.valueOf(newObject.get("lawsuitUrl"));
                        hlsBpLitigationHt.setUrl(url1);
                    }

                    //SUBMIT_TIME submittime
                    String submitTime = String.valueOf(newObject.get("submittime"));
                    if(!"".equals(submitTime)&&!"null".equals(submitTime)&&submitTime!=null){
                        String newSubmitTime = formatter.format(new Date(Long.parseLong(submitTime)));
                        try {
                            hlsBpLitigationHt.setSubmitTime(formatter.parse(newSubmitTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //CASE_NO caseno
                    String caseNo = String.valueOf(newObject.get("caseno"));
                    hlsBpLitigationHt.setCaseNo(caseNo);
                    //TITLE title
                    String title = String.valueOf(newObject.get("title"));
                    hlsBpLitigationHt.setTitle(title);
                    //ABSTRACTS abstracts
                    String abstracts = String.valueOf(newObject.get("abstracts"));
                    hlsBpLitigationHt.setAbstracts(abstracts);
                    //JUDGE_TIME judgetime
                    String judgeTime = String.valueOf(newObject.get("judgetime"));
                    if(!"".equals(judgeTime)&&!"null".equals(judgeTime)&&judgeTime!=null){
                        hlsBpLitigationHt.setJudgeTime(formatter.parse(judgeTime));
                    }

                    //CASE_TYPE casetype
                    String caseType = String.valueOf(newObject.get("casetype"));
                    hlsBpLitigationHt.setCaseType(caseType);

                    //DOC_TYPE doctype
                    String docType = String.valueOf(newObject.get("doctype"));
                    hlsBpLitigationHt.setDocType(docType);

                    //THIRD_PARTIES thirdParties
                    String thirdParties = String.valueOf(newObject.get("thirdParties"));
                    hlsBpLitigationHt.setThirdParties(thirdParties);

                    //DEFENDANTS defendants
                    String defendants = String.valueOf(newObject.get("defendants"));
                    hlsBpLitigationHt.setDefendants(defendants);
                    hlsBpLitigationHtService.insertSelective(iRequest,hlsBpLitigationHt);
                }
            }
            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //终本案件 hls_bp_litigation_final
    public Map updataHlsBpLitigationFinal(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL10+pageSize+"&pageNum="+page+"&keyword="+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpLitigationFinal hlsBpLitigationFinal = new HlsBpLitigationFinal();
                    hlsBpLitigationFinal.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //CASE_CODE caseCode
                    String caseCode = String.valueOf(newObject.get("caseCode"));
                    hlsBpLitigationFinal.setCaseCode(caseCode);
                    //EXEC_COURT_NAME execCourtName
                    String execCourtName = String.valueOf(newObject.get("execCourtName"));
                    hlsBpLitigationFinal.setExecCourtName(execCourtName);
                    //EXEC_MONEY execMoney
                    String execMoney = String.valueOf(newObject.get("execMoney"));
                    hlsBpLitigationFinal.setExecMoney(execMoney);
                    //CASE_CREATE_TIME caseCreateTime
                    String caseCreateTime = String.valueOf(newObject.get("caseCreateTime"));
                    if(!"".equals(caseCreateTime)&&!"null".equals(caseCreateTime)&&caseCreateTime!=null){
                        String newCaseCreateTime = formatter.format(new Date(Long.parseLong(caseCreateTime)));
                        try {
                            hlsBpLitigationFinal.setCaseCreateTime(formatter.parse(newCaseCreateTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //CASE_FINAL_TIME caseFinalTime
                    String caseFinalTime = String.valueOf(newObject.get("caseFinalTime"));
                    if(!"".equals(caseFinalTime)&&!"null".equals(caseFinalTime)&&caseFinalTime!=null){
                        String newCaseFinalTime = formatter.format(new Date(Long.parseLong(caseFinalTime)));
                        try {
                            hlsBpLitigationFinal.setCaseFinalTime(formatter.parse(newCaseFinalTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    //Z_NAME zname
                    String zName = String.valueOf(newObject.get("zname"));
                    hlsBpLitigationFinal.setZName(zName);
                    hlsBpLitigationFinalMapper.insertHlsBpLitigationFinal(hlsBpLitigationFinal);
                    // hlsBpLitigationFinalMapper.insert(hlsBpLitigationFinal);
                }
            }

            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //行政处罚信息 hls_bp_sanction
    public Map updataHlsBpSanction(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL11+pageSize+"&pageNum="+page+"&keyword="+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpSanction hlsBpSanction = new HlsBpSanction();
                    hlsBpSanction.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //CONTENT content
                    String content = String.valueOf(newObject.get("content"));
                    hlsBpSanction.setContent(content);
                    //PUNISH_NUMBER punishNumber
                    String punishNumber = String.valueOf(newObject.get("punishNumber"));
                    hlsBpSanction.setPunishNumber(punishNumber);
                    //DESCRIPTION description
                    String description = String.valueOf(newObject.get("description"));
                    hlsBpSanction.setDescription(description);


                    //DECISION_DATE decisionDate
                    String decisionDate = String.valueOf(newObject.get("decisionDate"));
                    if(!"".equals(decisionDate)&&!"null".equals(decisionDate)&&decisionDate!=null){
                        hlsBpSanction.setDecisionDate(formatter.parse(decisionDate));
                    }


                    //TYPE type
                    String type = String.valueOf(newObject.get("type"));
                    hlsBpSanction.setType(type);
                    //DEPARTMENT_NAME departmentName
                    String departmentName = String.valueOf(newObject.get("departmentName"));
                    hlsBpSanction.setDepartmentName(departmentName);
                    //PUBLISH_DATE publishDate
                    String publishDate = String.valueOf(newObject.get("publishDate"));
                    if(!"".equals(publishDate)&&!"null".equals(publishDate)&&publishDate!=null){
                        hlsBpSanction.setPublishDate(formatter.parse(publishDate));
                    }


                    hlsBpSanctionService.insertSelective(iRequest,hlsBpSanction);
                }
            }
            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    //税收违法信息 hls_bp_illegal_taxation
    public Map updataHlsBpIllegalTaxation(IRequest iRequest, HlsCusBpMaster hlsCusBpMaster,int page,int pageSize){
        HashMap map = new HashMap();
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 1000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;

        try {
            String url = URL12+pageSize+"&pageNum="+page+"&keyword="+hlsCusBpMaster.getRegisterCertNum();
            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject object = jsonObject.getJSONObject("result");
            String reason = String.valueOf(jsonObject.get("reason"));
            String error_code = String.valueOf(jsonObject.get("error_code"));
            Long total = 0L;
            if(("ok").equals(reason)){
                String to = String.valueOf(jsonObject.getJSONObject("result").get("total"));
                if(!"null".equals(to)){
                    total = Long.valueOf(to);
                }
                JSONArray jsonArray = object.getJSONArray("items");
                for(int i=0;i<jsonArray.size();i++){
                    HlsBpIllegalTaxation hlsBpIllegalTaxation = new HlsBpIllegalTaxation();
                    hlsBpIllegalTaxation.setBpId(hlsCusBpMaster.getBpId());

                    JSONObject newObject =(JSONObject)jsonArray.get(i);

                    //CASE_TYPE case_type
                    String caseType = String.valueOf(newObject.get("case_type"));
                    hlsBpIllegalTaxation.setCaseType(caseType);
                    //PUBLISH_TIME publish_time
                    String publishTime = String.valueOf(newObject.get("publish_time"));
                    if(!"".equals(publishTime)&&!"null".equals(publishTime)&&publishTime!=null){
                        hlsBpIllegalTaxation.setPublishTime(formatter.parse(publishTime));
                    }

                    //DEPARTMENT department
                    String department = String.valueOf(newObject.get("department"));
                    hlsBpIllegalTaxation.setDepartment(department);


                    //TAXPAYER_NAME taxpayer_name
                    String taxpayerName = String.valueOf(newObject.get("taxpayer_name"));
                    hlsBpIllegalTaxation.setTaxpayerName(taxpayerName);

                    hlsBpIllegalTaxationService.insertSelective(iRequest,hlsBpIllegalTaxation);
                }
            }
            //记录请求天眼查询结果
            JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
            jcTianyanchaInterfaceInfo.setDocumentId(hlsCusBpMaster.getBpId());
            jcTianyanchaInterfaceInfo.setDocumentType("HLS_BP_MASTER");
            jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(error_code));
            jcTianyanchaInterfaceInfo.setStatus(reason);
            jcTianyanchaInterfaceInfo.setTotal(total);
            jcTianyanchaInterfaceInfo.setData(String.valueOf(object));
            jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            map.put("error_code",error_code);
            map.put("reason",reason);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();

        }
        return map;
    }

    @Override
    public Map queryBySky(IRequest iRequest,HlsCusBpMaster hlsCusBpMaster,int page,int pageSize) {
//        BasicHttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
//        HttpConnectionParams.setSoTimeout(httpParams, 1000);
//        HttpClient httpClient = new DefaultHttpClient(httpParams);
//        String result = null;
//        try {
//            String url = "https://open.api.tianyancha.com/services/open/ic/baseinfoV3/2.0?keyword="+hlsCusBpMaster.getBpName();
//            HttpGet get = new HttpGet(url);
//            // 设置header
//            get.setHeader("Authorization",token);
//            // 设置类型
//            HttpResponse response = httpClient.execute(get);
//            HttpEntity entity = response.getEntity();
//            result = EntityUtils.toString(entity, "utf-8");
//            //
//            hlsCusBpMaster =  this.updataInfo(iRequest,result,hlsCusBpMaster);
//            //
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            httpClient.getConnectionManager().shutdown();
//        }
        //
        //以下正式环境放开

        //企业基本信息（含主要人员)
        //this.updataInfo(iRequest,hlsCusBpMaster);
        //企业股东
        //this.updataShareholderInfo(iRequest,hlsCusBpMaster);
        //历史股东信息
        //this.updataShareholderInfoHt(iRequest,hlsCusBpMaster);

        //对外投资
        //this.updataOutInvestment(iRequest,hlsCusBpMaster);

        //分支机构
        //this.updataCustomerRelationshipParch(iRequest,hlsCusBpMaster,page,pageSize);
        //总公司
        //this.updataCustomerRelationship(iRequest,hlsCusBpMaster);
        //变更记录
        //this.updataHlsBpChangeReq(iRequest,hlsCusBpMaster,page,pageSize);

        //法律诉讼
        //this.updataHlsBpLitigation(iRequest,hlsCusBpMaster,page,pageSize);

        //历史法律诉讼
        //this.updataHlsBpLitigationHt(iRequest,hlsCusBpMaster,page,pageSize);

        //终本案件
        //this.updataHlsBpLitigationFinal(iRequest,hlsCusBpMaster,page,pageSize);

        //行政处罚信息
        //this.updataHlsBpSanction(iRequest,hlsCusBpMaster,page,pageSize);
        //税收违法信息
        //this.updataHlsBpIllegalTaxation(iRequest,hlsCusBpMaster,page,pageSize);


//        if("ok".equals(this.updataInfo(iRequest,hlsCusBpMaster).get("reason"))){
//
//        }
        //
        HashMap map = new HashMap();
        //先更新基表后返回新的数据
        HlsCusBpMaster cusBpMaster = this.updataInfo(iRequest,hlsCusBpMaster);
        cusBpMaster.setRegisterCertNum(hlsCusBpMaster.getRegisterCertNum());
        map.put("hlsCusBpMaster",cusBpMaster);
        Map map2 = this.updataShareholderInfo(iRequest,cusBpMaster);
        if(!"0".equals(map2.get("error_code"))&&!"300000".equals(map2.get("error_code"))){
            map.put("error_code",map2.get("error_code"));
        }
        Map map3 = this.updataShareholderInfoHt(iRequest,cusBpMaster);
        if(!"0".equals(map3.get("error_code"))&&!"300000".equals(map3.get("error_code"))){
            map.put("error_code",map3.get("error_code"));
        }
        Map map4 = this.updataOutInvestment(iRequest,cusBpMaster);
        if(!"0".equals(map4.get("error_code"))&&!"300000".equals(map4.get("error_code"))){
            map.put("error_code",map4.get("error_code"));
        }
        Map map5 = this.updataCustomerRelationshipParch(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map5.get("error_code"))&&!"300000".equals(map5.get("error_code"))){
            map.put("error_code",map5.get("error_code"));
        }
        Map map6 = this.updataCustomerRelationship(iRequest,cusBpMaster);
        if(!"0".equals(map6.get("error_code"))&&!"300000".equals(map6.get("error_code"))){
            map.put("error_code",map6.get("error_code"));
        }
        Map map7 = this.updataHlsBpChangeReq(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map7.get("error_code"))&&!"300000".equals(map7.get("error_code"))){
            map.put("error_code",map7.get("error_code"));
        }
        Map map8 = this.updataHlsBpLitigation(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map8.get("error_code"))&&!"300000".equals(map8.get("error_code"))){
            map.put("error_code",map8.get("error_code"));
        }
        Map map9 = this.updataHlsBpLitigationHt(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map9.get("error_code"))&&!"300000".equals(map9.get("error_code"))){
            map.put("error_code",map9.get("error_code"));
        }
        Map map10 = this.updataHlsBpLitigationFinal(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map10.get("error_code"))&&!"300000".equals(map10.get("error_code"))){
            map.put("error_code",map10.get("error_code"));
        }

        Map map11 = this.updataHlsBpSanction(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map11.get("error_code"))&&!"300000".equals(map11.get("error_code"))){
            map.put("error_code",map11.get("error_code"));
        }

        Map map12 = this.updataHlsBpIllegalTaxation(iRequest,cusBpMaster,page,pageSize);
        if(!"0".equals(map12.get("error_code"))&&!"300000".equals(map12.get("error_code"))){
            map.put("error_code",map12.get("error_code"));
        }else{
            map.put("error_code",map12.get("error_code"));
        }
        return map;
    }

    @Override
    public String queryBpTypeByRole(Long bpId) {
        //如果角色类别存在主机厂/经销商走主机厂经销商准入，否则走分销商准入(同时存在主机厂/经销商&&分销商，优先主机厂/经销商准入)
        List<String> bpRoleList = new ArrayList<>();
        Map bpIdMap = new HashMap<>(1);
        bpIdMap.put("bpId", bpId);
        List<com.hand.hls.prj.dto.HlsBpMasterRole> roleList = hlsBpMasterRoleMapper.query2(bpIdMap);
        for (HlsBpMasterRole role : roleList) {
            bpRoleList.add(role.getBpType());
        }
        if (bpRoleList.contains(VENDER)) {
            return VENDER;
        } else if (bpRoleList.contains(DEALER)) {
            return DEALER;
        } else {
            return DISTRIBUTOR;
        }
    }

    @Override
    public void bpWflSubmit(IRequest iRequest, HlsCusBpMaster bpMaster) {
        List<HlsCusBpMaster> list = new ArrayList();
        String workFlow = WORKFLOW_TYPE;
        Map<String, Object> params = new HashMap<String, Object>();
        bpMaster = (this.self()).selectByPrimaryKey(iRequest, bpMaster);
        list.add(bpMaster);
        params.put("workFlowType", workFlow);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, workFlow);
        params.put(IActivitiCommonService.DEMO_NAME, "BP");
        params.put(IActivitiCommonService.BUSINESS_KEY, bpMaster.getBpId());
        params.put("documentCategory", BP_DOCUMENT_CATEGORY);
        params.put("documentName", bpMaster.getBpName());
        params.put("documentNumber", bpMaster.getBpCode());
        params.put("bpId", bpMaster.getBpId());
        params.put("startUserName", iRequest.getUserName());
        params.put("companyId", iRequest.getCompanyId());
        this.activitiStartService.start(iRequest, list, params);
        bpMaster.setBpApproveStatus("APPROVING");
        (this.self()).updateByPrimaryKeySelective(iRequest, bpMaster);

    }

//    public void bpWflSubmit(IRequest iRequest, HlsCusBpMaster bpMaster, Long allocationId, Long bpRelationId) {
//        List<HlsCusBpMasterRelation> list = new ArrayList();
//        Map<String, Object> map = new HashMap();
//        bpMaster = (this.self()).selectByPrimaryKey(iRequest, bpMaster);
//        HlsCusBpMasterRelation bpMasterRelation = hlsCusBpMasterRelationMapper.queryRelationByBpRelationId(bpRelationId).get(0);
//        String relationTypeN = bpMasterRelation.getRelationTypeN();
//        String relatedBpIdN = bpMasterRelation.getRelatedBpIdN();
//        String bpCategory = bpMaster.getBpCategory();
//        String bpType;
//
//        /*if (!StringUtils.equals(bpMaster.getBpType(), DISTRIBUTOR)) {
//            documentListService.validateNecessaryDocumentList(iRequest, bpMaster.getBpId(), "HLS_BP_MASTER");
//        }*/
//
//        //对于伙伴类别非主机厂/经销商，bpApproveStatus默认改为审批通过
//        if (!"VENDER".equals(bpCategory) && !"DEALER".equals(bpCategory)) {
//            bpMaster.setBpApproveStatus("APPROVED");
//        }
//
//        //如果审批结果为已通过，不改变状态
//        if (!"APPROVED".equals(bpMaster.getBpApproveStatus())) {
//            bpMaster.setBpApproveStatus("APPROVING");
//        }
//        (this.self()).updateByPrimaryKeySelective(iRequest, bpMaster);
//
//        list.add(bpMasterRelation);
//        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(bpMaster));
//
//        //如果角色类别存在主机厂/经销商走主机厂经销商准入，否则走分销商准入(同时存在主机厂/经销商&&分销商，优先主机厂/经销商准入)
//        bpType = queryBpTypeByRole(bpMaster.getBpId());
//
//        if (StringUtils.equals(bpType, DISTRIBUTOR)) {
//            map.put("workFlowType", DISTRIBUTOR_WORKFLOW_TYPE);
//            bpMasterRelation.setAdmitStatus("APPROVING");
//            hlsCusBpMasterRelationMapper.updateByPrimaryKey(bpMasterRelation);
//        } else {
//            map.put("workFlowType", WORKFLOW_TYPE);
//            //流程开始前清除决策报告信息，确保每次在流程中的决策报告是最新的
//            bpMasterRelation.setAdmitStatus("APPROVING");
//            bpMasterRelation.setReportLink(null);
//            bpMasterRelation.setReportStatus(null);
//            bpMasterRelation.setReportQueryTimes(null);
//            bpMasterRelation.setReportToken(null);
//            hlsCusBpMasterRelationMapper.updateByPrimaryKey(bpMasterRelation);
//        }
//        map.put("bpMaster", jsonObject.toString());
//        map.put("documentId", bpMaster.getBpId());
//        map.put("documentCategory", BP_DOCUMENT_CATEGORY);
//        map.put("documentType", bpMaster.getBpType());
//        map.put("bpName", bpMaster.getBpName());
//        map.put("documentName", bpMaster.getBpName()+"-"+relationTypeN+"-"+relatedBpIdN);
//        map.put("documentNumber", bpMaster.getBpCode());
//        map.put("allocationId", allocationId);
//        map.put("bpRelationId", bpMasterRelation.getBpRelationId());
//        this.activitiStartService.start(iRequest, list, map);
//    }

    @Override
    public String getAuthorityString(IRequest iRequest) {
        FndCompany fndCompany = new FndCompany();
        fndCompany.setCompanyId(iRequest.getCompanyId());
        fndCompany = fndCompanyService.selectByPrimaryKey(iRequest, fndCompany);
        String employeeCode = iRequest.getEmployeeCode();
        String positionCode = iRequest.getAttribute("positionCode") == null ? "" : (String) iRequest.getAttribute("positionCode");
        String unitCode = iRequest.getAttribute("unitCode") == null ? "" : (String) iRequest.getAttribute("unitCode");
        String authorityRuleString = '"' + fndCompany.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + employeeCode + '"';
        return authorityRuleString;
    }

    @Override
    public List<Long> getCityIdAndProvinceIdByDistrictId(Long districtId) {
        List<Long> res = new ArrayList<>();
        String provinceIdAndCityId = mapper.getCityIdAndProvinceIdByDistrictId(districtId);
        String[] ans = provinceIdAndCityId.split("_");
        res.add(Long.parseLong(ans[0]));
        res.add(Long.parseLong(ans[1]));
        return res;
    }

    @Override
    public void saveOrder(Long conditionId, Long orderId) {
        mapper.saveOrder(conditionId,orderId);
    }

    @Override
    public void saveBusinessCondition(Long conditionId, Long bpId) {
        mapper.saveBusinessCondition(conditionId,bpId);
    }

    @Override
    public List<Long> getConditionId(Long bpId) {
        return mapper.getConditionId(bpId);
    }


}
