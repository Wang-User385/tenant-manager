package com.hand.hls.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.credit.dto.QueryLateInfo;
import com.hand.hls.credit.dto.QueryPrjQuotationDTO;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.credit.dto.QueryHlsBpMasterDTO;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 订单预审
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/11 16:17
 */
@Service
public class TongDunServiceImpl implements TongDunService {

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private HlsWsRequestsMapper hlsWsRequestsMapper;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    /*
            测试IP:http://172.17.241.66:8088/
            准生产IP:http://172.17.241.69:8088/
            生产IP:http://172.17.241.12:8088/
     */
    private static final String YS_URL = "http://172.17.241.66:8088/riskService/atreus/riskDecision/qclszqys";

    private static final String ZS_URL = "http://172.17.241.66:8088/riskService/atreus/riskDecision/qclszqzs";


    //预审
    @Override
    public String preliminaryValid(Long projectId, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        commonLogHead(hlsWsRequests, "预审", projectId, request);
        ResponseData responseData = new ResponseData();
        //项目id为空则预审失败
        if (projectId == null) {
            commonLog(responseData, "100001", "E", "项目id为空", hlsWsRequests);
            return "Error";
        }
        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则预审失败
        if (bpId == null) {
            commonLog(responseData, "100001", "E", "商业伙伴id为空", hlsWsRequests);
            return "Error";
        }
        //通过bpId获取商业伙伴信息
        QueryHlsBpMasterDTO queryHlsBpMasterDTO = hlsCusBpMasterMapper.getQueryHlsBpMasterDTOByBpId(bpId);
        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        //获取业务申请编号
        String businessApplyNo = hlsCusPrjProjectMapper.getBusinessApplyNoByProjectId(projectId);
        //业务流水号
        String businessNo = UUID.randomUUID().toString().replace("-", "");
        HashMap<String, String> param = new HashMap<>();
        //设置预审参数
        if (setPreliminaryParam(hlsWsRequests, responseData,
                queryHlsBpMasterDTO, parse, businessApplyNo, businessNo, param)) {
            return "Error";
        }
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(YS_URL, preliminaryTestMap(), header);
            if (200 != httpExecuteResponse.getResponseCode()) {
                commonLog(responseData, "100001", "E", "同盾接口请求失败", hlsWsRequests);
                return "Error";
            }
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = (JSONObject) resp.get("data");
            //如果是正常返回的data则不会有success字段
            if (data.getBoolean("success") != null) {
                commonLog(responseData, "100001", "E", "参数异常", hlsWsRequests);
                return "Error";
            }
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "E", "同盾预审失败", hlsWsRequests);
                return "Reject";
            }
        } catch (Exception e) {
            commonLog(responseData, "100001", "E", "请求同盾接口异常", hlsWsRequests);
            return "Error";
        }
        return "Accept";
    }

    //正审
    @Override
    public String interlocutoryValid(Long projectId, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        commonLogHead(hlsWsRequests, "正审", projectId, request);
        ResponseData responseData = new ResponseData();
        String jsonString = hlsCusPrjProjectMapper.getRiskInfoByProjectId(projectId);
        JSONObject param = JSONObject.parseObject(jsonString);
        //设置正审参数
        String error = setInterlocutoryParam(projectId, hlsWsRequests, responseData, param);
        if (error != null) return error;
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            Map<String, String> map = JSONObject.toJavaObject(param, Map.class);
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(ZS_URL, interlocutoryTestMap(), header);
            if (200 != httpExecuteResponse.getResponseCode()) {
                commonLog(responseData, "100001", "E", "同盾接口请求失败", hlsWsRequests);
                return "Error";
            }
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = (JSONObject) resp.get("data");
            //如果是正常返回的data则不会有success字段
            if (data.getBoolean("success") != null) {
                commonLog(responseData, "100001", "E", "参数异常", hlsWsRequests);
                return "Error";
            }
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "E", "同盾正审失败", hlsWsRequests);
                return "Reject";
            }
            if ("Review".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "S", "同盾正审成功但是有风险", hlsWsRequests);
                return "Review";
            }
        } catch (Exception e) {
            commonLog(responseData, "100001", "E", "请求同盾接口异常", hlsWsRequests);
            return "Error";
        }
        return "Accept";
    }

    private String setInterlocutoryParam(Long projectId, HlsWsRequests hlsWsRequests, ResponseData responseData, JSONObject param) {
        //项目id为空则预审失败
        if (projectId == null) {
            commonLog(responseData, "100001", "E", "项目id为空", hlsWsRequests);
            return "Error";
        }

        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则预审失败
        if (bpId == null) {
            commonLog(responseData, "100001", "E", "商业伙伴id为空", hlsWsRequests);
            return "Error";
        }
        //通过bpId获取商业伙伴信息
        QueryHlsBpMasterDTO queryHlsBpMasterDTO = hlsCusBpMasterMapper.getQueryHlsBpMasterDTOByBpId(bpId);
        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        //获取业务申请编号
        String businessApplyNo = hlsCusPrjProjectMapper.getBusinessApplyNoByProjectId(projectId);
        //业务流水号
        String businessNo = UUID.randomUUID().toString().replace("-", "");
//        HashMap<String, String> param = new HashMap<>();
        param.put("bizid", businessNo);
        //客户编号
        param.put("custno", queryHlsBpMasterDTO.getBpCode());
        //策略编码
        param.put("policycode", "CarPre_zs");
        //渠道标识
        param.put("appcode", "qclszl");
        //机构编号
        param.put("orgcode", "GtZl");
        //运行模式
        param.put("runtype", "1");
        //产品编号 目前易靓为XC
        param.put("productcode", "XC");
        //目前客户类型只能接受个人  个人：1 企业：2 同业：3
        param.put("custtype", "1");
        //业务发生时间
        param.put("biztime", parse.format(new Date()));
        //来源渠道 目前固定为yilaing
        param.put("sourcechannel", "yiliang");
        //业务申请编号
        param.put("bappcode", businessApplyNo);
        //申请日期
        param.put("sqri", parse.format(new Date()));
        //业务类型  目前易靓固定为NEW_CAR  CAR_LOAN_OFFSET: 车抵贷 SECOND_HAND_CAR: 二手车，NEW_CAR: 新车
        param.put("svtyp", "NEW_CAR");
        //客户名称
        param.put("custname", queryHlsBpMasterDTO.getBpName());
        //客户证件类型 目前只支持ID_CARD身份证
        if (!"ID_CARD".equals(queryHlsBpMasterDTO.getIdCardNo())) {
            commonLog(responseData, "100001", "E", "证件类型暂时不支持", hlsWsRequests);
            return "Error";
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());
        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());
        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());

        //证件签发日期
        if (queryHlsBpMasterDTO.getIdIssueDate() == null) {
            commonLog(responseData, "100001", "E", "证件签发日期为空", hlsWsRequests);
            return "Error";
        }
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));

        if (queryHlsBpMasterDTO.getIdExpirationDate() == null) {
            commonLog(responseData, "100001", "E", "证件到期日期为空", hlsWsRequests);
            return "Error";
        }
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));

        //报价信息
        QueryPrjQuotationDTO quotationInfo = hlsCusPrjQuotationMapper.
                getQueryPrjQuotationDTOByProjectId(projectId);
        //逾期信息
        QueryLateInfo queryLateInfo = hlsCusPrjQuotationMapper.getQueryLateInfoByQuotationId(quotationInfo.getQuotationId());
        //从调用接口开始到一年前逾期4-30天次数
        param.put("last1yearM1count", String.valueOf(queryLateInfo.getFourToThirtyDaysOverdueCount()));
        //从调用接口开始到一年前逾期31-60天次数
        param.put("last1yearM2count", String.valueOf(queryLateInfo.getThirtyOneToSixtyDaysOverdueCount()));
        //从调用接口开始到一年前有多少起租日
        param.put("last1YearCount", String.valueOf(queryLateInfo.getLeaseStartDateCount()));
        return null;
    }


    private boolean setPreliminaryParam(HlsWsRequests hlsWsRequests, ResponseData responseData, QueryHlsBpMasterDTO queryHlsBpMasterDTO, SimpleDateFormat parse, String businessApplyNo, String businessNo, HashMap<String, String> param) {
        param.put("bizid", businessNo);
        //客户编号
        param.put("custno", queryHlsBpMasterDTO.getBpCode());
        //策略编码
        param.put("policycode", "CarPre_ys");
        //渠道标识
        param.put("appcode", "qclszl");
        //机构编号
        param.put("orgcode", "GtZl");
        //运行模式
        param.put("runtype", "1");
        //产品编号 目前易靓为XC
        param.put("productcode", "XC");
        //目前客户类型只能接受个人  个人：1 企业：2 同业：3
        param.put("custtype", "1");
        //业务发生时间
        param.put("biztime", parse.format(new Date()));
        //来源渠道 目前固定为yilaing
        param.put("sourcechannel", "yiliang");
        //业务申请编号
        param.put("bappcode", businessApplyNo);
        //申请日期
        param.put("sqri", parse.format(new Date()));
        //业务类型  目前易靓固定为NEW_CAR  CAR_LOAN_OFFSET: 车抵贷 SECOND_HAND_CAR: 二手车，NEW_CAR: 新车
        param.put("svtyp", "NEW_CAR");
        //客户名称
        param.put("custname", queryHlsBpMasterDTO.getBpName());

        //客户证件类型 目前只支持ID_CARD身份证
        if (!"ID_CARD".equals(queryHlsBpMasterDTO.getIdCardNo())) {
            commonLog(responseData, "100001", "E", "证件类型暂时不支持", hlsWsRequests);
            return true;
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());

        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());

        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());

        //证件签发日期
        if (queryHlsBpMasterDTO.getIdIssueDate() == null) {
            commonLog(responseData, "100001", "E", "证件签发日期为空", hlsWsRequests);
            return true;
        }
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));

        if (queryHlsBpMasterDTO.getIdExpirationDate() == null) {
            commonLog(responseData, "100001", "E", "证件到期日期为空", hlsWsRequests);
            return true;
        }
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));
        return false;
    }

    private void commonLog(ResponseData responseData, String code, String returnStatus, String parameter, HlsWsRequests hlsWsRequests) {
        responseData.setCode(code);
        responseData.setMessage(parameter);
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
    }

    private void commonLogHead(HlsWsRequests hlsWsRequests, String functionName, Object param, HttpServletRequest request) {
        //获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
        //功能名称
        hlsWsRequests.setFunctionName(functionName);
        //状态变更日期
        hlsWsRequests.setStatusDate(new Date());
        // user_id
        String userId = request.getParameter("user_id");
        if (userId != null) {
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
        //请求状态
        hlsWsRequests.setStatusCode("200");
        //参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(param);
        hlsWsRequests.setRequestJson(s);
    }

    //预审测试
    private HashMap<String, String> preliminaryTestMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("bizid", "2024071001");
        //error
//        map.put("bizid", "2024071002");
        map.put("custno", "1001");
        //error
//        map.put("custno", "1002");
        map.put("policycode", "CarPre_ys");
        map.put("appcode", "qclszl");
        map.put("orgcode", "GtZl");
        map.put("runtype", "1");
        map.put("productcode", "XC");
        map.put("custtype", "1");
        map.put("biztime", "2024-06-24 10:56:31");
        map.put("sourcechannel", "yiliang");
        map.put("bappcode", "2024071001");
        //error
//        map.put("bappcode", "2024071002");
        map.put("sqri", "2024-06-24 10:56:31");
        map.put("svtyp", "NEW_CAR");
        map.put("custname", "孙红艳");
        map.put("certtype", "ID_CARD");
        map.put("certid", "320324198810245180");
        map.put("mobino", "18066082965");
        map.put("idissue", "2024-01-01 00:00:00");
        map.put("idexp", "2034-01-01 00:00:00");
        //error
//        map.put("idexp", "2024-01-01 00:00:00");

        return map;
    }

    //正审测试
    private HashMap<String, String> interlocutoryTestMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("bizid", "2024071003");
        map.put("custno", "1003");
        map.put("policycode", "CarPre_zs");
        map.put("appcode", "qclszl");
        map.put("orgcode", "GtZl");
        map.put("runtype", "1");
        map.put("productcode", "XC");
        map.put("custtype", "1");
        map.put("biztime", "2024-06-24 10:56:31");
        map.put("sourcechannel", "yiliang");
        map.put("bappcode", "2024071003");
        map.put("sqri", "2024-06-24 10:56:31");
        map.put("svtyp", "NEW_CAR");
        map.put("custname", "孙红艳");
        map.put("certtype", "ID_CARD");
        map.put("certid", "320324198810245180");
        map.put("mobino", "18066082965");
        map.put("idissue", "2024-01-01 00:00:00");
        map.put("idexp", "2034-01-01 00:00:00");
        map.put("sex", "1");
        map.put("nation", "01");
        map.put("birthdate", "2000-01-01 00:00:00");
        map.put("age", "24");
        map.put("nationality", "中国");
        map.put("domicileshen", "370000");
        map.put("domicileshi", "370100");
        map.put("domicilequ", "370102");
        map.put("islocaldomicile", "1");
        map.put("homeaddressprovince", "370000");
        map.put("homeaddresspcity", "370100");
        map.put("jzdzqx", "370102");
        map.put("housetype", "8");
        map.put("marriage", "10");
        map.put("childnum", "没有");
        map.put("isdriverlicence", "1");
        map.put("driverlicencetype", "C1");
        map.put("driverstatus", "A");
        map.put("jzjzrq", "2026-01-01 00:00:00");
        map.put("diploma", "20");
        map.put("industry", "D");
        map.put("occu", "0");
        map.put("position", "4");
        map.put("companyshen", "370000");
        map.put("companyshi", "370100");
        map.put("companyqu", "370102");
        map.put("dealername", "测试");
        map.put("dealerprovince", "370000");
        map.put("dealercity", "370100");
        map.put("dealerqu", "370102");
        map.put("carbrand2", "大众");
        map.put("cartype", "2023款 200万辆纪念版 280TSI DSG舒适型");
        map.put("chexi", "迈腾");
        map.put("carcolor", "黑");
        map.put("carzkcount", "7");
        map.put("sfjk", "国产");
        map.put("carno", "LSJA36U66PN332550");
        map.put("rllx", "01");
        map.put("cardateofproduction", "2024-01-01  0:00:00");
        map.put("carnatureofuse", "2");
        map.put("registeredcity", "370101");
        map.put("sfazgps", "1");
        map.put("financingamount", "100000");
        map.put("sfje", "10000");
        map.put("clxsjg", "80000");
        map.put("cfpp", "80000");
        map.put("paymentratio", "0.1");
        map.put("ctac", "80000");
        map.put("yhke", "1875");
        map.put("shenqingqixain", "48");
        return map;
    }


}
