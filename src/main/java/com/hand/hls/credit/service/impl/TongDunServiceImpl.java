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
    private  String YS_URL;

    @Value("${tongdun.zs}")
    private  String ZS_URL;


    //预审
    @Override
    public String preliminaryValid(Long projectId, HttpServletRequest request) throws HlsCusException {
        JSONObject returnJson = new JSONObject();
        //项目id为空则预审失败
        if (projectId == null) {
            throw new HlsCusException(getReturnJson("100001","项目id为空"));
        }
        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectPrjById(projectId);
        //不为空且不为新建则该订单已经结束
        if ("APPROVED".equals(hlsCusPrjProject.getPreStatus())){
            throw new HlsCusException(getReturnJson("100101","该项目预审已通过，无需重复提交"));
        }
        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则预审失败
        if (bpId == null) {
            throw new HlsCusException(getReturnJson("100001","商业伙伴id为空"));
        }

        //校验文件类型是《个人信息采集及使用授权协议》的附件是否已经上传
        Integer attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "JY_FQ_XXCJSYSQ","PRJ_PROJECT_ATTACHMENT", "PRE_EXAMINE");
        if(attachMulti == 0){
            throw new HlsCusException(getReturnJson("100001","该进件项目的《个人信息采集及使用授权协议》附件未上传！"));
        }
        /*
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

        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.getSinglePrjProjectByProjectId(projectId);
        try {
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(YS_URL, param, header);
            if (200 != httpExecuteResponse.getResponseCode()) {
                throw new HlsCusException(getReturnJson("100001","同盾接口请求失败"));
            }
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = (JSONObject) resp.get("data");
            //如果是正常返回的data则不会有success字段
            if (data.getBoolean("success") != null) {
                throw new HlsCusException(getReturnJson("100001","同盾接口返回参数异常"));
            }
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                prjProject.setPreStatus("Reject");
                return "Reject";
            }
        } catch (Exception e) {
            throw new HlsCusException(getReturnJson("100001","请求同盾接口异常"));
        }
        hlsCusPrjProject.setPreStatus("APPROVED");
        hlsCusPrjProjectMapper.updateByPrimaryKey(hlsCusPrjProject);
         */
        return "Accept";
    }

    //正审
    @Override
    public String interlocutoryValid(Long projectId, HttpServletRequest request) throws HlsCusException {
        String jsonString = hlsCusPrjProjectMapper.getRiskInfoByProjectId(projectId);
        JSONObject param = JSONObject.parseObject(jsonString);
        if (param.containsKey("dealerid")){
            param.remove("dealerid");
        }
        if (param.containsKey("dealername")){
            param.remove("dealername");
        }
        //申请风控审核时，校验riskInfo是否为空，空则报错
        if(StringUtil.isEmpty(jsonString)){
            throw new HlsCusException(getReturnJson("100001","该进件项目的riskinfo信息为空"));
        }
        //申请风控审核时，校验（承租人身份证、驾驶证）的附件是否已经上传
        Integer SfzAttachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CZR_SFZ","PRJ_PROJECT_ATTACHMENT", "EXAMINE");
        if(SfzAttachMulti == 0){
            throw new HlsCusException(getReturnJson("100001","该进件项目的《承租人身份证》附件未上传！"));
        }
        Integer JszAttachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CZR_JSZ","PRJ_PROJECT_ATTACHMENT", "EXAMINE");
        if(JszAttachMulti == 0){
            throw new HlsCusException(getReturnJson("100001","该进件项目的《承租人驾驶证》附件未上传！"));
        }
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.getSinglePrjProjectByProjectId(projectId);
        if(!prjProject.getPreStatus().equals("APPROVED")){
            throw new HlsCusException(getReturnJson("100101","该进件项目未通过预审，请先申请风控预审"));
        }
        /*
        //同盾接口不通，暂时注释掉
        //设置正审参数
        String info = setInterlocutoryParam(projectId,param);
        if (info != null) return info;
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            Map<String, String> map = JSONObject.toJavaObject(param, Map.class);
            HttpExecuteResponse httpExecuteResponse = HttpClientUtils.doPost(ZS_URL, map, header);
            if (200 != httpExecuteResponse.getResponseCode()) {
                throw new HlsCusException(getReturnJson("100001","同盾接口请求失败"));
            }
            String responseAsString = httpExecuteResponse.getResponseAsString();
            JSONObject resp = JSONObject.parseObject(responseAsString);
            JSONObject data = (JSONObject) resp.get("data");
            //如果是正常返回的data则不会有success字段
            if (data.getBoolean("success") != null) {
                throw new HlsCusException(getReturnJson("100001","同盾接口返回参数异常！"));
            }
            if ("Reject".equals(data.getString("finalDecisionCode"))) {
                prjProject.setProjectStatus("REJECTED");
                prjProject.setLastUpdateDate(new Date());
                hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
                return "Reject";
            }
            if ("Review".equals(data.getString("finalDecisionCode"))) {
                //同盾接口如果返回谨慎通过，就发起进件正审流程
                //这里暂未写发起正审流程代码
                prjProject.setProjectStatus("APPROVING");
                prjProject.setLastUpdateDate(new Date());
                hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
                return "Review";
            }
        } catch (Exception e) {
            throw new HlsCusException(getReturnJson("100001","请求同盾接口异常！"));
        }
        prjProject.setProjectStatus("APPROVED");
        prjProject.setApprovedDate(new Date());
        hlsCusPrjProjectMapper.updateByPrimaryKey(prjProject);
         */
        return "Accept";
    }

    private String setInterlocutoryParam(Long projectId, JSONObject param) throws HlsCusException {
        JSONObject returnJson = new JSONObject();
        //项目id为空则正审失败
        if (projectId == null) {
            throw new HlsCusException(getReturnJson("100001","项目id为空"));
        }

        String projectStatus = hlsCusPrjProjectMapper.getProjectStatusByProjectId(projectId);
        if (!StringUtil.isEmpty(projectStatus)&&!"NEW".equals(projectStatus)){
            return "Repeat";
        }
        //获取商业伙伴id
        Long bpId = hlsCusPrjProjectBpMapper.getBpIdByProjectId(projectId);
        //bpId为空则正审失败
        if (bpId == null) {
            throw new HlsCusException(getReturnJson("100001","商业伙伴id为空"));
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
            throw new HlsCusException(getReturnJson("100001","证件类型暂时不支持！"));
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());
        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());
        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());

        //证件签发日期
        if (queryHlsBpMasterDTO.getIdIssueDate() == null) {
            throw new HlsCusException(getReturnJson("100001","证件签发日期为空！"));
        }
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));

        if (queryHlsBpMasterDTO.getIdExpirationDate() == null) {
            throw new HlsCusException(getReturnJson("100001","证件到期日期为空！"));
        }
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));
        //逾期信息
        Map<String,String> queryLateInfo = hlsCusConContractMapper.getQueryLateInfoByProjectId(projectId);
        //从调用接口开始到一年前逾期4-30天次数
        param.put("last1yearM1count", queryLateInfo.get("last1yearM1count"));
        //从调用接口开始到一年前逾期31-60天次数
        param.put("last1yearM2count", queryLateInfo.get("last1yearM2count"));
        //从调用接口开始到一年前有多少起租日
        param.put("last1YearCount", queryLateInfo.get("last1YearCount"));
        return null;
    }


    private boolean setPreliminaryParam( QueryHlsBpMasterDTO queryHlsBpMasterDTO, SimpleDateFormat parse, String businessApplyNo, String businessNo, HashMap<String, String> param) throws HlsCusException {
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
            throw new HlsCusException(getReturnJson("100001","证件类型暂时不支持！"));
        }
        param.put("certtype", queryHlsBpMasterDTO.getIdType());

        //客户证件号码
        param.put("certid", queryHlsBpMasterDTO.getIdCardNo());

        //手机号
        param.put("mobino", queryHlsBpMasterDTO.getPhone());

        //证件签发日期
        if (queryHlsBpMasterDTO.getIdIssueDate() == null) {
            throw new HlsCusException(getReturnJson("100001","证件签发日期为空！"));
        }
        param.put("idissue", parse.format(queryHlsBpMasterDTO.getIdIssueDate()));

        if (queryHlsBpMasterDTO.getIdExpirationDate() == null) {
            throw new HlsCusException(getReturnJson("100001","证件到期日期为空！"));
        }
        //证件到期日期
        param.put("idexp", parse.format(queryHlsBpMasterDTO.getIdExpirationDate()));
        return false;
    }

    private String getReturnJson(String code , String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("message",message);
        return jsonObject.toJSONString();
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
