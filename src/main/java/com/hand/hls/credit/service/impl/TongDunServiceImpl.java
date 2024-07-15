package com.hand.hls.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.mapper.HlsBpMasterBusinessConditionMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.credit.dto.QueryLateInfo;
import com.hand.hls.credit.dto.QueryPrjQuotationDTO;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.credit.dto.QueryHlsBpMasterDTO;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.mapper.ProjectLeaseItemSalesMapper;
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
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;


    @Autowired
    private ProjectLeaseItemSalesMapper projectLeaseItemSalesMapper;

    @Autowired
    private HlsBpMasterBusinessConditionMapper hlsBpMasterBusinessConditionMapper;

    //预审
    @Override
    public boolean preliminaryValid(Long projectId, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        //获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
        //功能名称
        hlsWsRequests.setFunctionName("预审");
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
        String s = JSONObject.toJSONString(projectId);
        hlsWsRequests.setRequestJson(s);

        ResponseData responseData = new ResponseData();
        //项目id为空则预审失败
        if (projectId == null) {
            commonLog(responseData, "100001", "项目id为空", hlsWsRequests);
            return false;
        }

        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则预审失败
        if (bpId == null) {
            commonLog(responseData, "100001", "商业伙伴id为空", hlsWsRequests);
            return false;
        }
        //通过bpId获取商业伙伴信息
        QueryHlsBpMasterDTO queryHlsBpMasterDTO = hlsCusBpMasterMapper.getQueryHlsBpMasterDTOByBpId(bpId);
        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        //获取业务申请编号
        String businessApplyNo = hlsCusPrjProjectMapper.getBusinessApplyNoByProjectId(projectId);
        //业务流水号
        String businessNo = UUID.randomUUID().toString().replace("-", "");
        HashMap<String, String> param = new HashMap<>();
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
            commonLog(responseData, "100001", "证件类型暂时不支持", hlsWsRequests);
            return false;
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());
        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());
        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());
        //证件签发日期
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));
        /*
            测试IP:http://172.17.241.66:8088/
            准生产IP:http://172.17.241.69:8088/
            生产IP:http://172.17.241.12:8088/
         */
        String url = "http://172.17.241.66:8088/riskService/atreus/riskDecision/qclszqsp";
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(url, param, header);
//            if (200 != httpExecuteResponse.getResponseCode()) {
//                commonLog(responseData, "100001", "同盾预审失败", hlsWsRequests);
//                return false;
//            }
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = JSONObject.parseObject((String) resp.get("data"));
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "同盾预审失败", hlsWsRequests);
                return false;
            }
        } catch (Exception e) {
            commonLog(responseData, "100001", "请求同盾接口异常", hlsWsRequests);
            return false;
        }
        return true;
    }

    private void commonLog(ResponseData responseData, String code, String parameter, HlsWsRequests hlsWsRequests) {
        responseData.setCode(code);
        responseData.setMessage(parameter);
        hlsWsRequests.setReturnStatus("E");
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
    }


    //正审
    @Override
    public String interlocutoryValid(Long projectId, String jsonString, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        //获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
        //功能名称
        hlsWsRequests.setFunctionName("正审");
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
        String s = JSONObject.toJSONString(projectId + jsonString);
        hlsWsRequests.setRequestJson(s);
        ResponseData responseData = new ResponseData();
        JSONObject param = JSONObject.parseObject(jsonString);
        //清除多余字段
        //clear(param);
        //项目id为空则预审失败
        if (projectId == null) {
            commonLog(responseData, "100001", "项目id为空", hlsWsRequests);
            return "Reject";
        }

        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则预审失败
        if (bpId == null) {
            commonLog(responseData, "100001", "商业伙伴id为空", hlsWsRequests);
            return "Reject";
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
            commonLog(responseData, "100001", "证件类型暂时不支持", hlsWsRequests);
            return "Reject";
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());
        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());
        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());
        //证件签发日期
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));
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

         /*
            测试IP:http://172.17.241.66:8088/
            准生产IP:http://172.17.241.69:8088/
            生产IP:http://172.17.241.12:8088/
         */
        String url = "http://172.17.241.66:8088/riskService/atreus/riskDecision/qclszqsp";
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            Map<String, String> map = JSONObject.toJavaObject(param, Map.class);
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(url, map, header);
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = JSONObject.parseObject((String) resp.get("data"));
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "同盾正审失败", hlsWsRequests);
                return "Reject";
            }
            if ("Review".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "同盾正审成功但是有风险", hlsWsRequests);
                return "Review";
            }
        } catch (Exception e) {
            commonLog(responseData, "100001", "请求同盾接口异常", hlsWsRequests);
            return "Reject";
        }
        return "Accept";
    }
    private void clear(JSONObject param) {
        param.remove("orderNo");
        param.remove("sellerCode");
        param.remove("sellerName");
        param.remove("licensePlateOwnerCode");
        param.remove("licensePlateOwnerName");
        param.remove("mortgagorCode");
        param.remove("mortgagorName");
        param.remove("licensePlateCityCode");
        param.remove("licensePlateCityName");
        param.remove("mortgageCityCode");
        param.remove("mortgageCityName");
        param.remove("salesCityCode");
        param.remove("salesCityName");
        param.remove("brandCode");
        param.remove("brandName");
        param.remove("seriesCode");
        param.remove("seriesName");
        param.remove("modelCode");
        param.remove("modelName");
        param.remove("vin");
        param.remove("color");
        param.remove("carProductionDate");
        param.remove("engineNumber");
        param.remove("mandatoryInsuranceAmount");
        param.remove("commercialInsuranceType");
        param.remove("termCount");
        param.remove("monthPayment");
        param.remove("rate");
        param.remove("firstPayment");
        param.remove("carGuidePrice");
        param.remove("carSalePrice");
        param.remove("applyLoanAmount");
        param.remove("carRestPrice");
        param.remove("plusFinanceAmount");
        param.remove("startRentDate");
    }

}
