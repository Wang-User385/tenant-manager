package com.hand.hls.webexcel.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.bp.mapper.HlsBpFinancialHeaderMapper;
import com.hand.hls.bp.service.IHlsBpFinancialHeaderService;
import com.hand.hls.fnd.service.HlsFinStatementHdService;
import com.hand.hls.fnd.service.HlsFinStatementLnService;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import com.hand.hls.hls.mapper.HlsWebExcelCalcResultMapper;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;
import com.hand.hls.pam.mapper.HlsCusLeaseItemMapper;
import com.hand.hls.pam.mapper.HlsLeaseItemDetailMapper;
import com.hand.hls.pam.service.HlsCusLeaseItemService;
import com.hand.hls.pam.service.IHlsLeaseItemDetailService;
import com.hand.hls.webexcel.service.IWebExcelCalcService;
import com.hand.hls.webexcel.service.IWebExcelCalcUtilService;
import hls.core.utils.exception.HlsCusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaseRentalReportWebExcelCalc implements IWebExcelCalcService {

    //根据类型获取对应的实现类
    private final static String SOURCE_DOCUMENT_CATEGORY = "HLS_LEASE_ITEM_RENTAL";

    //webexcel存储的单据类型
    private final static String WEB_EXCEL_RESULT_CATEGORY = "LEASE_RENTAL_REPORT";

    private static final String SINGLE = "SINGLE";
    private static final String MULTI_LINE = "MULTI_LINE";

    private final static String HD_SHEET_NAME = "Sheet1";

    private final static String YEAR_STR = "年";
    private final static String MONTH_STR = "月";

    private final static String LEASE_CATEGORY = "月";

    @Autowired
    private IWebExcelCalcUtilService webExcelCalcUtilService;

    @Autowired
    private IHlsBpFinancialHeaderService iHlsBpFinancialHeaderService;

    @Autowired
    private HlsBpFinancialHeaderMapper hlsBpFinancialHeaderMapper;

    @Autowired
    private HlsWebExcelCalcResultMapper hlsWebExcelCalcResultMapper;

    @Autowired
    private HlsFinStatementHdService hlsFinStatementHdService;

    @Autowired
    private HlsFinStatementLnService hlsFinStatementLnService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IHlsLeaseItemDetailService hlsLeaseItemDetailService;

    @Autowired
    private HlsLeaseItemDetailMapper hlsLeaseItemDetailMapper;

    @Autowired
    private HlsCusLeaseItemService hlsCusLeaseItemService;

    @Autowired
    private HlsCusLeaseItemMapper hlsCusLeaseItemMapper;

    @Override
    public HlsWebExcelCalcResult calcExcel(IRequest iRequest, HlsWebExcelCalcResult calcResult) throws HlsCusException {

        //获取未压缩的sheets
        String sheets = calcResult.getSheets();
        Map map = new HashMap();

        //设置表对应的dto路径
        map.put("hls_lease_item_detail","com.hand.hls.pam.dto.HlsLeaseItemDetail");

        //解析头配置信息
         /*     List<Map> hdList = new ArrayList<>();
        try {
            hdList  = webExcelCalcUtilService.getExcelSingleListByHdInfo(iRequest,sheets,calcResult.getExcelId(),map,SINGLE);
        } catch (Exception e) {
            logger.info("获取excel头信息失败，{}",e.getMessage());
            throw new HlsCusException("头信息解析失败，请检查头配置" + e.getLocalizedMessage());
        }*/


        //解析行配置信息
        List<Map> lnList = new ArrayList<>();
        try {
            lnList  = webExcelCalcUtilService.getExcelSingleListByHdInfo(iRequest,sheets,calcResult.getExcelId(),map,MULTI_LINE);
        } catch (Exception e) {
            logger.info("获取excel行信息失败，{}",e.getMessage());
            throw new HlsCusException("行信息解析失败，请检查头配置" + e.getLocalizedMessage());
        }

        if(calcResult.getResultId() == null){
            if(calcResult.getSourceDocumentId() == null){
                throw new HlsCusException("单据信息异常,请联系管理员!");
            }
        }else {
            calcResult = hlsWebExcelCalcResultMapper.selectByPrimaryKey(calcResult);
        }

        HlsLeaseItemDetail hlsLeaseItemDetail = new HlsLeaseItemDetail();
        hlsLeaseItemDetail.setLeaseItemId(calcResult.getSourceDocumentId());
        hlsLeaseItemDetailMapper.deleteLeaseItemDetailByLeaseItemId(hlsLeaseItemDetail);

        //租赁物明细数据校验
        for(Map<String,List> result: lnList){
            for(String key : result.keySet()){
                List<HlsLeaseItemDetail> leaseItemDetailList = result.get(key);
                if(HD_SHEET_NAME.equals(key)){
                    //Sheet1 数据直接插表,
                    for(int i = 1; i < leaseItemDetailList.size(); i++){
                        HlsLeaseItemDetail itemDetail = leaseItemDetailList.get(i);
                        if(itemDetail.getLeaseFullName() != null){
                            if(itemDetail.getQuantity() == null){
                               throw new HlsCusException("数量不能未空！");
                            }
                            if(itemDetail.getNetBookValue() == null){
                                throw new HlsCusException("账面价值（元）不能未空！");
                            }
                            if(itemDetail.getNetAssetValue() == null){
                                throw new HlsCusException("账面净值（元）不能未空！");
                            }
                        }
                    }
                }

            }

        }

        //租赁物明细数据存表
        for(Map<String,List> result: lnList){
            for(String key : result.keySet()){
                List<HlsLeaseItemDetail> leaseItemDetailList = result.get(key);
                if(HD_SHEET_NAME.equals(key)){
                    //Sheet1 数据直接插表,
                    for(int i = 1; i < leaseItemDetailList.size(); i++){
                        HlsLeaseItemDetail itemDetail = leaseItemDetailList.get(i);
                        if(itemDetail.getLeaseFullName() != null){
                            itemDetail.setLeaseItemId(calcResult.getSourceDocumentId());
                            itemDetail.setLeaseCategory("LEASE");
                            hlsLeaseItemDetailService.insert(iRequest, itemDetail);

                        }
                    }
                }

            }

        }

        return calcResult;
    }

    @Override
    public String getSourceDocumentCategory() {
        return SOURCE_DOCUMENT_CATEGORY;
    }

}
