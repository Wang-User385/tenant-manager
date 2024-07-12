package com.hand.hls.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.credit.dto.QueryPrjProjectLeaseItemDTO;
import com.hand.hls.credit.dto.QueryPrjQuotationDTO;
import com.hand.hls.credit.dto.QueryProjectLeaseItemSalesDTO;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.credit.dto.QueryHlsBpMasterDTO;
import com.hand.hls.prj.dto.PrjProjectLeaseItemSales;
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
        hlsWsRequests.setFunctionName("关单");
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
        //目前易靓为XC
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
        String url = "http://172.17.241.66:8088/qclszqsp";
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
    public String interlocutoryValid(Long projectId, HttpServletRequest request) {
        //保存日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        //获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
        //功能名称
        hlsWsRequests.setFunctionName("关单");
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
        //目前易靓为XC
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
        //性别
        param.put("sex", queryHlsBpMasterDTO.getGender());
        //民族
        param.put("nation", queryHlsBpMasterDTO.getEthnicity());
        //出生日期
        param.put("birthdate", queryHlsBpMasterDTO.getDateOfBirth());
        //年龄
        param.put("age", String.valueOf(queryHlsBpMasterDTO.getAge()));
        //国籍
        param.put("nationality", queryHlsBpMasterDTO.getNationality());
        //户籍所属省份
        param.put("domicileshen", queryHlsBpMasterDTO.getDomicileProvince());
        //户籍所属市
        param.put("domicileshi", queryHlsBpMasterDTO.getDomicileCity());
        //居住地址省
        param.put("homeaddressprovince", queryHlsBpMasterDTO.getHouseProvince());
        //居住地址市
        param.put("homeaddresspcity", queryHlsBpMasterDTO.getHouseCity());
        //居住地址
        param.put("homeaddress", queryHlsBpMasterDTO.getHouseAddress());
        //有无驾照
        param.put("isdriverlicence", queryHlsBpMasterDTO.getDriverLicenseFlag());
        //驾照类型
        param.put("driverlicencetype", queryHlsBpMasterDTO.getDriverLicenseType());
        //驾照截止日期
        param.put("jzjzrq", parse.format(queryHlsBpMasterDTO.getDriverLicenseDeadline()));
        //公司所属省份
        param.put("companyshen", queryHlsBpMasterDTO.getCompanyProvince());
        //公司所属市
        param.put("companyshi", queryHlsBpMasterDTO.getCompanyCity());

        //获取车辆信息
        QueryPrjProjectLeaseItemDTO carInfo = hlsCusPrjProjectLeaseItemMapper.
                getQueryPrjProjectLeaseItemDTOByProjectId(projectId);
        //车辆品牌
        param.put("carbrand2",carInfo.getBrandC());
        //车系
        param.put("chexi",carInfo.getSeriesC());
        //车辆准载（定员）
        param.put("carzkcount",carInfo.getVehicleCapacity());
        //上牌城市
        param.put("registeredcity",carInfo.getCityCode());
        //融资金额
        param.put("financingamount",String.valueOf(carInfo.getFinanceAmount()));
        //车辆厂商指导价格
        param.put("cfpp",String.valueOf(carInfo.getListPrice()));

        //报价信息
        QueryPrjQuotationDTO quotationInfo = hlsCusPrjQuotationMapper.
                getQueryPrjQuotationDTOByProjectId(projectId);
        //首付比例
        param.put("paymentratio",String.valueOf(quotationInfo.getDownPaymentRatio()));
        //月还款额
        param.put("yhke",String.valueOf(quotationInfo.getPmt()));
        //车辆厂商指导价格
        param.put("shenqingqixain",String.valueOf(quotationInfo.getLeaseTimes()));

        //经销商信息
        QueryProjectLeaseItemSalesDTO salesInfo =  projectLeaseItemSalesMapper.
                getQueryProjectLeaseItemSalesDTOProjectLeaseItemId(carInfo.getProjectLeaseItemId());
        //经销商所在省份
        param.put("dealerprovince",String.valueOf(salesInfo.getProvinceId()));
        //经销商所在城市
        param.put("dealercity",String.valueOf(salesInfo.getCityId()));


        /*
            测试IP:http://172.17.241.66:8088/
            准生产IP:http://172.17.241.69:8088/
            生产IP:http://172.17.241.12:8088/
         */
        String url = "http://172.17.241.66:8088/qclszqsp";
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(url, param, header);
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = JSONObject.parseObject((String) resp.get("data"));
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                commonLog(responseData, "100001", "同盾正审失败", hlsWsRequests);
                return "Reject";
            }
            if ("Review".equals(data.getString("finalDecisionCode"))){
                commonLog(responseData, "100001", "同盾正审成功但是有风险", hlsWsRequests);
                return "Review";
            }
        } catch (Exception e) {
            commonLog(responseData, "100001", "请求同盾接口异常", hlsWsRequests);
            return "Reject";
        }


        return "Accept";
    }


}
