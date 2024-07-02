package com.hand.hls.fp.mapper;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundFillingDetail;
import com.hand.hls.fp.dto.JcFundFillingLn;

import java.util.List;

/**
 * Demo class
 *
 * @author gaoqiang
 * @date 2021/08/30
 */

public interface JcFundFillingDetailMapper extends Mapper<JcFundFillingDetail> {

    void deleteFillingDetail(JcFundFillingDetail detail);

    void deleteFillingDetailSummary(JcFundFillingDetail detail);

    List<JcFundFillingDetail> queryAll(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> queryAllSummary(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> queryAllByLnId(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> queryAllNew(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> queryDetail(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryDetail(JcFundFillingDetail jcFundFillingDetail);

    void deleteFundLn(JcFundFillingDetail jcFundFillingDetail);

    void deleteFundDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailSxKyh(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailSxBlh(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTjKyh(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTjBlh(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalSec(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalInflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalOutflow(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> queryDetailMon(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryDetailQuarter(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryDetailMon(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryDetailWeek(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryDeliveryDetail(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryYdDetail(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryYdYearDetail(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryYdQuarterDetail(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryYdMonDetail(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> querySummaryYdWeekDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalSecMon(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailSxKyhMon(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailSxBlhMon(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTjKyhMon(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTjBlhMon(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalInflowMon(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalOutflowMon(JcFundFillingDetail jcFundFillingDetail);

    List<JcFundFillingDetail> queryDetailWeek(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalHA(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalBlance(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalCZ(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailCapitalInflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalJYInflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalSecSummary(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalSecSummaryDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailSxKyhc(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailSxBlhc(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTjKyhc(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTjBlhc(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalTZOutflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalCapitalOutflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalCZOutflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalJYOutflow(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalLoadDelivery(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAY(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAQ(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAM(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAW(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAYDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAQDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAMDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalWdAmountAWDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAY(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAQ(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAM(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAW(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAYDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAQDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAMDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalYdAmountAWDetail(JcFundFillingDetail jcFundFillingDetail);


    JcFundFillingDetail queryDetailTotalZqAmountAY(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAQ(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAM(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAW(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAYDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAQDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAMDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalZqAmountAWDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAY(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAQ(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAM(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAW(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAYDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAQDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAMDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalPjAmountAWDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneY(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneQ(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneM(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneW(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneYDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneQDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneMDetail(JcFundFillingDetail jcFundFillingDetail);

    JcFundFillingDetail queryDetailTotalEndBlaneWDetail(JcFundFillingDetail jcFundFillingDetail);
}