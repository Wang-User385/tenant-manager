package com.hand.hls.credit.service.impl;

import cfca.paperless.base.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.credit.dto.QueryLateInfo;
import com.hand.hls.credit.dto.QueryPrjQuotationDTO;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.credit.dto.QueryHlsBpMasterDTO;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.PrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;


    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    /*
            测试IP:http://172.17.241.66:8088/
            准生产IP:http://172.17.241.69:8088/
            生产IP:http://172.17.241.12:8088/
     */

    @Value("${tongdun.ys}")
    private String YS_URL;

    @Value("${tongdun.zs}")
    private String ZS_URL;


    //预审
    @Override
    public String preliminaryValid(Long projectId, HttpServletRequest request) throws HlsCusException {
        //同盾控制开关
        if (!"Y".equals(hlsCusConContractMapper.getTongDunFlag())) {
            return "Accept";
        }
        ResponseData responseData = new ResponseData();
        //项目id为空则预审失败
        if (projectId == null) {
            throw new HlsCusException(getReturnJson("100001", "项目id为空"));
        }
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectPrjById(projectId);
        //不为空且不为新建则该订单已经结束
        if ("APPROVED".equals(hlsCusPrjProject.getPreStatus())) {
            throw new HlsCusException(getReturnJson("100101", "该项目预审已通过，无需重复提交"));
        }
        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则预审失败
        if (bpId == null) {
            throw new HlsCusException(getReturnJson("100001", "商业伙伴id为空"));
        }

        //校验文件类型是《个人信息采集及使用授权协议》的附件是否已经上传
        Integer attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "JY_FQ_XXCJSYSQ", "PRJ_PROJECT_ATTACHMENT", "PRE_EXAMINE");
        if (attachMulti == 0) {
            throw new HlsCusException(getReturnJson("100001", "该进件项目的《个人信息采集及使用授权协议》附件未上传！"));
        }

        //同盾接口请求暂时注释
        //通过bpId获取商业伙伴信息
        QueryHlsBpMasterDTO queryHlsBpMasterDTO = hlsCusBpMasterMapper.getQueryHlsBpMasterDTOByBpId(bpId);

        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        //获取业务申请编号
        String businessApplyNo = hlsCusPrjProjectMapper.getBusinessApplyNoByProjectId(projectId);
        //业务流水号
        String businessNo = UUID.randomUUID().toString().replace("-", "");
        HashMap<String, String> param = new HashMap<>();
        //设置预审参数
        if (setPreliminaryParam(queryHlsBpMasterDTO, parse, businessApplyNo, businessNo, param)) {
            return "Error";
        }
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        String info = "请求同盾接口异常";
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.getSinglePrjProjectByProjectId(projectId);
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        try {
            commonLogHead(hlsWsRequests, "GT_YS_参数详情", param, request);
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(YS_URL, param, header);
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = (JSONObject) resp.get("data");
            info = data.toString();
            if (200 != httpExecuteResponse.getResponseCode()) {
                responseDataPrivate(responseData, false, "400", data.toString(), "E", hlsWsRequests);
                throw new HlsCusException(getReturnJson("100001", data.toString()));
            }

            //如果是正常返回的data则不会有success字段
            if (data.getBoolean("success") != null) {
                throw new HlsCusException(getReturnJson("100001", "同盾接口返回参数异常"));
            }
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                prjProject.setPreStatus("Reject");
                responseDataPrivate(responseData, true, "200", data.toString(), "S", hlsWsRequests);
                return "Reject";
            }
        } catch (Exception e) {
            responseDataPrivate(responseData, false, "400", info, "E", hlsWsRequests);
            throw new HlsCusException(getReturnJson("100001", info));
        }
        hlsCusPrjProject.setPreStatus("APPROVED");
        hlsCusPrjProjectMapper.updateByPrimaryKey(hlsCusPrjProject);
        responseDataPrivate(responseData, true, "200", info, "S", hlsWsRequests);
        return "Accept";
    }

    //正审
    @Override
    public String interlocutoryValid(Long projectId, HttpServletRequest request, long nowTime, long creationTime, long riskDays, String projectStatus) throws HlsCusException {
        //同盾控制开关
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.getSinglePrjProjectByProjectId(projectId);
        if (!"Y".equals(hlsCusConContractMapper.getTongDunFlag())) {
            prjProject.setConfirmStatus("APPROVED");
            prjProject.setLastUpdateDate(new Date());
            hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
            return "Accept";
        }
        ResponseData responseData = new ResponseData();
        String jsonString = hlsCusPrjProjectMapper.getRiskInfoByProjectId(projectId);
        //申请风控审核时，校验riskInfo是否为空，空则报错
        if (StringUtil.isEmpty(jsonString)) {
            throw new HlsCusException(getReturnJson("100001", "该进件项目的riskinfo信息为空"));
        }
        JSONObject param = JSONObject.parseObject(jsonString);
        param.remove("dealerid");
        param.remove("dealername");

        if (!prjProject.getPreStatus().equals("APPROVED")) {
            throw new HlsCusException(getReturnJson("100101", "该进件项目未通过预审，请先申请风控预审"));
        }
        if (projectStatus.equals("APPROVED")) {
            throw new HlsCusException(getReturnJson("100101", "该进件项目已通过正审，无需重复提交"));
        }
        if (projectStatus.equals("APPROVING")) {
            throw new HlsCusException(getReturnJson("100101", "该进件项目已发起正审流程，无需重复提交"));
        }
        //计算下单成功到当前日期间隔的时间
        long days = (nowTime - creationTime) / (24 * 60 * 60 * 1000);
        if (days > riskDays) {
            throw new HlsCusException(getReturnJson("100101", "该进件项目已超时，无法发起风控审核"));
        }

        //申请风控审核时，校验（承租人身份证、驾驶证）的附件是否已经上传
        Integer SfzAttachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CZR_SFZ", "PRJ_PROJECT_ATTACHMENT", "EXAMINE");
        if (SfzAttachMulti == 0) {
            throw new HlsCusException(getReturnJson("100001", "该进件项目的《承租人身份证》附件未上传！"));
        }
        Integer JszAttachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CZR_JSZ", "PRJ_PROJECT_ATTACHMENT", "EXAMINE");
        if (JszAttachMulti == 0) {
            throw new HlsCusException(getReturnJson("100001", "该进件项目的《承租人驾驶证》附件未上传！"));
        }

        //同盾接口不通，暂时注释掉
        //设置正审参数
        String info = setInterlocutoryParam(projectId, param);
        if (info != null) return info;
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        String errorInfo = "同盾接口请求异常";
        try {
            commonLogHead(hlsWsRequests, "GT_ZS_参数详情", param, request);
            Map<String, String> map = new HashMap<>();
            param.forEach((k, v) -> {
                if (v instanceof BigDecimal) {
                    map.put(k, v.toString());
                } else {
                    map.put(k, (String) v);
                }
            });
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(ZS_URL, map, header);
            if (200 != httpExecuteResponse.getResponseCode()) {
                throw new HlsCusException(getReturnJson("100001", "同盾接口请求失败"));
            }
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = (JSONObject) resp.get("data");
            errorInfo = data.toString();
            //如果是正常返回的data则不会有success字段
            if (data.getBoolean("success") != null) {
                responseDataPrivate(responseData, false, "400", data.toString(), "E", hlsWsRequests);
                throw new HlsCusException(getReturnJson("100001", data.toString()));
            }
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                prjProject.setConfirmStatus("REJECTED");
                prjProject.setLastUpdateDate(new Date());
                hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
                responseDataPrivate(responseData, true, "200", data.toString(), "S", hlsWsRequests);
                return "Reject";
            }
            if ("Review".equals(data.getString("finalDecisionCode"))) {
                //同盾接口如果返回谨慎通过，就发起进件正审流程
                prjProject.setConfirmStatus("CAREFUL_APPROVED");
                prjProject.setLastUpdateDate(new Date());
                hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
                responseDataPrivate(responseData, true, "200", data.toString(), "S", hlsWsRequests);
                return "Review";
            }
            if ("Accept".equals(data.getString("finalDecisionCode"))) {
                //同盾接口如果返回通过，就发起进件正审流程
                prjProject.setConfirmStatus("APPROVED");
                prjProject.setLastUpdateDate(new Date());
                hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
                responseDataPrivate(responseData, true, "200", data.toString(), "S", hlsWsRequests);
                return "Accept";
            }
        } catch (Exception e) {
            responseDataPrivate(responseData, false, "400", errorInfo, "E", hlsWsRequests);
            throw new HlsCusException(getReturnJson("100001", errorInfo));
        }
        return "Error";
    }

    private String setInterlocutoryParam(Long projectId, JSONObject param) throws HlsCusException {
        JSONObject returnJson = new JSONObject();
        //项目id为空则正审失败
        if (projectId == null) {
            throw new HlsCusException(getReturnJson("100001", "项目id为空"));
        }

        String projectStatus = hlsCusPrjProjectMapper.getProjectStatusByProjectId(projectId);
        if (!StringUtil.isEmpty(projectStatus) && !"NEW".equals(projectStatus)) {
            return "Repeat";
        }
        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则正审失败
        if (bpId == null) {
            throw new HlsCusException(getReturnJson("100001", "商业伙伴id为空"));
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
        if (!"ID_CARD".equals(queryHlsBpMasterDTO.getIdType())) {
            throw new HlsCusException(getReturnJson("100001", "证件类型暂时不支持！"));
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());
        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());
        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());

        //证件签发日期
        if (queryHlsBpMasterDTO.getIdIssueDate() == null) {
            throw new HlsCusException(getReturnJson("100001", "证件签发日期为空！"));
        }
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));

        if (queryHlsBpMasterDTO.getIdExpirationDate() == null) {
            throw new HlsCusException(getReturnJson("100001", "证件到期日期为空！"));
        }
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));
        //逾期信息
        Map<String, String> queryLateInfo = hlsCusConContractMapper.getQueryLateInfoByProjectId(bpId);
        //从调用接口开始到一年前逾期4-30天次数
        param.put("last1yearM1count", queryLateInfo.get("last1yearM1count"));
        //从调用接口开始到一年前逾期31-60天次数
        param.put("last1yearM2count", queryLateInfo.get("last1yearM2count"));
        //从调用接口开始到一年前有多少起租日
        param.put("last1YearCount", queryLateInfo.get("last1YearCount"));
        return null;
    }


    private boolean setPreliminaryParam(QueryHlsBpMasterDTO queryHlsBpMasterDTO, SimpleDateFormat parse, String businessApplyNo, String businessNo, HashMap<String, String> param) throws HlsCusException {
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
        if (!"ID_CARD".equals(queryHlsBpMasterDTO.getIdType())) {
            throw new HlsCusException(getReturnJson("100001", "证件类型暂时不支持！"));
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());

        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());

        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());

        //证件签发日期
        if (queryHlsBpMasterDTO.getIdIssueDate() == null) {
            throw new HlsCusException(getReturnJson("100001", "证件签发日期为空！"));
        }
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));

        if (queryHlsBpMasterDTO.getIdExpirationDate() == null) {
            throw new HlsCusException(getReturnJson("100001", "证件到期日期为空！"));
        }
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));
        return false;
    }

    private void responseDataPrivate(ResponseData responseData, boolean flag, String code, String info, String logFlag, HlsWsRequests hlsWsRequests) {
        responseData.setCode(code);
        responseData.setSuccess(flag);
        responseData.setMessage(info);
        commonLog(responseData, logFlag, hlsWsRequests);
    }

    private String getReturnJson(String code, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }


    private void commonLog(ResponseData responseData, String returnStatus, HlsWsRequests hlsWsRequests) {
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
    }

    private void commonLogHead(HlsWsRequests hlsWsRequests, String functionName, Object param, HttpServletRequest request) {
        //获取请求路径
//        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl("");
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
            hlsWsRequests.setCreatedBy(Long.valueOf(userId));
        }

        //请求状态
        hlsWsRequests.setStatusCode("200");
        //参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(param);
        hlsWsRequests.setRequestJson(s);
    }

}
