package com.hand.hls.fnd.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.fnd.service.ICalcPriceService;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationCompareService;
import com.hand.hls.hls.service.HlsDurationLnService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.utils.HlsCusMathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContractChangePrjQuotationServiceImpl extends BaseServiceImpl<HlsCusPrjQuotation> implements QuotationCommon {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String FLOATING_TYPE_FLOAT = "FLOATING";
    private static final String LPR_ADJUST_PERIOD_THIS = "THIS_PERIOD";
    private static final String LPR_ADJUST_PERIOD_NEXT = "NEXT_PERIOD";

    private static final String sourceDocumentCategory = "CON_CONTRACT_CHANGE";
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    @Autowired
    HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    ICalcPriceService iCalcPriceService;
    @Autowired
    HlsDurationLnService hlsDurationLnService;
    @Autowired
    HlsDurationLnMapper hlsDurationLnMapper;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsDurationCompareService hlsDurataionCompareService;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private static final String LPR_ADJUSTMENT_TERM_0 = "0";

    @Override
    public String getSourceDocumentCategory() {
        return sourceDocumentCategory;
    }

    @Override
    public HlsCusPrjQuotation PrjQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation prjQuotation) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String jsonStr = JSON.toJSONString(hlsCusCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, prjQuotation.getSheets(), prjQuotation.getPriceList(), "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);
        if (prjQuotationDto.getLeaseTimes() == null) {
            throw new IllegalArgumentException("期数未取到！");
        }
        prjQuotationDto.setPriceList(prjQuotation.getPriceList());
        prjQuotationDto.setSheets(prjQuotation.getSheets());
        prjQuotationDto.setCompressSheets(prjQuotation.getCompressSheets());
        prjQuotationDto.setSourceDocumentCategory(prjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setSourceDocumentId(prjQuotation.getSourceDocumentId());
        prjQuotationDto.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationDto.setDataClass("NORMAL");

        List<HlsCusPrjQuotation> prjQuotationList = prjQuotationMapper.prjQuotationDetailQuery(prjQuotationDto);
        if (prjQuotationList.size() == 1) {
            prjQuotationDto.setQuotationId(prjQuotationList.get(0).getQuotationId());
            prjQuotationDto.setSourceDocumentId(prjQuotationList.get(0).getSourceDocumentId());
        } else if (prjQuotationList.size() > 0) {
            throw new IllegalArgumentException("当前单据下存在多条报价信息，请清除多余数据！");
        }
        prjQuotationDto.setStatus("NEW");
        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        HlsCusPrjQuotationDetails prjQuotationDetails = new HlsCusPrjQuotationDetails();
        prjQuotationDetails.setQuotationId(prjQuotationDto.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = new ArrayList<>();
        detailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(prjQuotationDetails);
        if (detailsList.size() > 0) {
            detailsList.get(0).setSheets(prjQuotationDto.getCompressSheets());
            detailsList.get(0).set__status("update");
        } else {
            prjQuotationDetails.setSheets(prjQuotationDto.getCompressSheets());
            detailsList.add(prjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        hlsCusPrjQuotationDetailsService.batchUpdate(iRequest, detailsList);

        //保存报价现金流表
        hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);

        HlsCusPrjQuotation hlsCusPrjQuotationNew = prjQuotationMapper.selectByPrimaryKey(prjQuotationDto);

        //浮动利率-
        if (FLOATING_TYPE_FLOAT.equalsIgnoreCase(hlsCusPrjQuotationNew.getIntRateType())) {
            if (hlsCusPrjQuotationNew.getLprLinkDate() != null) {

                if (LPR_ADJUST_PERIOD_THIS.equalsIgnoreCase(hlsCusPrjQuotationNew.getLprAdjustmentPeriod())) {
                    //下次调息日期 - 本期情况下，lpr挂钩日期加上调息期限  - 放款日/起租日
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(hlsCusPrjQuotationNew.getLprLinkDate());
                    cal.add(Calendar.MONTH, Integer.parseInt(hlsCusPrjQuotationNew.getLprAdjustmentTerm()));
                    Date nextAdjustmentDate = cal.getTime();
                    hlsCusPrjQuotationNew.setNextAdjustmentDate(nextAdjustmentDate);

                    //1217-- 如果调戏期次是本期，且调息周期为0 ， 那么需要将他的下次调息日期更新为明天，目的是这种合同需要每一天都跑job，以新的利率计算
                    if(LPR_ADJUSTMENT_TERM_0.equalsIgnoreCase(hlsCusPrjQuotationNew.getLprAdjustmentTerm())){
                        Calendar cale = Calendar.getInstance();
                        cale.setTime(nextAdjustmentDate);
                        cale.add(Calendar.DAY_OF_MONTH, 1);
                        Date nextAdjustmentDateSpecial = cale.getTime();
                        hlsCusPrjQuotationNew.setNextAdjustmentDate(nextAdjustmentDateSpecial);

                        Calendar calen = Calendar.getInstance();
                        calen.setTime(hlsCusPrjQuotationNew.getLprLinkDate());
                        calen.add(Calendar.DAY_OF_MONTH, 1);
                        Date LprLinkeDateSpecial = calen.getTime();
                        hlsCusPrjQuotationNew.setLprLinkDate(LprLinkeDateSpecial);

                    }


                    hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, hlsCusPrjQuotationNew);
                } else if (LPR_ADJUST_PERIOD_NEXT.equalsIgnoreCase(hlsCusPrjQuotationNew.getLprAdjustmentPeriod())) {
                    //下次调息日期 - 次期情况下 ,算出日期在的期次，该期次的计算日即为下次调息日期
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(hlsCusPrjQuotationNew.getLprLinkDate());
                    cal.add(Calendar.MONTH, Integer.parseInt(hlsCusPrjQuotationNew.getLprAdjustmentTerm()));
                    Date nextAdjustmentDate = cal.getTime();

                    HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                    hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotationNew.getQuotationId());
                    hlsCusPrjQuotationCashflow.setSourceDocumentId(hlsCusPrjQuotationNew.getSourceDocumentId());
                    hlsCusPrjQuotationCashflow.setLprCalcDate(nextAdjustmentDate);

                    List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.queryPrjCashflowByDate(hlsCusPrjQuotationCashflow);
                    if (prjQuotationCashflowList.size() > 0) {
                        nextAdjustmentDate = prjQuotationCashflowList.get(0).getCalcDate();
                    }
                    hlsCusPrjQuotationNew.setNextAdjustmentDate(nextAdjustmentDate);
                    hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, hlsCusPrjQuotationNew);
                }
            }
        }


        //自定义本金报价 才会调用 前台的报价计算
        //回写hls_duration_ln
        HlsDurationLn durationLn = new HlsDurationLn();
        durationLn.setChangeQuotationId(hlsCusPrjQuotationNew.getQuotationId());
        List<HlsDurationLn> durationLnList = hlsDurationLnMapper.select(durationLn);
        if(durationLnList.size()>0){
            HlsDurationLn ln = durationLnList.get(0);
            ln.setAltLprRefDay(hlsCusPrjQuotationNew.getAltLprRefDay());
            ln.setAltBaseRate(hlsCusPrjQuotationNew.getAltBaseRate());
            ln.setPrinAtCd(hlsCusPrjQuotationNew.getPrinAtCd());
            ln.setUnreceivedFineAmt(hlsCusPrjQuotationNew.getUnreceivedFineAmt());
            ln.setResidualPrinAtCd(hlsCusPrjQuotationNew.getResidualPrinAtCd());
            ln.setMonthAfterAlt(hlsCusPrjQuotationNew.getMonthAfterAlt());
            ln.setFineReorganizedFlag(hlsCusPrjQuotationNew.getFineReorganizedFlag());
            ln.setAltRentingFrequency(hlsCusPrjQuotationNew.getAltRentingFrequency());
            ln.setAltRentingFreCustom(hlsCusPrjQuotationNew.getAltRentingFreCustom());
            ln.setAltRentalRoundingType(hlsCusPrjQuotationNew.getAltRentalRoundingType());
            ln.setAltPayType(hlsCusPrjQuotationNew.getAltPayType());
            ln.setAltRentalPayDate(hlsCusPrjQuotationNew.getAltRentalPayDate());
            ln.setAltLeaseTimes(hlsCusPrjQuotationNew.getAltLeaseTimes());
            ln.setAltIntRate(hlsCusPrjQuotationNew.getAltIntRate());
            ln.setAltFloatingWayRate(hlsCusPrjQuotationNew.getAltFloatingWayRate());
            ln.setAltCalcDateType(hlsCusPrjQuotationNew.getAltCalcDateType());
            ln.setAltGraceLeaseTerm(hlsCusPrjQuotationNew.getAltGraceLeaseTerm());
            HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            prjQuotationCashflow.setQuotationId(hlsCusPrjQuotationNew.getQuotationId());

            List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflow);



            HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
            contractCashflow.setContractId(ln.getContractId());
            contractCashflow.setCfStatus("RELEASE");
            List<HlsCusConContractCashflow> contractCashflowList = cashflowMapper.select(contractCashflow);
            //罚息现金流
            Double unreceivedFineAmt = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                    filter(item -> item.getCfItem().compareTo(9L) == 0 && item.getCfStatus().equals("RELEASE")).
                    collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount)).doubleValue(), 0.0), 2);
            //已收租金
            Double receivedAmount = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                    filter(item -> item.getCfItem().compareTo(1L) == 0 && item.getCfStatus().equals("RELEASE")).
                    collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedAmount)).doubleValue(), 0.0), 2);


            //变更后总租金
            Double totalRental = HlsCusMathUtil.round(nvl(prjQuotationCashflowList.stream().
                    filter(item -> item.getCfItem().compareTo(1L) == 0 && item.getCfStatus().equals("RELEASE")).
                    collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount)).doubleValue(), 0.0), 2);

            ln.setUnreceivedFineAmt(unreceivedFineAmt);
            ln.setRentalResidualAmount(HlsCusMathUtil.sub(totalRental,receivedAmount,2));

            hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
            //插入对比表 hls_duration_compare
            hlsDurataionCompareService.createCompare(iRequest, ln);
        }


        return prjQuotationDto;
    }
}
