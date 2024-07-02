package com.hand.hls.rw.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.rw.controllers.HlsCusRiskWarningController;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.rw.service.HlsCusIRiskWarningInfoService;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import com.hand.hls.ty.dto.JcTianyanchaInterfaceInfo;
import com.hand.hls.ty.mapper.JcTianyanchaInterfaceInfoMapper;
import com.hand.hls.ty.service.IJcTianyanchaInterfaceInfoService;
import hls.core.utils.exception.HlsCusException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HlsCusRiskWarningJob extends AbstractJobWithIRequest {
    @Autowired
    private HlsCusRiskWarningController warningController;
    @Autowired
    private HlsCusIRiskWarningInfoService service;
    @Autowired
    private HlsCusIRiskWarningService hlsCusIRiskWarningService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private JcTianyanchaInterfaceInfoMapper jcTianyanchaInterfaceInfoMapper;
    @Autowired
    private IJcTianyanchaInterfaceInfoService jcTianyanchaInterfaceInfoService;

    @Autowired
    private HlsCusRiskWarningMapper hlsCusRiskWarningMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    HlsCusIRiskWarningInfoService infoService;

    //测试接口信息（到期）
//    private static final String authId = "41euao3txms3jbts";
//    private static final String username = "datafeed201912251229";
//    private static final String key = "6c0d7905-ba53-488a-8e62-50cead578779";
    //生产接口信息
    private static final String authId = "6vodsd196xi7ijkw";
    private static final String username = "data2021062501";
    private static final String key = "76d70ccb-bb2b-4f8c-a1e1-c39e8a8a420b";
    private static final String sign = DigestUtils.md5Hex(username+key);
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;
    @Autowired
    private WebApplicationContext context;
    protected MockMvc mockMvc;

    //添加监控企业
    public void adMinitor(String domainNames) throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = "https://pro.tianyancha.com/cloud-monitor/group/map.json?_authId="+authId+"&_username="+username+"&_sign="+sign;
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("domainNames",domainNames));
        httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取返回数据
            if(response.getStatusLine().getStatusCode()==200){
                //记录请求天眼查询结果
                JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
                //jcTianyanchaInterfaceInfo.setDocumentId();
                jcTianyanchaInterfaceInfo.setDocumentType("PLM_RISK_WARNING");
                jcTianyanchaInterfaceInfo.setErrorCode(Long.valueOf(response.getStatusLine().getStatusCode()));
                jcTianyanchaInterfaceInfo.setData(EntityUtils.toString(response.getEntity()));
                jcTianyanchaInterfaceInfoMapper.insertSelective(jcTianyanchaInterfaceInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //取消监控企业
    public void calMinitor(String domainNames) throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = "https://pro.tianyancha.com/cloud-monitor/group/map.json?_authId="+authId+"&_username="+username+"&_sign="+sign+"&domainNames="+domainNames;
        HttpDelete httpDelete = new HttpDelete(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            //获取返回数据
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //绑定回调接口
    public void addApi() throws UnsupportedEncodingException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //测试外网
//        String baseUrl = "http://3986c748o3.zicp.vip/leaf_jczl/hls/risk/notice";
        //生产外网
        String baseUrl = "http://1.85.49.155:8077/jczl/hls/risk/notice";
        String url = "https://pro.tianyancha.com/cloud-monitor/sender/api.json?_authId="+authId+"&_username="+username+"&_sign="+sign;
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("url",baseUrl));
        httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取返回数据
            if(response.getStatusLine().getStatusCode()==200){
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) throws Exception {
//        IRequest requestCtx = RequestHelper.getCurrentRequest(true);
//        IRequest requestCtx = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HlsCusRiskWarning warning = new HlsCusRiskWarning();
        //天眼查 收到天眼查调用 讲数据写进 warning
        //获取公司合作的企业信息（客户全称）
        StringBuffer name = new StringBuffer();
        List<String> bpNames = hlsCusBpMasterMapper.selectBpNameList(248L);
        for(String bpName: bpNames){
            name.append(bpName).append(",");
        }
        //测试
        String domainNames = name.substring(0,name.length()-1);
        //String domainNames = "北京字节跳动科技有限公司,陕西君成融资租赁股份限公司,麦克维尔中央空调有限公司";
//        this.adMinitor(domainNames);//需要绑定新企业时候执行  新作job 暂时注释这里
//        this.addApi();//--只需要执行一次  新作job 暂时注释这里
        this.getData(iRequest);

        //进行提交审批
        HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
        hlsCusRiskWarning.setSubmitStatus("N");  //查找未提交的单据进行遍历提交
//        hlsCusRiskWarning.setRiskType("AUTOMATIC_WARNING");
        //List<HlsCusRiskWarning> hlsCusRiskWarningList = hlsCusIRiskWarningService.selectSelective(requestCtx,hlsCusRiskWarning);
        //测试
//        hlsCusRiskWarning.setRiskWarningId(144L);
        List<HlsCusRiskWarning> hlsCusRiskWarningList = hlsCusIRiskWarningService.selectSelective(iRequest,hlsCusRiskWarning);
        if(hlsCusRiskWarningList.size()>0){
            //触发controller 提交工作流
            try {
                mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
                MvcResult mvcResult = mockMvc.perform(post("/plm/risk/warning/submit/wfl/job").with(new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        return request;
                    }
                })).andExpect(status().is(200)).andReturn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        hlsCusIRiskWarningService.submitWfl(iRequest,hlsCusRiskWarningList.get(0));

    }


    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    // 通过job定时 去数据库查询当天、未检索的数据 进行解析 最后生成预警
    public void getData(IRequest requestCtx){
        //
        int k=0;
        int j=0;
        Date date = new Date();
          JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo = new JcTianyanchaInterfaceInfo();
          jcTianyanchaInterfaceInfo.setRetrievalState("UNRETRIEVAL");  //最后当遍历完保存成功预警信息后对检索状态进行修改（已检索）
          jcTianyanchaInterfaceInfo.setUsgDate(date);
          List<JcTianyanchaInterfaceInfo> tianyanchaInterfaceInfos = jcTianyanchaInterfaceInfoMapper.queryJcTianyanchaInterfaceInfoList(jcTianyanchaInterfaceInfo);
        HlsCusRiskWarning cusRiskWarning = null;
          for(JcTianyanchaInterfaceInfo jcTianyanchaInterfaceInfo1:tianyanchaInterfaceInfos){
             if(jcTianyanchaInterfaceInfo1.getData()!=null&&!"".equals(jcTianyanchaInterfaceInfo1.getData())){
                 //this.hlsCusIRiskWarningService.updateRiskWarningBySky(requestCtx,riskWarning,jcTianyanchaInterfaceInfo1.getData());
                 JSONObject jsonObject = JSONObject.parseObject(jcTianyanchaInterfaceInfo1.getData());
                 String type = String.valueOf(jsonObject.get("type"));
                 JSONArray jsonArray = jsonObject.getJSONArray("items");

                 //工商变更
                 if("11".equals(type)){
                     cusRiskWarning = updataCommercialChange(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //法院公告
                }else if("32".equals(type)){
                     cusRiskWarning = updataCourtNotice(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }
                    //被执行人
                }else if("34".equals(type)){
                     cusRiskWarning = updataExecutee(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }
                    //行政处罚【工商局】
                }else if("42".equals(type)){
                     cusRiskWarning = updataAdministrative(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }
                    //严重违法
                }else if("43".equals(type)){
                     cusRiskWarning = updataSerious(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //股权出质
                }else if("44".equals(type)){
                     cusRiskWarning = updataEquity(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //动产抵押
                }else if("45".equals(type)){
                     cusRiskWarning = updataMortgage(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //立案信息
                }else if("80".equals(type)){
                     cusRiskWarning = updataFiling(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //土地抵押
                }else if("85".equals(type)){
                     cusRiskWarning = updataLand(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //大股东变更
                }else if("116".equals(type)){
                     cusRiskWarning = updataShareholders(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }
                    //股权变更
                }else if("122".equals(type)){
                     cusRiskWarning = updataChange(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                    //新闻舆情
                }else if("3001".equals(type)){
                     cusRiskWarning = updataOpinion(requestCtx,jsonArray,k);
                     if(cusRiskWarning!=null){
                         jcTianyanchaInterfaceInfo1.setRetrievalState("RETRIEVAL");//已检索
                     }

                }else{

                }

                 jcTianyanchaInterfaceInfoMapper.updateByPrimaryKeySelective(jcTianyanchaInterfaceInfo1);

             }


         }

//        //如果成功保存，就调用审批
//        if(cusRiskWarning!=null){
//            try {
//                hlsCusIRiskWarningService.submitWfl(requestCtx,cusRiskWarning);
//            } catch (HlsCusException e) {
//                e.printStackTrace();
//            }
//        }
      }


    //新闻舆情
    public HlsCusRiskWarning updataOpinion (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("新闻舆情："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            String docid = String.valueOf(newObject.get("docid").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            String bpId = String.valueOf(newObject.get("companyId"));
            Long companyId = 0L;
            if(!"null".equals(bpId)){
                 companyId = Long.valueOf(bpId);
            }


            String source = String.valueOf(newObject.get("source"));
            String title = String.valueOf(newObject.get("title"));
            String url = String.valueOf(newObject.get("url"));

            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"新闻标题："+title+"，\n");
            stringBuffer.append("       "+"来源："+source+"，\n");
            stringBuffer.append("       "+"原文链接："+url+"]\n");
            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            //Date date = new Date();
            //hlsCusRiskWarning.setApplyDate(formatter.parse(formatter.format(date),'yyyy-mm-dd'));
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);
                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }


    //股权变更
    public HlsCusRiskWarning updataChange (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("股权变更："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            //Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            String bpId = String.valueOf(newObject.get("companyId"));
            Long companyId = 0L;
            if(!"null".equals(bpId)){
                companyId = Long.valueOf(bpId);
            }

            String capital = String.valueOf(newObject.get("capital"));
            String capitalBefore = String.valueOf(newObject.get("capitalBefore"));
            String percent = String.valueOf(newObject.get("percent"));
            String shareholderName = String.valueOf(newObject.get("shareholderName"));
            String percentBefore = String.valueOf(newObject.get("percentBefore"));


            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"股东名称："+shareholderName+"，\n");
            stringBuffer.append("       "+"原认缴出资额："+capitalBefore+"，\n");
            stringBuffer.append("       "+"现认缴出资额："+capital+"，\n");
            stringBuffer.append("       "+"原出资比例："+percentBefore+"，\n");
            stringBuffer.append("       "+"现出资比例："+percent+"]\n");
            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }

    //大股东变更
    public HlsCusRiskWarning updataShareholders (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("大股东变更："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            String bpId = String.valueOf(newObject.get("companyId"));
            Long companyId = 0L;
            if(!"null".equals(bpId)){
                companyId = Long.valueOf(bpId);
            }

            String bigShareholder = String.valueOf(newObject.get("bigShareholder"));
            String bigShareholderBefore = String.valueOf(newObject.get("bigShareholderBefore"));

            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"原大股东名称 ："+bigShareholderBefore+"，\n");
            stringBuffer.append("       "+"现大股东名称 ："+bigShareholder+"]\n");

            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }

    //土地抵押
    public HlsCusRiskWarning updataLand (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime1 = new Date();
        Date newTime2 = new Date();
        Date newTime3 = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("土地抵押："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            String bpId = String.valueOf(newObject.get("companyId"));
            Long companyId = 0L;
            if(!"null".equals(bpId)){
                companyId = Long.valueOf(bpId);
            }

            String mortgageApplicationName = String.valueOf(newObject.get("mortgageApplicationName"));
            String mortgageArea = String.valueOf(newObject.get("mortgageArea"));
            String nature = String.valueOf(newObject.get("nature"));
            String landArea = String.valueOf(newObject.get("landArea"));
            String otherItemApplicationNameNum = String.valueOf(newObject.get("otherItemApplicationNameNum"));
            String landAministrativeArea = String.valueOf(newObject.get("landAministrativeArea"));
            String landMark = String.valueOf(newObject.get("landMark"));
            String useRightNum = String.valueOf(newObject.get("useRightNum"));
            String landLoc = String.valueOf(newObject.get("landLoc"));
            String mortgageAmount = String.valueOf(newObject.get("mortgageAmount"));
            String landNum = String.valueOf(newObject.get("landNum"));
            String userType = String.valueOf(newObject.get("userType"));
            String evaluateAmount = String.valueOf(newObject.get("evaluateAmount"));
            String mortgagePerson = String.valueOf(newObject.get("mortgagePerson"));
            String mortgageToUser = String.valueOf(newObject.get("mortgageToUser"));

            String startDateClean = String.valueOf(newObject.get("startDateClean"));
            if(!"null".equals(startDateClean)){
                String newFilingDate = formatter.format(new Date(Long.parseLong(startDateClean)));
                try {
                    newTime1 = formatter.parse(newFilingDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String endDateClean = String.valueOf(newObject.get("endDateClean"));
            if(!"null".equals(endDateClean)){
                String newCloseDate = formatter.format(new Date(Long.parseLong(endDateClean)));
                try {
                    newTime2 = formatter.parse(newCloseDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"土地抵押权人 ："+mortgageApplicationName+"，\n");
            stringBuffer.append("       "+"抵押面积(公顷) ："+mortgageArea+"，\n");
            stringBuffer.append("       "+"土地抵押人性质 ："+nature+"，\n");
            stringBuffer.append("       "+"土地面积(公顷) ："+landArea+"，\n");
            stringBuffer.append("       "+"土地他项权利人证号："+otherItemApplicationNameNum+"，\n");
            stringBuffer.append("       "+"所在行政区 ："+landAministrativeArea+"，\n");
            stringBuffer.append("       "+" 宗地标识 ："+landMark+"，\n");
            stringBuffer.append("       "+"土地使用权证号 ："+useRightNum+"，\n");
            stringBuffer.append("       "+"宗地坐落："+landLoc+"，\n");
            stringBuffer.append("       "+"抵押金额(万元)："+mortgageAmount+"，\n");
            stringBuffer.append("       "+"宗地编号："+landNum+"，\n");
            stringBuffer.append("       "+"抵押土地权属性质与使用权类型："+userType+"，\n");
            stringBuffer.append("       "+"评估金额(万元)："+evaluateAmount+"，\n");
            stringBuffer.append("       "+"土地抵押人名称："+mortgagePerson+"，\n");
            stringBuffer.append("       "+"抵押土地用途："+mortgageToUser+"，\n");
            stringBuffer.append("       "+"土地抵押登记起始时间 ："+newTime1+"，\n");
            stringBuffer.append("       "+"土地抵押结束时间 ："+newTime2+"]\n");
            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }

    //立案信息
    public HlsCusRiskWarning updataFiling (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime1 = new Date();
        Date newTime2 = new Date();
        Date newTime3 = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("立案信息："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));

            String plaintiff = String.valueOf(newObject.get("plaintiff"));
            String caseStatus = String.valueOf(newObject.get("caseStatus"));
            String assistant = String.valueOf(newObject.get("assistant"));
            String court = String.valueOf(newObject.get("court"));
            String caseNo = String.valueOf(newObject.get("caseNo"));
            String caseType = String.valueOf(newObject.get("caseType"));
            String third = String.valueOf(newObject.get("third"));
            String defendant = String.valueOf(newObject.get("defendant"));
            String judge = String.valueOf(newObject.get("judge"));
            String department = String.valueOf(newObject.get("department"));

            String filingDate = String.valueOf(newObject.get("filingDate"));
            if(!"null".equals(filingDate)){
                String newFilingDate = formatter.format(new Date(Long.parseLong(filingDate)));
                try {
                    newTime1 = formatter.parse(newFilingDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String closeDate = String.valueOf(newObject.get("closeDate"));
            if(!"null".equals(closeDate)){
                String newCloseDate = formatter.format(new Date(Long.parseLong(closeDate)));
                try {
                    newTime2 = formatter.parse(newCloseDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String startTime = String.valueOf(newObject.get("startTime"));
            if(!"null".equals(startTime)){
                String newStartTime = formatter.format(new Date(Long.parseLong(startTime)));
                try {
                    newTime3 = formatter.parse(newStartTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，");
            stringBuffer.append("       "+"公诉人/原告/上诉人/申请人 ："+plaintiff+"，\n");
            stringBuffer.append("       "+"案件状态 ："+caseStatus+"，\n");
            stringBuffer.append("       "+"法官助理 ："+assistant+"，\n");
            stringBuffer.append("       "+"法院 ："+court+"，\n");
            stringBuffer.append("       "+"案号："+caseNo+"，\n");
            stringBuffer.append("       "+"案件类型 ："+caseType+"，\n");
            stringBuffer.append("       "+"第三人 ："+third+"，\n");
            stringBuffer.append("       "+"被告人/被告/被上诉人/被申请人 ："+defendant+"，\n");
            stringBuffer.append("       "+"承办法官："+judge+"，\n");
            stringBuffer.append("       "+"承办部门："+department+"，\n");
            stringBuffer.append("       "+"立案日期 ："+newTime1+"，\n");
            stringBuffer.append("       "+"开庭日期 ："+newTime3+"，\n");
            stringBuffer.append("       "+"结束日期 ："+newTime2+"]\n");
            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }


    //动产抵押
    public HlsCusRiskWarning updataMortgage (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime1 = new Date();
        Date newTime2 = new Date();
        Date newTime3 = new Date();
        Date newTime4 = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(String.valueOf(newObject.get("companyId")));
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            //
            JSONObject baseInfoObject = (JSONObject)newObject.get("baseInfo");
            stringBuffer.append("       "+"基本信息："+"\n");
            String overviewAmount = String.valueOf(baseInfoObject.get("overviewAmount"));
            String scope = String.valueOf(baseInfoObject.get("scope"));
            String status = String.valueOf(baseInfoObject.get("status"));
            String remark = String.valueOf(baseInfoObject.get("remark"));
            String regDate = String.valueOf(baseInfoObject.get("regDate"));
            if(!"null".equals(regDate)){
                String newRegDate = formatter.format(new Date(Long.parseLong(regDate)));
                try {
                    newTime2 = formatter.parse(newRegDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String overviewType = String.valueOf(baseInfoObject.get("overviewType"));
            String type = String.valueOf(baseInfoObject.get("type"));
            String cancelReason = String.valueOf(baseInfoObject.get("cancelReason"));
            String overviewScope = String.valueOf(baseInfoObject.get("overviewScope"));

            String amount = String.valueOf(baseInfoObject.get("amount"));
            String overviewRemark = String.valueOf(baseInfoObject.get("overviewRemark"));
            String overviewTerm = String.valueOf(baseInfoObject.get("overviewTerm"));
            String regDepartment = String.valueOf(baseInfoObject.get("regDepartment"));
            String regNum = String.valueOf(baseInfoObject.get("regNum"));
            String term = String.valueOf(baseInfoObject.get("term"));
            String base = String.valueOf(baseInfoObject.get("base"));
            String cancelDate = String.valueOf(baseInfoObject.get("cancelDate"));
            if(!"null".equals(cancelDate)){
                String newCancelDate = formatter.format(new Date(Long.parseLong(cancelDate)));
                try {
                    newTime3 = formatter.parse(newCancelDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String publishDate = String.valueOf(baseInfoObject.get("publishDate"));
            if(!"null".equals(publishDate)){
                String newPublishDate = formatter.format(new Date(Long.parseLong(publishDate)));
                try {
                    newTime4 = formatter.parse(newPublishDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            stringBuffer.append("               ["+"概况数额："+overviewAmount+"，\n");
            stringBuffer.append("                "+"担保范围："+scope+"，\n");
            stringBuffer.append("                "+"状态 ："+status+"，\n");
            stringBuffer.append("                "+"备注 ："+remark+"，\n");
            stringBuffer.append("                "+"登记日期："+newTime2+"，\n");
            stringBuffer.append("                "+"概况种类："+overviewType+"，\n");
            stringBuffer.append("                "+"被担保债权种类："+type+"，\n");
            stringBuffer.append("                "+"注销原因："+cancelReason+"，\n");
            stringBuffer.append("                "+"概况担保的范围："+overviewScope+"，\n");
            stringBuffer.append("                "+"被担保债权数额："+amount+"，\n");
            stringBuffer.append("                "+"概况备注："+overviewRemark+"，\n");
            stringBuffer.append("                "+"概况债务人履行债务的期限："+overviewTerm+"，\n");
            stringBuffer.append("                "+"登记机关："+regDepartment+"，\n");
            stringBuffer.append("                "+"登记编号："+regNum+"，\n");
            stringBuffer.append("                "+"债务人履行债务的期限："+term+"，\n");
            stringBuffer.append("                "+"省份："+base+"，\n");
            stringBuffer.append("                "+"注销日期："+newTime3+"，\n");
            stringBuffer.append("                "+"公示日期 ："+newTime4+"]\n");
            //
            JSONArray jsonArrayChangeInfoList = newObject.getJSONArray("changeInfoList");
            stringBuffer.append("       "+"变更信息："+"\n");
            for(int j=0;j<jsonArrayChangeInfoList.size();j++){
                JSONObject changeInfoListObject =(JSONObject)jsonArray.get(i);
                String changeContent = String.valueOf(changeInfoListObject.get("changeContent"));
                String changeDate = String.valueOf(newObject.get("changeDate"));
                if(!"null".equals(changeDate)){
                    String newChangeDate = formatter.format(new Date(Long.parseLong(changeDate)));
                    try {
                        newTime1 = formatter.parse(newChangeDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                stringBuffer.append("               ["+"变更日期："+newTime1+"，\n");
                stringBuffer.append("                "+"变更内容 ："+changeContent+"]\n");
            }

            //
            JSONArray jsonArrayPawnInfoList = newObject.getJSONArray("pawnInfoList");
            stringBuffer.append("       "+"抵押物："+"\n");
            for(int n=0;n<jsonArrayPawnInfoList.size();n++){
                JSONObject pawnInfoListObject =(JSONObject)jsonArray.get(i);
                String detail = String.valueOf(pawnInfoListObject.get("detail"));
                String ownership = String.valueOf(pawnInfoListObject.get("ownership"));
                String pawnName = String.valueOf(pawnInfoListObject.get("pawnName"));
                String remark1 = String.valueOf(pawnInfoListObject.get("remark"));
                stringBuffer.append("               ["+"数量、质量、状况、所在地等情况："+detail+"，\n");
                stringBuffer.append("                "+"所有权归属："+ownership+"，\n");
                stringBuffer.append("                "+"名称 ："+pawnName+"，\n");
                stringBuffer.append("                "+"备注 ："+remark1+"]\n");
            }
            //
            JSONArray jsonArrayPeopleInfo = newObject.getJSONArray("peopleInfo");
            stringBuffer.append("       "+"抵押人："+"\n");
            for(int m=0;m<jsonArrayPeopleInfo.size();m++){
                JSONObject peopleInfoObject =(JSONObject)jsonArray.get(i);
                String licenseNum = String.valueOf(peopleInfoObject.get("licenseNum"));
                String peopleName = String.valueOf(peopleInfoObject.get("peopleName"));
                String liceseType = String.valueOf(peopleInfoObject.get("liceseType"));
                stringBuffer.append("               ["+"证照/证件号码："+licenseNum+"，\n");
                stringBuffer.append("                "+"抵押权人名称："+peopleName+"，\n");
                stringBuffer.append("                "+"抵押权人证照/证件类型："+liceseType+"]\n");
            }

            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }

        }
        return newCusRiskWarning;
    }


    //股权出质
    public HlsCusRiskWarning updataEquity (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime1 = new Date();
        Date newTime2 = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("股权出质："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(newObject.get("companyId").toString());
            String equityAmount = newObject.get("equityAmount").toString();
            String regNumber = String.valueOf(newObject.get("regNumber"));
            String pledgee = String.valueOf(newObject.get("pledgee"));
            String putDate = String.valueOf(newObject.get("putDate"));
            if(!"null".equals(putDate)){
                String newChangeTime = formatter.format(new Date(Long.parseLong(putDate)));
                try {
                    newTime1 = formatter.parse(newChangeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String regDate = String.valueOf(newObject.get("regDate"));
            if(!"null".equals(regDate)){
                String newRemoveDate = formatter.format(new Date(Long.parseLong(regDate)));
                try {
                    newTime2 = formatter.parse(newRemoveDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String state = String.valueOf(newObject.get("state"));
            String pledgor = String.valueOf(newObject.get("pledgor"));

            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"出质股权数额 ："+equityAmount+"，\n");
            stringBuffer.append("       "+"注册号 ："+regNumber+"，\n");
            stringBuffer.append("       "+"质权人 ："+pledgee+"，\n");
            stringBuffer.append("       "+"列入原因 ："+state+"，\n");
            stringBuffer.append("       "+"出质人："+pledgor+"，\n");
            stringBuffer.append("       "+"股权出质设立发布日期 ："+newTime1+"，\n");
            stringBuffer.append("       "+"股权出质设立登记日期 ："+newTime2+"]\n");
            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }

    //严重违法
    public HlsCusRiskWarning updataSerious (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime1 = new Date();
        Date newTime2 = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("严重违法："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(newObject.get("companyId").toString());
            String removeReason = newObject.get("removeReason").toString();
            String removeDepartment = String.valueOf(newObject.get("removeDepartment"));
            String putReason = String.valueOf(newObject.get("putReason"));
            String putDepartment = String.valueOf(newObject.get("putDepartment"));
            String execMoney = String.valueOf(newObject.get("execMoney"));
            String putDate = String.valueOf(newObject.get("putDate"));
            if(!"null".equals(putDate)){
                String newChangeTime = formatter.format(new Date(Long.parseLong(putDate)));
                try {
                    newTime1 = formatter.parse(newChangeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String removeDate = String.valueOf(newObject.get("removeDate"));
            if(!"null".equals(removeDate)){
                String newRemoveDate = formatter.format(new Date(Long.parseLong(removeDate)));
                try {
                    newTime2 = formatter.parse(newRemoveDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"移除原因 ："+removeReason+"，\n");
            stringBuffer.append("       "+"移除部门 ："+removeDepartment+"，\n");
            stringBuffer.append("       "+"列入原因 ："+putReason+"，\n");
            stringBuffer.append("       "+"列入部门 ："+putDepartment+"，\n");
            stringBuffer.append("       "+"列入日期 ："+newTime1+"，\n");
            stringBuffer.append("       "+"移除日期 ："+newTime2+"]\n");
            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }

    //行政处罚【工商局】
    public HlsCusRiskWarning updataAdministrative (IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime1 = new Date();
        Date newTime2 = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("行政处罚【工商局】："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(newObject.get("companyId").toString());
            String content = newObject.get("content").toString();
            String punishNumber = String.valueOf(newObject.get("punishNumber"));
            String description = String.valueOf(newObject.get("description"));
            String decisionDate = String.valueOf(newObject.get("decisionDate"));
            if(!"null".equals(decisionDate)){
                String newDecisionDate = formatter.format(new Date(Long.parseLong(decisionDate)));
                try {
                    newTime1 = formatter.parse(newDecisionDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String type = String.valueOf(newObject.get("type"));
            String departmentName = String.valueOf(newObject.get("departmentName"));
            String publishDate = String.valueOf(newObject.get("publishDate"));
            if(!"null".equals(publishDate)){
                String newChangeTime = formatter.format(new Date(Long.parseLong(publishDate)));
                try {
                    newTime2 = formatter.parse(newChangeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"行政处罚内容："+content+"，\n");
            stringBuffer.append("       "+"定书文号："+punishNumber+"，\n");
            stringBuffer.append("       "+"描述  ："+description+"，\n");
            stringBuffer.append("       "+"违法行为类型："+type+"，\n");
            stringBuffer.append("       "+"决定日期 ："+newTime1+"，\n");
            stringBuffer.append("       "+"决定机关名称 ："+departmentName+"，\n");
            stringBuffer.append("       "+"公示日期 ："+newTime2+"]\n");

            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }



        }
        return newCusRiskWarning;
    }


    //被执行人
    public HlsCusRiskWarning updataExecutee(IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("被执行人："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);

            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(newObject.get("companyId").toString());
            String caseCode = newObject.get("caseCode").toString();
            String execCourtName = String.valueOf(newObject.get("execCourtName"));
            String pname = String.valueOf(newObject.get("pname"));
            String partyCardNum = String.valueOf(newObject.get("partyCardNum"));
            String execMoney = String.valueOf(newObject.get("execMoney"));
            String caseCreateTime = String.valueOf(newObject.get("caseCreateTime"));
            if(!"null".equals(caseCreateTime)){
                String newChangeTime = formatter.format(new Date(Long.parseLong(caseCreateTime)));
                try {
                    newTime = formatter.parse(newChangeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"案号："+caseCode+"，\n");
            stringBuffer.append("       "+"执行法院："+execCourtName+"，\n");
            stringBuffer.append("       "+"被执行人名称 ："+pname+"，\n");
            stringBuffer.append("       "+"身份证号／组织机构代码 ："+partyCardNum+"，\n");
            stringBuffer.append("       "+"创建时间 ："+newTime+"，\n");
            stringBuffer.append("       "+"执行标的 ："+execMoney+"]\n");

            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }



        }
        return newCusRiskWarning;
    }

    //法院公告
    public HlsCusRiskWarning updataCourtNotice(IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime = new Date();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("法院公告："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(newObject.get("companyId").toString());
            String announceId = newObject.get("announceId").toString();
            String bltnno = String.valueOf(newObject.get("bltnno"));
            String bltnstate = String.valueOf(newObject.get("bltnstate"));
            String bltntype = String.valueOf(newObject.get("bltntype"));
            String bltntypename = String.valueOf(newObject.get("bltntypename"));
            String caseno = String.valueOf(newObject.get("caseno"));
            String content = String.valueOf(newObject.get("content"));
            String courtcode = String.valueOf(newObject.get("courtcode"));
            String dealgrade = String.valueOf(newObject.get("dealgrade"));
            String dealgradename = String.valueOf(newObject.get("dealgradename"));
            String judge = String.valueOf(newObject.get("judge"));
            String party1 = String.valueOf(newObject.get("party1"));
            String party2 = String.valueOf(newObject.get("party2"));
            String province = String.valueOf(newObject.get("province"));
            String publishdate = String.valueOf(newObject.get("publishdate"));
            if(!"null".equals(publishdate)){
                String newChangeTime = formatter.format(new Date(Long.parseLong(publishdate)));
                try {
                    newTime = formatter.parse(newChangeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String publishpage = String.valueOf(newObject.get("publishpage"));
            String reason = String.valueOf(newObject.get("reason"));

            //格式化
            stringBuffer.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBuffer.append("       "+"公司名称："+companyName+"，\n");
            stringBuffer.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBuffer.append("       "+"公告id："+announceId+"，\n");
            stringBuffer.append("       "+" 公告号："+bltnno+"，\n");
            stringBuffer.append("       "+"公告状态号 ："+bltnstate+"，\n");
            stringBuffer.append("       "+"公告类型 ："+bltntype+"，\n");
            stringBuffer.append("       "+"公告类型名称 ："+bltntypename+"，\n");
            stringBuffer.append("       "+"案件号 ："+caseno+"，\n");
            stringBuffer.append("       "+"案件内容 ："+content+"，\n");
            stringBuffer.append("       "+"法院名 ："+courtcode+"，\n");
            stringBuffer.append("       "+"处理等级 ："+dealgrade+"，\n");
            stringBuffer.append("       "+"处理等级名称 ："+dealgradename+"，\n");
            stringBuffer.append("       "+"法官 ："+judge+"，\n");
            stringBuffer.append("       "+"原告 ："+party1+"，\n");
            stringBuffer.append("       "+"当事人 ："+party2+"，\n");
            stringBuffer.append("       "+"省份 ："+province+"，\n");
            stringBuffer.append("       "+"刊登日期 ："+newTime+"，\n");
            stringBuffer.append("       "+"刊登版面 ："+publishpage+"，\n");
            stringBuffer.append("       "+"原因 ："+reason+"]\n");


            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBuffer.substring(0,stringBuffer.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
        return newCusRiskWarning;
    }

    //工商变更
    public HlsCusRiskWarning updataCommercialChange(IRequest iRequest,JSONArray jsonArray,int k){
        HlsCusRiskWarning newCusRiskWarning = new HlsCusRiskWarning();
        Date newTime = new Date();
        StringBuffer stringBufferCommercialChange = new StringBuffer();
        stringBufferCommercialChange.append("工商变更："+'\n');
        for(int i=0;i<jsonArray.size();i++){
            JSONObject newObject =(JSONObject)jsonArray.get(i);
            Long id = Long.valueOf(newObject.get("id").toString());
            Long companyGid = Long.valueOf(newObject.get("companyGid").toString());
            String companyName = newObject.get("companyName").toString();
            Long companyId = Long.valueOf(newObject.get("companyId").toString());
            String changeItem = newObject.get("changeItem").toString();
            String contentBefore = newObject.get("contentBefore").toString();
            String contentAfter = newObject.get("contentAfter").toString();
            String changeTime = newObject.get("changeTime").toString();
            if(!"null".equals(changeTime)){
                String newChangeTime = formatter.format(new Date(Long.parseLong(changeTime)));
                try {
                    newTime = formatter.parse(newChangeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //格式化
            stringBufferCommercialChange.append("       ["+"公司id（公司唯一标识）："+companyGid+"，\n");
            stringBufferCommercialChange.append("       "+"公司名称："+companyName+"，\n");
           // stringBufferCommercialChange.append("       "+"公司id（Deprecated）："+companyId+"，\n");
            stringBufferCommercialChange.append("       "+"变更事项："+changeItem+"，\n");
            stringBufferCommercialChange.append("       "+" 变更前："+contentBefore+"，\n");
            stringBufferCommercialChange.append("       "+"变更后 ："+contentAfter+"，\n");
            stringBufferCommercialChange.append("       "+"变更时间 ："+newTime+"]\n");

            //有且只有一条
            HlsCusRiskWarning hlsCusRiskWarning = new HlsCusRiskWarning();
            //根据公司名称找bpId
            Long newBpId = hlsCusBpMasterMapper.selectBpId(companyName);
            if(newBpId==null||newBpId==0){
                break;
            }
            hlsCusRiskWarning.setBpId(newBpId);
            hlsCusRiskWarning.setApplyDate(new Date());
            HlsCusRiskWarning cusRiskWarning = hlsCusRiskWarningMapper.queryHlsCusRiskWarningByDocumentId(hlsCusRiskWarning);
            //说明该公司当前时间（今天）存在预警（type）就拼接（更新）
            if(cusRiskWarning!=null){
                String oldRiskInfo = cusRiskWarning.getRiskInfo();
                cusRiskWarning.setCompanyName(companyName);
                if(cusRiskWarning.getTotalNum()==null){
                    cusRiskWarning.setTotalNum("0");
                }
                int t = Integer.parseInt(cusRiskWarning.getTotalNum())+1;
                cusRiskWarning.setTotalNum(String.valueOf(t));
                cusRiskWarning.setRiskInfo(oldRiskInfo+stringBufferCommercialChange.substring(0,stringBufferCommercialChange.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest,cusRiskWarning);

                //不存在就插入
            }else{
                //保存风险预警
                HlsCusRiskWarning riskWarning = saveHlsCusRiskWarning(iRequest);
                riskWarning.setBpId(newBpId);
                riskWarning.setCompanyName(companyName);
                riskWarning.setTotalNum("1");
                riskWarning.setSubmitStatus("N"); // 未提交
                riskWarning.setRiskInfo(stringBufferCommercialChange.substring(0,stringBufferCommercialChange.length()));
                newCusRiskWarning = hlsCusIRiskWarningService.insertSelective(iRequest,riskWarning);

                //预警明细
                HlsCusRiskWarningInfo riskWarningInfo = new HlsCusRiskWarningInfo();
                riskWarningInfo.setBpId(newCusRiskWarning.getBpId());
                List<HlsCusRiskWarningInfo> riskWarningInfos = infoService.queryPrjCon(iRequest,riskWarningInfo);
                if(riskWarningInfos.size() > 0){
                    for(HlsCusRiskWarningInfo info : riskWarningInfos){
                        HlsCusRiskWarningInfo warningInfo = new HlsCusRiskWarningInfo();
                        warningInfo.setRiskWarningId(riskWarning.getRiskWarningId());
                        warningInfo.setProjectId(info.getProjectId());
                        warningInfo.setBpId(newCusRiskWarning.getBpId());
                        warningInfo.setOverdueAmount(info.getOverdueAmount());
                        warningInfo.setOverdueTimes(info.getOverdueTimes());
                        warningInfo.setOverdueDays(info.getOverdueDays());
                        warningInfo.setFinanceAmount(info.getFinanceAmount());
                        warningInfo.setReceivedTimes(info.getReceivedTimes());
                        warningInfo.setFiveClassResult(info.getFiveClassResult());
                        warningInfo.setTotalTimes(info.getTotalTimes());
                        infoService.insertSelective(iRequest,warningInfo);
                    }
                }
            }
        }
       return newCusRiskWarning;
    }

    public HlsCusRiskWarning saveHlsCusRiskWarning(IRequest iRequest){
        //保存风险预警
        HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
        riskWarning.setDocumentCategory("RISK_WARNING");
        riskWarning.setDocumentType("RISK_WARNING");
        riskWarning.setBusinessType("RISK_WARNING");
        riskWarning.setStatus("NEW");
        riskWarning.setCompanyId(248L);
        riskWarning.setCreationUserId(10001L);
        riskWarning.setCreatedBy(10001L);
        riskWarning.setIsReleaseWarning("N");
        riskWarning.setIsPresenceWarning("Y");
        riskWarning.setApplyDate(new Date());
        riskWarning.setDataClass("NORMAL");
        //RISK_TYPE 自动预警
        riskWarning.setRiskType("AUTOMATIC_WARNING");
        //生成单据编号
        Map<String, String> params = new HashMap<String, String>();
        riskWarning.setRiskWarningNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, riskWarning.getDocumentCategory(), riskWarning.getDocumentType(), riskWarning.getBusinessType(), params));
        return riskWarning;
    }



    //
//    public List<HlsCusRiskWarning> queryHlsCusRiskWarningToWFL(){
//
//    }
}
