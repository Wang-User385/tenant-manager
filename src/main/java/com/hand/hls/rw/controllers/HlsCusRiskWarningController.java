package com.hand.hls.rw.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.console.RequestHolder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cap.dto.HlsCusCapAccountMonthlyBalance;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;
import com.hand.hls.rw.service.HlsCusIRiskWarningInfoService;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import com.hand.hls.ty.dto.JcTianyanchaInterfaceInfo;
import com.hand.hls.ty.mapper.JcTianyanchaInterfaceInfoMapper;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class HlsCusRiskWarningController extends BaseController {

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

    @Autowired
    private JcTianyanchaInterfaceInfoMapper jcTianyanchaInterfaceInfoMapper;

    //回调接口，用来绑定时做校验
    @GetMapping(value="/hls/risk/notice")
    @ResponseBody
    public JSONObject validate(String verifyCode,String sign){
        String key = "6c0d7905-ba53-488a-8e62-50cead578779";
        JSONObject result = new JSONObject();
        result.put("verifyCode",verifyCode);
        result.put("sign",sign);
//        System.out.println(result.toString());
        return result;
    }

    @PostMapping(value="/hls/risk/notice",produces = {"text/html;charset=utf-8"})
    public String getNotice(HttpServletRequest request) throws IOException {
        String postData = new String(ByteStreams.toByteArray(request.getInputStream()),"utf-8");
        String sign = request.getParameter("sign");
        JSONObject jsonObject = JSONObject.parseObject(postData);
        Long reason = Long.valueOf(String.valueOf(jsonObject.get("total")));
        String type = String.valueOf(jsonObject.get("type"));
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        Long companyId = 0L;
        String companyName = null;
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
             companyName = newObject.get("companyName").toString();
             companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
        }
        JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
        //jcTianyanchaInterfaceInfo.setDocumentId();
        jcTianyanchaInterfaceInfo.setDocumentType("PLM_RISK_WARNING");
        jcTianyanchaInterfaceInfo.setData(postData);
        jcTianyanchaInterfaceInfo.setDocumentId(companyId);
        jcTianyanchaInterfaceInfo.setCompanyName(companyName);
        //未检索
        jcTianyanchaInterfaceInfo.setRetrievalState("UNRETRIEVAL");

        jcTianyanchaInterfaceInfo.setUsgDate(new Date());
        jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
        IRequest requestContext = createRequestContext(request);
        HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
        //this.service.updateRiskWarningBySky(requestContext,riskWarning,postData);

        return "ok";
    }


    @RequestMapping(value = "/hls/risk/save/success")
    @ResponseBody
    public ResponseData saveWarning(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,Long bpId, String riskType,HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusRiskWarning> warnings = new  ArrayList<HlsCusRiskWarning>();
        if (bpId != null) {
            //预警主表信息插入
            HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
            riskWarning.setBpId(bpId);
            Map<String, String> params1 = new HashMap<String, String>();
            String riskWarningNumber = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, "RISK_WARNING", "RISK_WARNING", "RISK_WARNING", params1);
            riskWarning.setRiskWarningNumber(riskWarningNumber);
            riskWarning.setApplyDate(new Date());
            riskWarning.setStatus("NEW");
            riskWarning.setRiskType(riskType);
            riskWarning.setBusinessType("RISK_WARNING");
            riskWarning.setDocumentCategory("RISK_WARNING");
            riskWarning.setDocumentType("RISK_WARNING");
            riskWarning.setCompanyId(requestCtx.getCompanyId());
            service.insertSelective(requestCtx,riskWarning);
            //预警明细
            HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
            riskWarningInfo.setBpId(bpId);
            List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(requestCtx,riskWarningInfo);
            if(riskWarningInfos.size() > 0){
                for(HlsCusRiskWarningInfo info : riskWarningInfos){
                    HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                    warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                    warningInfo.setProjectId(info.getProjectId());
                    warningInfo.setBpId(bpId);
                    warningInfo.setOverdueAmount(info.getOverdueAmount());
                    warningInfo.setOverdueTimes(info.getOverdueTimes());
                    warningInfo.setOverdueDays(info.getOverdueDays());
                    warningInfo.setFinanceAmount(info.getFinanceAmount());
                    warningInfo.setReceivedTimes(info.getReceivedTimes());
                    warningInfo.setFiveClassResult(info.getFiveClassResult());
                    warningInfo.setTotalTimes(info.getTotalTimes());
                    infoService.insertSelective(requestCtx,warningInfo);
                }
            }
            if(riskType.equals("AUTOMATIC_WARNING")){
                //天眼查 接口  接口返回信息存入表  HlsCusRiskWarningInfo
                String str = "str";
            }


            //添加返回参数
            warnings.add(riskWarning);

        }
        return new ResponseData(warnings);
    }

    @RequestMapping(value = "/plm/risk/warning/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarning dto = param.toJavaObject(HlsCusRiskWarning.class);
        return new ResponseData(service.queryAll(requestContext, dto));
    }

    @RequestMapping(value = "/plm/risk/warning/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarning dto = param.toJavaObject(HlsCusRiskWarning.class);
        List<HlsCusRiskWarning> list = new ArrayList<>();
        list.add(service.submit(iRequest, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/risk/warning/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusRiskWarning> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:工作流提交
     * @Author: Wty
     * @Date: Created om 16:55 2018/6/4
     */
    @RequestMapping(value = "/plm/risk/warning/submit/wfl")
    @ResponseBody
    public ResponseData submitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarning dto = param.toJavaObject(HlsCusRiskWarning.class);
        //HlsCusRiskWarning hlsCusRiskWarning = service.submitWfl(iRequest, dto);
        HlsCusRiskWarning hlsCusRiskWarning = service.submitWithoutWfl(iRequest, dto);
        List<HlsCusRiskWarning> list = new ArrayList<>();
        if (hlsCusRiskWarning != null) {
            list.add(hlsCusRiskWarning);
        }
        return new ResponseData(list);
    }

    /**
     * @Description:job工作流提交
     * @Author: Wty
     * @Date: Created om 16:55 2018/6/4
     */
    @RequestMapping(value = "/plm/risk/warning/submit/wfl/job")
    @ResponseBody
    public void submitWflJob(HttpServletRequest request) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        IRequest requestCtx =  RequestHelper.getCurrentRequest(true);
        RequestHelper.setCurrentRequest(iRequest);
        iRequest.setUserId(10001L);
        iRequest.setUserName("管理员");
        iRequest.setEmployeeCode("ADMIN");
        iRequest.setCompanyId(248L);
        iRequest.setRoleId(10146L);
        iRequest.setSubject("HAP");
        iRequest.setLocale("zh_CN");
        iRequest.setAttribute("allocationId",1401L);
        ServletRequestAttributes servletRequestAttributs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(servletRequestAttributs,true);
        HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
        hlsCusRiskWarning.setSubmitStatus("N");
        List<HlsCusRiskWarning> hlsCusRiskWarningList = service.selectSelective(iRequest,hlsCusRiskWarning);
        if(hlsCusRiskWarningList.size()>0){
            for(HlsCusRiskWarning warning :hlsCusRiskWarningList){
                 service.submitWfl(iRequest, warning);
            }
        }
    }

    /**
     * @Description:解除风险预警工作流
     * @Author: Wty
     * @Date: Created om 16:56 2018/6/4
     */
    @RequestMapping(value = "/plm/risk/warning/release/wfl")
    @ResponseBody
    public ResponseData release(HlsCusRiskWarning dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        service.releaseWarning(iRequest, dto);
        return new ResponseData();
    }

    /**
     * @Description:风险预警变更工作流
     * @Author: Wty
     * @Date: Created om 16:56 2018/6/4
     */
    @RequestMapping(value = "/plm/risk/warning/submit/change/wfl")
    @ResponseBody
    public ResponseData submitChangeWfl(@RequestBody HlsCusRiskWarning dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        HlsCusRiskWarning riskWarning = service.submitChangeWfl(iRequest, dto);
        List<HlsCusRiskWarning> list = new ArrayList<>();
        if (riskWarning != null) {
            list.add(riskWarning);
        }
        return new ResponseData(list);
    }

    /**
     * @Description:风险预警备份变更信息
     * @Author: Wty
     * @Date: Created om 10:37 2018/6/5
     */
    @RequestMapping(value = "/plm/risk/warning/copy/change")
    @ResponseBody
    public ResponseData copyChangeData(HlsCusRiskWarning dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        service.copyChangeData(iRequest, dto);
        return new ResponseData();
    }

    /**
     * @Description:风险预警rollTable查询
     * @Author: Wty
     * @Date: Created om 13:25 2018/6/5
     */
    @RequestMapping(value = "/plm/risk/warning/homeThird/query")
    @ResponseBody
    public ResponseData homeThirdQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                       HttpServletRequest request,
                                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarning dto = param.toJavaObject(HlsCusRiskWarning.class);

        return new ResponseData(service.homeThirdQuery(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:风险预警是否解除预警查询
     * @Author: Wty
     * @Date: Created om 13:25 2018/6/5
     */
    @RequestMapping(value = "/plm/risk/warning/isReleasing/select")
    @ResponseBody
    public ResponseData selectIsReleasing(HlsCusRiskWarning dto,
                                          HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectIsReleasing(iRequest, dto));
    }

    /**
     * @Description:首页chart图
     * @Author: Wty
     * @Date: Created om 14:02 2018/6/6
     */
    @RequestMapping(value = "/plm/risk/warning/home/chart/select")
    @ResponseBody
    public ResponseData selectHomeChart(HlsCusRiskWarning dto,
                                        HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectHomeChart(iRequest, dto));
    }




    @RequestMapping(value = "/plm/risk/warning/createChange")
    @ResponseBody
    public ResponseData createChange(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarning dto = param.toJavaObject(HlsCusRiskWarning.class);
        List<HlsCusRiskWarning> list = new ArrayList<>();
        list.add(service.createChange(iRequest, dto));
        return new ResponseData(list);
    }


    /**
     * 取消变更
     * @param requestData
     * @param request
     * @return
     */
    @RequestMapping(value = "/plm/risk/warning/cancelChange")
    @ResponseBody
    public ResponseData cancelChange(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarning dto = param.toJavaObject(HlsCusRiskWarning.class);
        List<HlsCusRiskWarning> list = new ArrayList<>();
        list.add(service.cancelChange(iRequest, dto));
        return new ResponseData(list);
    }
    @RequestMapping(value = "/plm/risk/warning/excel/download")
    public void exportAccountPredict(HttpServletRequest request, @RequestParam String config,
                                     HttpServletResponse httpServletResponse) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusCapAccountMonthlyBalance.class, ColumnInfo.class);
            ExportConfig<HlsCusCapAccountMonthlyBalance, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            this.excelService.exportAndDownloadExcel("hls.core.plm.rw.mapper.HlsCusRiskWarningMapper.homeThirdQuery", exportConfig, request, httpServletResponse, requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
