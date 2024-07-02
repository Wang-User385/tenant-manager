package com.hand.hls.ecif.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.dto.HapInterfaceHeader;
import com.hand.hap.intergration.dto.HapInterfaceLine;
import com.hand.hap.intergration.service.IHapInterfaceHeaderService;
import com.hand.hap.intergration.service.IHapInterfaceLineService;
import com.hand.hap.intergration.util.HapInvokeLogUtils;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.bp.service.HlsBpMasterAddressService;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.common.dto.HlsCusDocumentRecordList;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.mapper.HlsCusHapInterfaceOutboundMapper;
import com.hand.hls.common.service.HlsCusDocumentRecordListService;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.common.utils.DoPostSoap;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;

import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;
import com.hand.hls.ecif.mapper.HlsCusBpMasterRequestRecordsMapper;

import com.hand.hls.ecif.mapper.HlsCusEcifBpMasterChangeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

import static redis.clients.jedis.Protocol.CHARSET;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterRequestRecordsServiceImpl extends BaseServiceImpl<HlsCusBpMasterRequestRecords> implements HlsCusBpMasterRequestRecordsService{


    @Autowired
    private HlsCusHapInterfaceOutboundService hlsHapInterfaceOutboundService;

    @Autowired
    private HlsCusDocumentRecordListService hlsCusDocumentRecordListService;

    @Autowired
    private IHapInterfaceHeaderService hapInterfaceHeaderService;

    @Autowired
    private IHapInterfaceLineService hpInterfaceLineService;

    @Autowired
    private HlsCusBpMasterRequestRecordsMapper hlsCusBpMasterRequestRecordsMapper;

    @Autowired
    private HlsCusBpMasterService hlsBpMasterService;

    @Autowired
    private HlsBpMasterAddressService hlsBpMasterAddressService;

    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusEcifBpMasterChangeMapper hlsCusEcifBpMasterChangeMapper;

    @Autowired
    private HlsCusEasSourceRecordMapper hlsCusEasSourceRecordMapper;


    //单笔查询接口数据封装
    public String ecifSignalQueryDataEncapsulation(IRequest iRequest, HlsCusBpMasterRequestRecords dto) {
        String  soapXml=null;

        soapXml ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cus=\"http://service.ws.ecif.cib.com/CustBasicInofService\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <cus:queryCustDetailBasicInfo>\n" +
                "         <!--Optional:-->\n" +
                "         <oprtnInd>"+dto.getOprtnInd()+"</oprtnInd>\n" +
                "         <!--Optional:-->\n" +
                "         <cstNo>"+dto.getCstNo()+"</cstNo>\n" +
                "         <!--Optional:-->\n" +
                "         <unifdSoclCrdtCd>"+dto.getUnifdSoclCrdtCd()+"</unifdSoclCrdtCd>\n" +
                "         <!--Optional:-->\n" +
                "         <cstNmS>"+dto.getCstNmS()+"</cstNmS>\n" +
                "         <!--Optional:-->\n" +
                "         <identNo>"+dto.getIdentNo()+"</identNo>\n" +
                "         <!--Optional:-->\n" +
                "         <instIdentTp>"+dto.getInstIdentTp()+"</instIdentTp>\n" +
                "      </cus:queryCustDetailBasicInfo>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return soapXml;
    }

    //批量创建接口数据封装
    public String ecifBatchCreateDataEncapsulation(IRequest iRequest, List<HlsCusBpMasterRequestRecords> listData) {

        String headXml="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ecif=\"http://web.ecif.cib.com/EcifNoCreateCustServiceImpl\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ecif:custCreateUpdateList>\n";

        String bodyXml="";

        String tailXml="  </ecif:custCreateUpdateList>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        for (int i = 0; i < listData.size(); i++) {
            HlsCusBpMasterRequestRecords item = listData.get(i);
            bodyXml=bodyXml+" <customer>\n" +
                    "            <!--Optional:-->\n" +
                    "            <businessDate>"+item.getBusinessDate()+"</businessDate>\n" +
                    "            <!--Optional:-->\n" +
                    "            <corprtnScop1>"+item.getCorprtnScop1()+"</corprtnScop1>\n" +
                    "            <!--Optional:-->\n" +
                    "            <crdtCstFlg>"+item.getCrdtCstFlg()+"</crdtCstFlg>\n" +
                    "            <!--Optional:-->\n" +
                    "            <createDate>"+item.getEcifCreateDate()+"</createDate>\n" +
                    "            <!--Optional:-->\n" +
                    "            <cstLoNatlStdIdy>"+item.getCstLoNatlStdIdy()+"</cstLoNatlStdIdy>\n" +
                    "            <!--Optional:-->\n" +
                    "            <cstNmS>"+item.getCstNmS()+"</cstNmS>\n" +
                    "            <!--Optional:-->\n" +
                    "            <cstNo>"+item.getCstNo()+"</cstNo>\n" +
                    "            <!--Optional:-->\n" +
                    "            <cstTp>"+item.getCstTp()+"</cstTp>\n" +
                    "            <!--Optional:-->\n" +
                    "            <customerName>"+"</customerName>\n" +
                    "            <!--Optional:-->\n" +
                    "            <dataSource>"+item.getDataSource()+"</dataSource>\n" +
                    "            <!--Optional:-->\n" +
                    "            <deptName>"+"</deptName>\n" +
                    "            <!--Optional:-->\n" +
                    "            <ecnmTp>"+item.getEcnmTp()+"</ecnmTp>\n" +
                    "            <!--Optional:-->\n" +
                    "            <entpAtchRltnp>"+item.getEntpAtchRltnp()+"</entpAtchRltnp>\n" +
                    "            <!--Optional:-->\n" +
                    "            <identNo>"+item.getIdentNo()+"</identNo>\n" +
                    "            <!--Optional:-->\n" +
                    "            <indpdLglPrsnCstFlg>"+"</indpdLglPrsnCstFlg>\n" +
                    "            <!--Optional:-->\n" +
                    "            <instIdentTp>"+item.getInstIdentTp()+"</instIdentTp>\n" +
                    "            <!--Optional:-->\n" +
                    "            <lastUpdatedTe>"+item.getLastUpdatedTe()+"</lastUpdatedTe>\n" +
                    "            <!--Optional:-->\n" +
                    "            <lglRprsNm>"+item.getLglRprsNm()+"</lglRprsNm>\n" +
                    "            <!--Optional:-->\n" +
                    "            <mainCorprtnActvCtyOrDstcCd>"+item.getMaincorprtnactvctyordstccd()+"</mainCorprtnActvCtyOrDstcCd>\n" +
                    "            <!--Optional:-->\n" +
                    "            <operationSource>"+item.getDataSource()+"</operationSource>\n" +
                    "            <!--Optional:-->\n" +
                    "            <operator>"+item.getOperator()+"</operator>\n" +
                    "            <!--Optional:-->\n" +
                    "            <oprtnInd>"+item.getOprtnInd()+"</oprtnInd>\n" +
                    "            <!--Optional:-->\n" +
                    "            <ovrsCstChinNm>"+item.getOvrsCstChinNm()+"</ovrsCstChinNm>\n" +
                    "            <!--Optional:-->\n" +
                    "            <rgstrtnCtyAndDstcCd>"+item.getRgstrtnCtyAndDstcCd()+"</rgstrtnCtyAndDstcCd>\n" +
                    "            <!--Optional:-->\n" +
                    "            <SWIFTNo>"+item.getSwiftno()+"</SWIFTNo>\n" +
                    "            <!--Optional:-->\n" +
                    "            <sprIndpdLglPrsnPcpID>"+"</sprIndpdLglPrsnPcpID>\n" +
                    "            <!--Optional:-->\n" +
                    "            <unifdSoclCrdtCd>"+item.getUnifdSoclCrdtCd()+"</unifdSoclCrdtCd>\n" +
                    "            <!--Optional:-->\n" +
                    "            <updateDate>"+item.getUpdateDate()+"</updateDate>\n" +
                    "            <!--Optional:-->\n" +
                    "            <updateSource>"+item.getUpdateSource()+"</updateSource>\n" +
                    "         </customer>\n";
        }

        String  soapXml =headXml+bodyXml+tailXml;
        return soapXml;
    }





    //调用请求
    public HlsCusHapInterfaceOutbound ecifHttpPost(String URL, String soapXml)throws IOException {
        String  retStr=null;
        HlsCusHapInterfaceOutbound hapInterfaceOutbound=new HlsCusHapInterfaceOutbound();
        try {
            DoPostSoap doPostSoap=new DoPostSoap();
            hapInterfaceOutbound=  doPostSoap.doPostSoap(soapXml,URL);

        } catch (IOException e) {
          //  e.printStackTrace();
            throw new IOException(e);
        }
        return hapInterfaceOutbound;
    }

    //批量创建接口xml解析
    public List<HlsCusBpMasterRequestRecords> ecifBatchCreateXmlParsing(String retStr)throws Exception {
        List<HlsCusBpMasterRequestRecords> bpMasterList = new ArrayList<>();
        try {
        // 创建SAXReader阅读器对象
        SAXReader reader = new SAXReader();
        InputStream is = new ByteArrayInputStream(retStr.getBytes(CHARSET));
        Document document = reader.read(is);
        // 获得文档对象的根节点
        Element root = document.getRootElement();
        // 获取根节点下面名叫Body的节点
        Element Body= root.element("Body");
        // 获取Body节点下面名叫custCreateUpdateListResponse的节点
        List<Element> custCreateUpdateListResponse = Body.element("custCreateUpdateListResponse").elements();
        // 获取custCreateUpdateListResponse节点下面名叫custCreateMofyDeleOperObj的节点清单
        for (int i=0; i<custCreateUpdateListResponse.size(); i++) {
            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords =new HlsCusBpMasterRequestRecords();
            Element item = custCreateUpdateListResponse.get(i);
            String code=  item.element("code").getText();
            hlsCusBpMasterRequestRecords.setCode(code);
            String cstNmS=  item.element("cstNmS").getText();
            hlsCusBpMasterRequestRecords.setCstNmS(cstNmS);
            if("00000".equals(code)){
                String cstNo=  item.element("cstNo").getText();
                hlsCusBpMasterRequestRecords.setCstNo(cstNo);
            }
            String identNo=  item.element("identNo").getText();
            hlsCusBpMasterRequestRecords.setIdentNo(identNo);
            String instIdentTp=  item.element("instIdentTp").getText();
            hlsCusBpMasterRequestRecords.setInstIdentTp(instIdentTp);
            String msg=  item.element("msg").getText();
            hlsCusBpMasterRequestRecords.setMsg(msg);
            String oprtnInd=  item.element("oprtnInd").getText();
            hlsCusBpMasterRequestRecords.setOprtnInd(oprtnInd);
            hlsCusBpMasterRequestRecords.setCode(code);
            bpMasterList.add(hlsCusBpMasterRequestRecords);
        }
        } catch (Exception e) {
           // e.printStackTrace();
            throw new IOException(e);
        }
        return bpMasterList;
    }


    //单笔查询接口xml解析
    public  HlsCusBpMasterRequestRecords ecifSignalQueryXmlParsing(String retStr)throws Exception {
        HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords =new HlsCusBpMasterRequestRecords();



        try {
            // 创建SAXReader阅读器对象
            SAXReader reader = new SAXReader();

            InputStream is = new ByteArrayInputStream(retStr.getBytes(CHARSET));
            // 命令阅读器从输入流中读取文档对象
            Document document = reader.read(is);
            // 获得文档对象的根节点
            Element root = document.getRootElement();
            // 获取根节点下面名叫Body的节点
            Element Body = root.element("Body");
            // 获取Body节点下面名叫queryCustDetailBasicInfoResponse的节点
            Element queryCustDetailBasicInfoResponse = Body.element("queryCustDetailBasicInfoResponse");
            // 获取queryCustDetailBasicInfoResponse节点下面名叫customer的节点
            Element customer = queryCustDetailBasicInfoResponse.element("customer");
            // 获取customer节点下面名叫cstNo的节点值
            if(customer.element("cstNo")==null){
                hlsCusBpMasterRequestRecords.setMsg("未找到满足条件的ECIF客户！");
            }
            else {
                String cstNo = customer.element("cstNo").getText();
                hlsCusBpMasterRequestRecords.setCstNo(cstNo);
                String businessDate = customer.element("businessDate").getText();
                hlsCusBpMasterRequestRecords.setBusinessDate(businessDate);
                String code = customer.element("code").getText();
                hlsCusBpMasterRequestRecords.setCode(code);
                String corprtnScop1 = customer.element("corprtnScop1").getText();
                if("null".equals(corprtnScop1)){
                    corprtnScop1=null;
                }
                hlsCusBpMasterRequestRecords.setCorprtnScop1(corprtnScop1);
                String crdtCstFlg = customer.element("crdtCstFlg").getText();
                hlsCusBpMasterRequestRecords.setCrdtCstFlg(crdtCstFlg);
                String cstLoNatlStdIdy = customer.element("cstLoNatlStdIdy").getText();
                hlsCusBpMasterRequestRecords.setCstLoNatlStdIdy(cstLoNatlStdIdy);
                String cstNmS = customer.element("cstNmS").getText();
                hlsCusBpMasterRequestRecords.setCstNmS(cstNmS);
                String cstTp = customer.element("cstTp").getText();
                hlsCusBpMasterRequestRecords.setCstTp(cstTp);
                String ecnmTp = customer.element("ecnmTp").getText();
                hlsCusBpMasterRequestRecords.setEcnmTp(ecnmTp);
                String entpAtchRltnp = customer.element("entpAtchRltnp").getText();
                hlsCusBpMasterRequestRecords.setEntpAtchRltnp(entpAtchRltnp);
                String identNo = customer.element("identNo").getText();
                hlsCusBpMasterRequestRecords.setIdentNo(identNo);
                String instIdentTp = customer.element("instIdentTp").getText();
                hlsCusBpMasterRequestRecords.setInstIdentTp(instIdentTp);
                String lglRprsNm = customer.element("lglRprsNm").getText();
                hlsCusBpMasterRequestRecords.setLglRprsNm(lglRprsNm);
                String mainCorprtnActvCtyOrDstcCd = customer.element("mainCorprtnActvCtyOrDstcCd").getText();
                hlsCusBpMasterRequestRecords.setMaincorprtnactvctyordstccd(mainCorprtnActvCtyOrDstcCd);
               // String msg = customer.element("msg").getText();
               // hlsCusBpMasterRequestRecords.setMsg(msg);
                hlsCusBpMasterRequestRecords.setMsg("查询成功！");

                String ovrsCstChinNm = customer.element("ovrsCstChinNm").getText();
                if("null".equals(ovrsCstChinNm)){
                    ovrsCstChinNm=null;
                }
                hlsCusBpMasterRequestRecords.setOvrsCstChinNm(ovrsCstChinNm);
                String rgstrtnCtyAndDstcCd = customer.element("rgstrtnCtyAndDstcCd").getText();
                hlsCusBpMasterRequestRecords.setRgstrtnCtyAndDstcCd(rgstrtnCtyAndDstcCd);
                String SWIFTNo = customer.element("SWIFTNo").getText();
                if("null".equals(SWIFTNo)){
                    SWIFTNo=null;
                }
                hlsCusBpMasterRequestRecords.setSwiftno(SWIFTNo);
                String unifdSoclCrdtCd = customer.element("unifdSoclCrdtCd").getText();
                if("null".equals(unifdSoclCrdtCd)){
                    unifdSoclCrdtCd=null;
                }
                hlsCusBpMasterRequestRecords.setUnifdSoclCrdtCd(unifdSoclCrdtCd);



            }


        } catch (Exception e) {
            //e.printStackTrace();
            throw new IOException(e);
        }
        return hlsCusBpMasterRequestRecords;
    }


    //调用日志插入
    @Override
    public HlsCusHapInterfaceOutbound outboundInvokeInsert(IRequest iRequest,HlsCusHapInterfaceOutbound outbound) {
        return this.hlsHapInterfaceOutboundService.insertSelective(iRequest,outbound);
    }


    //日志信息与单据信息关联为手工重发做准备
    public void outboundAndDocumentRecordAssociated(IRequest iRequest,HlsCusHapInterfaceOutbound outboundDto, List<HlsCusBpMasterRequestRecords> bpMasterDto,String sourceTable,String sourceTableName) {
    for (HlsCusBpMasterRequestRecords item :bpMasterDto){
        HlsCusDocumentRecordList hlsCusDocumentRecordList = new HlsCusDocumentRecordList();
        if("HLS_BP_MASTER".equals(sourceTable)){
            hlsCusDocumentRecordList.setSourceId(item.getBpId());
        }
        else{
            hlsCusDocumentRecordList.setSourceId(item.getBpRecordId());
        }
        hlsCusDocumentRecordList.setSourceTable(sourceTable);
        hlsCusDocumentRecordList.setMark(sourceTableName);
        hlsCusDocumentRecordList.setOutboundId(outboundDto.getOutboundId());
        hlsCusDocumentRecordListService.insertSelective(iRequest,hlsCusDocumentRecordList);
    }

    }

    //接口地址信息获取
    @Override
    public HapInterfaceLine getInterfaceUrl(IRequest iRequest, String interfaceCode, String lineCode) {
        HapInterfaceHeader hapInterfaceHeader= new HapInterfaceHeader();
        hapInterfaceHeader.setInterfaceCode(interfaceCode);
        hapInterfaceHeader.setEnableFlag("Y");
        List <HapInterfaceHeader>  hapInterfaceHeaderList= hapInterfaceHeaderService.select(iRequest,hapInterfaceHeader,1,10);

        HapInterfaceLine hapInterfaceLine= new HapInterfaceLine();
        hapInterfaceLine.setEnableFlag("Y");
        hapInterfaceLine.setHeaderId(hapInterfaceHeaderList.get(0).getHeaderId());
        hapInterfaceLine.setLineCode(lineCode);
      return  hpInterfaceLineService.select(iRequest,hapInterfaceLine,1,10).get(0);
    }


    //ECIF接口批量创建调用
    @Override
    public HlsCusBpMasterRequestRecords wsEcifBatchCreateUpdate(IRequest iRequest, HlsCusBpMasterRequestRecords dto) {
        String stackTrace=null;
        String requestStatus=null;
        String sourceTable="WS_BP_MASTER_REQUEST_RECORDS";
        String sourceTableName="商业伙伴批量创建中间表";
        HlsCusHapInterfaceOutbound returnInfo=new HlsCusHapInterfaceOutbound();

        List<HlsCusBpMasterRequestRecords> returnBpMasterList=new ArrayList<>();
        //1.数据封装
        List<HlsCusBpMasterRequestRecords> listData=new ArrayList<>();
        if(dto.getOutboundId()!=null){//outboundId 不为空说明是重发
            listData= hlsCusBpMasterRequestRecordsMapper.selectByOutboundIdAndBpId(dto);
        }
        else if("BP".equals(dto.getDataType())){
            List<HlsCusBpMasterRequestRecords> recordsList=hlsCusBpMasterRequestRecordsMapper.selectByBpId(dto);

            for(HlsCusBpMasterRequestRecords item:recordsList){
                item=  this.insertSelective(iRequest,item);
                listData.add(item);
            }
        }
        else if("PROJECT".equals(dto.getDataType())){
            List<HlsCusBpMasterRequestRecords> recordsList=hlsCusBpMasterRequestRecordsMapper.selectByProjectId(dto);

            for(HlsCusBpMasterRequestRecords item:recordsList){
                item=  this.insertSelective(iRequest,item);
                listData.add(item);
            }
        }

        //没有数据不需要往下走
        if(listData.size()==0){
           return dto;
        }

        String soapXml=  ecifBatchCreateDataEncapsulation(iRequest,listData);
        //2.请求调用
        HapInterfaceLine hapInterfaceLine= getInterfaceUrl(iRequest,"ECIF_ITFC","ECIF_CREATE_UPDATE_LIST");
        String URL=hapInterfaceLine.getIftUrl();

        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();
        try{
            //2.请求调用
            returnInfo=ecifHttpPost(URL, soapXml);

            //3.解析xml
             returnBpMasterList= ecifBatchCreateXmlParsing(returnInfo.getResponseContent());
        }
        catch(Throwable throwable){
            //stackTrace=printStackTraceToString(e);
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }

        end = System.currentTimeMillis();
        endDate=new Date();
        //4.日志插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(URL);
        outbound.setRequestParameter(soapXml);
        if(stackTrace!=null){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
        }

        String responseCode=returnInfo.getResponseCode();

        for(HlsCusBpMasterRequestRecords item:returnBpMasterList){
            if(!"00000".equals(item.getCode())){
                requestStatus="failure";
                responseCode="";
            }
        }


        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnInfo.getResponseContent());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息success
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());
        HlsCusHapInterfaceOutbound outboundData= outboundInvokeInsert( iRequest,outbound);
        //5.日志信息与单据信息关联
        outboundAndDocumentRecordAssociated(iRequest, outbound, listData, sourceTable,sourceTableName);

        //6.业务逻辑处理
        for(HlsCusBpMasterRequestRecords item:returnBpMasterList){
            dto.setMsg(item.getMsg());
            dto.setCode(item.getCode());
            if("00000".equals(item.getCode())){//交易成功的
                //通过三要素查询系统中的客户
                for(HlsCusBpMasterRequestRecords bpRecord:listData){
                   if(bpRecord.getCstNmS().equals(item.getCstNmS())&&bpRecord.getInstIdentTp().equals(item.getInstIdentTp())&&bpRecord.getIdentNo().equals(item.getIdentNo())) {

                       if(!"Y".equals(bpRecord.getIsFirstSuccess())){//如果是首次推送的话插入版本信息表
                           HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange= new HlsCusEcifBpMasterChange();
                           hlsCusEcifBpMasterChange.setBpId(bpRecord.getBpId());
                           hlsCusEcifBpMasterChange = hlsCusEcifBpMasterChangeMapper.selectChangeInfoByBpId(hlsCusEcifBpMasterChange);

                           hlsCusEcifBpMasterChange.setOprtnInd("1");
                           Map<String, String> params = new HashMap<String, String>();
                           hlsCusEcifBpMasterChange.setChangeNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "BP_ECIF_CHANGE", "BP_ECIF_CHANGE", "BP_ECIF_CHANGE", params));
                           hlsCusEcifBpMasterChange.setCstNo(item.getCstNo());
                           hlsCusEcifBpMasterChange.setMsg(item.getMsg());
                           hlsCusEcifBpMasterChange.setWflStatus("APPROVED");
                           hlsCusEcifBpMasterChange.setSubmitUserId(iRequest.getUserId());
                           hlsCusEcifBpMasterChange.setSubmitDate(new Date());
                           hlsCusEcifBpMasterChangeMapper.insertSelective(hlsCusEcifBpMasterChange);

                       }
                       HlsCusBpMaster hlsBpMasterNew= new HlsCusBpMaster();
                       hlsBpMasterNew.setBpId(bpRecord.getBpId());
                       hlsBpMasterNew.setCustomerNumber(item.getCstNo());
                       hlsBpMasterNew.setIsFirstSuccess("Y");
                       hlsBpMasterService.updateByPrimaryKeySelective(iRequest,hlsBpMasterNew);
                   }
                }

            }
        }

        return dto;
    }


    //ECIF单笔查询接口调用
    @Override
    public HlsCusBpMasterRequestRecords wsEcifSignalQuery(IRequest iRequest, HlsCusBpMasterRequestRecords dto) {
        String stackTrace=null;
        String requestStatus=null;

        HlsCusHapInterfaceOutbound returnInfo=new HlsCusHapInterfaceOutbound();
        HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecordsReturnInfo= new HlsCusBpMasterRequestRecords();


        //1.数据封装
        List<HlsCusBpMasterRequestRecords> listData=new ArrayList<>();
        if(dto.getOutboundId()!=null){//outboundId 不为空说明是重发
            dto= hlsCusBpMasterRequestRecordsMapper.selectByOutboundId(dto).get(0);
        }
        else {
//            //操作标识
//            dto.setOprtnInd("1");
//            //总行客户号
//            dto.setCstNo("902000023885");
//            //客户名称
//            dto.setCstNmS("");
//            //证件号码
//            dto.setIdentNo("");
//            //证件类型
//            dto.setInstIdentTp("");
//            //统一社会信用代码
//            dto.setUnifdSoclCrdtCd("");
            dto = this.insertSelective(iRequest, dto);
        }
        listData.add(dto);
        String soapXml=  ecifSignalQueryDataEncapsulation(iRequest,dto);

        HapInterfaceLine hapInterfaceLine= getInterfaceUrl(iRequest,"ECIF_ITFC","ECIF_SINGLE_QUERY");
        String URL=hapInterfaceLine.getIftUrl();
        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();
        try{
            //2.请求调用
            returnInfo=ecifHttpPost(URL, soapXml);
            //3.解析xml
            hlsCusBpMasterRequestRecordsReturnInfo= ecifSignalQueryXmlParsing(returnInfo.getResponseContent());
            hlsCusBpMasterRequestRecordsReturnInfo = this.insertSelective(iRequest, hlsCusBpMasterRequestRecordsReturnInfo);
        }
        catch(Throwable throwable){
            //stackTrace=printStackTraceToString(e);
            hlsCusBpMasterRequestRecordsReturnInfo.setMsg("ECIF单一查询接口出错请联系管理员！");
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();
        //4.日志插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(URL);
        outbound.setRequestParameter(soapXml);
        if(stackTrace!=null){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
        }

        String responseCode=returnInfo.getResponseCode();

        if(hlsCusBpMasterRequestRecordsReturnInfo.getCstNo()==null){
            requestStatus="failure";
            responseCode="";
        }

        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnInfo.getResponseContent());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());

        HlsCusHapInterfaceOutbound outboundData= outboundInvokeInsert( iRequest,outbound);
        //5.日志信息与单据信息关联
        String sourceTable="WS_BP_MASTER_REQUEST_RECORDS";
        String sourceTableName="商业伙伴批量创建中间表";
        outboundAndDocumentRecordAssociated(iRequest, outbound, listData, sourceTable,sourceTableName);
        //6.业务逻辑处理


        return hlsCusBpMasterRequestRecordsReturnInfo;
    }




