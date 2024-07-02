package com.hand.hls.webexcel.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import com.hand.hls.hls.mapper.HlsWebExcelCalcResultMapper;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;
import com.hand.hls.pam.mapper.HlsLeaseItemDetailMapper;
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
public class PledgeDetailWebExcelCalc implements IWebExcelCalcService {

    //根据类型获取对应的实现类
    private final static String SOURCE_DOCUMENT_CATEGORY = "MORTGAGE_PLEDGE_REPORT";

    //webexcel存储的单据类型
    private final static String WEB_EXCEL_RESULT_CATEGORY = "MORTGAGE_PLEDGE_REPORT";

    private static final String MULTI_LINE = "MULTI_LINE";

    private final static String LN_SHEET_NAME = "抵质押物明细";

    private final static String YEAR_STR = "年";
    private final static String MONTH_STR = "月";

    private final static String FIN_SHEET_NAME = "财务指标";

    @Autowired
    private IWebExcelCalcUtilService webExcelCalcUtilService;


    @Autowired
    private HlsWebExcelCalcResultMapper hlsWebExcelCalcResultMapper;

    @Autowired
    private IHlsLeaseItemDetailService hlsLeaseItemDetailService;
    @Autowired
    private HlsLeaseItemDetailMapper hlsLeaseItemDetailMapper;


    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public HlsWebExcelCalcResult calcExcel(IRequest iRequest, HlsWebExcelCalcResult calcResult) throws HlsCusException {

        //获取未压缩的sheets
        String sheets = calcResult.getSheets();
        Map map = new HashMap();

        //设置表对应的dto路径
        map.put("hls_lease_item_detail", "com.hand.hls.pam.dto.HlsLeaseItemDetail");


        //解析行配置信息
        List<Map> lnList = new ArrayList<>();
        try {
            lnList = webExcelCalcUtilService.getExcelSingleListByHdInfo(iRequest, sheets, calcResult.getExcelId(), map, MULTI_LINE);
        } catch (Exception e) {
            logger.info("获取excel行信息失败，{}", e.getMessage());
            throw new HlsCusException("行信息解析失败，请检查头配置" + e.getLocalizedMessage());
        }

        if (calcResult.getResultId() == null) {
            if (calcResult.getSourceDocumentId() == null) {
                throw new HlsCusException("单据信息异常,请联系管理员!");
            }
        } else {
            calcResult = hlsWebExcelCalcResultMapper.selectByPrimaryKey(calcResult);
        }


        //每次重算先删除已保存的业务信息
        HlsLeaseItemDetail hlsLeaseItemDetail = new HlsLeaseItemDetail();
        hlsLeaseItemDetail.setLeaseItemId(calcResult.getSourceDocumentId());
        hlsLeaseItemDetailMapper.deleteLeaseItemDetailByLeaseItemId(hlsLeaseItemDetail);

        //抵质押物明细数据校验
        for (Map result : lnList) {
            if (result.get(LN_SHEET_NAME) != null) {
                List<HlsLeaseItemDetail> hlsLeaseItemDetails = (List<HlsLeaseItemDetail>) result.get(LN_SHEET_NAME);
                for(int i = 1; i < hlsLeaseItemDetails.size(); i++){
                    HlsLeaseItemDetail leaseItemDetail = hlsLeaseItemDetails.get(i);
                    if(leaseItemDetail.getLeaseFullName() != null) {
                        if(leaseItemDetail.getQuantity() == null){
                            throw new HlsCusException("数量不能未空！");
                        }
                        if(leaseItemDetail.getNetBookValue() == null){
                            throw new HlsCusException("账面价值（元）不能未空！");
                        }
                        if(leaseItemDetail.getNetAssetValue() == null){
                            throw new HlsCusException("账面净值（元）不能未空！");
                        }
                        if(leaseItemDetail.getEvaluationValue() == null){
                            throw new HlsCusException("评估价值不能未空！");
                        }
                        if(leaseItemDetail.getMortgagePledgeAmount() == null){
                            throw new HlsCusException("抵质押金额（元）不能未空！");
                        }
                    }
                }

            }
        }

        for (Map result : lnList) {
            if (result.get(LN_SHEET_NAME) != null) {
                List<HlsLeaseItemDetail> hlsLeaseItemDetails = (List<HlsLeaseItemDetail>) result.get(LN_SHEET_NAME);

                for(int i = 1; i < hlsLeaseItemDetails.size(); i++){
                    HlsLeaseItemDetail leaseItemDetail = hlsLeaseItemDetails.get(i);
                    if(leaseItemDetail.getLeaseFullName() != null) {
                        leaseItemDetail.setLeaseItemId(calcResult.getSourceDocumentId());
                        leaseItemDetail.setLeaseCategory("PLEDGE");
                        hlsLeaseItemDetailService.insert(iRequest, leaseItemDetail);
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