//    public static String printStackTraceToString(Throwable t) {
//     StringWriter sw = new StringWriter();
//     t.printStackTrace(new PrintWriter(sw, true));
//     return sw.getBuffer().toString();
//    }



    @Override
    public HlsCusBpMasterRequestRecords ecifSignalUpdate(IRequest iRequest, HlsCusBpMasterRequestRecords dto) {

        HlsCusBpMasterRequestRecords  hlsCusBpMasterRequestRecordsData=  hlsCusBpMasterRequestRecordsMapper.getDataByReturnToBusinessData(dto);

        HlsCusBpMaster hlsBpMaster= new HlsCusBpMaster();

        hlsBpMaster.setBpId(dto.getBpId());
        //客户名称
        hlsBpMaster.setBpName(hlsCusBpMasterRequestRecordsData.getCstNmS());
        //机构证件类型
        hlsBpMaster.setRegistrationNumType(hlsCusBpMasterRequestRecordsData.getInstIdentTp());
        //证件号码
        hlsBpMaster.setRegisterCertNum(hlsCusBpMasterRequestRecordsData.getIdentNo());
        //总行客户类型
        hlsBpMaster.setHeadOfficeBpType(hlsCusBpMasterRequestRecordsData.getCstTpC());
        //境外客户中文名称
        hlsBpMaster.setOverseasCustomerName(hlsCusBpMasterRequestRecordsData.getOvrsCstChinNm());
        //国家和地区代码 rgstrtn_cty_and_dstc_cd
        //首先查询此客户的注册地址信息是否维护了 否则新增一条
        HlsCusBpMasterAddress hlsCusBpMasterAddress = new HlsCusBpMasterAddress();
        hlsCusBpMasterAddress.setBpId(dto.getBpId());
        hlsCusBpMasterAddress.setAddressType("REGISTERED_ADRESS");//注册地址
        hlsCusBpMasterAddress.setEnabledFlag("Y");
        List<HlsCusBpMasterAddress>  hlsCusBpMasterAddressList=  hlsBpMasterAddressService.select(iRequest,hlsCusBpMasterAddress,1,100);
        if(hlsCusBpMasterAddressList.size()>0){
            HlsCusBpMasterAddress  hlsCusBpMasterAddressData=hlsCusBpMasterAddressList.get(0);
            if(!hlsCusBpMasterRequestRecordsData.getRgstrtnCtyAndDstcCdC().equals(hlsCusBpMasterAddressData.getCountry())){
                if("46".equals(hlsCusBpMasterRequestRecordsData.getRgstrtnCtyAndDstcCdC())){//不是中国时
                    hlsCusBpMasterAddressData.setProvince(null);
                    hlsCusBpMasterAddressData.setDistrict(null);
                    hlsCusBpMasterAddressData.setCity(null);
                    hlsCusBpMasterAddressData.setCountry(hlsCusBpMasterRequestRecordsData.getRgstrtnCtyAndDstcCdC());
                }
            }
            hlsBpMasterAddressService.updateByPrimaryKey(iRequest,hlsCusBpMasterAddressData);
        }
        else{
            HlsCusBpMasterAddress  hlsCusBpMasterAddressData= new HlsCusBpMasterAddress();
            hlsCusBpMasterAddressData.setBpId(dto.getBpId());
            hlsCusBpMasterAddressData.setCountry(hlsCusBpMasterRequestRecordsData.getRgstrtnCtyAndDstcCdC());
            hlsCusBpMasterAddressData.setAddressType("REGISTERED_ADRESS");
            hlsCusBpMasterAddressData.setEnabledFlag("Y");

            hlsBpMasterAddressService.updateByPrimaryKey(iRequest,hlsCusBpMasterAddressData);
        }

        //主要经营活动所在国家/地区
        hlsBpMaster.setBusinessArea(hlsCusBpMasterRequestRecordsData.getMaincorprtnactvctyordstccdC());
        //客户所属国标行业
        hlsBpMaster.setEconomicInduClassify(hlsCusBpMasterRequestRecordsData.getCstLoNatlStdIdyC());
        //SWIFT BIC
        hlsBpMaster.setSwift(hlsCusBpMasterRequestRecordsData.getSwiftno());
        //企业隶属关系
        hlsBpMaster.setEnterpriseAffiliation(hlsCusBpMasterRequestRecordsData.getEntpAtchRltnpC());
        //经济类型
        hlsBpMaster.setBpFinancialType(hlsCusBpMasterRequestRecordsData.getEcnmTpC());
        //法定代表人/经营者名称
        hlsBpMaster.setLegalPerson(hlsCusBpMasterRequestRecordsData.getLglRprsNm());
        //经营范围
        hlsBpMaster.setBusinessScope(hlsCusBpMasterRequestRecordsData.getCorprtnScop1());
        //信用客户标志
        hlsBpMaster.setCreditCustomer(hlsCusBpMasterRequestRecordsData.getCrdtCstFlgC());
        //客户类型
        hlsBpMaster.setBpType(hlsCusBpMasterRequestRecordsData.getOrgCustTypeC());

        hlsBpMasterService.updateByPrimaryKeySelective(iRequest,hlsBpMaster);

        return dto;
    }



    //客户变更查询接口数据封装
    public String ecifChangeQueryDataEncapsulation(IRequest iRequest,  String changeDate) {
        String  soapXml=null;

        soapXml ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cus=\"http://service.ws.ecif.cib.com/CustChangeInfoServiceImpl\">\n"+
   "<soapenv:Header/>\n"+
   "<soapenv:Body>\n"+
      "<cus:selectCustChangeInfo>\n"+
         "<!--Optional:-->\n"+
         "<changeDate>"+changeDate+"</changeDate>\n"+
      "</cus:selectCustChangeInfo>\n"+
   "</soapenv:Body>\n"+
"</soapenv:Envelope>";

        return soapXml;
    }




    //批量创建接口xml解析
    public List<HlsCusBpMasterRequestRecords> ecifChangeQueryXmlParsing(IRequest iRequest,String retStr)throws Exception {
        List<HlsCusBpMasterRequestRecords> bpMasterList = new ArrayList<>();
        try {
            // 创建SAXReader阅读器对象
            SAXReader reader = new SAXReader();
            InputStream is = new ByteArrayInputStream(retStr.getBytes(CHARSET));
            Document document = reader.read(is);
            // 获得文档对象的根节点
            Element root = document.getRootElement();
            // 获取根节点下面名叫Body的节点
            Element Body= root.element("Body");
            // 获取Body节点下面名叫selectCustChangeInfoResponse的节点
            List<Element> selectCustChangeInfoResponse = Body.element("selectCustChangeInfoResponse").elements();
            // 获取selectCustChangeInfoResponse节点下面名叫custChangeInfoVO的节点清单
            for (int i=0; i<selectCustChangeInfoResponse.size(); i++) {
                HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords =new HlsCusBpMasterRequestRecords();
                Element item = selectCustChangeInfoResponse.get(i);

//                String code=  item.element("code").getText();
//                hlsCusBpMasterRequestRecords.setCode(code);

                //总行客户号
                String cstNo=  item.element("cstNo").getText();
                hlsCusBpMasterRequestRecords.setCstNo(cstNo);
                //客户名称
                String cstNmS=  item.element("cstNmS").getText();
                hlsCusBpMasterRequestRecords.setCstNmS(cstNmS);
                //机构证件类型
                String instIdentTp=  item.element("instIdentTp").getText();
                hlsCusBpMasterRequestRecords.setInstIdentTp(instIdentTp);
                //证件号码
                String identNo=  item.element("identNo").getText();
                hlsCusBpMasterRequestRecords.setIdentNo(identNo);
                //统一社会信用代码
                String unifdSoclCrdtCd=  item.element("unifdSoclCrdtCd").getText();
                hlsCusBpMasterRequestRecords.setUnifdSoclCrdtCd(unifdSoclCrdtCd);
                //客户类型
                String cstTp=  item.element("cstTp").getText();
                hlsCusBpMasterRequestRecords.setCstTp(cstTp);
                //境外客户中文名称
//                String ovrsCstChinNm=  item.element("ovrsCstChinNm").getText();
//                hlsCusBpMasterRequestRecords.setOvrsCstChinNm(ovrsCstChinNm);
                //国家和地区代码
                String rgstrtnCtyAndDstcCd=  item.element("rgstrtnCtyAndDstcCd").getText();
                hlsCusBpMasterRequestRecords.setRgstrtnCtyAndDstcCd(rgstrtnCtyAndDstcCd);
                //主要经营活动所在国家/地区
                String mainCorprtnActvCtyOrDstcCd=  item.element("mainCorprtnActvCtyOrDstcCd").getText();
                hlsCusBpMasterRequestRecords.setMaincorprtnactvctyordstccd(mainCorprtnActvCtyOrDstcCd);
                //客户所属国标行业
//                String cstLoNatlStdIdy=  item.element("cstLoNatlStdIdy").getText();
//                hlsCusBpMasterRequestRecords.setCstLoNatlStdIdy(cstLoNatlStdIdy);
                //企业隶属关系
                String entpAtchRltnp=  item.element("entpAtchRltnp").getText();
                hlsCusBpMasterRequestRecords.setEntpAtchRltnp(entpAtchRltnp);
                //经济类型
                String ecnmTp=  item.element("ecnmTp").getText();
                hlsCusBpMasterRequestRecords.setEcnmTp(ecnmTp);
                //法定代表人/经营者名称
                String lglRprsNm=  item.element("lglRprsNm").getText();
                hlsCusBpMasterRequestRecords.setLglRprsNm(lglRprsNm);
                //经营范围
                String corprtnScop1=  item.element("corprtnScop1").getText();
                hlsCusBpMasterRequestRecords.setCorprtnScop1(corprtnScop1);
                //信用客户标志
                String crdtCstFlg=  item.element("crdtCstFlg").getText();
                hlsCusBpMasterRequestRecords.setCrdtCstFlg(crdtCstFlg);
                //操作类型
                String operatorType=  item.element("operatorType").getText();
                hlsCusBpMasterRequestRecords.setOperatorType(operatorType);
                //变更时间
                String changeDate=  item.element("changeDate").getText();
                hlsCusBpMasterRequestRecords.setChangeDate(changeDate);
                //操作人
                String operatorUser=  item.element("operatorUser").getText();
                hlsCusBpMasterRequestRecords.setOperatorUser(operatorUser);

                hlsCusBpMasterRequestRecords.setRequestDate(new Date());

//                String msg=  item.element("msg").getText();
//                hlsCusBpMasterRequestRecords.setMsg(msg);

//                hlsCusBpMasterRequestRecords.setCode(code);

                //传过来的一版
                hlsCusBpMasterRequestRecordsMapper.insertSelective(hlsCusBpMasterRequestRecords);

                //通过ECIF客户编号获取系统当前版
                HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecordsP2 =new HlsCusBpMasterRequestRecords();
                        hlsCusBpMasterRequestRecordsP2.setCstNo(cstNo);
                List<HlsCusBpMasterRequestRecords> hlsCusBpMasterRequestRecordsList=  hlsCusBpMasterRequestRecordsMapper.selectByCstNo(hlsCusBpMasterRequestRecordsP2);
                if(hlsCusBpMasterRequestRecordsList.size()>0){
                    HlsCusBpMasterRequestRecords bpItem =new HlsCusBpMasterRequestRecords();
                    bpItem=hlsCusBpMasterRequestRecordsList.get(0);
                    if("Y".equals(bpItem.getIsFirstSuccess())){//是否在系统中推送过

                        // !bpItem.getUnifdSoclCrdtCd().equals(unifdSoclCrdtCd) 统一社会信用代码不管
                     if(!bpItem.getCstNmS().equals(cstNmS)||
                             !bpItem.getInstIdentTp().equals(instIdentTp)||
                             !bpItem.getIdentNo().equals(identNo)||
                             !bpItem.getCstTp().equals(cstTp)||
                             !bpItem.getRgstrtnCtyAndDstcCd().equals(rgstrtnCtyAndDstcCd)||
                             !bpItem.getMaincorprtnactvctyordstccd().equals(mainCorprtnActvCtyOrDstcCd)||
                             !bpItem.getEntpAtchRltnp().equals(entpAtchRltnp)||
                             !bpItem.getEcnmTp().equals(ecnmTp)||
                             !bpItem.getLglRprsNm().equals(lglRprsNm)||
                             !bpItem.getCorprtnScop1().equals(corprtnScop1)||
                             !bpItem.getCrdtCstFlg().equals(crdtCstFlg)
                             ){//对比系统中数据是否有变化
                         HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecordsP =new HlsCusBpMasterRequestRecords();
                         hlsCusBpMasterRequestRecordsP.setBpRecordId(hlsCusBpMasterRequestRecords.getBpRecordId());
                         hlsCusBpMasterRequestRecordsP= hlsCusBpMasterRequestRecordsMapper.getDataByReturnToBusinessData(hlsCusBpMasterRequestRecordsP);
                         hlsCusBpMasterRequestRecordsP.setChangeDate(changeDate);
                         hlsCusBpMasterRequestRecordsP.setOperatorUser("ECIF平台-"+operatorUser);
                         hlsCusBpMasterRequestRecordsP.setBpId(bpItem.getBpId());




                         //先进行数据备份 备份传过来的数据
                         HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange = new HlsCusEcifBpMasterChange();
                         hlsCusEcifBpMasterChange.setBpId(hlsCusBpMasterRequestRecordsP.getBpId());
                         hlsCusEcifBpMasterChange.setCstNmS(hlsCusBpMasterRequestRecordsP.getCstNmS());
                         hlsCusEcifBpMasterChange.setInstIdentTp(hlsCusBpMasterRequestRecordsP.getInstIdentTpC());
                         hlsCusEcifBpMasterChange.setIdentNo(hlsCusBpMasterRequestRecordsP.getIdentNo());
                         hlsCusEcifBpMasterChange.setCstTp(hlsCusBpMasterRequestRecordsP.getCstTpC());
                         hlsCusEcifBpMasterChange.setOvrsCstChinNm(hlsCusBpMasterRequestRecordsP.getBeforeOvrsCstChinNm());
                         hlsCusEcifBpMasterChange.setRgstrtnCtyAndDstcCd(hlsCusBpMasterRequestRecordsP.getRgstrtnCtyAndDstcCdC());
                         hlsCusEcifBpMasterChange.setMaincorprtnactvctyordstccd(hlsCusBpMasterRequestRecordsP.getMaincorprtnactvctyordstccdC());
                         hlsCusEcifBpMasterChange.setCstLoNatlStdIdy(hlsCusBpMasterRequestRecordsP.getBeforeCstLoNatlStdIdy());
                         hlsCusEcifBpMasterChange.setSwiftno(hlsCusBpMasterRequestRecordsP.getBeforeSwiftno());
                         hlsCusEcifBpMasterChange.setEntpAtchRltnp(hlsCusBpMasterRequestRecordsP.getEntpAtchRltnpC());
                         hlsCusEcifBpMasterChange.setEcnmTp(hlsCusBpMasterRequestRecordsP.getEcnmTpC());
                         hlsCusEcifBpMasterChange.setLglRprsNm(hlsCusBpMasterRequestRecordsP.getLglRprsNm());
                         hlsCusEcifBpMasterChange.setCorprtnScop1(hlsCusBpMasterRequestRecordsP.getCorprtnScop1());
                         hlsCusEcifBpMasterChange.setCrdtCstFlg(hlsCusBpMasterRequestRecordsP.getCrdtCstFlgC());
                         hlsCusEcifBpMasterChange.setCstNo(hlsCusBpMasterRequestRecordsP.getCstNo());

                         if("07".equals(hlsCusBpMasterRequestRecordsP.getInstIdentTpC())||"02".equals(hlsCusBpMasterRequestRecordsP.getInstIdentTpC())){
                             hlsCusEcifBpMasterChange.setUnifdSoclCrdtCd(hlsCusBpMasterRequestRecordsP.getIdentNo());
                         }


                         hlsCusEcifBpMasterChange.setSubmitDate(new Date());
                         hlsCusEcifBpMasterChange.setWflStatus("APPROVED");
                         hlsCusEcifBpMasterChange.setOperatorUser(hlsCusBpMasterRequestRecordsP.getOperatorUser());
                         Map<String, String> params = new HashMap<String, String>();
                         hlsCusEcifBpMasterChange.setChangeNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "BP_ECIF_CHANGE", "BP_ECIF_CHANGE", "BP_ECIF_CHANGE", params));
                         hlsCusEcifBpMasterChangeMapper.insertSelective(hlsCusEcifBpMasterChange);

                         //覆盖数据
                         dataBackBpMasterChange(iRequest, hlsCusEcifBpMasterChange);

                         //bpMasterList.add(hlsCusBpMasterRequestRecordsP);
                     }
                    }

                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new IOException(e);
        }
        return bpMasterList;
    }




    //ECIF客户变更查询接口调用
    @Override
    public void wsEcifChangeQuery(IRequest iRequest, String changeDate) {
        String stackTrace=null;
        String requestStatus=null;

        HlsCusHapInterfaceOutbound returnInfo=new HlsCusHapInterfaceOutbound();
        List<HlsCusBpMasterRequestRecords> returnBpMasterList=new ArrayList<>();


        //1.数据封装
        String soapXml=  ecifChangeQueryDataEncapsulation(iRequest,changeDate);

        HapInterfaceLine hapInterfaceLine= getInterfaceUrl(iRequest,"ECIF_ITFC","ECIF_CHANGE_QUERY");
        String URL=hapInterfaceLine.getIftUrl();
        Date endDate=null;
        Date startDate=null;
        long start=0l;
        long end=0l;
        start = System.currentTimeMillis();
        startDate=new Date();
        try{
            //2.请求调用
            returnInfo=ecifHttpPost(URL, soapXml);
            //3.解析xml
            returnBpMasterList= ecifChangeQueryXmlParsing(iRequest,returnInfo.getResponseContent());

        }
        catch(Throwable throwable){
            //stackTrace=printStackTraceToString(e);
            stackTrace= HapInvokeLogUtils.getRootCauseStackTrace(throwable);
            throwable.printStackTrace();
        }
        end = System.currentTimeMillis();
        endDate=new Date();
        //4.日志插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName(hapInterfaceLine.getLineName());
        outbound.setInterfaceUrl(URL);
        outbound.setRequestParameter(soapXml);
        if(stackTrace!=null){
            requestStatus="failure";
        }
        else{
            requestStatus="success";
        }

        String responseCode=returnInfo.getResponseCode();

//        for(HlsCusBpMasterRequestRecords item:returnBpMasterList){
//            if(!"00000".equals(item.getCode())){
//                requestStatus="failure";
//                responseCode="";
//            }
//        }
        outbound.setRequestStatus(requestStatus);
        outbound.setResponseContent(returnInfo.getResponseContent());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setStackTrace(stackTrace);//错误信息
        outbound.setResponseCode(responseCode);//请求code
        outbound.setLineId(hapInterfaceLine.getLineId());

        HlsCusHapInterfaceOutbound outboundData= outboundInvokeInsert( iRequest,outbound);
//        //5.日志信息与单据信息关联
//        String sourceTable="WS_BP_MASTER_REQUEST_RECORDS";
//        String sourceTableName="商业伙伴批量创建中间表";
//        outboundAndDocumentRecordAssociated(iRequest, outbound, listData, sourceTable,sourceTableName);

    }


    public void dataBackBpMaster(IRequest iRequest,HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange) {
        HlsCusBpMaster hlsBpMaster= new HlsCusBpMaster();

        hlsBpMaster.setBpId(hlsCusEcifBpMasterChange.getBpId());
        //客户名称
        hlsBpMaster.setBpName(hlsCusEcifBpMasterChange.getCstNmS());
        //机构证件类型
        hlsBpMaster.setRegistrationNumType(hlsCusEcifBpMasterChange.getInstIdentTp());
        //证件号码
        hlsBpMaster.setRegisterCertNum(hlsCusEcifBpMasterChange.getIdentNo());
        //总行客户类型
        hlsBpMaster.setHeadOfficeBpType(hlsCusEcifBpMasterChange.getCstTp());
        //境外客户中文名称
        hlsBpMaster.setOverseasCustomerName(hlsCusEcifBpMasterChange.getOvrsCstChinNm());
        //国家和地区代码 rgstrtn_cty_and_dstc_cd
        //首先查询此客户的注册地址信息是否维护了 否则新增一条
        HlsCusBpMasterAddress hlsCusBpMasterAddress = new HlsCusBpMasterAddress();
        hlsCusBpMasterAddress.setBpId(hlsCusEcifBpMasterChange.getBpId());
        hlsCusBpMasterAddress.setAddressType("REGISTERED_ADDRESS");//注册地址
        hlsCusBpMasterAddress.setEnabledFlag("Y");
        List<HlsCusBpMasterAddress>  hlsCusBpMasterAddressList=  hlsBpMasterAddressService.select(iRequest,hlsCusBpMasterAddress,1,100);
        if(hlsCusBpMasterAddressList.size()>0){
            HlsCusBpMasterAddress  hlsCusBpMasterAddressData=hlsCusBpMasterAddressList.get(0);
            if(!hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd().equals(hlsCusBpMasterAddressData.getCountry())){
                if("46".equals(hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd())){//不是中国时
                    hlsCusBpMasterAddressData.setProvince(null);
                    hlsCusBpMasterAddressData.setDistrict(null);
                    hlsCusBpMasterAddressData.setCity(null);
                    hlsCusBpMasterAddressData.setCountry(hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd());
                }
            }
            hlsBpMasterAddressService.updateByPrimaryKey(iRequest,hlsCusBpMasterAddressData);
        }
        else{
            HlsCusBpMasterAddress  hlsCusBpMasterAddressData= new HlsCusBpMasterAddress();
            hlsCusBpMasterAddressData.setBpId(hlsCusEcifBpMasterChange.getBpId());
            hlsCusBpMasterAddressData.setCountry(hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd());
            hlsCusBpMasterAddressData.setAddressType("REGISTERED_ADDRESS");
            hlsCusBpMasterAddressData.setEnabledFlag("Y");

            hlsBpMasterAddressService.insertSelective(iRequest,hlsCusBpMasterAddressData);
        }


        //主要经营活动所在国家/地区
        hlsBpMaster.setBusinessArea(hlsCusEcifBpMasterChange.getMaincorprtnactvctyordstccd());
        //客户所属国标行业
        hlsBpMaster.setEconomicInduClassify(hlsCusEcifBpMasterChange.getCstLoNatlStdIdy());
        //SWIFT BIC
        hlsBpMaster.setSwift(hlsCusEcifBpMasterChange.getSwiftno());
        //企业隶属关系
        hlsBpMaster.setEnterpriseAffiliation(hlsCusEcifBpMasterChange.getEntpAtchRltnp());
        //经济类型
        hlsBpMaster.setBpFinancialType(hlsCusEcifBpMasterChange.getEcnmTp());
        //法定代表人/经营者名称
        hlsBpMaster.setLegalPerson(hlsCusEcifBpMasterChange.getLglRprsNm());
        //经营范围
        hlsBpMaster.setBusinessScope(hlsCusEcifBpMasterChange.getCorprtnScop1());
        //信用客户标志
        hlsBpMaster.setCreditCustomer(hlsCusEcifBpMasterChange.getCrdtCstFlg());
        //客户类型
        //hlsBpMaster.setBpType(hlsCusEcifBpMasterChange.getOrgCustType());

        hlsBpMasterService.updateByPrimaryKeySelective(iRequest,hlsBpMaster);

    }

    public void dataBackBpMasterChange(IRequest iRequest,HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange) {
        HlsCusBpMaster hlsBpMaster= new HlsCusBpMaster();

        hlsBpMaster.setBpId(hlsCusEcifBpMasterChange.getBpId());
        //客户名称
        hlsBpMaster.setBpName(hlsCusEcifBpMasterChange.getCstNmS());
        //机构证件类型
        hlsBpMaster.setRegistrationNumType(hlsCusEcifBpMasterChange.getInstIdentTp());
        //证件号码
        hlsBpMaster.setRegisterCertNum(hlsCusEcifBpMasterChange.getIdentNo());
        //总行客户类型
        //hlsBpMaster.setHeadOfficeBpType(hlsCusEcifBpMasterChange.getCstTp());
        //境外客户中文名称
        //hlsBpMaster.setOverseasCustomerName(hlsCusEcifBpMasterChange.getOvrsCstChinNm());
        //国家和地区代码 rgstrtn_cty_and_dstc_cd
        //首先查询此客户的注册地址信息是否维护了 否则新增一条
        HlsCusBpMasterAddress hlsCusBpMasterAddress = new HlsCusBpMasterAddress();
        hlsCusBpMasterAddress.setBpId(hlsCusEcifBpMasterChange.getBpId());
        hlsCusBpMasterAddress.setAddressType("REGISTERED_ADDRESS");//注册地址
        hlsCusBpMasterAddress.setEnabledFlag("Y");
        List<HlsCusBpMasterAddress>  hlsCusBpMasterAddressList=  hlsBpMasterAddressService.select(iRequest,hlsCusBpMasterAddress,1,100);
        if(hlsCusBpMasterAddressList.size()>0){
            HlsCusBpMasterAddress  hlsCusBpMasterAddressData=hlsCusBpMasterAddressList.get(0);
            if(!hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd().equals(hlsCusBpMasterAddressData.getCountry())){
                if("46".equals(hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd())){//不是中国时
                    hlsCusBpMasterAddressData.setProvince(null);
                    hlsCusBpMasterAddressData.setDistrict(null);
                    hlsCusBpMasterAddressData.setCity(null);
                    hlsCusBpMasterAddressData.setCountry(hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd());
                }
            }
            hlsBpMasterAddressService.updateByPrimaryKey(iRequest,hlsCusBpMasterAddressData);
        }
        else{
            HlsCusBpMasterAddress  hlsCusBpMasterAddressData= new HlsCusBpMasterAddress();
            hlsCusBpMasterAddressData.setBpId(hlsCusEcifBpMasterChange.getBpId());
            hlsCusBpMasterAddressData.setCountry(hlsCusEcifBpMasterChange.getRgstrtnCtyAndDstcCd());
            hlsCusBpMasterAddressData.setAddressType("REGISTERED_ADDRESS");
            hlsCusBpMasterAddressData.setEnabledFlag("Y");

            hlsBpMasterAddressService.insertSelective(iRequest,hlsCusBpMasterAddressData);
        }


        //主要经营活动所在国家/地区
        hlsBpMaster.setBusinessArea(hlsCusEcifBpMasterChange.getMaincorprtnactvctyordstccd());
        //客户所属国标行业
       // hlsBpMaster.setEconomicInduClassify(hlsCusEcifBpMasterChange.getCstLoNatlStdIdy());
        //SWIFT BIC
        //hlsBpMaster.setSwift(hlsCusEcifBpMasterChange.getSwiftno());
        //企业隶属关系
        hlsBpMaster.setEnterpriseAffiliation(hlsCusEcifBpMasterChange.getEntpAtchRltnp());
        //经济类型
        hlsBpMaster.setBpFinancialType(hlsCusEcifBpMasterChange.getEcnmTp());
        //法定代表人/经营者名称
        hlsBpMaster.setLegalPerson(hlsCusEcifBpMasterChange.getLglRprsNm());
        //经营范围
        hlsBpMaster.setBusinessScope(hlsCusEcifBpMasterChange.getCorprtnScop1());
        //信用客户标志
        hlsBpMaster.setCreditCustomer(hlsCusEcifBpMasterChange.getCrdtCstFlg());
        //客户类型
        //hlsBpMaster.setBpType(hlsCusEcifBpMasterChange.getOrgCustType());

        hlsBpMasterService.updateByPrimaryKeySelective(iRequest,hlsBpMaster);

    }

}